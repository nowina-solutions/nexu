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
package lu.nowina.nexu.api;

import static org.apache.commons.lang.StringUtils.isNotEmpty;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Configuration of the NexU Platform
 *
 * @author David Naramski
 */
public class AppConfig {

    private static final String ADVANCED_MODE_AVAILABLE = "advanced_mode_available";
    private static final String APPLICATION_NAME = "application_name";
    private static final String DEBUG = "debug";
    private static final String HTTP_SERVER_CLASS = "http_server_class";
    private static final String NEXU_HOSTNAME = "nexu_hostname";
    private static final String INSTALL_URL = "install_url";
    private static final String SERVER_URL = "server_url";
    private static final String BINDING_IP = "binding_ip";
    private static final String BINDING_PORTS = "binding_ports";
    private static final String CONNECTIONS_CACHE_MAX_SIZE = "connections_cache_max_size";
    private static final String ENABLE_POP_UPS = "enable_pop_ups";
    //This property is less restrictive than enable_pop_ups, since this one allows display of certificate selection
    private static final String ENABLE_INFORMATIVE_POP_UPS = "enable_informative_pop_ups";
    private static final String USE_SYSTEM_PROXY = "use_system_proxy";
    private static final String PROXY_SERVER = "proxy_server";
    private static final String PROXY_PORT = "proxy_port";
    private static final String PROXY_PROTOCOLE = "proxy_use_https";
    private static final String PROXY_AUTHENTICATION = "proxy_authentication";
    private static final String PROXY_USERNAME = "proxy_username";
    private static final String PROXY_PASSWORD = "proxy_password";
    private static final String USER_PREFERENCES_EDITABLE = "user_preferences_editable";
    private static final String REQUEST_PROCESSOR_CLASS = "request_processor_class";

    private static final String ROLLING_LOG_FILE_SIZE = "rolling_log_file_size";
    private static final String ROLLING_LOG_FILE_NUMBER = "rolling_log_file_number";

    private static final String BINDING_PORTS_HTTPS = "binding_ports_https";

    private static final String ENABLE_DATABASE_WEB_LOADER = "enable_database_web_loader";

    private static final String ENABLE_SYSTRAY_MENU = "enable_systray_menu";
    private static final String CORS_ALLOWED_ORIGIN = "cors_allowed_origin";

    private static final String TICKET_URL = "ticket_url";
    private static final String ENABLE_INCIDENT_REPORT = "enable_incident_report";

    private static final String SHOW_SPLASH_SCREEN = "show_splash_screen";

    private static final String DISPLAY_BACK_BUTTON = "display_back_button";

    private static final String DEFAULT_PRODUCT = "default_product_";

    private static final Logger logger = LoggerFactory.getLogger(AppConfig.class.getName());

    private String bindingIP;

    private List<Integer> bindingPorts;

    private String serverUrl;

    private String installUrl;

    private String nexuHostname;

    private String httpServerClass;

    private boolean debug;

    private boolean advancedModeAvailable;

    private String applicationName;

    private String applicationVersion;

    private int connectionsCacheMaxSize;

    private boolean enablePopUps;
    private boolean enableInformativePopUps;

    private boolean sendAnonymousInfoToProxy;

    private boolean useSystemProxy;
    private String proxyServer;
    private Integer proxyPort;
    private boolean proxyUseHttps;
    private boolean proxyAuthentication;
    private String proxyUsername;
    private String proxyPassword;

    private boolean userPreferencesEditable;

    private String requestProcessorClass;

    private File nexuHome;

    private List<Integer> bindingPortsHttps;

    private String rollingLogMaxFileSize;

    private int rollingLogMaxFileNumber;

    private boolean enableDatabaseWebLoader;

    private boolean enableSystrayMenu;

    private String ticketUrl;

    private boolean enableIncidentReport;

    private boolean corsAllowAllOrigins;
    private Set<String> corsAllowedOrigins;

    private boolean showSplashScreen;

    private boolean displayBackButton;

    private Product defaultProduct;

    public AppConfig() {
        try {
            final URL versionResourceURL = this.getClass().getResource("/version.txt");
            if (versionResourceURL == null) {
                logger.error("Cannot retrieve application version: version.txt not found");
            } else {
                this.applicationVersion = IOUtils.toString(versionResourceURL);
            }
        } catch (final IOException e) {
            logger.error("Cannot retrieve application version: " + e.getMessage(), e);
            this.applicationVersion = "";
        }
    }

    public String getBindingIP() {
        return this.bindingIP;
    }

    public void setBindingIP(final String bindingIP) {
        this.bindingIP = bindingIP;
    }

    public List<Integer> getBindingPorts() {
        return this.bindingPorts;
    }

    public void setBindingPorts(final List<Integer> bindingPorts) {
        this.bindingPorts = Collections.unmodifiableList(bindingPorts);
    }

    public String getServerUrl() {
        return this.serverUrl;
    }

    public void setServerUrl(final String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public String getInstallUrl() {
        return this.installUrl;
    }

    public void setInstallUrl(final String installUrl) {
        this.installUrl = installUrl;
    }

    public String getNexuHostname() {
        return this.nexuHostname;
    }

    public void setNexuHostname(final String nexuHostname) {
        this.nexuHostname = nexuHostname;
    }

    public String getHttpServerClass() {
        return this.httpServerClass;
    }

    public void setHttpServerClass(final String httpServerClass) {
        this.httpServerClass = httpServerClass;
    }

    public boolean isDebug() {
        return this.debug;
    }

    public void setDebug(final boolean debug) {
        this.debug = debug;
    }

    public boolean isAdvancedModeAvailable() {
        return this.advancedModeAvailable;
    }

    public void setAdvancedModeAvailable(final boolean advancedModeAvailable) {
        this.advancedModeAvailable = advancedModeAvailable;
    }

    public String getApplicationName() {
        return this.applicationName;
    }

    public void setApplicationName(final String applicationName) {
        this.applicationName = applicationName;
    }

    public String getApplicationVersion() {
        return this.applicationVersion;
    }

    public int getConnectionsCacheMaxSize() {
        return this.connectionsCacheMaxSize;
    }

    public void setConnectionsCacheMaxSize(final int connectionsCacheMaxSize) {
        this.connectionsCacheMaxSize = connectionsCacheMaxSize;
    }

    public boolean isEnablePopUps() {
        return this.enablePopUps;
    }

    public void setEnablePopUps(final boolean enablePopUps) {
        this.enablePopUps = enablePopUps;
    }

    public boolean isSendAnonymousInfoToProxy() {
        return this.sendAnonymousInfoToProxy;
    }

    public void setSendAnonymousInfoToProxy(final boolean sendAnonymousInfoToProxy) {
        this.sendAnonymousInfoToProxy = sendAnonymousInfoToProxy;
    }

    public boolean isUseSystemProxy() {
        return this.useSystemProxy;
    }

    public void setUseSystemProxy(final boolean useSystemProxy) {
        this.useSystemProxy = useSystemProxy;
    }

    public String getProxyServer() {
        return this.proxyServer;
    }

    public void setProxyServer(final String proxyServer) {
        this.proxyServer = proxyServer;
    }

    public Integer getProxyPort() {
        return this.proxyPort;
    }

    public void setProxyPort(final Integer proxyPort) {
        this.proxyPort = proxyPort;
    }

    public boolean isProxyUseHttps() {
        return this.proxyUseHttps;
    }

    public void setProxyUseHttps(final boolean proxyUseHttps) {
        this.proxyUseHttps = proxyUseHttps;
    }

    public boolean isProxyAuthentication() {
        return this.proxyAuthentication;
    }

    public void setProxyAuthentication(final boolean proxyAuthentication) {
        this.proxyAuthentication = proxyAuthentication;
    }

    public String getProxyUsername() {
        return this.proxyUsername;
    }

    public void setProxyUsername(final String proxyUsername) {
        this.proxyUsername = proxyUsername;
    }

    public String getProxyPassword() {
        return this.proxyPassword;
    }

    public void setProxyPassword(final String proxyPassword) {
        this.proxyPassword = proxyPassword;
    }

    public boolean isUserPreferencesEditable() {
        return this.userPreferencesEditable;
    }

    public void setUserPreferencesEditable(final boolean userPreferencesEditable) {
        this.userPreferencesEditable = userPreferencesEditable;
    }

    public String getRequestProcessorClass() {
        return this.requestProcessorClass;
    }

    public void setRequestProcessorClass(final String requestProcessorClass) {
        this.requestProcessorClass = requestProcessorClass;
    }

    public List<Integer> getBindingPortsHttps() {
        return this.bindingPortsHttps;
    }

    public void setBindingPortsHttps(final List<Integer> bindingPortsHttps) {
        this.bindingPortsHttps = Collections.unmodifiableList(bindingPortsHttps);
    }

    public String getRollingLogMaxFileSize() {
        return this.rollingLogMaxFileSize;
    }

    /**
     * This method allows to set the maximum size for a log file. Expected format : 64KB, 10MB,...
     *
     * @param rollingLogMaxFileSize
     */
    public void setRollingLogMaxFileSize(final String rollingLogMaxFileSize) {
        this.rollingLogMaxFileSize = rollingLogMaxFileSize;
    }

    public int getRollingLogMaxFileNumber() {
        return this.rollingLogMaxFileNumber;
    }

    /**
     * This method allows to set the maxium number of log files to keep on the file system.
     *
     * @param rollingLogMaxFileNumber
     */
    public void setRollingLogMaxFileNumber(final int rollingLogMaxFileNumber) {
        this.rollingLogMaxFileNumber = rollingLogMaxFileNumber;
    }

    public boolean isEnableDatabaseWebLoader() {
        return this.enableDatabaseWebLoader;
    }

    public void setEnableDatabaseWebLoader(final boolean enableDatabaseWebLoader) {
        this.enableDatabaseWebLoader = enableDatabaseWebLoader;
    }

    public boolean isEnableSystrayMenu() {
        return this.enableSystrayMenu;
    }

    public void setEnableSystrayMenu(final boolean enableSystrayMenu) {
        this.enableSystrayMenu = enableSystrayMenu;
    }

    public File getNexuHome() {
        if (this.nexuHome != null) {
            return this.nexuHome;
        }

        final ConfigurationManager configurationManager = this.getConfigurationManager();
        try {
            this.nexuHome = configurationManager.manageConfiguration(this.getApplicationName());
        } catch (final IOException e) {
            logger.error("Error while managing Nexu config : {}", e.getMessage(), e);
            this.nexuHome = null;
        }
        return this.nexuHome;
    }



    public void loadFromProperties(final Properties props) {
        this.setApplicationName(props.getProperty(APPLICATION_NAME, "NexU"));

        final String bindingPortsStr = props.getProperty(BINDING_PORTS, "9795");
        if (isNotEmpty(bindingPortsStr)) {
            this.setBindingPorts(this.toListOfInt(bindingPortsStr));
        }

        this.setBindingIP(props.getProperty(BINDING_IP, "127.0.0.1"));
        this.setServerUrl(props.getProperty(SERVER_URL, "http://lab.nowina.solutions/nexu"));
        this.setInstallUrl(props.getProperty(INSTALL_URL, "http://nowina.lu/nexu/"));
        this.setNexuHostname(props.getProperty(NEXU_HOSTNAME, "localhost"));
        this.setHttpServerClass(props.getProperty(HTTP_SERVER_CLASS, "lu.nowina.nexu.jetty.JettyServer"));
        this.setDebug(Boolean.parseBoolean(props.getProperty(DEBUG, "false")));
        this.setAdvancedModeAvailable(Boolean.parseBoolean(props.getProperty(ADVANCED_MODE_AVAILABLE, "true")));
        this.setConnectionsCacheMaxSize(Integer.parseInt(props.getProperty(CONNECTIONS_CACHE_MAX_SIZE, "50")));
        this.setEnablePopUps(Boolean.parseBoolean(props.getProperty(ENABLE_POP_UPS, "true")));
        this.setEnableInformativePopUps(Boolean.parseBoolean(props.getProperty(ENABLE_INFORMATIVE_POP_UPS, "true")));
        // Always set to false, just in case
        this.setSendAnonymousInfoToProxy(false);

        this.setUseSystemProxy(Boolean.parseBoolean(props.getProperty(USE_SYSTEM_PROXY, "false")));
        this.setProxyServer(props.getProperty(PROXY_SERVER, ""));
        final String proxyPortStr = props.getProperty(PROXY_PORT, null);
        this.setProxyPort((proxyPortStr != null) ? Integer.valueOf(proxyPortStr) : null);
        this.setProxyUseHttps(Boolean.parseBoolean(props.getProperty(PROXY_PROTOCOLE, "false")));
        this.setProxyAuthentication(Boolean.parseBoolean(props.getProperty(PROXY_AUTHENTICATION, "false")));
        this.setProxyUsername(props.getProperty(PROXY_USERNAME, ""));
        this.setProxyPassword(props.getProperty(PROXY_PASSWORD, ""));
        this.setUserPreferencesEditable(Boolean.parseBoolean(props.getProperty(USER_PREFERENCES_EDITABLE, "true")));

        this.setRollingLogMaxFileNumber(Integer.parseInt(props.getProperty(ROLLING_LOG_FILE_NUMBER, "5")));
        this.setRollingLogMaxFileSize(props.getProperty(ROLLING_LOG_FILE_SIZE, "10MB"));

        this.setRequestProcessorClass(props.getProperty(REQUEST_PROCESSOR_CLASS, "lu.nowina.nexu.jetty.RequestProcessor"));

        final String bindingPortHttpsStr = props.getProperty(BINDING_PORTS_HTTPS, "9895");
        if (isNotEmpty(bindingPortHttpsStr)) {
            this.setBindingPortsHttps(this.toListOfInt(bindingPortHttpsStr));
        }

        this.setEnableDatabaseWebLoader(Boolean.parseBoolean(props.getProperty(ENABLE_DATABASE_WEB_LOADER, "true")));
        this.setEnableSystrayMenu(Boolean.parseBoolean(props.getProperty(ENABLE_SYSTRAY_MENU, "true")));
        this.setCorsAllowedOrigins(props.getProperty(CORS_ALLOWED_ORIGIN, "*"));
        this.setTicketUrl(props.getProperty(TICKET_URL, "https://github.com/nowina-solutions/nexu/issues/new"));
        this.setEnableIncidentReport(Boolean.parseBoolean(props.getProperty(ENABLE_INCIDENT_REPORT, "false")));
        this.setShowSplashScreen(Boolean.parseBoolean(props.getProperty(SHOW_SPLASH_SCREEN, "false")));
        this.setDisplayBackButton(Boolean.parseBoolean(props.getProperty(DISPLAY_BACK_BUTTON, "false")));
    }

    public void initDefaultProduct(final Properties props) {
    	// Perform this work in a separate method to have the logger well configured.
        for (final Entry<Object, Object> entry : props.entrySet()) {
            if (((String) entry.getKey()).startsWith(DEFAULT_PRODUCT)) {
                // Initialize default product
                final String osProperty = ((String) entry.getKey()).substring(DEFAULT_PRODUCT.length());
                if (StringUtils.isEmpty(osProperty) || StringUtils.isEmpty((String) entry.getValue())) {
                    logger.warn("Invalid 'default_product' property. Property: " + entry.getKey());
                } else {
                    final OS osEnum;
                    try{
                    	osEnum = OS.valueOf(osProperty);
                    } catch(final IllegalArgumentException e) {
                        logger.warn("Invalid 'default_product' property. Property: " + entry.getKey());
                        continue;
                    }
                    final EnvironmentInfo environmentInfo = EnvironmentInfo.buildFromSystemProperties(System.getProperties());
                    if (environmentInfo.getOs().equals(osEnum)) {
                    	try {
                    		final Class<? extends Product> defaultProduct =
                    				Class.forName((String) entry.getValue()).asSubclass(Product.class);
                    		this.defaultProduct = defaultProduct.newInstance();
                    	} catch (final ClassNotFoundException | ClassCastException e) {
                            logger.warn("Invalid 'default_product' property. Property: " + entry.getKey() +
                            		". Value is not a valid Product class: " + entry.getValue());
                    	} catch (final InstantiationException | IllegalAccessException e) {
                    		logger.error("Error occurred during instantiation of default product. Property: " + entry.getKey() +
                            		". Product class: " + entry.getValue());
                    	}
                    }
                }
            }
        }
    }
    
    /**
     * Returns a list of {@link Integer} from <code>str</code> which should be tokenized by commas.
     *
     * @param str
     *            A list of strings tokenized by commas.
     * @return A list of {@link Integer}.
     */
    protected List<Integer> toListOfInt(final String str) {
        final List<Integer> ports = new ArrayList<Integer>();
        for (final String port : str.split(",")) {
            ports.add(Integer.parseInt(port.trim()));
        }
        return ports;
    }

    public ConfigurationManager getConfigurationManager() {
        return new ConfigurationManager();
    }

    public boolean isEnableInformativePopUps() {
        return this.enableInformativePopUps;
    }

    public void setEnableInformativePopUps(final boolean enableInformativePopUps) {
        this.enableInformativePopUps = enableInformativePopUps;
    }
    
    public boolean isCorsAllowAllOrigins() {
		return corsAllowAllOrigins;
	}

	public Set<String> getCorsAllowedOrigins() {
        return this.corsAllowedOrigins;
    }

    public void setCorsAllowedOrigins(final String corsAllowedOrigins) {
    	if("*".equals(corsAllowedOrigins)) {
    		this.corsAllowAllOrigins = true;
    		this.corsAllowedOrigins = Collections.emptySet();
    	} else {
    		this.corsAllowAllOrigins = false;
    		final String[] corsAllowedOriginsArray = corsAllowedOrigins.split(",");
    		this.corsAllowedOrigins = new HashSet<String>(corsAllowedOriginsArray.length);
    		for(final String corsAllowedOrigin : corsAllowedOriginsArray) {
    			this.corsAllowedOrigins.add(corsAllowedOrigin.trim());
    		}
    	}
    }

    public String getTicketUrl() {
        return this.ticketUrl;
    }

    public void setTicketUrl(final String ticketUrl) {
        this.ticketUrl = ticketUrl;
    }

    public boolean isEnableIncidentReport() {
        return this.enableIncidentReport;
    }

    public void setEnableIncidentReport(final boolean enableIncidentReport) {
        this.enableIncidentReport = enableIncidentReport;
    }

    public boolean isShowSplashScreen() {
        return this.showSplashScreen;
    }

    public void setShowSplashScreen(final boolean showSplashScreen) {
        this.showSplashScreen = showSplashScreen;
    }

    public boolean isDisplayBackButton() {
        return this.displayBackButton;
    }

    public void setDisplayBackButton(final boolean displayBackButton) {
        this.displayBackButton = displayBackButton;
    }

    public Product getDefaultProduct() {
        return defaultProduct;
    }

    public void setDefaultProduct(Product defaultProduct) {
        this.defaultProduct = defaultProduct;
    }

}
