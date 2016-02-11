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
