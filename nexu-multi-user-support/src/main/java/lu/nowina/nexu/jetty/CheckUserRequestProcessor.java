package lu.nowina.nexu.jetty;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lu.nowina.nexu.NexuException;
import lu.nowina.nexu.api.EnvironmentInfo;
import lu.nowina.nexu.api.OS;
import lu.nowina.nexu.process.NativeProcessExecutor;

/**
 * Specialized version of {@link RequestProcessor} that checks whether requests
 * come from the user running this instance of NexU.
 *
 * @author Jean Lepropre (jean.lepropre@nowina.lu)
 */
public class CheckUserRequestProcessor extends RequestProcessor {

	private static final Logger LOGGER = LoggerFactory.getLogger(CheckUserRequestProcessor.class.getName());
	
	private static final OS OPERATING_SYSTEM = EnvironmentInfo.buildFromSystemProperties(System.getProperties()).getOs();
	
	private static final String EXPECTED_USERNAME = System.getProperty("user.name");
	
	private static final String PORT_TO_PID_PATTERN_PREFIX = "\\s+TCP\\s+[0-9a-f\\.]+:";
	private static final String PORT_TO_PID_PATTERN_SUFFIX = "\\s+[0-9a-f\\.:]+\\s+ESTABLISHED\\s+([0-9]+)";

	private final UserByPIDStrategy userByPIDStrategy;
	
	public CheckUserRequestProcessor() {
		try {
			switch(OPERATING_SYSTEM) {
			case WINDOWS:
				// Use reflection to avoid any wrong initialization issues
				userByPIDStrategy = Class.forName("lu.nowina.nexu.jetty.Win32JNAUserByPIDStrategy").asSubclass(
						UserByPIDStrategy.class).newInstance();
				break;
			default:
				userByPIDStrategy = null;
			}
		} catch (InstantiationException e) {
			throw new IllegalStateException(e);
		} catch (IllegalAccessException e) {
			throw new IllegalStateException(e);
		} catch (ClassNotFoundException e) {
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
		final String username = getUsername(pid);
		if(!EXPECTED_USERNAME.equals(username)) {
			return "Request comes from user " + username;
		} else {
			return null;
		}
	}
	
	private int getPID(final int port) {
		final Pattern pattern = Pattern.compile(PORT_TO_PID_PATTERN_PREFIX + port + PORT_TO_PID_PATTERN_SUFFIX);
		final String netstatResult = executeCommand("netstat -no");
		final Matcher matcher = pattern.matcher(netstatResult);
		if(!matcher.find()) {
			LOGGER.error("Pattern " + pattern.toString() + " cannot match " + netstatResult);
			// Do not put netstat result, nor pattern in exception to prevent sending private information through the Internet
			throw new IllegalStateException("Cannot match pattern with netstat result");
		}
		return Integer.parseInt(matcher.group(1));
	}
	
	private String getUsername(final int pid) {
		final String username = userByPIDStrategy.getUserForPID(pid);
		LOGGER.info("User for pid " + pid + ": " + username);
		return username;
	}
	
	private String executeCommand(final String command) {
		final NativeProcessExecutor executor = new NativeProcessExecutor(command, 10000);
		final int resultCode = executor.getResultCode();
		if(resultCode != 0) {
			throw new NexuException("Result code of " + command + " is different from 0: " + resultCode);
		}
		return executor.getResult();
	}
}
