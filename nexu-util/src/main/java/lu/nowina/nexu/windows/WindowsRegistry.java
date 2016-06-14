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
package lu.nowina.nexu.windows;

import lu.nowina.nexu.NexuException;
import lu.nowina.nexu.process.NativeProcessExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WindowsRegistry {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(WindowsRegistry.class);
	
	private static final String REGISTRY_INTERNET_SETTINGS_LOCATION = "HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Internet Settings";
	
	private static final String PROXY_ENABLE_KEY = "ProxyEnable";
	private static final String PROXY_SERVER_KEY = "ProxyServer";
	private static final String PROXY_EXCEPTION_KEY = "ProxyOverride";

	private static final int RESULT_LOCATION = 5;
	
	public static boolean isProxyEnable() {
		final String registryResult = executeCommand(buildCommand(REGISTRY_INTERNET_SETTINGS_LOCATION, PROXY_ENABLE_KEY));
		final String[] array = registryResult.split("\\s+");
		final byte result = Byte.decode(array[RESULT_LOCATION]);
		return result != 0x0;
	}
	
	public static String getProxyServer() {
		try {
			final String registryResult = executeCommand(buildCommand(REGISTRY_INTERNET_SETTINGS_LOCATION, PROXY_SERVER_KEY));
			final String[] array = registryResult.split("\\s+");
			return array[RESULT_LOCATION];
		} catch (NexuException e) {
			LOGGER.info("There is no proxy server declared");
			return null;
		}
	}
	
	public static String[] getBypassAddresses() {
		try {
			final String registryResult = executeCommand(buildCommand(REGISTRY_INTERNET_SETTINGS_LOCATION, PROXY_EXCEPTION_KEY));
			final String[] array = registryResult.split("\\s+");
			return array[RESULT_LOCATION].split(";");
		} catch (NexuException e) {
			LOGGER.info("There is no address to bypass");
			return new String[0];
		}
	}
	
	private static String buildCommand(final String location, final String key) {
		return "reg query \"" + location + "\" /v " + key;
	}
	
	private static String executeCommand(final String command) {
		final NativeProcessExecutor executor = new NativeProcessExecutor(command, 10000);
		final int resultCode = executor.getResultCode();
		if(resultCode != 0) {
			throw new NexuException("Result code of " + command + " is different from 0: " + resultCode);
		}
		return executor.getResult();
	}
}
