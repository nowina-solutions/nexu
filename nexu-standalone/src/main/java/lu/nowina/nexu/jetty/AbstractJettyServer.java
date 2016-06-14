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

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;

import lu.nowina.nexu.HttpServer;
import lu.nowina.nexu.api.NexuAPI;

/**
 * Abstract base implementation of {@link HttpServer} for <code>Jetty</code>.
 *
 * @author Jean Lepropre (jean.lepropre@nowina.lu)
 */
public abstract class AbstractJettyServer implements HttpServer {

	private NexuAPI api;
	
	private Server server;
	
	public AbstractJettyServer() {
		super();
	}

	@Override
	public void setConfig(NexuAPI api) {
		this.api = api;
	}

	@Override
	public void start() throws Exception {
		server = new Server();
		server.setConnectors(getConnectors());
		
		final Class<? extends RequestProcessor> clazz =
				Class.forName(api.getAppConfig().getRequestProcessorClass()).asSubclass(RequestProcessor.class);
		final RequestProcessor handler = clazz.newInstance();
		handler.setConfig(api);
		handler.setNexuHostname(api.getAppConfig().getNexuHostname());

		server.setHandler(handler);
		server.start();
	}

	@Override
	public void stop() throws Exception {
		server.stop();
		server = null;
	}
	
	@Override
	public void join() throws Exception {
		server.join();
	}
	
	/**
	 * Returns the configured API.
	 * @return The configured API.
	 */
	protected NexuAPI getApi() {
		return api;
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
