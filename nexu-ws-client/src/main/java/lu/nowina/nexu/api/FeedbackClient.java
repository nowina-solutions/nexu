package lu.nowina.nexu.api;

import java.net.URL;

import javax.xml.ws.BindingProvider;

public class FeedbackClient {

	private String baseUrl;

	private FeedbackEndpoint endpoint;

	public FeedbackClient(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	private FeedbackEndpoint getEndpoint() {
		if (endpoint == null) {
			try {
				String serviceUrl = baseUrl + "/api/v1/feedback";
				FeedbackEndpointService service = new FeedbackEndpointService(new URL(serviceUrl + "?wsdl"));
				FeedbackEndpoint port = service.getPort(FeedbackEndpoint.class);

				/* Set NEW Endpoint Location */
				BindingProvider provider = (BindingProvider) port;
				provider.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, serviceUrl);

				endpoint = port;
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return endpoint;
	}

	public void reportError(Feedback feedback) {
		getEndpoint().reportError(feedback);
	}

}
