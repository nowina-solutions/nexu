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

import java.util.Optional;

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
import lu.nowina.nexu.api.FeedbackClient;

/**
 * JavaFX {@link Preloader} used to display and log error messages during JavaFX startup.
 *
 * @author Jean Lepropre (jean.lepropre@nowina.lu)
 */
public class NexUPreLoader extends Preloader {

	private static final Logger LOGGER = LoggerFactory.getLogger(NexUPreLoader.class);
	
	public NexUPreLoader() {
		super();
	}

	private AppConfig getConfig() {
		return NexuLauncher.getConfig();
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
		alert.setTitle("Error");
		alert.setHeaderText("An error has occurred when starting " + getConfig().getApplicationName());
		alert.setContentText("Do want to provide some anonymous feedback to improve application?");
		
		final Optional<ButtonType> result = alert.showAndWait();
		if(result.get() == ButtonType.OK) {
			final Throwable cause = info.getCause();
			final Exception exception;
			if(cause instanceof Exception) {
				exception = (Exception) cause;
			} else {
				exception = new NexuException(cause);
			}
			final Feedback feedback = new Feedback(exception);
			feedback.setNexuVersion(getConfig().getApplicationVersion());
			feedback.setInfo(EnvironmentInfo.buildFromSystemProperties(System.getProperties()));
			final FeedbackClient client = new FeedbackClient(getConfig().getServerUrl());
			client.reportError(feedback);
		}
		return true;
	}
}
