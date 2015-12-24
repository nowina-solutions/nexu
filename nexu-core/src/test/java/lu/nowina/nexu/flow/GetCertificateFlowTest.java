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
package lu.nowina.nexu.flow;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import lu.nowina.nexu.AbstractConfigureLoggerTest;
import lu.nowina.nexu.api.AppConfig;
import lu.nowina.nexu.api.CardAdapter;
import lu.nowina.nexu.api.DetectedCard;
import lu.nowina.nexu.api.Feedback;
import lu.nowina.nexu.api.FeedbackStatus;
import lu.nowina.nexu.api.GetCertificateRequest;
import lu.nowina.nexu.api.GetCertificateResponse;
import lu.nowina.nexu.api.Match;
import lu.nowina.nexu.api.NexuAPI;
import lu.nowina.nexu.api.TokenId;
import lu.nowina.nexu.api.flow.Operation;
import lu.nowina.nexu.api.flow.OperationResult;
import lu.nowina.nexu.api.flow.OperationStatus;
import lu.nowina.nexu.flow.operation.BasicOperationFactory;
import lu.nowina.nexu.flow.operation.CreateTokenOperation;
import lu.nowina.nexu.flow.operation.GetMatchingCardAdaptersOperation;
import lu.nowina.nexu.flow.operation.OperationFactory;
import lu.nowina.nexu.view.core.UIDisplay;
import lu.nowina.nexu.view.core.UIOperation;

import org.junit.Assert;
import org.junit.Test;

import eu.europa.esig.dss.token.JKSSignatureToken;
import eu.europa.esig.dss.token.SignatureTokenConnection;

public class GetCertificateFlowTest extends AbstractConfigureLoggerTest {

	@Test
	@SuppressWarnings("unchecked")
	public void testNoProduct() throws Exception {

		final UIDisplay display = mock(UIDisplay.class);

		final NexuAPI api = mock(NexuAPI.class);
		when(api.detectCards()).thenReturn(Collections.emptyList());

		final OperationFactory operationFactory = mock(OperationFactory.class);

		final GetMatchingCardAdaptersOperation operation = new GetMatchingCardAdaptersOperation();
		operation.setParams(api);
		operation.setDisplay(display);
		operation.setOperationFactory(operationFactory);
		when(operationFactory.getOperation(GetMatchingCardAdaptersOperation.class, api)).thenReturn(operation);

		final Operation<Object> successOperation = mock(Operation.class);
		when(successOperation.perform()).thenReturn(new OperationResult<Object>(OperationStatus.SUCCESS));
		
		final Feedback feedback = new Feedback();
		feedback.setFeedbackStatus(FeedbackStatus.NO_PRODUCT_FOUND);
		when(operationFactory.getOperation(
				UIOperation.class, display, "/fxml/provide-feedback.fxml", new Object[]{feedback})).thenReturn(successOperation);

		when(operationFactory.getOperation(
				UIOperation.class, display, "/fxml/message.fxml", new Object[]{"Finished"})).thenReturn(successOperation);
		
		final GetCertificateFlow flow = new GetCertificateFlow(display);
		flow.setOperationFactory(operationFactory);
		
		final GetCertificateRequest req = new GetCertificateRequest();
		final GetCertificateResponse resp = flow.process(api, req);
		Assert.assertNull(resp);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testNotRecognizedRequestSupport() throws Exception {
		final UIDisplay display = mock(UIDisplay.class);

		final NexuAPI api = mock(NexuAPI.class);
		when(api.detectCards()).thenReturn(Arrays.asList(new DetectedCard("atr", 0)));
		
		final AppConfig appConfig = new AppConfig();
		appConfig.setAdvancedModeAvailable(true);
		when(api.getAppConfig()).thenReturn(appConfig);

		final OperationFactory operationFactory = mock(OperationFactory.class);
		
		final Operation<List<Match>> getMatchingCardAdaptersOperation = mock(Operation.class);
		when(getMatchingCardAdaptersOperation.perform()).thenReturn(new OperationResult<List<Match>>(Collections.emptyList()));
		when(operationFactory.getOperation(GetMatchingCardAdaptersOperation.class, api)).thenReturn(getMatchingCardAdaptersOperation);
		
		final CreateTokenOperation createTokenOperation = new CreateTokenOperation();
		createTokenOperation.setParams(api, Collections.emptyList());
		createTokenOperation.setDisplay(display);
		createTokenOperation.setOperationFactory(operationFactory);
		when(operationFactory.getOperation(CreateTokenOperation.class, api, Collections.emptyList())).thenReturn(createTokenOperation);
		
		final Operation<Object> returnFalseOperation = mock(Operation.class);
		when(returnFalseOperation.perform()).thenReturn(new OperationResult<Object>(false));
		when(operationFactory.getOperation(
				UIOperation.class, display, "/fxml/unsupported-product.fxml")).thenReturn(returnFalseOperation);

		final Operation<Object> successOperation = mock(Operation.class);
		when(successOperation.perform()).thenReturn(new OperationResult<Object>(OperationStatus.SUCCESS));
		final Feedback feedback = new Feedback();
		feedback.setFeedbackStatus(FeedbackStatus.PRODUCT_NOT_SUPPORTED);
		when(operationFactory.getOperation(
				UIOperation.class, display, "/fxml/provide-feedback.fxml", new Object[]{feedback})).thenReturn(successOperation);
		when(operationFactory.getOperation(
				UIOperation.class, display, "/fxml/message.fxml", new Object[]{"Finished"})).thenReturn(successOperation);
		
		final GetCertificateFlow flow = new GetCertificateFlow(display);
		flow.setOperationFactory(operationFactory);
		
		final GetCertificateRequest req = new GetCertificateRequest();
		final GetCertificateResponse resp = flow.process(api, req);
		Assert.assertNull(resp);
	}

	@Test
	public void testCardRecognized() {

		UIDisplay display = mock(UIDisplay.class);

		CardAdapter adapter = mock(CardAdapter.class);

		SignatureTokenConnection token = new JKSSignatureToken(this.getClass().getResourceAsStream("/keystore.jks"), "password");

		NexuAPI api = mock(NexuAPI.class);
		DetectedCard detectedCard = new DetectedCard("atr", 0);

		when(api.detectCards()).thenReturn(Arrays.asList(detectedCard));
		when(api.matchingCardAdapters(detectedCard)).thenReturn(Arrays.asList(new Match(adapter, detectedCard)));
		when(api.registerTokenConnection(token)).thenReturn(new TokenId("id"));
		when(api.getTokenConnection(new TokenId("id"))).thenReturn(token);
		when(adapter.connect(eq(api), eq(detectedCard), any())).thenReturn(token);

		final OperationFactory operationFactory = new BasicOperationFactory();
		operationFactory.setDisplay(display);
		
		GetCertificateFlow flow = new GetCertificateFlow(display);
		flow.setOperationFactory(operationFactory);
		GetCertificateResponse resp = flow.process(api, new GetCertificateRequest());
		Assert.assertNotNull(resp);
		Assert.assertNotNull(resp.getEncryptionAlgorithm());
		Assert.assertNotNull(resp.getTokenId());
		Assert.assertEquals(new TokenId("id"), resp.getTokenId());
		Assert.assertNotNull(resp.getKeyId());

	}

}
