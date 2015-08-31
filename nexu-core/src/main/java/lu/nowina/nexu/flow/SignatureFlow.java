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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import lu.nowina.nexu.InternalAPI;
import lu.nowina.nexu.api.Feedback;
import lu.nowina.nexu.api.FeedbackStatus;
import lu.nowina.nexu.api.NexuAPI;
import lu.nowina.nexu.api.ScAPI;
import lu.nowina.nexu.api.SignatureRequest;
import lu.nowina.nexu.api.SignatureResponse;
import lu.nowina.nexu.api.signature.smartcard.TokenId;
import lu.nowina.nexu.view.core.UIDisplay;
import eu.europa.esig.dss.SignatureValue;
import eu.europa.esig.dss.token.DSSPrivateKeyEntry;
import eu.europa.esig.dss.token.SignatureTokenConnection;

public class SignatureFlow extends TokenFlow<SignatureRequest, SignatureResponse> {

	private static final Logger logger = Logger.getLogger(SignatureFlow.class.getName());

	public SignatureFlow(UIDisplay display) {
		super(display);
	}

	@Override
	protected SignatureResponse start(NexuAPI api, SignatureRequest req) {

		if ((req.getToBeSigned() == null) || (req.getToBeSigned().getBytes() == null)) {
			throw new IllegalArgumentException("ToBeSigned is null");
		}

		if((req.getDigestAlgorithm() == null)) {
			throw new IllegalArgumentException("Digest algorithm expected");
		}

		SignatureTokenConnection token = null;
		try {

			TokenId tokenId = getTokenId(api, req.getTokenId());
			if (tokenId != null) {

				token = api.getTokenConnection(tokenId);
				logger.info("Token " + token);

				if (token != null) {
					DSSPrivateKeyEntry key = selectPrivateKey(api, token, req.getKeyId());

					if (key != null) {

						logger.info("Key " + key + " " + key.getCertificate().getSubjectDN() + " from "
								+ key.getCertificate().getIssuerDN());
						SignatureValue value = token.sign(req.getToBeSigned(), req.getDigestAlgorithm(), key);
						logger.info("Signature performed " + value);

						if (isAdvancedCreation()) {

							Feedback feedback = new Feedback();
							feedback.setFeedbackStatus(FeedbackStatus.SUCCESS);
							feedback.setApiParameter(getApiParams());
							feedback.setSelectedAPI(getSelectedApi());
							feedback.setSelectedCard(getSelectedCard());

							if ((feedback.getSelectedCard() != null) && (feedback.getSelectedAPI() != null)
									&& ((feedback.getSelectedAPI() == ScAPI.MSCAPI)
											|| (feedback.getApiParameter() != null))) {

								Feedback back = displayAndWaitUIOperation("/fxml/store-result.fxml", feedback);
								if (back != null) {
									((InternalAPI) api).store(back.getSelectedCard().getAtr(), back.getSelectedAPI(),
											back.getApiParameter());
								}

							} else {
								displayAndWaitUIOperation("/fxml/message.fxml", "Signature performed");
							}

						} else {
							displayAndWaitUIOperation("/fxml/message.fxml", "Signature performed");
						}

						SignatureResponse resp = new SignatureResponse(value);
						return resp;

					} else {

						displayAndWaitUIOperation("/fxml/message.fxml", "Error - No keys");

					}

				} else {

					displayAndWaitUIOperation("/fxml/message.fxml", "Error - Token no recognized");

				}

			}

		} catch (Exception e) {
			logger.log(Level.SEVERE, "Flow error", e);

			Feedback feedback = new Feedback();
			feedback.setFeedbackStatus(FeedbackStatus.EXCEPTION);

			StringWriter buffer = new StringWriter();
			PrintWriter writer = new PrintWriter(buffer);
			e.printStackTrace(writer);
			writer.close();

			feedback.setStacktrace(buffer.toString());

			displayAndWaitUIOperation("/fxml/provide-feedback.fxml", feedback);

			displayAndWaitUIOperation("/fxml/message.fxml");
		} finally {
			if (token !=null){
				token.close();
			}
		}

		return null;

	}

}
