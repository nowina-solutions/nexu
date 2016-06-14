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

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Configuration of the NexU Platform
 * 
 * @author David Naramski
 *
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
	private static final String SEND_ANONYMOUS_INFO_TO_PROXY = "send_anonymous_info_to_proxy";
	private static final String USE_SYSTEM_PROXY = "use_system_proxy";
	private static final String PROXY_SERVER = "proxy_server";
	private static final String PROXY_PORT = "proxy_port";
	private static final String PROXY_PROTOCOLE = "proxy_use_https";
	private static final String PROXY_AUTHENTICATION = "proxy_authentication";
	private static final String PROXY_USERNAME = "proxy_username";
	private static final String PROXY_PASSWORD = "proxy_password";
	private static final String USER_PREFERENCES_EDITABLE = "user_preferences_editable";
	private static final String REQUEST_PROCESSOR_CLASS = "request_processor_class";

	private static final String BINDING_PORTS_HTTPS = "binding_ports_https";
	private static final String SIGNATURE_REQUEST_VALIDATOR_NONCES_CACHE_MAX_SIZE = "signature_request_validator_nonces_cache_max_size";

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

	private int signatureRequestValidatorNoncesCacheMaxSize;



	public AppConfig() {
		try {
			final URL versionResourceURL = this.getClass().getResource("/version.txt");
			if(versionResourceURL == null) {
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
		return bindingIP;
	}

	public void setBindingIP(String bindingIP) {
		this.bindingIP = bindingIP;
	}

	public List<Integer> getBindingPorts() {
		return bindingPorts;
	}

	public void setBindingPorts(List<Integer> bindingPorts) {
		this.bindingPorts = Collections.unmodifiableList(bindingPorts);
	}

	public String getServerUrl() {
		return serverUrl;
	}

	public void setServerUrl(String serverUrl) {
		this.serverUrl = serverUrl;
	}

	public String getInstallUrl() {
		return installUrl;
	}

	public void setInstallUrl(String installUrl) {
		this.installUrl = installUrl;
	}

	public String getNexuHostname() {
		return nexuHostname;
	}

	public void setNexuHostname(String nexuHostname) {
		this.nexuHostname = nexuHostname;
	}

	public String getHttpServerClass() {
		return httpServerClass;
	}

	public void setHttpServerClass(String httpServerClass) {
		this.httpServerClass = httpServerClass;
	}

	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public boolean isAdvancedModeAvailable() {
		return advancedModeAvailable;
	}

	public void setAdvancedModeAvailable(boolean advancedModeAvailable) {
		this.advancedModeAvailable = advancedModeAvailable;
	}

	public String getApplicationName() {
		return applicationName;
	}

	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}

	public String getApplicationVersion() {
		return applicationVersion;
	}

	public int getConnectionsCacheMaxSize() {
		return connectionsCacheMaxSize;
	}

	public void setConnectionsCacheMaxSize(int connectionsCacheMaxSize) {
		this.connectionsCacheMaxSize = connectionsCacheMaxSize;
	}

	public boolean isEnablePopUps() {
		return enablePopUps;
	}

	public void setEnablePopUps(boolean enablePopUps) {
		this.enablePopUps = enablePopUps;
	}

	public boolean isSendAnonymousInfoToProxy() {
		return sendAnonymousInfoToProxy;
	}

	public void setSendAnonymousInfoToProxy(boolean sendAnonymousInfoToProxy) {
		this.sendAnonymousInfoToProxy = sendAnonymousInfoToProxy;
	}

	public boolean isUseSystemProxy() {
		return useSystemProxy;
	}

	public void setUseSystemProxy(boolean useSystemProxy) {
		this.useSystemProxy = useSystemProxy;
	}

	public String getProxyServer() {
		return proxyServer;
	}

	public void setProxyServer(String proxyServer) {
		this.proxyServer = proxyServer;
	}

	public Integer getProxyPort() {
		return proxyPort;
	}

	public void setProxyPort(Integer proxyPort) {
		this.proxyPort = proxyPort;
	}

	public boolean isProxyUseHttps() {
		return proxyUseHttps;
	}

	public void setProxyUseHttps(boolean proxyUseHttps) {
		this.proxyUseHttps = proxyUseHttps;
	}

	public boolean isProxyAuthentication() {
		return proxyAuthentication;
	}

	public void setProxyAuthentication(boolean proxyAuthentication) {
		this.proxyAuthentication = proxyAuthentication;
	}

	public String getProxyUsername() {
		return proxyUsername;
	}

	public void setProxyUsername(String proxyUsername) {
		this.proxyUsername = proxyUsername;
	}

	public String getProxyPassword() {
		return proxyPassword;
	}

	public void setProxyPassword(String proxyPassword) {
		this.proxyPassword = proxyPassword;
	}

	public boolean isUserPreferencesEditable() {
		return userPreferencesEditable;
	}

	public void setUserPreferencesEditable(boolean userPreferencesEditable) {
		this.userPreferencesEditable = userPreferencesEditable;
	}

	public String getRequestProcessorClass() {
		return requestProcessorClass;
	}

	public void setRequestProcessorClass(String requestProcessorClass) {
		this.requestProcessorClass = requestProcessorClass;
	}


	public List<Integer> getBindingPortsHttps() {
		return bindingPortsHttps;
	}

	public void setBindingPortsHttps(List<Integer> bindingPortsHttps) {
		this.bindingPortsHttps = Collections.unmodifiableList(bindingPortsHttps);
	}

	public int getSignatureRequestValidatorNoncesCacheMaxSize() {
		return signatureRequestValidatorNoncesCacheMaxSize;
	}

	public void setSignatureRequestValidatorNoncesCacheMaxSize(int signatureRequestValidatorNoncesCacheMaxSize) {
		this.signatureRequestValidatorNoncesCacheMaxSize = signatureRequestValidatorNoncesCacheMaxSize;
	}

	
	public File getNexuHome() {
		if(nexuHome != null) {
			return nexuHome;
		}
		final File userHome = new File(System.getProperty("user.home"));
		if (!userHome.exists()) {
			return null;
		}
		final File file = new File(userHome, "." + getApplicationName());
		if (file.exists()) {
			return file.canWrite() ? nexuHome = file : null;
		} else {
			return file.mkdir() && file.canWrite() ? nexuHome = file : null;
		}
	}
	
	public void loadFromProperties(final Properties props) {
		setApplicationName(props.getProperty(APPLICATION_NAME, "NexU"));
		
		final String bindingPortsStr = props.getProperty(BINDING_PORTS, "9795");
		if(StringUtils.isNotEmpty(bindingPortsStr)) {
			setBindingPorts(toListOfInt(bindingPortsStr));
		}
		
		setBindingIP(props.getProperty(BINDING_IP, "127.0.0.1"));
		setServerUrl(props.getProperty(SERVER_URL, "http://lab.nowina.solutions/nexu"));
		setInstallUrl(props.getProperty(INSTALL_URL, "http://nowina.lu/nexu/"));
		setNexuHostname(props.getProperty(NEXU_HOSTNAME, "localhost"));
		setHttpServerClass(props.getProperty(HTTP_SERVER_CLASS, "lu.nowina.nexu.https.JettyHttpsServer"));
		setDebug(Boolean.parseBoolean(props.getProperty(DEBUG, "false")));
		setAdvancedModeAvailable(Boolean.parseBoolean(props.getProperty(ADVANCED_MODE_AVAILABLE, "true")));
		setConnectionsCacheMaxSize(Integer.parseInt(props.getProperty(CONNECTIONS_CACHE_MAX_SIZE, "50")));
		setEnablePopUps(Boolean.parseBoolean(props.getProperty(ENABLE_POP_UPS, "true")));
		setSendAnonymousInfoToProxy(Boolean.parseBoolean(props.getProperty(SEND_ANONYMOUS_INFO_TO_PROXY, "true")));

		setUseSystemProxy(Boolean.parseBoolean(props.getProperty(USE_SYSTEM_PROXY, "false")));
		setProxyServer(props.getProperty(PROXY_SERVER, ""));
		final String proxyPortStr = props.getProperty(PROXY_PORT, null);
		setProxyPort((proxyPortStr != null) ? Integer.valueOf(proxyPortStr) : null);
		setProxyUseHttps(Boolean.parseBoolean(props.getProperty(PROXY_PROTOCOLE, "false")));
		setProxyAuthentication(Boolean.parseBoolean(props.getProperty(PROXY_AUTHENTICATION, "false")));
		setProxyUsername(props.getProperty(PROXY_USERNAME, ""));
		setProxyPassword(props.getProperty(PROXY_PASSWORD, ""));
		setUserPreferencesEditable(Boolean.parseBoolean(props.getProperty(USER_PREFERENCES_EDITABLE, "true")));
		
		setRequestProcessorClass(props.getProperty(REQUEST_PROCESSOR_CLASS, "lu.nowina.nexu.jetty.RequestProcessor"));

		final String bindingPortHttpsStr = props.getProperty(BINDING_PORTS_HTTPS, "9895");
		if(StringUtils.isNotEmpty(bindingPortHttpsStr)) {
			setBindingPortsHttps(toListOfInt(bindingPortHttpsStr));
		}
		setSignatureRequestValidatorNoncesCacheMaxSize(
				Integer.parseInt(props.getProperty(SIGNATURE_REQUEST_VALIDATOR_NONCES_CACHE_MAX_SIZE, "500")));
	}



	/**
	 * Returns a list of {@link Integer} from <code>str</code> which should be
	 * tokenized by commas.
	 * @param str A list of strings tokenized by commas.
	 * @return A list of {@link Integer}.
	 */
	protected List<Integer> toListOfInt(String str) {
		final List<Integer> ports = new ArrayList<Integer>();
		for(final String port: str.split(",")) {
			ports.add(Integer.parseInt(port.trim()));
		}
		return ports;
	}
}
