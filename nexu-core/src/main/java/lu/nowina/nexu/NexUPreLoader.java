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
import java.util.Optional;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.Preloader;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import lu.nowina.nexu.api.AppConfig;
import lu.nowina.nexu.api.EnvironmentInfo;
import lu.nowina.nexu.api.Feedback;
import lu.nowina.nexu.generic.FeedbackSender;
import lu.nowina.nexu.generic.HttpDataSender;

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
	
	@Override
	public void handleApplicationNotification(PreloaderNotification info) {
		if(info instanceof PreloaderMessage) {
			final PreloaderMessage preloaderMessage = (PreloaderMessage) info;
			LOGGER.warn("PreLoaderMessage: type = " + preloaderMessage.getMessageType() + ", title = " + preloaderMessage.getTitle()
				+", header = " + preloaderMessage.getHeaderText() + ", content = " + preloaderMessage.getContentText());

			final Alert alert = new Alert(preloaderMessage.getMessageType());
			alert.setTitle(preloaderMessage.getTitle());
			alert.setHeaderText(preloaderMessage.getHeaderText());
			alert.setContentText(preloaderMessage.getContentText());
			final Optional<ButtonType> result = alert.showAndWait();
			if(preloaderMessage.isSendFeedback() && (result.get() == ButtonType.OK)) {
				sendFeedback(preloaderMessage.getException());
			}
		} else {
			LOGGER.error("Unknown preloader notification class: " + info.getClass().getName());
		}
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		// Nothing to do
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
		final FeedbackSender sender = new FeedbackSender(getConfig(), new HttpDataSender(NexuLauncher.getProxyConfigurer()));
		sender.sendFeedback(feedback);
	}
	
	/**
	 * POJO that holds information about a message that must be displayed by {@link NexUPreLoader}.
	 *
	 * @author Jean Lepropre (jean.lepropre@nowina.lu)
	 */
	static class PreloaderMessage implements PreloaderNotification {
		private final AlertType messageType;
		private final String title;
		private final String headerText;
		private final String contentText;
		private final boolean sendFeedback;
		private final Throwable exception;
		
		public PreloaderMessage(AlertType messageType, String title, String headerText, String contentText, boolean sendFeedback,
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
