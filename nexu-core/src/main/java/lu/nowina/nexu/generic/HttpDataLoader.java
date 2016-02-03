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

import lu.nowina.nexu.api.EnvironmentInfo;
import lu.nowina.nexu.api.plugin.HttpStatus;
import lu.nowina.nexu.stats.PlatformStatistic;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpDataLoader {

	private static final Logger logger = LoggerFactory.getLogger(HttpDataLoader.class.getName());
	
	private final HttpClient client;
	private final String applicationVersion;
	private final boolean sendAnonymousInfoToProxy;

	public HttpDataLoader(final String applicationVersion, final boolean sendAnonymousInfoToProxy) {
		this.client = new HttpClient();
		this.applicationVersion = applicationVersion;
		this.sendAnonymousInfoToProxy = sendAnonymousInfoToProxy;
	}
	
	public byte[] fetchDatabase(String databaseUrl) throws IOException {
		return performGetRequest(databaseUrl);
	}

	public byte[] fetchNexuInfo(String infoUrl) throws IOException {
		return performGetRequest(infoUrl);
	}

	private byte[] performGetRequest(String requestUrl) throws IOException {
		final GetMethod get = new GetMethod(requestUrl);
		
		if(sendAnonymousInfoToProxy) {
			final EnvironmentInfo info = EnvironmentInfo.buildFromSystemProperties(System.getProperties());
			get.setQueryString(new NameValuePair[] {
					new NameValuePair(PlatformStatistic.APPLICATION_VERSION, applicationVersion),
					new NameValuePair(PlatformStatistic.JRE_VENDOR, info.getJreVendor().toString()),
					new NameValuePair(PlatformStatistic.OS_NAME, info.getOsName()),
					new NameValuePair(PlatformStatistic.OS_ARCH, info.getOsArch()),
					new NameValuePair(PlatformStatistic.OS_VERSION, info.getOsVersion())
			});
		}

		client.executeMethod(get);
		if(HttpStatus.OK.getHttpCode() != get.getStatusCode()) {
			logger.info("Cannot perform GET request at " + requestUrl + ", status code = " + get.getStatusCode());
			return null;
		}
		return get.getResponseBody();
	}
}
