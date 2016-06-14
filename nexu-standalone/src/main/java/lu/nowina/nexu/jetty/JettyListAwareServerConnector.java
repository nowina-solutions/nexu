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
import java.util.Iterator;
import java.util.List;

import org.eclipse.jetty.server.ConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Adapter class of {@link ServerConnector} that supports a list of ports.
 *
 * @author Jean Lepropre (jean.lepropre@nowina.lu)
 */
public class JettyListAwareServerConnector extends ServerConnector {

	private static final Logger LOGGER = LoggerFactory.getLogger(JettyListAwareServerConnector.class);
	
	private List<Integer> ports;
	private int currentPort;
	
	public JettyListAwareServerConnector(Server server) {
		super(server);
	}

	public JettyListAwareServerConnector(Server server, ConnectionFactory... factories) {
		super(server, factories);
	}

	/**
	 * If opened, returns the port on which this connector is bound.
	 * <p>If not opened, returns the port currently tried.
	 */
	@Override
	public int getPort() {
		return currentPort;
	}
	
	/**
	 * This implementation throws an {@link IllegalStateException}.
	 * <p>Use {@link #setPorts(List)} instead.
	 */
	@Override
	public void setPort(int port) {
		throw new IllegalStateException("This implementation only supports setPorts(List<Integer>).");
	}
	
	/**
	 * Sets the ports that will be tried by this connector.
	 * @param ports The ports.
	 * @throws IllegalArgumentException If <code>ports</code> is <code>null</code>, empty or if one element
	 * is not between 0 and 65535.
	 * @throws IllegalStateException If connector is already opened.
	 */
	public void setPorts(List<Integer> ports) {
		if(isOpen()) {
			throw new IllegalStateException("Connector is already opened.");
		}
		if((ports == null) || ports.isEmpty()) {
			throw new IllegalArgumentException("List of ports cannot be null or empty.");
		}
		for(int port : ports) {
			if(port < 0 || port > 0xFFFF) {
				throw new IllegalArgumentException("Port out of range:" + port);
			}
		}
        
		this.ports = ports;
		currentPort = ports.get(0);
	}

	@Override
	public void open() throws IOException {
		for(final Iterator<Integer> it = ports.iterator(); it.hasNext(); ) {
			currentPort = it.next();
			try {
				super.open();
				LOGGER.info("Bound on port " + currentPort);
				return;
			} catch(IOException e) {
				LOGGER.warn("IOException (" + e.getMessage() + ") when trying to bind on port " + currentPort +", will try next port.");
			}
		}
		throw new IOException("Cannot bind a free port in list " + toString(ports) + ".");
	}
	
	private String toString(List<Integer> ports) {
		final StringBuilder sb = new StringBuilder("(");
		for(final Iterator<Integer> it = ports.iterator(); it.hasNext(); ) {
			sb.append(it.next());
			if(it.hasNext()) {
				sb.append(", ");
			}
		}
		sb.append(")");
		return sb.toString();
	}
}
