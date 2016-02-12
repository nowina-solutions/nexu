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
