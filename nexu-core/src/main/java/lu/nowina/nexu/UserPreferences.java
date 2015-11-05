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

import java.util.prefs.Preferences;

public class UserPreferences {

	private static final String PROXY_SERVER = "nowina.nexu.proxyServer";

	private static final String PROXY_PORT = "nowina.nexu.proxyPort";

	private static final String PROXY_AUTHENTIFICATION = "nowina.nexu.proxyAuthentification";

	private static final String PROXY_USERNAME = "nowina.nexu.proxyUsername";

	private static final String PROXY_PASSWORD = "nowina.nexu.proxyPassword";

	private Preferences prefs;

	private String proxyServer;

	private String proxyPort;

	private Boolean proxyAuthentification;

	private String proxyUsername;

	private String proxyPassword;

	public UserPreferences() {
		prefs = Preferences.userRoot().node(this.getClass().getName());
		proxyServer = prefs.get(PROXY_SERVER, "");
		proxyPort = prefs.get(PROXY_PORT, "");
		proxyAuthentification = Boolean.valueOf(prefs.get(PROXY_AUTHENTIFICATION, "false"));
		proxyUsername = prefs.get(PROXY_USERNAME, "");
		proxyPassword = prefs.get(PROXY_PASSWORD, "");
	};

	public void setProxyServer(String proxyServer) {
		prefs.put(PROXY_SERVER, proxyServer);
		this.proxyServer = proxyServer;
	}

	public void setProxyPort(String proxyPort) {
		prefs.put(PROXY_PORT, proxyPort);
		this.proxyPort = proxyPort;
	}

	public void setProxyAuthentification(Boolean proxyAuthentification) {
		prefs.put(PROXY_AUTHENTIFICATION, proxyAuthentification.toString());
		this.proxyAuthentification = proxyAuthentification;
	}

	public void setProxyUsername(String proxyUsername) {
		prefs.put(PROXY_USERNAME, proxyUsername);
		this.proxyUsername = proxyUsername;
	}

	public void setProxyPassword(String proxyPassword) {
		prefs.put(PROXY_PASSWORD, proxyPassword);
		this.proxyPassword = proxyPassword;
	}

	public String getProxyServer() {
		return proxyServer;
	}

	public String getProxyPort() {
		return proxyPort;
	}

	public Boolean getProxyAuthentification() {
		return proxyAuthentification;
	}

	public String getProxyUsername() {
		return proxyUsername;
	}

	public String getProxyPassword() {
		return proxyPassword;
	}

}
