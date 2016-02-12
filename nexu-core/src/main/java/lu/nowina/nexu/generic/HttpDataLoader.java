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
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lu.nowina.nexu.ProxyConfigurer;
import lu.nowina.nexu.api.EnvironmentInfo;
import lu.nowina.nexu.api.plugin.HttpStatus;
import lu.nowina.nexu.stats.PlatformStatistic;

public class HttpDataLoader {

	private static final Logger logger = LoggerFactory.getLogger(HttpDataLoader.class.getName());
	
	private final ProxyConfigurer proxyConfigurer;
	private final String applicationVersion;
	private final boolean sendAnonymousInfoToProxy;

	public HttpDataLoader(final ProxyConfigurer proxyConfigurer, final String applicationVersion, final boolean sendAnonymousInfoToProxy) {
		this.proxyConfigurer = proxyConfigurer;
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
		final HttpGet get = new HttpGet(requestUrl);
		
		if(sendAnonymousInfoToProxy) {
			final EnvironmentInfo info = EnvironmentInfo.buildFromSystemProperties(System.getProperties());
			URI uri = null;
			try {
				uri = new URIBuilder(get.getURI())
						.addParameter(PlatformStatistic.APPLICATION_VERSION, applicationVersion)
						.addParameter(PlatformStatistic.JRE_VENDOR, info.getJreVendor().toString())
						.addParameter(PlatformStatistic.OS_NAME, info.getOsName())
						.addParameter(PlatformStatistic.OS_ARCH, info.getOsArch())
						.addParameter(PlatformStatistic.OS_VERSION, info.getOsVersion())
						.build();
			} catch (URISyntaxException e) {
				throw new RuntimeException(e);
			}
			get.setURI(uri);
		}
		proxyConfigurer.setupProxy(get);
		HttpClient client = HttpClients.custom().setDefaultCredentialsProvider(
				proxyConfigurer.getProxyCredentialsProvider(get.getConfig().getProxy())).build();

		HttpResponse response = client.execute(get);
		if(HttpStatus.OK.getHttpCode() != response.getStatusLine().getStatusCode()) {
			logger.warn("Cannot perform GET request at " + requestUrl + ", status code = " + response.getStatusLine().getStatusCode());
			return null;
		}
		ResponseHandler<String> responseHandler = new BasicResponseHandler();
		return responseHandler.handleResponse(response).getBytes();
	}
}
