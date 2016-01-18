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
package lu.nowina.nexu.jetty;

import lu.nowina.nexu.HttpServer;
import lu.nowina.nexu.InternalAPI;
import lu.nowina.nexu.UserPreferences;
import lu.nowina.nexu.api.AppConfig;
import lu.nowina.nexu.api.NexuAPI;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;

/**
 * Abstract base implementation of {@link HttpServer} for <code>Jetty</code>.
 *
 * @author Jean Lepropre (jean.lepropre@nowina.lu)
 */
public abstract class AbstractJettyServer implements HttpServer {

	private InternalAPI api;
	private UserPreferences prefs;
	private AppConfig conf;
	
	private Server server;
	
	public AbstractJettyServer() {
		super();
	}

	@Override
	public void setConfig(InternalAPI api, UserPreferences prefs, AppConfig config) {
		this.api = api;
		this.prefs = prefs;
		this.conf = config;
	}

	@Override
	public void start() throws Exception {
		server = new Server(conf.getBindingPort());
		server.setConnectors(getConnectors());
		
		final RequestProcessor handler = new RequestProcessor(conf.getInstallUrl(), conf.getNexuUrl());
		handler.setConfig(api, prefs);

		server.setHandler(handler);
		server.start();
		server.join();
	}

	@Override
	public void stop() throws Exception {
		server.stop();
		server = null;
	}
	
	/**
	 * Returns the configured API.
	 * @return The configured API.
	 */
	protected NexuAPI getApi() {
		return api;
	}
	
	/**
	 * Returns the configured user preferences.
	 * @return The configured user preferences.
	 */
	protected UserPreferences getPrefs() {
		return prefs;
	}
	
	/**
	 * Returns the configuration of the application.
	 * @return The configuration of the application.
	 */
	protected AppConfig getConf() {
		return conf;
	}
	
	/**
	 * Returns the Jetty server that is currently in building.
	 * @return The Jetty server that is currently in building.
	 */
	protected Server getServer() {
		return server;
	}
	
	/**
	 * Returns the connectors that must be used by this server.
	 * @return The connectors that must be used by this server.
	 */
	protected abstract Connector[] getConnectors();
}
