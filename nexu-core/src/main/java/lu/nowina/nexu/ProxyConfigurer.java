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

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpRequestBase;

import lu.nowina.nexu.api.AppConfig;

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
	private boolean proxyUseHttps;
	
	public ProxyConfigurer(final AppConfig config, final UserPreferences preferences) {
		updateValues(config, preferences);
	}
	
	public void updateValues(final AppConfig config, final UserPreferences preferences) {
		useSystemProxy = (preferences.isUseSystemProxy() != null) ? preferences.isUseSystemProxy() : config.isUseSystemProxy();
		proxyServer = (preferences.getProxyServer() != null) ? preferences.getProxyServer() : config.getProxyServer();
		proxyPort = (preferences.getProxyPort() != null) ? preferences.getProxyPort() : config.getProxyPort();
		proxyUseHttps = (preferences.isProxyUseHttps() != null) ? preferences.isProxyUseHttps() : config.isProxyUseHttps();
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
	
	public boolean isProxyUseHttps() {
		return proxyUseHttps;
	}

	public String getProxyUsername() {
		return proxyUsername;
	}

	public String getProxyPassword() {
		return proxyPassword;
	}
	
	public void setupProxy(HttpRequestBase request) {
		if(!StringUtils.isEmpty(proxyServer)) {
			setupProxyAuthentication();
			HttpHost proxy = new HttpHost(proxyServer, proxyPort, proxyUseHttps ? "https" : "http");
			RequestConfig config = RequestConfig.custom().setProxy(proxy).build();
			request.setConfig(config);
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
