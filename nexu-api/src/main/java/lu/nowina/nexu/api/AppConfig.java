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

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Configuration of the NexU Platform
 * 
 * @author David Naramski
 *
 */
public class AppConfig {

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
	private boolean proxyAuthentication;
	private String proxyUsername;
	private String proxyPassword;
	
	private boolean userPreferencesEditable;
	
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
}
