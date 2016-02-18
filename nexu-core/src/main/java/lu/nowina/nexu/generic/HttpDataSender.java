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
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HTTP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lu.nowina.nexu.ProxyConfigurer;
import lu.nowina.nexu.api.plugin.HttpStatus;

public class HttpDataSender {
	
	private static final Logger logger = LoggerFactory.getLogger(HttpDataSender.class);
	
	private ProxyConfigurer proxyConfigurer;
	
	public HttpDataSender(ProxyConfigurer proxyConfigurer) {
		this.proxyConfigurer = proxyConfigurer;
	}
	
	public void sendFeedback(String url, String feedbackXml) throws IOException {
		performPostRequestWithNoResponse(url, feedbackXml);
	}
	
	public void performPostRequestWithNoResponse(String requestUrl, String entity) throws IOException {
		final HttpPost post = new HttpPost(requestUrl);
		post.setHeader("Content-type", "application/xml");
		post.setEntity(new StringEntity(entity));
		proxyConfigurer.setupProxy(post);
		HttpClient client = HttpClients.custom().setDefaultCredentialsProvider(
				proxyConfigurer.getProxyCredentialsProvider(post.getConfig().getProxy())).build();
		
		HttpResponse response = client.execute(post);
		if(HttpStatus.OK.getHttpCode() != response.getStatusLine().getStatusCode()) {
			logger.warn("Cannot perform POST request at " + requestUrl + ", status code = " + response.getStatusLine().getStatusCode());
		}
	}
}
