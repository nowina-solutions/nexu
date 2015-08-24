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
package lu.nowina.nexu.flow;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import eu.europa.esig.dss.token.DSSPrivateKeyEntry;
import eu.europa.esig.dss.token.MSCAPISignatureToken;
import eu.europa.esig.dss.token.Pkcs11SignatureToken;
import eu.europa.esig.dss.token.Pkcs12SignatureToken;
import eu.europa.esig.dss.token.SignatureTokenConnection;
import lu.nowina.nexu.api.DetectedCard;
import lu.nowina.nexu.api.Feedback;
import lu.nowina.nexu.api.FeedbackStatus;
import lu.nowina.nexu.api.Match;
import lu.nowina.nexu.api.NexuAPI;
import lu.nowina.nexu.api.ScAPI;
import lu.nowina.nexu.api.signature.smartcard.CardAdapter;
import lu.nowina.nexu.api.signature.smartcard.TokenId;
import lu.nowina.nexu.model.KeystoreParams;
import lu.nowina.nexu.model.Pkcs11Params;
import lu.nowina.nexu.view.core.UIDisplay;
import lu.nowina.nexu.view.core.UIFlow;

public abstract class TokenFlow<I, O> extends UIFlow<I, O> {

	private static final Logger logger = Logger.getLogger(TokenFlow.class.getName());

	private boolean advancedModeAvailable = true;
	
	private boolean advancedCreation = false;
	
	private ScAPI selectedApi;
	
	private String apiParams;
	
	private DetectedCard selectedCard;

	public TokenFlow(UIDisplay display) {
		super(display);
	}

	protected DSSPrivateKeyEntry selectPrivateKey(NexuAPI api, SignatureTokenConnection token, String keyFilter) {
		
		return selectPrivateKey(token, keyFilter);
		
	}
	
	protected SignatureTokenConnection getTokenConnection(NexuAPI api, TokenId previousTokenId) {

		
		TokenId tokenId = getTokenId(api, previousTokenId);
		if(tokenId != null) {
			return api.getTokenConnection(tokenId);
		}
		
		return null;
		
	}
	
	protected TokenId getTokenId(NexuAPI api, TokenId previousTokenId) {
		
		if(previousTokenId != null) {
			return previousTokenId;
		}
		
		TokenId tokenId = createToken(api);
		return tokenId;
		
	}
	
	protected TokenId createToken(NexuAPI api) {

		List<DetectedCard> detectedCards = api.detectCards();
		logger.info(detectedCards.size() + " card detected");

		if (detectedCards.size() == 0) {

			Feedback feedback = new Feedback();
			feedback.setFeedbackStatus(FeedbackStatus.NO_PRODUCT_FOUND);
			displayAndWaitUIOperation("/fxml/provide-feedback.fxml", feedback);

		} else {

			TokenId tokenId = createToken(api, detectedCards);
			return tokenId;

		}
		
		return null;
	}
	
	protected TokenId createToken(NexuAPI api, List<DetectedCard> detectedCards) {

		List<DetectedCard> supportedCards = new ArrayList<>();

		for (DetectedCard d : detectedCards) {

			List<Match> matchingAdapters = api.matchingCardAdapters(d);

			if (matchingAdapters != null && !matchingAdapters.isEmpty()) {
				supportedCards.add(d);
			}

		}

		logger.info(supportedCards.size() + " card supported");

		if (supportedCards.size() != 0) {

			return createTokenAuto(api, detectedCards);

		} else {

			boolean advanced = false;

			if (isAdvancedModeAvailable()) {

				logger.info("Advanced mode available");
				advanced = displayAndWaitUIOperation("/fxml/unsupported-product.fxml");

			}

			if (advanced) {

				if(detectedCards.size() == 1) {
					DetectedCard firstMatch = detectedCards.get(0);
					this.selectedCard = firstMatch;
				}
				
				this.advancedCreation = true;
				return createTokenAdvanced(api);

			} else {

				logger.info("Request support");
				
				Feedback feedback = new Feedback();
				feedback.setFeedbackStatus(FeedbackStatus.PRODUCT_NOT_SUPPORTED);
				
				displayAndWaitUIOperation("/fxml/provide-feedback.fxml", feedback);

			}

		}

		return null;
	}

	/**
	 * 
	 * @return
	 */
	private TokenId createTokenAdvanced(NexuAPI api) {
		logger.info("Advanced mode selected");
		ScAPI scApi = displayAndWaitUIOperation("/fxml/api-selection.fxml");
		this.selectedApi = scApi;
		switch (scApi) {
		case MSCAPI:
			return api.registerTokenConnection(new MSCAPISignatureToken());
		case PKCS_11:
			Pkcs11Params pkcs11Params = displayAndWaitUIOperation("/fxml/pkcs11-params.fxml");
			String absolutePath = pkcs11Params.getPkcs11Lib().getAbsolutePath();
			absolutePath = absolutePath.replaceAll("\\s", "\\\\ ");
			this.apiParams = absolutePath;
			return api.registerTokenConnection(new Pkcs11SignatureToken(absolutePath,
					getPasswordInputCallback()));
		case PKCS_12:
			KeystoreParams pkcs12Params = displayAndWaitUIOperation("/fxml/keystore-params.fxml");
			return api.registerTokenConnection(
					new Pkcs12SignatureToken(pkcs12Params.getPassword(), pkcs12Params.getPkcs12File()));
		}
		return null;
	}

	/**
	 * 
	 * @param api
	 * @param detectedCards
	 * @return
	 */
	private TokenId createTokenAuto(NexuAPI api, List<DetectedCard> detectedCards) {
		DetectedCard card = null;

		if (detectedCards.size() == 1) {
			card = detectedCards.get(0);
			logger.info("One card detected " + card);

		} else {
			// size() > 1
			logger.info("Select one card");
		}

		List<Match> adapters = api.matchingCardAdapters(card);
		Match firstMatch = adapters.get(0);
		this.selectedCard = firstMatch.getCard();
		CardAdapter adapter = firstMatch.getAdapter();

		TokenId tokenId = adapter.connect(api, card, getPasswordInputCallback());
		return tokenId;
	}

	/**
	 * Return a private key of the provided token. The key is selected depending
	 * on the optional filter and user choice.
	 * 
	 * @param token
	 * @param keyFilter
	 * @return
	 */
	protected DSSPrivateKeyEntry selectPrivateKey(SignatureTokenConnection token, String keyFilter) {
		List<DSSPrivateKeyEntry> keys = token.getKeys();
		DSSPrivateKeyEntry key = null;

		Iterator<DSSPrivateKeyEntry> it = keys.iterator();
		while(it.hasNext()) {
			DSSPrivateKeyEntry e = it.next();
			if("CN=Token Signing Public Key".equals(e.getCertificate().getIssuerDN().getName())) {
				it.remove();
			}
		}
		
		if(keys.isEmpty()) {
			return null;
		} else if (keys.size() == 1) {
			key = keys.get(0);
		} else {

			if (keyFilter != null) {
				for (DSSPrivateKeyEntry k : keys) {
					if (k.getCertificate().getDSSIdAsString().equals(keyFilter)) {
						key = k;
					}
				}
			}

			if (key == null) {
				key = displayAndWaitUIOperation("/fxml/key-selection.fxml", keys);
			}

		}
		return key;
	}

	private boolean isAdvancedModeAvailable() {
		return advancedModeAvailable;
	}
	
	public boolean isAdvancedCreation() {
		return advancedCreation;
	}

	protected String getApiParams() {
		return apiParams;
	}
	
	protected ScAPI getSelectedApi() {
		return selectedApi;
	}
	
	protected DetectedCard getSelectedCard() {
		return selectedCard;
	}
	
}