package lu.nowina.nexu.jetty;

/**
 * This interface must be implemented by classes able to return a <code>PID</code>
 * associated with a listening TCP port.
 *
 * @author Martin Schleyer (schleyer@oszimt.de)
 */
interface PIDByPortStrategy {

	/**
	 * Returns the process pid that owns the listening socket identified by <code>port</code>. 
	 * @param port The port of the listening socket.
	 * @return The pid that owns the listening socket identified by <code>port</code>.
	 */
	int getPIDForPort(int port);
	
}
