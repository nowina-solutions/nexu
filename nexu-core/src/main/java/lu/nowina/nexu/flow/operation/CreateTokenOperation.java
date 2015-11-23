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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lu.nowina.nexu.NexuLauncher;
import lu.nowina.nexu.api.CardAdapter;
import lu.nowina.nexu.api.DetectedCard;
import lu.nowina.nexu.api.Feedback;
import lu.nowina.nexu.api.FeedbackStatus;
import lu.nowina.nexu.api.Match;
import lu.nowina.nexu.api.NexuAPI;
import lu.nowina.nexu.api.ScAPI;
import lu.nowina.nexu.api.TokenId;
import lu.nowina.nexu.model.KeystoreParams;
import lu.nowina.nexu.model.Pkcs11Params;
import lu.nowina.nexu.view.core.UIOperation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.esig.dss.token.MSCAPISignatureToken;
import eu.europa.esig.dss.token.Pkcs11SignatureToken;
import eu.europa.esig.dss.token.Pkcs12SignatureToken;
import eu.europa.esig.dss.token.SignatureTokenConnection;
import eu.europa.esig.dss.token.mocca.MOCCASignatureTokenConnection;

/**
 * This {@link CompositeOperation} allows to create a {@link TokenId}.
 *
 * <p>Expected parameter: {@link NexuAPI}.
 *
 * @author Jean Lepropre (jean.lepropre@nowina.lu)
 */
public class CreateTokenOperation extends AbstractCompositeOperation<Map<TokenOperationResultKey, Object>> {

	private static final Logger LOG = LoggerFactory.getLogger(CreateTokenOperation.class.getName());

	private NexuAPI api;
	
	public CreateTokenOperation() {
		super();
	}

	@Override
	public void setParams(Object... params) {
		try {
			this.api = (NexuAPI) params[0];
		} catch(final ArrayIndexOutOfBoundsException | ClassCastException e) {
			throw new IllegalArgumentException("Expected parameter: NexuAPI");
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public OperationResult<Map<TokenOperationResultKey, Object>> perform() {
		final List<DetectedCard> detectedCards = api.detectCards();
		LOG.info(detectedCards.size() + " card detected");

		if (detectedCards.size() == 0) {
			final Feedback feedback = new Feedback();
			feedback.setFeedbackStatus(FeedbackStatus.NO_PRODUCT_FOUND);
			operationFactory.getOperation(UIOperation.class, display, "/fxml/provide-feedback.fxml",
					new Object[]{feedback}).perform();
			return new OperationResult<Map<TokenOperationResultKey, Object>>(OperationStatus.FAILED);
		} else {
			return createToken(detectedCards);
		}
	}

	@SuppressWarnings("unchecked")
	private OperationResult<Map<TokenOperationResultKey, Object>> createToken(List<DetectedCard> detectedCards) {
		if(detectedCards.size() > 1) {
			LOG.warn("More than one card. Not supported yet. We will take the first one having a matching adapter.");
		}
		
		final List<DetectedCard> supportedCards = new ArrayList<>();
		for (final DetectedCard d : detectedCards) {
			final List<Match> matchingAdapters = api.matchingCardAdapters(d);
			if (matchingAdapters != null && !matchingAdapters.isEmpty()) {
				supportedCards.add(d);
			}
		}

		LOG.info(supportedCards.size() + " card supported");

		if (supportedCards.size() != 0) {
			return createTokenAuto(supportedCards.get(0));
		} else {
			boolean advanced = false;
			if (isAdvancedModeAvailable()) {
				LOG.info("Advanced mode available");
				final OperationResult<Boolean> result =
						operationFactory.getOperation(UIOperation.class, display, "/fxml/unsupported-product.fxml").perform();
				if(!result.getStatus().equals(OperationStatus.SUCCESS)) {
					return new OperationResult<Map<TokenOperationResultKey, Object>>(OperationStatus.FAILED);
				}
				advanced = result.getResult();
			}
			
			if (advanced) {
				LOG.info("Advanced mode");
				return createTokenAdvanced(detectedCards.get(0));
			} else {
				LOG.info("Request support");
				final Feedback feedback = new Feedback();
				feedback.setFeedbackStatus(FeedbackStatus.PRODUCT_NOT_SUPPORTED);
				operationFactory.getOperation(UIOperation.class, display, "/fxml/provide-feedback.fxml", new Object[]{feedback}).perform();
				return new OperationResult<Map<TokenOperationResultKey, Object>>(OperationStatus.FAILED);
			}
		}
	}

	private OperationResult<Map<TokenOperationResultKey, Object>> createTokenAuto(final DetectedCard supportedCard) {
		final List<Match> adapters = api.matchingCardAdapters(supportedCard);
		final Match firstMatch = adapters.get(0);
		final CardAdapter adapter = firstMatch.getAdapter();

		final SignatureTokenConnection connect = adapter.connect(api, supportedCard, display.getPasswordInputCallback());
		if (connect == null) {
			LOG.error("No connect returned");
			return new OperationResult<Map<TokenOperationResultKey, Object>>(OperationStatus.FAILED);
		}
		final TokenId tokenId = api.registerTokenConnection(connect);
		if (tokenId == null) {
			LOG.error("Received null TokenId after registration");
			return new OperationResult<Map<TokenOperationResultKey, Object>>(OperationStatus.FAILED);
		}
		final Map<TokenOperationResultKey, Object> map = new HashMap<TokenOperationResultKey, Object>();
		map.put(TokenOperationResultKey.TOKEN_ID, tokenId);
		map.put(TokenOperationResultKey.ADVANCED_CREATION, false);
		map.put(TokenOperationResultKey.SELECTED_CARD, supportedCard);
		return new OperationResult<Map<TokenOperationResultKey, Object>>(map);
	}

	protected boolean isAdvancedModeAvailable() {
		return NexuLauncher.getConfig().isAdvancedModeAvailable();
	}
	
	private OperationResult<Map<TokenOperationResultKey, Object>> createTokenAdvanced(final DetectedCard detectedCard) {
		LOG.info("Advanced mode selected");
		@SuppressWarnings("unchecked")
		final OperationResult<ScAPI> result =
				operationFactory.getOperation(UIOperation.class, display, "/fxml/api-selection.fxml").perform();
		if(!result.getStatus().equals(OperationStatus.SUCCESS)) {
			return new OperationResult<Map<TokenOperationResultKey, Object>>(OperationStatus.FAILED);
		}
		final Map<TokenOperationResultKey, Object> map = new HashMap<TokenOperationResultKey, Object>();
		map.put(TokenOperationResultKey.ADVANCED_CREATION, true);
		map.put(TokenOperationResultKey.SELECTED_API, result.getResult());
		map.put(TokenOperationResultKey.SELECTED_CARD, detectedCard);
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
			if(!op2.getStatus().equals(OperationStatus.SUCCESS)) {
				return new OperationResult<Map<TokenOperationResultKey, Object>>(OperationStatus.FAILED);
			}
			final Pkcs11Params pkcs11Params = op2.getResult();
			final String absolutePath = pkcs11Params.getPkcs11Lib().getAbsolutePath();
			map.put(TokenOperationResultKey.SELECTED_API_PARAMS, absolutePath);
			tokenId = api.registerTokenConnection(new Pkcs11SignatureToken(absolutePath, display.getPasswordInputCallback()));
			break;
		case PKCS_12:
			@SuppressWarnings("unchecked")
			final OperationResult<KeystoreParams> op3 =
				operationFactory.getOperation(UIOperation.class, display, "/fxml/keystore-params.fxml").perform();
			if(!op3.getStatus().equals(OperationStatus.SUCCESS)) {
				return new OperationResult<Map<TokenOperationResultKey, Object>>(OperationStatus.FAILED);
			}
			final KeystoreParams keysToreParams = op3.getResult();
			map.put(TokenOperationResultKey.SELECTED_API_PARAMS, keysToreParams.getPkcs12File().getAbsolutePath());
			tokenId = api.registerTokenConnection(new Pkcs12SignatureToken(keysToreParams.getPassword(), keysToreParams.getPkcs12File()));
			break;
		default:
			return new OperationResult<Map<TokenOperationResultKey, Object>>(OperationStatus.FAILED);
		}
		map.put(TokenOperationResultKey.TOKEN_ID, tokenId);
		return new OperationResult<Map<TokenOperationResultKey,Object>>(map);
	}
}
