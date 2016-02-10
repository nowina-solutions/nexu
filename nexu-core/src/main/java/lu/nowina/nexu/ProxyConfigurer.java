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

import java.net.Authenticator;
import java.net.PasswordAuthentication;

import lu.nowina.nexu.api.AppConfig;

import org.apache.commons.lang.StringUtils;

/**
 * Configurer for the HTTP proxy that takes into account properties and user preferences.
 *
 * @author Jean Lepropre (jean.lepropre@nowina.lu)
 */
public class ProxyConfigurer {

	private boolean useSystemProxy;
	private String proxyServer;
	private Integer proxyPort;
	private boolean proxyAuthentication;
	private String proxyUsername;
	private String proxyPassword;
	
	public ProxyConfigurer(final AppConfig config, final UserPreferences preferences) {
		useSystemProxy = (preferences.isUseSystemProxy() != null) ? preferences.isUseSystemProxy() : config.isUseSystemProxy();
		proxyServer = (preferences.getProxyServer() != null) ? preferences.getProxyServer() : config.getProxyServer();
		proxyPort = (preferences.getProxyPort() != null) ? preferences.getProxyPort() : config.getProxyPort();
		proxyAuthentication = (preferences.isProxyAuthentication() != null) ? preferences.isProxyAuthentication() : config.isProxyAuthentication();
		proxyUsername = (preferences.getProxyUsername() != null) ? preferences.getProxyUsername() : config.getProxyUsername();
		proxyPassword = (preferences.getProxyPassword() != null) ? preferences.getProxyPassword() : config.getProxyPassword();
	}

	public boolean isUseSystemProxy() {
		return useSystemProxy;
	}

	public String getProxyServer() {
		return proxyServer;
	}

	public Integer getProxyPort() {
		return proxyPort;
	}

	public boolean isProxyAuthentication() {
		return proxyAuthentication;
	}

	public String getProxyUsername() {
		return proxyUsername;
	}

	public String getProxyPassword() {
		return proxyPassword;
	}

	public void setupProxy() {
		if(useSystemProxy) {
			System.setProperty("java.net.useSystemProxies", "true");
			setupProxyAuthentication();
		} else if(!StringUtils.isEmpty(proxyServer)) {
			System.setProperty("http.proxyHost", proxyServer);
			System.setProperty("http.proxyPort", proxyPort.toString());
			System.setProperty("https.proxyHost", proxyServer);
			System.setProperty("https.proxyPort", proxyPort.toString());
			setupProxyAuthentication();
		}
	}
	
	private void setupProxyAuthentication() {
		if(proxyAuthentication) {
			Authenticator.setDefault(new Authenticator() {
				@Override
				protected PasswordAuthentication getPasswordAuthentication() {
					if ((getRequestorType() == RequestorType.PROXY) &&
						(useSystemProxy ||
						 ((getRequestingHost().toLowerCase().equals(proxyServer.toLowerCase())) &&
						  (getRequestingPort() == proxyPort)))) {
						// Request comes from proxy server ==> we can provide user and password.
						return new PasswordAuthentication(proxyUsername,
								proxyPassword.toCharArray());  
					}
					return null;
				}  
			});
		}
	}
}
