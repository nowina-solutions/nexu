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
import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import lu.nowina.nexu.NexUPreLoader.PreloaderMessage;
import lu.nowina.nexu.api.AppConfig;
import lu.nowina.nexu.api.EnvironmentInfo;
import lu.nowina.nexu.api.plugin.HttpPlugin;
import lu.nowina.nexu.api.plugin.InitializationMessage;
import lu.nowina.nexu.api.plugin.NexuPlugin;
import lu.nowina.nexu.flow.BasicFlowRegistry;
import lu.nowina.nexu.flow.Flow;
import lu.nowina.nexu.flow.FlowRegistry;
import lu.nowina.nexu.flow.operation.BasicOperationFactory;
import lu.nowina.nexu.flow.operation.OperationFactory;
import lu.nowina.nexu.generic.DatabaseWebLoader;
import lu.nowina.nexu.generic.HttpDataLoader;
import lu.nowina.nexu.generic.SCDatabase;
import lu.nowina.nexu.generic.SCDatabaseLoader;
import lu.nowina.nexu.view.core.UIDisplay;

public class NexUApp extends Application {

	private static final Logger logger = LoggerFactory.getLogger(NexUApp.class.getName());

	private AppConfig getConfig() {
		return NexuLauncher.getConfig();
	}

	private Properties getProperties() {
		return NexuLauncher.getProperties();
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		Platform.setImplicitExit(false);

		final StandaloneUIDisplay uiDisplay = new StandaloneUIDisplay();
		final OperationFactory operationFactory = new BasicOperationFactory();
		operationFactory.setDisplay(uiDisplay);
		uiDisplay.setOperationFactory(operationFactory);
		
		DatabaseWebLoader loader = new DatabaseWebLoader(NexuLauncher.getConfig(),
				new HttpDataLoader(NexuLauncher.getProxyConfigurer(), getConfig().getApplicationVersion(), getConfig().isSendAnonymousInfoToProxy()));
		loader.start();
		
		final InternalAPI api = buildAPI(uiDisplay, operationFactory, loader);

		logger.info("Start Jetty");

		startHttpServer(api);

		new SystrayMenu(operationFactory, loader, api, new UserPreferences(getConfig().getApplicationName()));

		logger.info("Start finished");
	}

	protected InternalAPI buildAPI(final UIDisplay uiDisplay, final OperationFactory operationFactory, final DatabaseWebLoader loader) throws IOException {
		File nexuHome = NexuLauncher.getNexuHome();
		SCDatabase db = null;
		if (nexuHome != null) {
			File store = new File(nexuHome, "store.xml");
			logger.info("Load database from " + store.getAbsolutePath());
			db = SCDatabaseLoader.load(store);
		} else {
			db = new SCDatabase();
		}

		CardDetector detector = new CardDetector(EnvironmentInfo.buildFromSystemProperties(System.getProperties()));

		InternalAPI api = new InternalAPI(uiDisplay, db, detector, loader.getDatabase(), getFlowRegistry(), operationFactory, getConfig());

		for (String key : getProperties().stringPropertyNames()) {
			if (key.startsWith("plugin_")) {

				String pluginClassName = getProperties().getProperty(key);
				String pluginId = key.substring("plugin_".length());

				logger.info(" + Plugin " + pluginClassName);
				buildAndRegisterPlugin(api, pluginClassName, pluginId, false);

			}
		}
		return api;
	}

	/**
	 * Returns the {@link FlowRegistry} to use to resolve {@link Flow}s.
	 * @return The {@link FlowRegistry} to use to resolve {@link Flow}s.
	 */
	protected FlowRegistry getFlowRegistry() {
		return new BasicFlowRegistry();
	}
	
	private void startHttpServer(InternalAPI api) throws Exception {
		final HttpServer server = buildHttpServer();
		server.setConfig(api);
		try {
			server.start();
		} catch(Exception e) {
			try {
				server.stop();
			} catch(Exception e1) {}
			throw e;
		}
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			try {
				server.stop();
			} catch (Exception e) {
				logger.error("Cannot stop server", e);
			}
		}));
		new Thread(() -> {
			try {
				server.join();
			} catch(Exception e) {
				logger.error("Exception on join", e);
			}
		});
	}

	/**
	 * Build the HTTP Server for the platform
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private HttpServer buildHttpServer() {
		try {
			Class<HttpServer> cla = (Class<HttpServer>) Class.forName(getConfig().getHttpServerClass());
			logger.info("HttpServer is " + getConfig().getHttpServerClass());
			HttpServer server = cla.newInstance();
			return server;
		} catch (Exception e) {
			logger.error("Cannot instanciate Http Server " + getConfig().getHttpServerClass(), e);
			throw new RuntimeException("Cannot instanciate Http Server");
		}
	}

	private void buildAndRegisterPlugin(InternalAPI api, String pluginClassName, String pluginId, boolean exceptionOnFailure) {

		try {
			final Class<? extends NexuPlugin> clazz = Class.forName(pluginClassName).asSubclass(NexuPlugin.class);
			final NexuPlugin plugin = clazz.newInstance();
			notifyPreloader(plugin.init(pluginId, api));
			for (Object o : ClassUtils.getAllInterfaces(clazz)) {
				registerPlugin(api, pluginId, (Class<?>) o, plugin);
			}
		} catch (Exception e) {
			logger.error(MessageFormat.format("Cannot register plugin {0} (id: {1})", pluginClassName, pluginId), e);
			if (exceptionOnFailure) {
				throw new RuntimeException(e);
			}
		}

	}

	private void registerPlugin(InternalAPI api, String pluginId, Class<?> i, Object plugin) {
		if (HttpPlugin.class.equals(i)) {
			final HttpPlugin p = (HttpPlugin) plugin;
			api.registerHttpContext(pluginId, p);
		}
	}

	private void notifyPreloader(final List<InitializationMessage> messages) {
		for(final InitializationMessage message : messages) {
			final AlertType alertType;
			switch(message.getMessageType()) {
			case CONFIRMATION:
				alertType = AlertType.CONFIRMATION;
				break;
			case WARNING:
				alertType = AlertType.WARNING;
				break;
			default:
				throw new IllegalArgumentException("Unknown message type: " + message.getMessageType());	
			}
			final PreloaderMessage preloaderMessage = new PreloaderMessage(alertType, message.getTitle(),
					message.getHeaderText(), message.getContentText(), message.isSendFeedback(), message.getException());
			notifyPreloader(preloaderMessage);
		}
	}
	
	@Override
	public void stop() throws Exception {
		// Can only happen with explicit user request
	}
}
