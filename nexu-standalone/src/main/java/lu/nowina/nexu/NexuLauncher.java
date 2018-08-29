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

import org.apache.commons.io.IOUtils;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import javafx.application.Application;
import lu.nowina.nexu.api.AppConfig;

public class NexuLauncher {
	private static final Logger logger = LoggerFactory.getLogger(NexuLauncher.class.getName());

	private static AppConfig config;

	private static Properties props;

	private static ProxyConfigurer proxyConfigurer;

	public static void main(String[] args) throws Exception {
		NexuLauncher launcher = new NexuLauncher();
		launcher.launch(args);
	}

	protected void launch(String[] args) throws IOException {
		props = loadProperties();
		loadAppConfig(props);

		configureLogger(config);

		// Perform this work in a separate method to have the logger well configured.
		config.initDefaultProduct(props);
		
		proxyConfigurer = new ProxyConfigurer(config, new UserPreferences(config.getApplicationName()));

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
		console.setThreshold(config.isDebug() ? Level.DEBUG : Level.INFO);
		console.activateOptions();
		org.apache.log4j.Logger.getRootLogger().addAppender(console);

		RollingFileAppender rfa = new RollingFileAppender();
		rfa.setName("FileLogger");
		File nexuHome = config.getNexuHome();
		rfa.setFile(new File(nexuHome, "nexu.log").getAbsolutePath());
		rfa.setLayout(new PatternLayout("%d %-5p [%c{1}] %m%n"));
		rfa.setThreshold(config.isDebug() ? Level.DEBUG : Level.INFO);
		rfa.setAppend(true);
		rfa.activateOptions();
		rfa.setMaxFileSize(config.getRollingLogMaxFileSize());
		rfa.setMaxBackupIndex(config.getRollingLogMaxFileNumber());
		org.apache.log4j.Logger.getRootLogger().addAppender(rfa);

		org.apache.log4j.Logger.getLogger("org").setLevel(Level.INFO);
		org.apache.log4j.Logger.getLogger("httpclient").setLevel(Level.INFO);
		org.apache.log4j.Logger.getLogger("freemarker").setLevel(Level.INFO);
		org.apache.log4j.Logger.getLogger("lu.nowina").setLevel(Level.DEBUG);
		// Disable warnings for java.util.prefs: when loading userRoot on Windows,
		// JRE will also try to load/create systemRoot which is under HKLM. This last
		// operation will not be permitted unless user is Administrator. If it is not
		// the case, a warning will be issued but we can ignore it safely as we only
		// use userRoot which is under HKCU.
		org.apache.log4j.Logger.getLogger("java.util.prefs").setLevel(Level.ERROR);
	}

	protected void beforeLaunch() {
		// Do nothing by contract
	}

	public static AppConfig getConfig() {
		return config;
	}

	public static Properties getProperties() {
		return props;
	}

	public static ProxyConfigurer getProxyConfigurer() {
		return proxyConfigurer;
	}

	private static boolean checkAlreadyStarted() throws MalformedURLException {
		for (int port : config.getBindingPorts()) {
			final URL url = new URL("http://" + config.getBindingIP() + ":" + port + "/nexu-info");
			final URLConnection connection;
			try {
				connection = url.openConnection();
				connection.setConnectTimeout(2000);
				connection.setReadTimeout(2000);
			} catch (IOException e) {
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

	private final Properties loadProperties() throws IOException {

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
	public final void loadAppConfig(Properties props) {
		config = createAppConfig();
		config.loadFromProperties(props);
	}

	protected AppConfig createAppConfig() {
		return new AppConfig();
	}

	/**
	 * Returns the JavaFX {@link Application} class to launch.
	 * 
	 * @return The JavaFX {@link Application} class to launch.
	 */
	protected Class<? extends Application> getApplicationClass() {
		return NexUApp.class;
	}
}
