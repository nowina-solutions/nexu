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

	private Preferences prefs;

	private String proxyServer;

	private String proxyPort;

	public UserPreferences() {
		prefs = Preferences.userRoot().node(this.getClass().getName());
		proxyServer = prefs.get(PROXY_SERVER, null);
		proxyPort = prefs.get(PROXY_PORT, null);
	};

	public String getProxyServer() {
		return proxyServer;
	}

	public void setProxyServer(String proxyServer) {
		prefs.put(PROXY_SERVER, proxyServer);
		this.proxyServer = proxyServer;
	}

	public String getProxyPort() {
		return proxyPort;
	}

	public void setProxyPort(String proxyPort) {
		prefs.put(PROXY_PORT, proxyPort);
		this.proxyPort = proxyPort;
	}

}
