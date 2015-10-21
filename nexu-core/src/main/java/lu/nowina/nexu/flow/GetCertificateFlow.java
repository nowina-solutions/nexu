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
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import eu.europa.esig.dss.token.DSSPrivateKeyEntry;
import eu.europa.esig.dss.token.SignatureTokenConnection;
import eu.europa.esig.dss.x509.CertificateToken;
import lu.nowina.nexu.InternalAPI;
import lu.nowina.nexu.api.Feedback;
import lu.nowina.nexu.api.FeedbackStatus;
import lu.nowina.nexu.api.GetCertificateRequest;
import lu.nowina.nexu.api.GetCertificateResponse;
import lu.nowina.nexu.api.NexuAPI;
import lu.nowina.nexu.api.ScAPI;
import lu.nowina.nexu.api.signature.smartcard.TokenId;
import lu.nowina.nexu.view.core.UIDisplay;

public class GetCertificateFlow extends TokenFlow<GetCertificateRequest, GetCertificateResponse> {

	static final Logger logger = Logger.getLogger(GetCertificateFlow.class.getName());

	public GetCertificateFlow(UIDisplay display) {
		super(display);
	}

	@Override
	protected GetCertificateResponse process(NexuAPI api, GetCertificateRequest req) {

		try {

			TokenId tokenId = getTokenId(api, null);
			if (tokenId != null) {

				SignatureTokenConnection token = api.getTokenConnection(tokenId);
				if (token != null) {

					DSSPrivateKeyEntry key = selectPrivateKey(api, token, null);

					if (key != null) {

						if (isAdvancedCreation()) {
							Feedback feedback = new Feedback();
							feedback.setFeedbackStatus(FeedbackStatus.SUCCESS);
							feedback.setApiParameter(getApiParams());
							feedback.setSelectedAPI(getSelectedApi());
							feedback.setSelectedCard(getSelectedCard());

							if ((feedback.getSelectedCard() != null) && (feedback.getSelectedAPI() != null)
									&& ((feedback.getSelectedAPI() == ScAPI.MSCAPI) || (feedback.getApiParameter() != null))) {

								Feedback back = displayAndWaitUIOperation("/fxml/store-result.fxml", feedback);
								if (back != null) {
									((InternalAPI) api).store(back.getSelectedCard().getAtr(), back.getSelectedAPI(), back.getApiParameter());
								}
							} else {
								displayAndWaitUIOperation("/fxml/provide-feedback.fxml", feedback);
							}
						}

						GetCertificateResponse resp = new GetCertificateResponse();
						resp.setTokenId(tokenId);

						CertificateToken certificate = key.getCertificate();
						resp.setCertificate(certificate.getBase64Encoded());
						resp.setKeyId(certificate.getDSSIdAsString());
						resp.setEncryptionAlgorithm(certificate.getEncryptionAlgorithm());

						CertificateToken[] certificateChain = key.getCertificateChain();
						if (certificateChain != null) {
							List<String> listCertificates = new ArrayList<String>();
							for (CertificateToken certificateToken : certificateChain) {
								listCertificates.add(certificateToken.getBase64Encoded());
							}
							resp.setCertificateChain(listCertificates);
						}

						return resp;

					}

				}

			}

			displayAndWaitUIOperation("/fxml/message.fxml", "Finished");

		} catch (Exception e) {
			logger.log(Level.SEVERE, "Flow error", e);

			Feedback feedback = new Feedback(e);

			displayAndWaitUIOperation("/fxml/provide-feedback.fxml", feedback);

			displayAndWaitUIOperation("/fxml/message.fxml", "Failure");
		}

		return null;

	}

}
