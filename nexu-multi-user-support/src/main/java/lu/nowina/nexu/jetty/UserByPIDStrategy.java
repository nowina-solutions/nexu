package lu.nowina.nexu.jetty;

/**
 * This interface must be implemented by classes able to return a <code>user</code>
 * associated with a PID.
 *
 * @author Jean Lepropre (jean.lepropre@nowina.lu)
 */
interface UserByPIDStrategy {

	/**
	 * Returns the user that owns the process identified by <code>pid</code>. 
	 * @param pid The PID of the target process.
	 * @return The user that owns the process identified by <code>pid</code>.
	 */
	String getUserForPID(int pid);
	
}
