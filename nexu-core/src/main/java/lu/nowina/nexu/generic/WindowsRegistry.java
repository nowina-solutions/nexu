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
package lu.nowina.nexu.generic;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lu.nowina.nexu.NexuException;

public class WindowsRegistry {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(WindowsRegistry.class);
	
	private static final String REGISTRY_INTERNET_SETTINGS_LOCATION = "HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Internet Settings";
	
	private static final String PROXY_ENABLE_KEY = "ProxyEnable";
	private static final String PROXY_SERVER_KEY = "ProxyServer";
	private static final String PROXY_EXCEPTION_KEY = "ProxyOverride";

	private static final int RESULT_LOCATION = 5;
	
	public static boolean isProxyEnable() {
		String registryResult = readRegistry(REGISTRY_INTERNET_SETTINGS_LOCATION, PROXY_ENABLE_KEY);
		String[] array = registryResult.split("\\s+");
		byte result = Byte.decode(array[RESULT_LOCATION]);
		return result != 0x0;
	}
	
	public static String getProxyServer() {
		try {
			String registryResult = readRegistry(REGISTRY_INTERNET_SETTINGS_LOCATION, PROXY_SERVER_KEY);
			String[] array = registryResult.split("\\s+");
			return array[RESULT_LOCATION];
		} catch (NexuException e) {
			LOGGER.info("There is no proxy server declared");
			return null;
		}
	}
	
	public static String[] getBypassAddresses() {
		try {
			String registryResult = readRegistry(REGISTRY_INTERNET_SETTINGS_LOCATION, PROXY_EXCEPTION_KEY);
			String[] array = registryResult.split("\\s+");
			return array[RESULT_LOCATION].split(";");
		} catch (NexuException e) {
			LOGGER.info("There is no address to bypass");
			return new String[0];
		}
	}
	
	private static final String readRegistry(String location, String key) {
		try {
			Process process = Runtime.getRuntime().exec("reg query \"" + location + "\" /v " + key);
			
			StreamReader reader = new StreamReader(process.getInputStream());
			reader.start();
			
			if(!process.waitFor(10000, TimeUnit.MILLISECONDS)) {
				reader.join();
				throw new RuntimeException("Timeout when reading the registry");
			}
			int resultCode = process.exitValue();
			reader.join();
			
			if(resultCode != 0) {
				throw new NexuException("Unable to find the key " + key + " on the location " + location 
						+ " (result code : " + resultCode + ")");
			}
			
			return reader.getResult();
		} catch (IOException | InterruptedException e) {
			throw new RuntimeException(e);
		}
	}
	
	static class StreamReader extends Thread {
		private InputStream is;
		private StringWriter writer;
		
		public StreamReader(InputStream is) {
			this.is = is;
			writer = new StringWriter();
		}
		
		public void run() {
			try {
				IOUtils.copy(is, writer);
			} catch (IOException e) {
				throw new RuntimeException("Unable to read InputStream", e);
			} finally {
				IOUtils.closeQuietly(is);
			}
			
		}
		
		public String getResult() {
			return writer.toString();
		}
	}
}
