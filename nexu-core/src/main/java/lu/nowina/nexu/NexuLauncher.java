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
import java.util.Properties;

import javafx.application.Application;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.PatternLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

	private static final String BINDING_PORT = "binding_port";

	private static final Logger logger = LoggerFactory.getLogger(NexuLauncher.class.getName());

	private static AppConfig config;

	private static Properties props;

	public static void main(String[] args) throws Exception {
		NexuLauncher launcher = new NexuLauncher();
		launcher.launch(args);
	}

	public void launch(String[] args) throws IOException {
		logger.info("Read configuration");
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

	public static AppConfig getConfig() {
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
		URL url = new URL("http://" + config.getBindingIP() + ":" + config.getBindingPort() + "/nexu-info");
		try (InputStream in = url.openStream()) {
			String info = IOUtils.toString(in);
			logger.error("NexU already started. Version '" + info + "'");
			return true;
		} catch (Exception e) {
			logger.info("no " + url.toString() + " detected, " + e.getMessage());
			return false;
		}
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
		config.setBindingPort(Integer.parseInt(props.getProperty(BINDING_PORT, "9876")));
		config.setBindingIP(props.getProperty(BINDING_IP, "127.0.0.1"));
		config.setServerUrl(props.getProperty(SERVER_URL, "http://lab.nowina.solutions/nexu"));
		config.setInstallUrl(props.getProperty(INSTALL_URL, "http://nowina.lu/nexu/"));
		config.setNexuUrl(props.getProperty(NEXU_URL, "http://localhost:9876"));
		config.setHttpServerClass(props.getProperty(HTTP_SERVER_CLASS, JettyServer.class.getName()));
		config.setDebug(Boolean.parseBoolean(props.getProperty(DEBUG, "false")));
		config.setAdvancedModeAvailable(Boolean.parseBoolean(props.getProperty(ADVANCED_MODE_AVAILABLE, "true")));

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
