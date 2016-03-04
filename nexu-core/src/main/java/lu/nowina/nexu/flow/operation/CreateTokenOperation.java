/**
 * © Nowina Solutions, 2015-2015
 *
 * Concédée sous licence EUPL, version 1.1 ou – dès leur approbation par la Commission européenne - versions ultérieures de l’EUPL (la «Licence»).
 * Vous ne pouvez utiliser la présente œuvre que conformément à la Licence.
 * Vous pouvez obtenir une copie de la Licence à l’adresse suivante:
 *
 * http://ec.europa.eu/idabc/eupl5
 *
 * Sauf obligation légale ou contractuelle écrite, le logiciel distribué sous la Licence est distribué «en l’état»,
 * SANS GARANTIES OU CONDITIONS QUELLES QU’ELLES SOIENT, expresses ou implicites.
 * Consultez la Licence pour les autorisations et les restrictions linguistiques spécifiques relevant de la Licence.
 */
package lu.nowina.nexu.flow.operation;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lu.nowina.nexu.NexuException;
import lu.nowina.nexu.api.CardAdapter;
import lu.nowina.nexu.api.DetectedCard;
import lu.nowina.nexu.api.Feedback;
import lu.nowina.nexu.api.FeedbackStatus;
import lu.nowina.nexu.api.Match;
import lu.nowina.nexu.api.NexuAPI;
import lu.nowina.nexu.api.ScAPI;
import lu.nowina.nexu.api.TokenId;
import lu.nowina.nexu.api.flow.BasicOperationStatus;
import lu.nowina.nexu.api.flow.OperationResult;
import lu.nowina.nexu.model.KeystoreParams;
import lu.nowina.nexu.model.Pkcs11Params;
import lu.nowina.nexu.view.core.UIOperation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.esig.dss.token.JKSSignatureToken;
import eu.europa.esig.dss.token.MSCAPISignatureToken;
import eu.europa.esig.dss.token.Pkcs11SignatureToken;
import eu.europa.esig.dss.token.Pkcs12SignatureToken;
import eu.europa.esig.dss.token.SignatureTokenConnection;
import eu.europa.esig.dss.token.mocca.MOCCASignatureTokenConnection;

/**
 * This {@link CompositeOperation} allows to create a {@link TokenId}.
 *
 * <p>Expected parameters:
 * <ol>
 * <li>{@link NexuAPI}</li>
 * <li>List of {@link Match}</li>
 * </ol>
 *
 * @author Jean Lepropre (jean.lepropre@nowina.lu)
 */
public class CreateTokenOperation extends AbstractCompositeOperation<Map<TokenOperationResultKey, Object>> {

	private static final Logger LOG = LoggerFactory.getLogger(CreateTokenOperation.class.getName());

	private NexuAPI api;
	private List<Match> matchingCardAdapters;
	
	public CreateTokenOperation() {
		super();
	}

	@Override
	@SuppressWarnings("unchecked")
	public void setParams(Object... params) {
		try {
			this.api = (NexuAPI) params[0];
			this.matchingCardAdapters = (List<Match>) params[1];
		} catch(final ArrayIndexOutOfBoundsException | ClassCastException e) {
			throw new IllegalArgumentException("Expected parameters: NexuAPI, List of Match");
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public OperationResult<Map<TokenOperationResultKey, Object>> perform() {
		LOG.info(matchingCardAdapters.size() + " matching card adapters");

		if (!matchingCardAdapters.isEmpty()) {
			return createTokenAuto();
		} else {
			boolean advanced = false;
			if (api.getAppConfig().isAdvancedModeAvailable() && api.getAppConfig().isEnablePopUps()) {
				LOG.info("Advanced mode available");
				final OperationResult<Boolean> result =
						operationFactory.getOperation(UIOperation.class, display, "/fxml/unsupported-product.fxml").perform();
				if(result.getStatus().equals(BasicOperationStatus.USER_CANCEL)) {
					return new OperationResult<Map<TokenOperationResultKey, Object>>(BasicOperationStatus.USER_CANCEL);
				}
				advanced = result.getResult();
			}
			
			if (advanced) {
				LOG.info("Advanced mode");
				return createTokenAdvanced();
			} else {
				LOG.info("Request support");
				if(api.getAppConfig().isEnablePopUps()) {
					final Feedback feedback = new Feedback();
					feedback.setFeedbackStatus(FeedbackStatus.PRODUCT_NOT_SUPPORTED);
					operationFactory.getOperation(UIOperation.class, display, "/fxml/provide-feedback.fxml",
							new Object[]{feedback, api.getAppConfig().getServerUrl(), api.getAppConfig().getApplicationVersion(),
									api.getAppConfig().getApplicationName()}).perform();
				}
				return new OperationResult<Map<TokenOperationResultKey, Object>>(CoreOperationStatus.UNSUPPORTED_PRODUCT);
			}
		}
	}

	private OperationResult<Map<TokenOperationResultKey, Object>> createTokenAuto() {
		final Match match = matchingCardAdapters.get(0);
		final DetectedCard supportedCard = match.getCard();
		final CardAdapter adapter = match.getAdapter();

		final SignatureTokenConnection connect = adapter.connect(api, supportedCard, display.getPasswordInputCallback());
		if (connect == null) {
			LOG.error("No connect returned");
			return new OperationResult<Map<TokenOperationResultKey, Object>>(CoreOperationStatus.NO_TOKEN);
		}
		final TokenId tokenId = api.registerTokenConnection(connect);
		if (tokenId == null) {
			LOG.error("Received null TokenId after registration");
			return new OperationResult<Map<TokenOperationResultKey, Object>>(CoreOperationStatus.NO_TOKEN_ID);
		}
		final Map<TokenOperationResultKey, Object> map = new HashMap<TokenOperationResultKey, Object>();
		map.put(TokenOperationResultKey.TOKEN_ID, tokenId);
		map.put(TokenOperationResultKey.ADVANCED_CREATION, false);
		map.put(TokenOperationResultKey.SELECTED_CARD, supportedCard);
		map.put(TokenOperationResultKey.SELECTED_CARD_ADAPTER, adapter);
		return new OperationResult<Map<TokenOperationResultKey, Object>>(map);
	}

	private OperationResult<Map<TokenOperationResultKey, Object>> createTokenAdvanced() {
		LOG.info("Advanced mode selected");
		@SuppressWarnings("unchecked")
		final OperationResult<ScAPI> result =
				operationFactory.getOperation(UIOperation.class, display, "/fxml/api-selection.fxml").perform();
		if(result.getStatus().equals(BasicOperationStatus.USER_CANCEL)) {
			return new OperationResult<Map<TokenOperationResultKey, Object>>(BasicOperationStatus.USER_CANCEL);
		}
		final Map<TokenOperationResultKey, Object> map = new HashMap<TokenOperationResultKey, Object>();
		map.put(TokenOperationResultKey.ADVANCED_CREATION, true);
		map.put(TokenOperationResultKey.SELECTED_API, result.getResult());
		final DetectedCard selectedCard = api.detectCards().get(0);
		map.put(TokenOperationResultKey.SELECTED_CARD, selectedCard);
		final TokenId tokenId;
		switch (result.getResult()) {
		case MOCCA:
			tokenId = api.registerTokenConnection(new MOCCASignatureTokenConnection(display.getPasswordInputCallback()));
			break;
		case MSCAPI:
			tokenId = api.registerTokenConnection(new MSCAPISignatureToken());
			break;
		case PKCS_11:
			@SuppressWarnings("unchecked")
			final OperationResult<Pkcs11Params> op2 =
				operationFactory.getOperation(UIOperation.class, display, "/fxml/pkcs11-params.fxml").perform();
			if(op2.getStatus().equals(BasicOperationStatus.USER_CANCEL)) {
				return new OperationResult<Map<TokenOperationResultKey, Object>>(BasicOperationStatus.USER_CANCEL);
			}
			final Pkcs11Params pkcs11Params = op2.getResult();
			final String absolutePath = pkcs11Params.getPkcs11Lib().getAbsolutePath();
			map.put(TokenOperationResultKey.SELECTED_API_PARAMS, absolutePath);
			tokenId = api.registerTokenConnection(new Pkcs11SignatureToken(absolutePath, display.getPasswordInputCallback(),
					selectedCard.getTerminalIndex()));
			break;
		case PKCS_12:
			@SuppressWarnings("unchecked")
			final OperationResult<KeystoreParams> op3 =
				operationFactory.getOperation(UIOperation.class, display, "/fxml/keystore-params.fxml").perform();
			if(op3.getStatus().equals(BasicOperationStatus.USER_CANCEL)) {
				return new OperationResult<Map<TokenOperationResultKey, Object>>(BasicOperationStatus.USER_CANCEL);
			}
			final KeystoreParams keystoreParams = op3.getResult();
			map.put(TokenOperationResultKey.SELECTED_API_PARAMS, keystoreParams.getPkcs12File().getAbsolutePath());
			switch(keystoreParams.getType()) {
			case PKCS12:
				tokenId = api.registerTokenConnection(new Pkcs12SignatureToken(keystoreParams.getPassword(), keystoreParams.getPkcs12File()));
				break;
			case JKS:
				try {
					tokenId = api.registerTokenConnection(new JKSSignatureToken(new FileInputStream(keystoreParams.getPkcs12File()), keystoreParams.getPassword()));
				} catch (FileNotFoundException e) {
					throw new NexuException(e);
				}
				break;
			default:
				throw new IllegalStateException("Unhandled keystore type: " + keystoreParams.getType());
			}
			break;
		default:
			return new OperationResult<Map<TokenOperationResultKey, Object>>(CoreOperationStatus.UNSUPPORTED_PRODUCT);
		}
		map.put(TokenOperationResultKey.TOKEN_ID, tokenId);
		return new OperationResult<Map<TokenOperationResultKey,Object>>(map);
	}
}
