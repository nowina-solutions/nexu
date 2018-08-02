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
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.animation.PauseTransition;
import javafx.application.Preloader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import lu.nowina.nexu.api.AppConfig;

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
			alert.showAndWait();
		} else {
			LOGGER.error("Unknown preloader notification class: " + info.getClass().getName());
		}
	}
	
	/**
	 * <p>
	 * Displays splash screen at Nexu's startup.
	 * </p>
	 * <p>
	 * Splash screen is activated with runtime parameter :
	 * -Djavafx.preloader=lu.nowina.nexu.NexuPreloader
	 * </p>
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {
		if(getConfig().isShowSplashScreen()) {
			final ImageView splash = new ImageView(new Image(NexUPreLoader.class.getResourceAsStream("/images/splash.jpg")));
			final StackPane background = new StackPane(splash);
			final Scene splashScene = new Scene(background, 600, 300);
			primaryStage.setScene(splashScene);
			primaryStage.initStyle(StageStyle.UNDECORATED);
			primaryStage.show();
			final PauseTransition delay = new PauseTransition(Duration.seconds(3));
			delay.setOnFinished(event -> primaryStage.close());
			delay.play();
		}
    }

	@Override
	public boolean handleErrorNotification(ErrorNotification info) {
		// Log error messages
		LOGGER.error("An error has occurred during startup", info.getCause());
		
		// Display dialog
		final Alert alert = new Alert(AlertType.WARNING);
		alert.setTitle(resourceBundle.getString("preloader.error"));
		alert.setHeaderText(MessageFormat.format(resourceBundle.getString("preloader.error.occurred"), getConfig().getApplicationName()));
		alert.setContentText(resourceBundle.getString("contact.application.provider"));
		
		alert.showAndWait();
		return true;
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
		
		public PreloaderMessage(AlertType messageType, String title, String headerText, String contentText) {
			super();
			this.messageType = messageType;
			this.title = title;
			this.headerText = headerText;
			this.contentText = contentText;
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
	}
}
