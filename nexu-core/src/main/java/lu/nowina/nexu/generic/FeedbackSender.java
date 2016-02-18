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
package lu.nowina.nexu.generic;

import java.io.IOException;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lu.nowina.nexu.api.AppConfig;
import lu.nowina.nexu.api.Feedback;

public class FeedbackSender {
	
	private static final Logger logger = LoggerFactory.getLogger(FeedbackSender.class);
	
	private HttpDataSender dataSender;
	
	private String serverUrl;
	
	public FeedbackSender(AppConfig config, HttpDataSender dataSender) {
		this.serverUrl = config.getServerUrl();
		this.dataSender = dataSender;
	}

	public void sendFeedback(Feedback feedback) {
		try {
			StringWriter writer = new StringWriter();
			JAXBContext context = JAXBContext.newInstance(Feedback.class);
			Marshaller marshaller = context.createMarshaller();
			marshaller.marshal(feedback, writer);
			
			dataSender.sendFeedback(serverUrl + "/feedback", writer.toString());
		} catch (IOException | JAXBException e) {
			logger.error("Cannot send feedback", e);
		}
	}
}
