package lu.nowina.nexu.generic;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WindowsRegistry {
	
	private static final Logger logger = LoggerFactory.getLogger(WindowsRegistry.class);
	
	private static String REGISTRY_INTERNET_SETTINGS_LOCATION = "HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Internet Settings";
	
	private static String PROXY_ENABLE_KEY = "ProxyEnable";
	private static String PROXY_SERVER_KEY = "ProxyServer";
	private static String PROXY_EXCEPTION_KEY = "ProxyOverride";

	private static int resultLocation = 5;
	
	public static boolean isProxyEnable() {
		String registryResult = readRegistry(REGISTRY_INTERNET_SETTINGS_LOCATION, PROXY_ENABLE_KEY);
		String[] array = registryResult.split("\\s+");
		byte result = Byte.decode(array[resultLocation]);
		return result != 0x0;
	}
	
	public static String getProxyServer() {
		String registryResult = readRegistry(REGISTRY_INTERNET_SETTINGS_LOCATION, PROXY_SERVER_KEY);
		String[] array = registryResult.split("\\s+");
		if(array.length == 0)  {
			logger.warn("There is no proxy server declared");
			return null;
		} else {
			return array[resultLocation];
		}
	}
	
	public static String[] getBypassAddresses() {
		String registryResult = readRegistry(REGISTRY_INTERNET_SETTINGS_LOCATION, PROXY_EXCEPTION_KEY);
		String[] array = registryResult.split("\\s+");
		if(array.length == 0) {
			return new String[0];
		} else {
			return array[resultLocation].split(";");
		}
	}
	
	private static final String readRegistry(String location, String key) {
		try {
			Process process = Runtime.getRuntime().exec("reg query \"" + location + "\" /v " + key);
			
			StreamReader reader = new StreamReader(process.getInputStream());
			reader.start();
			int resultCode = process.waitFor();
			reader.join();
			
			if(resultCode != 0) {
				throw new RuntimeException("Unable to find the key " + key + " on the location " + location);
			}
			
			return reader.getResult();
		} catch (IOException | InterruptedException e) {
			throw new RuntimeException(e);
		}
	}
	
	static class StreamReader extends Thread {
		private InputStream is;
		private StringWriter writter = new StringWriter();
		
		public StreamReader(InputStream is) {
			this.is = is;
		}
		
		public void run() {
			try {
				int c;
				while((c = is.read()) != -1) {
					writter.write(c);
				}
			} catch (IOException e) {
			}
		}
		
		public String getResult() {
			return writter.toString();
		}
	}
}
