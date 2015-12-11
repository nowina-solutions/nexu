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
package lu.nowina.nexu.server.api.ws;

import javax.jws.WebMethod;
import javax.jws.WebService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lu.nowina.nexu.api.Feedback;
import lu.nowina.nexu.server.business.FeedbackManager;

/**
 * WebService exposed to NexU install base.
 * 
 * @author David Naramski
 *
 */
@Service
@WebService(targetNamespace = "http://api.nexu.nowina.lu/")
public class FeedbackEndpoint {

	@Autowired
	FeedbackManager feedbackService;

	@WebMethod
	public void reportError(Feedback feedback) throws Exception {
		feedbackService.reportError(feedback);
	}

}
