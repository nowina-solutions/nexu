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

import java.io.IOException;

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

	private int bindingPort;

	private String serverUrl;

	private String installUrl;

	private String nexuUrl;

	private String httpServerClass;

	private boolean debug;

	private boolean advancedModeAvailable;
	
	private String applicationName;
	
	private String applicationVersion;
	
	private int connectionsCacheMaxSize;
	
	public AppConfig() {
		try {
			this.applicationVersion = IOUtils.toString(this.getClass().getResource("/version.txt"));
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

	public int getBindingPort() {
		return bindingPort;
	}

	public void setBindingPort(int bindingPort) {
		this.bindingPort = bindingPort;
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

	public String getNexuUrl() {
		return nexuUrl;
	}

	public void setNexuUrl(String nexuUrl) {
		this.nexuUrl = nexuUrl;
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
}
