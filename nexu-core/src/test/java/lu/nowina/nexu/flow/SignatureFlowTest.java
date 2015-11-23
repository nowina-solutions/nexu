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
import static org.mockito.Mockito.withSettings;

import java.util.Arrays;

import lu.nowina.nexu.AbstractConfigureLoggerTest;
import lu.nowina.nexu.NexuException;
import lu.nowina.nexu.api.CardAdapter;
import lu.nowina.nexu.api.DetectedCard;
import lu.nowina.nexu.api.Match;
import lu.nowina.nexu.api.NexuAPI;
import lu.nowina.nexu.api.SignatureRequest;
import lu.nowina.nexu.api.SignatureResponse;
import lu.nowina.nexu.api.TokenId;
import lu.nowina.nexu.flow.operation.BasicOperationFactory;
import lu.nowina.nexu.flow.operation.Operation;
import lu.nowina.nexu.flow.operation.OperationFactory;
import lu.nowina.nexu.flow.operation.OperationResult;
import lu.nowina.nexu.flow.operation.OperationStatus;
import lu.nowina.nexu.view.core.UIDisplay;
import lu.nowina.nexu.view.core.UIOperation;

import org.junit.Assert;
import org.junit.Test;

import eu.europa.esig.dss.DigestAlgorithm;
import eu.europa.esig.dss.ToBeSigned;
import eu.europa.esig.dss.token.JKSSignatureToken;
import eu.europa.esig.dss.token.SignatureTokenConnection;

public class SignatureFlowTest extends AbstractConfigureLoggerTest {

	@Test
	public void testCardRecognized() throws Exception {
		UIDisplay display = mock(UIDisplay.class);

		CardAdapter adapter = mock(CardAdapter.class, withSettings().verboseLogging());

		SignatureTokenConnection token = new JKSSignatureToken(this.getClass().getResourceAsStream("/keystore.jks"), "password");

		NexuAPI api = mock(NexuAPI.class, withSettings().verboseLogging());
		DetectedCard detectedCard = new DetectedCard("atr", 0);
		when(api.detectCards()).thenReturn(Arrays.asList(detectedCard));
		when(api.matchingCardAdapters(detectedCard)).thenReturn(Arrays.asList(new Match(adapter, detectedCard)));
		when(api.registerTokenConnection(token)).thenReturn(new TokenId("id"));
		when(api.getTokenConnection(new TokenId("id"))).thenReturn(token);

		when(adapter.connect(eq(api), eq(detectedCard), any())).thenReturn(token);

		SignatureRequest req = new SignatureRequest();
		req.setToBeSigned(new ToBeSigned("hello".getBytes()));
		req.setDigestAlgorithm(DigestAlgorithm.SHA256);

		final OperationFactory noUIOperationFactory = new NoUIOperationFactory();
		noUIOperationFactory.setDisplay(display);

		SignatureFlow flow = new SignatureFlow(display);
		flow.setOperationFactory(noUIOperationFactory);
		SignatureResponse resp = flow.process(api, req);
		Assert.assertNotNull(resp);
		Assert.assertNotNull(resp.getSignatureValue());

	}

	@Test(expected = NexuException.class)
	public void testInputValidation1() throws Exception {

		UIDisplay display = mock(UIDisplay.class);

		NexuAPI api = mock(NexuAPI.class, withSettings().verboseLogging());

		SignatureRequest req = new SignatureRequest();
		req.setDigestAlgorithm(DigestAlgorithm.SHA256);

		SignatureFlow flow = new SignatureFlow(display);
		flow.process(api, req);

	}

	@Test(expected = NexuException.class)
	public void testInputValidation2() throws Exception {

		UIDisplay display = mock(UIDisplay.class);

		NexuAPI api = mock(NexuAPI.class, withSettings().verboseLogging());

		SignatureRequest req = new SignatureRequest();
		req.setToBeSigned(new ToBeSigned());

		SignatureFlow flow = new SignatureFlow(display);
		flow.process(api, req);

	}

	@Test(expected = NexuException.class)
	public void testInputValidation3() throws Exception {

		UIDisplay display = mock(UIDisplay.class);

		NexuAPI api = mock(NexuAPI.class, withSettings().verboseLogging());

		SignatureRequest req = new SignatureRequest();
		req.setToBeSigned(new ToBeSigned("hello".getBytes()));

		SignatureFlow flow = new SignatureFlow(display);
		flow.process(api, req);

	}

	private static class NoUIOperationFactory extends BasicOperationFactory {
		
		@SuppressWarnings("rawtypes")
		private final Operation successOperation;
		
		public NoUIOperationFactory() {
			this.successOperation = mock(Operation.class);
			when(successOperation.perform()).thenReturn(new OperationResult<Void>(OperationStatus.SUCCESS));
		}
		
		@Override
		@SuppressWarnings("unchecked")
		public <R, T extends Operation<R>> Operation<R> getOperation(Class<T> clazz, Object... params) {
			if(UIOperation.class.isAssignableFrom(clazz)) {
				return successOperation;
			} else {
				return super.getOperation(clazz, params);
			}
		}
	}
}
