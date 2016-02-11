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

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class UserPreferences {

	private static final String USE_SYSTEM_PROXY = "nowina.nexu.useSystemProxy";
	private static final String PROXY_SERVER = "nowina.nexu.proxyServer";
	private static final String PROXY_PORT = "nowina.nexu.proxyPort";
	private static final String PROXY_AUTHENTICATION = "nowina.nexu.proxyAuthentication";
	private static final String PROXY_USERNAME = "nowina.nexu.proxyUsername";
	private static final String PROXY_PASSWORD = "nowina.nexu.proxyPassword";
	private static final String PROXY_USE_HTTPS = "nowina.nexu.proxyHttps";

	private final Preferences prefs;

	private Boolean useSystemProxy;
	private String proxyServer;
	private Integer proxyPort;
	private Boolean proxyUseHttps;
	private Boolean proxyAuthentication;
	private String proxyUsername;
	private String proxyPassword;

	public UserPreferences(final String applicationName) {
		prefs = Preferences.userRoot().node(applicationName);
		
		final String useSystemProxyStr = prefs.get(USE_SYSTEM_PROXY, null);
		useSystemProxy = (useSystemProxyStr != null) ? Boolean.valueOf(useSystemProxyStr) : null;
		
		proxyServer = prefs.get(PROXY_SERVER, null);
		
		final String proxyPortStr = prefs.get(PROXY_PORT, null);
		proxyPort = (proxyPortStr != null) ? Integer.valueOf(proxyPortStr) : null;
		
		final String proxyHttps = prefs.get(PROXY_USE_HTTPS, null);
		proxyUseHttps = (proxyHttps != null) ? Boolean.valueOf(proxyHttps) : null;
		
		final String proxyAuthenticationStr = prefs.get(PROXY_AUTHENTICATION, null);
		proxyAuthentication = (proxyAuthenticationStr != null) ? Boolean.valueOf(proxyAuthenticationStr) : null;
		
		proxyUsername = prefs.get(PROXY_USERNAME, null);
		proxyPassword = prefs.get(PROXY_PASSWORD, null);
	}

	public void setUseSystemProxy(Boolean useSystemProxy) {
		if(useSystemProxy != null) {
			prefs.putBoolean(USE_SYSTEM_PROXY, useSystemProxy);
		} else {
			prefs.remove(USE_SYSTEM_PROXY);
		}
		this.useSystemProxy = useSystemProxy;
	}

	public void setProxyServer(String proxyServer) {
		if(proxyServer != null) {
			prefs.put(PROXY_SERVER, proxyServer);
		} else {
			prefs.remove(PROXY_SERVER);
		}
		this.proxyServer = proxyServer;
	}

	public void setProxyPort(Integer proxyPort) {
		if(proxyPort != null) {
			prefs.putInt(PROXY_PORT, proxyPort);
		} else {
			prefs.remove(PROXY_PORT);
		}
		this.proxyPort = proxyPort;
	}
	
	public void setProxyUseHttps(Boolean proxyUseHttps) {
		if(proxyUseHttps != null) {
			prefs.putBoolean(PROXY_USE_HTTPS, proxyUseHttps);
		} else {
			prefs.remove(PROXY_USE_HTTPS);
		}
		this.proxyUseHttps = proxyUseHttps;
	}

	public void setProxyAuthentication(Boolean proxyAuthentication) {
		if(proxyAuthentication != null) {
			prefs.putBoolean(PROXY_AUTHENTICATION, proxyAuthentication);
		} else {
			prefs.remove(PROXY_AUTHENTICATION);
		}
		this.proxyAuthentication = proxyAuthentication;
	}

	public void setProxyUsername(String proxyUsername) {
		if(proxyUsername != null) {
			prefs.put(PROXY_USERNAME, proxyUsername);
		} else {
			prefs.remove(PROXY_USERNAME);
		}
		this.proxyUsername = proxyUsername;
	}

	public void setProxyPassword(String proxyPassword) {
		if(proxyPassword != null) {
			prefs.put(PROXY_PASSWORD, proxyPassword);
		} else {
			prefs.remove(PROXY_PASSWORD);
		}
		this.proxyPassword = proxyPassword;
	}
	
	public Boolean isUseSystemProxy() {
		return useSystemProxy;
	}

	public String getProxyServer() {
		return proxyServer;
	}

	public Integer getProxyPort() {
		return proxyPort;
	}
	
	public Boolean isProxyUseHttps() {
		return proxyUseHttps;
	}

	public Boolean isProxyAuthentication() {
		return proxyAuthentication;
	}

	public String getProxyUsername() {
		return proxyUsername;
	}

	public String getProxyPassword() {
		return proxyPassword;
	}

	public void clear() {
		try {
			this.prefs.clear();
		} catch (BackingStoreException e) {
			throw new IllegalStateException(e);
		}
		useSystemProxy = null;
		proxyUseHttps = null;
		proxyServer = null;
		proxyPort = null;
		proxyAuthentication = null;
		proxyUsername = null;
		proxyPassword = null;
	}
}
