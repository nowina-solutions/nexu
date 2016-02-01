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
package lu.nowina.nexu;

import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.application.Preloader;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import lu.nowina.nexu.api.AppConfig;
import lu.nowina.nexu.api.EnvironmentInfo;
import lu.nowina.nexu.api.Feedback;
import lu.nowina.nexu.api.FeedbackClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JavaFX {@link Preloader} used to display and log error messages during JavaFX startup.
 *
 * @author Jean Lepropre (jean.lepropre@nowina.lu)
 */
public class NexUPreLoader extends Preloader {

	private static final Logger LOGGER = LoggerFactory.getLogger(NexUPreLoader.class);
	private final ResourceBundle resourceBundle;
	
	public NexUPreLoader() {
		super();
		resourceBundle = ResourceBundle.getBundle("bundles/nexu");
	}

	private AppConfig getConfig() {
		return NexuLauncher.getConfig();
	}
	
	private List<PreLoaderMessage> getPreLoaderMessages() {
		return NexuLauncher.getPreLoaderMessages();
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
    	// Check if some messages must be displayed
    	for(final PreLoaderMessage preLoaderMessage : getPreLoaderMessages()) {
    		LOGGER.warn("PreLoaderMessage: type = " + preLoaderMessage.getMessageType() + ", title = " + preLoaderMessage.getTitle()
    				+", header = " + preLoaderMessage.getHeaderText() + ", content = " + preLoaderMessage.getContentText());
    		
    		final Alert alert = new Alert(preLoaderMessage.getMessageType());
    		alert.setTitle(preLoaderMessage.getTitle());
    		alert.setHeaderText(preLoaderMessage.getHeaderText());
    		alert.setContentText(preLoaderMessage.getContentText());
    		final Optional<ButtonType> result = alert.showAndWait();
    		if(preLoaderMessage.isSendFeedback() && (result.get() == ButtonType.OK)) {
    			sendFeedback(preLoaderMessage.getException());
    		}
    	}
    }

	@Override
	public boolean handleErrorNotification(ErrorNotification info) {
		// Log error messages
		LOGGER.error("An error has occurred during startup", info.getCause());
		
		// Display dialog
		final Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle(resourceBundle.getString("preloader.error"));
		alert.setHeaderText(MessageFormat.format(resourceBundle.getString("preloader.error.occurred"), getConfig().getApplicationName()));
		alert.setContentText(resourceBundle.getString("provide.feedback"));
		
		final Optional<ButtonType> result = alert.showAndWait();
		if(result.get() == ButtonType.OK) {
			sendFeedback(info.getCause());
		}
		return true;
	}
	
	private void sendFeedback(final Throwable t) {
		final Exception exception;
		if(t instanceof Exception) {
			exception = (Exception) t;
		} else {
			exception = new NexuException(t);
		}
		final Feedback feedback = new Feedback(exception);
		feedback.setNexuVersion(getConfig().getApplicationVersion());
		feedback.setInfo(EnvironmentInfo.buildFromSystemProperties(System.getProperties()));
		final FeedbackClient client = new FeedbackClient(getConfig().getServerUrl());
		client.reportError(feedback);
	}
	
	/**
	 * POJO that holds information about a message that must be displayed by {@link NexUPreLoader}.
	 *
	 * @author Jean Lepropre (jean.lepropre@nowina.lu)
	 */
	static class PreLoaderMessage {
		private final AlertType messageType;
		private final String title;
		private final String headerText;
		private final String contentText;
		private final boolean sendFeedback;
		private final Throwable exception;
		
		public PreLoaderMessage(AlertType messageType, String title, String headerText, String contentText, boolean sendFeedback,
				Throwable exception) {
			super();
			this.messageType = messageType;
			this.title = title;
			this.headerText = headerText;
			this.contentText = contentText;
			this.sendFeedback = sendFeedback;
			this.exception = exception;
		}

		public AlertType getMessageType() {
			return messageType;
		}

		public String getTitle() {
			return title;
		}

		public String getHeaderText() {
			return headerText;
		}

		public String getContentText() {
			return contentText;
		}
		
		public boolean isSendFeedback() {
			return sendFeedback;
		}
		
		public Throwable getException() {
			return exception;
		}
	}
}
