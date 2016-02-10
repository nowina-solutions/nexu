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

import java.net.InetAddress;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;

public class JettyServer extends AbstractJettyServer {

	@Override
	protected Connector[] getConnectors() {
		final HttpConfiguration http = new HttpConfiguration();
		final JettyListAwareServerConnector connector = new JettyListAwareServerConnector(getServer());
		connector.addConnectionFactory(new HttpConnectionFactory(http));
		connector.setPorts(getApi().getAppConfig().getBindingPorts());
		connector.setHost(InetAddress.getLoopbackAddress().getCanonicalHostName());
		return new Connector[]{connector};
	}
}
