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

import java.io.File;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lu.nowina.nexu.api.plugin.HttpPlugin;
import lu.nowina.nexu.api.plugin.SignaturePlugin;
import lu.nowina.nexu.generic.SCDatabase;
import lu.nowina.nexu.jetty.JettyServer;
import lu.nowina.nexu.view.SystrayMenu;
import lu.nowina.nexu.view.core.UIDisplay;

public class NexUApp extends Application implements UIDisplay {

	private static final Logger logger = Logger.getLogger(NexUApp.class.getName());

	private Stage stage;

	public static void main(String[] args) {
		launch(NexUApp.class, args);
	}
	
	@Override
	public void start(Stage primaryStage) {
		Platform.setImplicitExit(false);

		this.stage = new Stage();

		logger.config("Read configuration");

		try {
			
			SCDatabase db = null;
			File store = new File("./store.xml");
			db = SCDatabase.load(store);

			UserPreferences prefs = new UserPreferences();
			AppConfig config = new AppConfig();
			InternalAPI api = new InternalAPI(this, prefs, db);

			InputStream configFile = NexUApp.class.getClassLoader().getResourceAsStream("nexu-config.properties");
			Properties props = new Properties();
			if(configFile != null) {
				props.load(configFile);
			}

			config.setBindingPort(Integer.parseInt(props.getProperty("binding_port", "9876")));
			config.setBindingIP(props.getProperty("binding_ip", "127.0.0.1"));

			for (String key : props.stringPropertyNames()) {
				if (key.startsWith("plugin_")) {

					String pluginClassName = props.getProperty(key);
					String pluginId = key.substring("plugin_".length());
					logger.config(" + Plugin " + pluginClassName);

					Class<?> clazz = Class.forName(pluginClassName);
					for (Class<?> i : clazz.getInterfaces()) {
						Object plugin = clazz.newInstance();
						if (SignaturePlugin.class.equals(i)) {
							SignaturePlugin p = (SignaturePlugin) plugin;
							p.init(pluginId, api);
						}
						if (HttpPlugin.class.equals(i)) {
							HttpPlugin p = (HttpPlugin) plugin;
							p.init(pluginId, api);
							api.registerHttpContext(pluginId, p);
						}
					}

				}
			}

			SystrayMenu traymenu = new SystrayMenu();
			traymenu.setConfig(prefs);

			logger.info("Start Jetty");

			new Thread(() -> {
				JettyServer server = new JettyServer();
				server.setConfig(api, prefs, config);
				try {
					server.start();
				} catch (Exception e) {
					logger.log(Level.SEVERE, "Cannot Jetty", e);
				}
			}).start();

			logger.info("Start finished");

		} catch (Exception e) {
			logger.log(Level.SEVERE, "Cannot start", e);
		}
	}

	@Override
	public void display(Parent panel) {
		if (!stage.isShowing()) {
			stage = createStage();
			logger.info("Loading ui " + panel + " is a new Stage " + stage);
		} else {
			logger.info("Stage still showing, display " + panel);
		}
		stage.setScene(new Scene(panel, 300, 250));
		stage.show();
	}

	private Stage createStage() {
		Stage newStage = new Stage();
		newStage.setAlwaysOnTop(true);
		newStage.setOnCloseRequest((e) -> {
			logger.info("Closing stage " + stage + " from " + Thread.currentThread().getName());
			stage.hide();
			e.consume();
		});
		return newStage;
	}

	@Override
	public void displayWaitingPane() {
	}

	@Override
	public void close() {

		Platform.runLater(() -> {
			Stage oldStage = stage;
			logger.info("Hide stage " + stage + " and create new stage");
			stage = createStage();
			oldStage.hide();
		});
	}

	@Override
	public void stop() throws Exception {
		logger.warning("Can only happen with explicite user request");
	}

}
