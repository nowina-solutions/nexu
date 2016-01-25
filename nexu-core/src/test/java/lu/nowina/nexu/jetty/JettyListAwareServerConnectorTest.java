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

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.Arrays;
import java.util.List;

import lu.nowina.nexu.AbstractConfigureLoggerTest;

import org.eclipse.jetty.server.NetworkConnector;
import org.eclipse.jetty.server.Server;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * JUnit test class for {@link JettyListAwareServerConnector}.
 *
 * @author Jean Lepropre (jean.lepropre@nowina.lu)
 */
public class JettyListAwareServerConnectorTest extends AbstractConfigureLoggerTest {

	private static final List<Integer> PORTS = Arrays.asList(65000, 65001, 65002, 65003, 65004);
	
	private Server server;
	
	public JettyListAwareServerConnectorTest() {
		super();
	}

	@Before
	public void createServer() {
		server = new Server();
		final JettyListAwareServerConnector connector = new JettyListAwareServerConnector(server);
		connector.setPorts(PORTS);
		server.addConnector(connector);
	}
	
	@After
	public void stopServer() {
		try {
			server.stop();
		} catch(Exception e) {}
	}
	
	@Test
	public void testAllFree() throws Exception {
		server.start();
		Assert.assertNotNull(server.getConnectors());
		Assert.assertEquals(server.getConnectors().length, 1);
		Assert.assertEquals(((NetworkConnector)server.getConnectors()[0]).getPort(), PORTS.get(0).intValue());
	}

	@Test
	public void testFirstPortReserved() throws Exception {
		final ServerSocket socket = new ServerSocket();
		socket.setReuseAddress(true);
		socket.bind(new InetSocketAddress(PORTS.get(0)));
		try {
			server.start();
			Assert.assertNotNull(server.getConnectors());
			Assert.assertEquals(server.getConnectors().length, 1);
			Assert.assertEquals(((NetworkConnector)server.getConnectors()[0]).getPort(), PORTS.get(1).intValue());
		} finally {
			socket.close();
		}
	}
	
	@Test(expected=IOException.class)
	public void testAllPortReserverd() throws Exception {
		final ServerSocket[] sockets = new ServerSocket[PORTS.size()];
		try {
			int i = 0;
			for(int port : PORTS) {
				sockets[i] = new ServerSocket();
				sockets[i].setReuseAddress(true);
				sockets[i++].bind(new InetSocketAddress(port));
			}
			server.start();
		} finally {
			for(final ServerSocket socket : sockets) {
				if(socket != null) {
					try {
						socket.close();
					} catch(Exception e) {}
				}
			}
		}
	}
}
