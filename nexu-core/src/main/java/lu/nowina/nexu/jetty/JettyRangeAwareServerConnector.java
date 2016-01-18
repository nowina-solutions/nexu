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

import org.eclipse.jetty.server.ConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Adapter class of {@link ServerConnector} that supports a range of ports.
 *
 * @author Jean Lepropre (jean.lepropre@nowina.lu)
 */
public class JettyRangeAwareServerConnector extends ServerConnector {

	private static final Logger LOGGER = LoggerFactory.getLogger(JettyRangeAwareServerConnector.class);
	
	private int minPort;
	private int maxPort;

	private int currentPort;
	
	public JettyRangeAwareServerConnector(Server server) {
		super(server);
	}

	public JettyRangeAwareServerConnector(Server server, ConnectionFactory... factories) {
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
	 * <p>Use {@link #setPortRange(int, int)} instead.
	 */
	@Override
	public void setPort(int port) {
		throw new IllegalStateException("This implementation only supports setPortRange(int, int).");
	}
	
	/**
	 * Sets the range of ports that will be tried by this connector.
	 * @param minPort The lower bound of the range.
	 * @param maxPort The upper bound of the range.
	 * @throws IllegalArgumentException If <code>minPort</code> is strictly greater than <code>maxPort</code> or
	 * if <code>minPort</code> or <code>maxPort</code> is not between 0 and 65535.
	 * @throws IllegalStateException If connector is already opened.
	 */
	public void setPortRange(int minPort, int maxPort) {
		if(isOpen()) {
			throw new IllegalStateException("Connector is already opened.");
		}
        if(minPort < 0 || minPort > 0xFFFF) {
            throw new IllegalArgumentException("Min port out of range:" + minPort);
        }
        if(maxPort < 0 || maxPort > 0xFFFF) {
            throw new IllegalArgumentException("Max port out of range:" + maxPort);
        }
		if(minPort > maxPort) {
			throw new IllegalArgumentException("Min port = " + minPort +", max port = " + maxPort);
		}
        
		this.minPort = minPort;
		this.maxPort = maxPort;
		this.currentPort = minPort;
	}

	@Override
	public void open() throws IOException {
		for(; currentPort <= maxPort; ++currentPort) {
			try {
				super.open();
				LOGGER.info("Bound on port " + currentPort);
				return;
			} catch(IOException e) {
				LOGGER.warn("IOException (" + e.getMessage() + ") when trying to bind on port " + currentPort +", will try next port.");
			}
		}
		throw new IOException("Cannot bind a free port in range [" + minPort + ", " + maxPort + "].");
	}
}
