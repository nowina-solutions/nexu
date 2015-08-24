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
package lu.nowina.nexu.view.core;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Logger;

import eu.europa.esig.dss.token.PasswordInputCallback;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import lu.nowina.nexu.api.NexuAPI;

/**
 * Represent the flow of UI of a complete operation.
 * 
 * @author David Naramski
 *
 */
public abstract class UIFlow<I, O> {

	private static final Logger logger = Logger.getLogger(UIFlow.class.getName());

	private UIDisplay display;

	private ExecutorService executor = Executors.newSingleThreadExecutor();

	public UIFlow(UIDisplay display) {
		if (display == null) {
			throw new IllegalArgumentException("display cannot be null");
		}
		this.display = display;
	}

	public final O execute(NexuAPI api, I input) {

		try {

			Future<O> task = executor.submit(() -> {
				O out = start(api, input);
				display.close();
				return out;
			});

			return task.get();

		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	/**
	 * Méthode déclenchée pour démarrer le process. Les variables environement
	 * et dialogDisplays sont déjà initialisée.
	 */
	protected abstract O start(NexuAPI api, I input);

	protected PasswordInputCallback getPasswordInputCallback() {
		return new FlowPasswordCallback();
	}

	protected <T extends Object> T displayAndWaitUIOperation(String fxml) {
		return displayAndWaitUIOperation(fxml, (Object[]) null);
	}

	protected <T extends Object> T displayAndWaitUIOperation(String fxml, Object... params) {

		logger.info("Loading " + fxml + " view");
		FXMLLoader loader = new FXMLLoader();
		try {
			loader.load(getClass().getResourceAsStream(fxml));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		Parent root = loader.getRoot();
		UIOperation<T> controller = loader.getController();

		display(root);
		return waitForUser(controller, params);
	}

	private <T> T waitForUser(UIOperation<T> controller, Object... params) {
		try {
			logger.info("Wait on Thread " + Thread.currentThread().getName());
			controller.init(params);
			T r = controller.waitEnd();
			display.displayWaitingPane();
			return r;
		} catch (InterruptedException e) {
			throw new RuntimeException();
		}
	}

	private void display(Parent root) {
		logger.info("Display " + root + " in display " + display + " from Thread " + Thread.currentThread().getName());
		Platform.runLater(() -> {
			logger.info(
					"Display " + root + " in display " + display + " from Thread " + Thread.currentThread().getName());
			display.display(root);
		});
	}

	private final class FlowPasswordCallback implements PasswordInputCallback {
		@Override
		public char[] getPassword() {
			logger.info("Request password");
			return displayAndWaitUIOperation("/fxml/password-input.fxml");
		}
	}

}
