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

import java.util.logging.Logger;

import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.ssl.SslContextFactory;

import lu.nowina.nexu.AppConfig;
import lu.nowina.nexu.InternalAPI;
import lu.nowina.nexu.UserPreferences;

public class JettyServer {

	private static final Logger logger = Logger.getLogger(JettyServer.class.getName());

	private UserPreferences prefs;

	private AppConfig conf;

	private Server server;

	private InternalAPI api;

	public void setConfig(InternalAPI api, UserPreferences prefs, AppConfig config) {
		this.api = api;
		this.prefs = prefs;
		this.conf = config;
	}

	public void start() throws Exception {
		logger.info("Start HTTP server, binding on " + conf.getBindingPort());

		Server server = new Server(conf.getBindingPort());

		RequestProcessor handler = new RequestProcessor();
		handler.setConfig(api, prefs);

		server.setHandler(handler);
		server.start();
		server.join();
	}

	public void stop() throws Exception {
		server.stop();
		server = null;
	}

}
