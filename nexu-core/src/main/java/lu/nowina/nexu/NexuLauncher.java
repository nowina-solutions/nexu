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
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Properties;

import javafx.application.Application;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.PatternLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import lu.nowina.nexu.api.AppConfig;
import lu.nowina.nexu.jetty.JettyServer;

public class NexuLauncher {

	private static final String ADVANCED_MODE_AVAILABLE = "advanced_mode_available";

	private static final String APPLICATION_NAME = "application_name";

	private static final String DEBUG = "debug";

	private static final String HTTP_SERVER_CLASS = "http_server_class";

	private static final String NEXU_URL = "nexu_url";

	private static final String INSTALL_URL = "install_url";

	private static final String SERVER_URL = "server_url";

	private static final String BINDING_IP = "binding_ip";

	private static final String MIN_BINDING_PORT_RANGE = "min_binding_port_range";
	
	private static final String MAX_BINDING_PORT_RANGE = "max_binding_port_range";

	private static final String CONNECTIONS_CACHE_MAX_SIZE = "connections_cache_max_size";

	private static final String ENABLE_POP_UPS = "enable_pop_ups";
	
	private static final Logger logger = LoggerFactory.getLogger(NexuLauncher.class.getName());

	private static AppConfig config;

	private static Properties props;

	public static void main(String[] args) throws Exception {
		NexuLauncher launcher = new NexuLauncher();
		launcher.launch(args);
	}

	public void launch(String[] args) throws IOException {
		props = loadProperties();
		config = loadAppConfig(props);

		configureLogger(config);

		beforeLaunch();

		boolean started = checkAlreadyStarted();
		if (!started) {
			NexUApp.launch(getApplicationClass(), args);
		}
	}

	private void configureLogger(AppConfig config) {
		SLF4JBridgeHandler.removeHandlersForRootLogger();
		SLF4JBridgeHandler.install();
		
		ConsoleAppender console = new ConsoleAppender(); // create appender
		String PATTERN = "%d [%p|%c|%C{1}|%t] %m%n";
		console.setLayout(new PatternLayout(PATTERN));
		console.setThreshold(config.isDebug() ? Level.DEBUG : Level.WARN);
		console.activateOptions();
		org.apache.log4j.Logger.getRootLogger().addAppender(console);

		FileAppender fa = new FileAppender();
		fa.setName("FileLogger");

		File nexuHome = getNexuHome();

		fa.setFile(new File(nexuHome, "nexu.log").getAbsolutePath());
		fa.setLayout(new PatternLayout("%d %-5p [%c{1}] %m%n"));
		fa.setThreshold(config.isDebug() ? Level.DEBUG : Level.WARN);
		fa.setAppend(true);
		fa.activateOptions();
		org.apache.log4j.Logger.getRootLogger().addAppender(fa);

		org.apache.log4j.Logger.getLogger("org").setLevel(Level.INFO);
		org.apache.log4j.Logger.getLogger("httpclient").setLevel(Level.INFO);
		org.apache.log4j.Logger.getLogger("freemarker").setLevel(Level.INFO);
		org.apache.log4j.Logger.getLogger("lu.nowina").setLevel(Level.DEBUG);
	}

	public void beforeLaunch() {

	}

	static AppConfig getConfig() {
		return config;
	}

	public static Properties getProperties() {
		return props;
	}

	/**
	 * Returns Nexu home directory. If the directory cannot be created or is nt writable, returns null.
	 * 
	 * @return
	 */
	public static File getNexuHome() {
		File userHome = new File(System.getProperty("user.home"));
		if (!userHome.exists()) {
			return null;
		}
		File nexuHome = new File(userHome, ".nexu");
		if (nexuHome.exists()) {
			return nexuHome.canWrite() ? nexuHome : null;
		} else {
			boolean result = nexuHome.mkdir();
			return result ? nexuHome : null;
		}
	}

	private static boolean checkAlreadyStarted() throws MalformedURLException {
		for(int port = config.getMinBindingPortRange(); port <= config.getMaxBindingPortRange(); ++port) {
			final URL url = new URL("http://" + config.getBindingIP() + ":" + port + "/nexu-info");
			final URLConnection connection;
			try {
				connection = url.openConnection();
				connection.setConnectTimeout(2000);
				connection.setReadTimeout(2000);
			} catch(IOException e) {
				logger.warn("IOException when trying to open a connection to " + url + ": " + e.getMessage(), e);
				continue;
			}
			try (InputStream in = connection.getInputStream()) {
				final String info = IOUtils.toString(in);
				logger.error("NexU already started. Version '" + info + "'");
				return true;
			} catch (Exception e) {
				logger.info("No " + url.toString() + " detected, " + e.getMessage());
			}
		}
		return false;
	}

	public Properties loadProperties() throws IOException {

		Properties props = new Properties();
		loadPropertiesFromClasspath(props);
		return props;

	}

	private void loadPropertiesFromClasspath(Properties props) throws IOException {
		InputStream configFile = NexUApp.class.getClassLoader().getResourceAsStream("nexu-config.properties");
		if (configFile != null) {
			props.load(configFile);
		}
	}

	/**
	 * Load the properties from the properties file.
	 * 
	 * @param props
	 * @return
	 */
	public AppConfig loadAppConfig(Properties props) {
		final AppConfig config = createAppConfig();

		config.setApplicationName(props.getProperty(APPLICATION_NAME, "NexU"));
		config.setMinBindingPortRange(Integer.parseInt(props.getProperty(MIN_BINDING_PORT_RANGE, "9876")));
		config.setMaxBindingPortRange(Integer.parseInt(props.getProperty(MAX_BINDING_PORT_RANGE, "9878")));
		config.setBindingIP(props.getProperty(BINDING_IP, "127.0.0.1"));
		config.setServerUrl(props.getProperty(SERVER_URL, "http://lab.nowina.solutions/nexu"));
		config.setInstallUrl(props.getProperty(INSTALL_URL, "http://nowina.lu/nexu/"));
		config.setNexuUrl(props.getProperty(NEXU_URL, "http://localhost:9876"));
		config.setHttpServerClass(props.getProperty(HTTP_SERVER_CLASS, JettyServer.class.getName()));
		config.setDebug(Boolean.parseBoolean(props.getProperty(DEBUG, "false")));
		config.setAdvancedModeAvailable(Boolean.parseBoolean(props.getProperty(ADVANCED_MODE_AVAILABLE, "true")));
		config.setConnectionsCacheMaxSize(Integer.parseInt(props.getProperty(CONNECTIONS_CACHE_MAX_SIZE, "50")));
		config.setEnablePopUps(Boolean.parseBoolean(props.getProperty(ENABLE_POP_UPS, "true")));
		
		return config;
	}

	protected AppConfig createAppConfig() {
		return new AppConfig();
	}
	
	/**
	 * Returns the JavaFX {@link Application} class to launch.
	 * @return The JavaFX {@link Application} class to launch.
	 */
	protected Class<? extends Application> getApplicationClass() {
		return NexUApp.class;
	}
}
