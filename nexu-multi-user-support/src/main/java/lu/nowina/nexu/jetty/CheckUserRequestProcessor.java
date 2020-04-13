package lu.nowina.nexu.jetty;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lu.nowina.nexu.api.EnvironmentInfo;
import lu.nowina.nexu.api.OS;
import lu.nowina.nexu.jetty.RequestProcessor;

/**
 * Specialized version of {@link RequestProcessor} that checks whether requests
 * come from the user running this instance of NexU.
 *
 * @author Jean Lepropre (jean.lepropre@nowina.lu)
 * @author Martin Schleyer (schleyer@oszimt.de)
 */
public class CheckUserRequestProcessor extends RequestProcessor {

	private static final Logger LOGGER = LoggerFactory.getLogger(CheckUserRequestProcessor.class.getName());
	
	private static final OS OPERATING_SYSTEM = EnvironmentInfo.buildFromSystemProperties(System.getProperties()).getOs();
	
	private static final String EXPECTED_USERNAME = System.getProperty("user.name");
	
	private final UserByPIDStrategy userByPIDStrategy;
	private final PIDByPortStrategy pidByPortStrategy;
	
	public CheckUserRequestProcessor() {
		try {
			switch(OPERATING_SYSTEM) {
			case WINDOWS:
				// Use reflection to avoid any wrong initialization issues
				userByPIDStrategy = Class.forName("lu.nowina.nexu.jetty.Win32JNAUserByPIDStrategy").asSubclass(
                        UserByPIDStrategy.class).newInstance();
                pidByPortStrategy = Class.forName("lu.nowina.nexu.jetty.Win32JNAPIDByPortStrategy").asSubclass(
                        PIDByPortStrategy.class).newInstance();
				break;
			default:
				userByPIDStrategy = null;
				pidByPortStrategy = null;
			}
		} catch (final InstantiationException e) {
			throw new IllegalStateException(e);
		} catch (final IllegalAccessException e) {
			throw new IllegalStateException(e);
		} catch (final ClassNotFoundException e) {
			throw new IllegalStateException(e);
		}
	}

	/**
	 * This implementation will perform following things in order:
	 * <ol>
	 * <li>Retrieve remote port. This port is a local port as we only accept requests from localhost in {@link RequestProcessor}.</li>
	 * <li>Query the OS to get the PID that owns this port.</li>
	 * <li>Query the OS to get the name of the user that is running this process.</li>
	 * <li>Compares this username to the one running this instance of NexU. If it is the same, request is valid. Otherwise, an error
	 * message is returned.</li>
	 * </ol>
	 * @param request The request to check.
	 * @return <code>null</code> if request comes from the same user than the one running this instance of NexU. Otherwise, an
	 * error message is returned.
	 */
	@Override
	protected String returnNullIfValid(final HttpServletRequest request) {
		if(!OS.WINDOWS.equals(OPERATING_SYSTEM)) {
			// Current implementation supports only Windows.
			return null;
		}
		
		final int port = request.getRemotePort();
        final int pid = getPID(port);
        if (pid <= 0) {
            return "No PID for port " + port + " found!";
        }
		final String username = getUsername(pid);
		if(!EXPECTED_USERNAME.equals(username)) {
			return "Request comes from user " + username;
		} else {
			return null;
		}
	}
	
	private int getPID(final int port) {
                int pid = pidByPortStrategy.getPIDForPort(port);
                LOGGER.info("PID for port " + port + " : " + pid);
                return pid;
	}
	
	private String getUsername(final int pid) {
		final String username = userByPIDStrategy.getUserForPID(pid);
		LOGGER.info("User for pid " + pid + ": " + username);
		return username;
	}
}
