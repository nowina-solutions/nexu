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

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.BasicCredentialsProvider;
import lu.nowina.nexu.api.AppConfig;
import lu.nowina.nexu.api.EnvironmentInfo;
import lu.nowina.nexu.api.OS;
import lu.nowina.nexu.web.WebUtilities;
import lu.nowina.nexu.windows.WindowsRegistry;

/**
 * Configurer for the HTTP proxy that takes into account properties and user preferences.
 *
 * @author Jean Lepropre (jean.lepropre@nowina.lu)
 */
public class ProxyConfigurer {
	
	private static final boolean isWindows;
	
	private boolean useSystemProxy;
	private String proxyServer;
	private Integer proxyPort;
	private boolean proxyAuthentication;
	private String proxyUsername;
	private String proxyPassword;
	private boolean proxyUseHttps;
	
	static {
		isWindows = EnvironmentInfo.buildFromSystemProperties(System.getProperties()).getOs().equals(OS.WINDOWS);
	}
	
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
		if(isWindows && useSystemProxy) {
			HttpHost proxy = null;
			if(WindowsRegistry.isProxyEnable()) {
				String proxyAddress = WindowsRegistry.getProxyServer();
				if(proxyAddress != null) {
					String hostName;
					String hostAddress;
					if(WebUtilities.isIpAddress(request.getURI().getAuthority())) {
						hostAddress = request.getURI().getAuthority();
						hostName = WebUtilities.resolveHostName(hostAddress);
					} else {
						hostName = request.getURI().getAuthority();
						hostAddress = WebUtilities.resolveIp(hostName);
					}
					
					boolean bypassProxy = false;
					for(final String bypassAddress : WindowsRegistry.getBypassAddresses()) {
						final int indexOfStar = bypassAddress.indexOf('*');
						final String bypassPrefix = (indexOfStar == -1) ? bypassAddress : bypassAddress.substring(0, indexOfStar);
						if(bypassPrefix.length() == 0) {
							// If bypass address starts with *, skip it
							continue;
						}
						if(((hostName != null) && hostName.startsWith(bypassPrefix)) ||
						   ((hostAddress != null) && hostAddress.startsWith(bypassPrefix))) {
							bypassProxy = true;
							break;
						}
					}
					
					if(!bypassProxy) {
						String[] array = proxyAddress.split(":");
						String proxyHost = array[0];
						String proxyPort = array[1];
						proxy = new HttpHost(proxyHost, Integer.parseInt(proxyPort), proxyUseHttps ? "https" : "http");
					}
				}
			}
			RequestConfig config = RequestConfig.custom().setProxy(proxy).build();
			request.setConfig(config);
		}
		else if(!StringUtils.isEmpty(proxyServer)) {
			HttpHost proxy = new HttpHost(proxyServer, proxyPort, proxyUseHttps ? "https" : "http");
			RequestConfig config = RequestConfig.custom().setProxy(proxy).build();
			request.setConfig(config);
		}
		else {
			RequestConfig config = RequestConfig.custom().build();
			request.setConfig(config);
		}
	}
	
	public CredentialsProvider getProxyCredentialsProvider(HttpHost proxy) {
		CredentialsProvider credsProvider = new BasicCredentialsProvider();
		if(proxy != null && proxyAuthentication) {
			credsProvider.setCredentials(
					new AuthScope(proxy.getHostName(), proxy.getPort()), 
					new UsernamePasswordCredentials(proxyUsername, proxyPassword));
		}
		return credsProvider;
	}
}
