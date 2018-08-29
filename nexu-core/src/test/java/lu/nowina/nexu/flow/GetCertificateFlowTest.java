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

import java.security.KeyStore.PasswordProtection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import eu.europa.esig.dss.token.JKSSignatureToken;
import eu.europa.esig.dss.token.PrefilledPasswordCallback;
import eu.europa.esig.dss.token.SignatureTokenConnection;
import lu.nowina.nexu.AbstractConfigureLoggerTest;
import lu.nowina.nexu.api.AppConfig;
import lu.nowina.nexu.api.ConfiguredKeystore;
import lu.nowina.nexu.api.DetectedCard;
import lu.nowina.nexu.api.Execution;
import lu.nowina.nexu.api.GetCertificateRequest;
import lu.nowina.nexu.api.GetCertificateResponse;
import lu.nowina.nexu.api.KeystoreType;
import lu.nowina.nexu.api.Match;
import lu.nowina.nexu.api.NewKeystore;
import lu.nowina.nexu.api.NexuAPI;
import lu.nowina.nexu.api.Product;
import lu.nowina.nexu.api.ProductAdapter;
import lu.nowina.nexu.api.ScAPI;
import lu.nowina.nexu.api.TokenId;
import lu.nowina.nexu.api.flow.BasicOperationStatus;
import lu.nowina.nexu.api.flow.NoOpFutureOperationInvocation;
import lu.nowina.nexu.api.flow.Operation;
import lu.nowina.nexu.api.flow.OperationFactory;
import lu.nowina.nexu.api.flow.OperationResult;
import lu.nowina.nexu.flow.operation.BasicOperationFactory;
import lu.nowina.nexu.flow.operation.ConfigureProductOperation;
import lu.nowina.nexu.flow.operation.CoreOperationStatus;
import lu.nowina.nexu.flow.operation.CreateTokenOperation;
import lu.nowina.nexu.flow.operation.GetMatchingProductAdaptersOperation;
import lu.nowina.nexu.keystore.KeystoreProductAdapter;
import lu.nowina.nexu.view.core.UIDisplay;
import lu.nowina.nexu.view.core.UIOperation;

public class GetCertificateFlowTest extends AbstractConfigureLoggerTest {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Test
    public void testNewKeystore() throws Exception {
        final UIDisplay display = mock(UIDisplay.class);
        when(display.getPasswordInputCallback())
        .thenReturn(new PrefilledPasswordCallback(new PasswordProtection("password".toCharArray())));

        final NexuAPI api = mock(NexuAPI.class);
        final AppConfig appConfig = new AppConfig();
        appConfig.setEnablePopUps(true);
        when(api.getAppConfig()).thenReturn(appConfig);

        final Product selectedProduct = new NewKeystore();
        when(api.detectCards()).thenReturn(Collections.emptyList());
        when(api.matchingProductAdapters(selectedProduct)).thenReturn(
                Arrays.asList(new Match(new KeystoreProductAdapter(this.tempFolder.getRoot()), selectedProduct)));
        final Collection<SignatureTokenConnection> coll = new ArrayList<>();
        when(api.registerTokenConnection(any())).then(new Answer<TokenId>() {
            @Override
            public TokenId answer(final InvocationOnMock invocation) throws Throwable {
                coll.add(invocation.getArgumentAt(0, SignatureTokenConnection.class));
                return new TokenId("id");
            }
        });
        when(api.getTokenConnection(new TokenId("id"))).then(new Answer<SignatureTokenConnection>() {
            @Override
            public SignatureTokenConnection answer(final InvocationOnMock invocation) throws Throwable {
                return coll.iterator().next();
            }
        });

        final ConfiguredKeystore configuredProduct = new ConfiguredKeystore();
        configuredProduct.setType(KeystoreType.JKS);
        configuredProduct.setUrl(this.getClass().getResource("/keystore.jks").toString());
        configuredProduct.setToBeSaved(true);
        final OperationFactory operationFactory = new NoUIOperationFactory(selectedProduct, configuredProduct);
        ((NoUIOperationFactory) operationFactory).setDisplay(display);

        final GetCertificateFlow flow = new GetCertificateFlow(display, api);
        flow.setOperationFactory(operationFactory);
        final Execution<GetCertificateResponse> resp = flow.process(api, new GetCertificateRequest());

        try(final SignatureTokenConnection token = new JKSSignatureToken(
                this.getClass().getResourceAsStream("/keystore.jks"), new PasswordProtection("password".toCharArray()))){
            Assert.assertNotNull(resp);
            Assert.assertTrue(resp.isSuccess());
            Assert.assertNotNull(resp.getResponse());
            Assert.assertEquals(token.getKeys().get(0).getCertificate(), resp.getResponse().getCertificate());
            Assert.assertEquals(token.getKeys().get(0).getEncryptionAlgorithm(),
                    resp.getResponse().getEncryptionAlgorithm());
            Assert.assertEquals(new TokenId("id"), resp.getResponse().getTokenId());
            Assert.assertEquals(token.getKeys().get(0).getCertificate().getDSSIdAsString(), resp.getResponse().getKeyId());
            Assert.assertNull(resp.getResponse().getPreferredDigest());
            Assert.assertNull(resp.getResponse().getSupportedDigests());
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testNotRecognizedRequestSupport() throws Exception {
        final UIDisplay display = mock(UIDisplay.class);

        final NexuAPI api = mock(NexuAPI.class);
        final DetectedCard product = new DetectedCard("atr", 0);
        when(api.detectCards()).thenReturn(Arrays.asList(product));

        final AppConfig appConfig = new AppConfig();
        appConfig.setAdvancedModeAvailable(true);
        appConfig.setEnablePopUps(true);
        appConfig.setTicketUrl("http://random.url");
        appConfig.setApplicationName("Dummy App");
        when(api.getAppConfig()).thenReturn(appConfig);

        final OperationFactory operationFactory = mock(OperationFactory.class);

        final Operation<Object> selectProductOperation = mock(Operation.class);
        when(selectProductOperation.perform()).thenReturn(new OperationResult<Object>(product));
        when(operationFactory.getOperation(eq(UIOperation.class), eq("/fxml/product-selection.fxml"),
                any(Object[].class))).thenReturn(selectProductOperation);

        final Operation<List<Match>> getMatchingCardAdaptersOperation = mock(Operation.class);
        when(getMatchingCardAdaptersOperation.perform())
        .thenReturn(new OperationResult<List<Match>>(Collections.emptyList()));
        when(operationFactory.getOperation(GetMatchingProductAdaptersOperation.class, Arrays.asList(product), api))
        .thenReturn(getMatchingCardAdaptersOperation);

        final Operation<List<Match>> configureProductOperation = mock(Operation.class);
        when(configureProductOperation.perform()).thenReturn(new OperationResult<List<Match>>(Collections.emptyList()));
        when(operationFactory.getOperation(ConfigureProductOperation.class, Collections.emptyList(), api))
        .thenReturn(configureProductOperation);

        final CreateTokenOperation createTokenOperation = new CreateTokenOperation();
        createTokenOperation.setParams(api, Collections.emptyList());
        createTokenOperation.setDisplay(display);
        createTokenOperation.setOperationFactory(operationFactory);
        when(operationFactory.getOperation(eq(CreateTokenOperation.class), eq("/fxml/api-selection.fxml"), any(Object[].class)))
        .thenReturn(createTokenOperation);
        when(operationFactory.getOperation(eq(CreateTokenOperation.class), eq(api), any(Object.class)))
        .thenReturn(createTokenOperation);

        final Operation<Object> returnFalseOperation = mock(Operation.class);
        when(returnFalseOperation.perform()).thenReturn(new OperationResult<Object>(false));
        when(operationFactory.getOperation(eq(UIOperation.class), eq("/fxml/unsupported-product.fxml"),
                any(Object[].class))).thenReturn(returnFalseOperation);

        final Operation<Object> returnScAPIOperation = mock(Operation.class);
        when(returnScAPIOperation.perform()).thenReturn(new OperationResult<Object>(ScAPI.PKCS_11));
        when(operationFactory.getOperation(eq(UIOperation.class), eq("/fxml/api-selection.fxml"),
                eq("unsuported.product.message"), any(String.class))).thenReturn(returnFalseOperation);

        when(operationFactory.getOperation(eq(UIOperation.class), eq("/fxml/message.fxml"),
                eq("unsuported.product.message"), any(String.class))).thenReturn(returnFalseOperation);



        when(operationFactory.getOperation(eq(UIOperation.class), eq("/fxml/message.fxml"),
                any(Object[].class))).thenReturn(returnScAPIOperation);

        final OperationResult<Object> or =  mock(OperationResult.class);
        when(or.getStatus()).thenReturn(BasicOperationStatus.USER_CANCEL);
        when(or.getResult()).thenReturn(Boolean.FALSE);
        final Operation<Object> returnOtherFalseOperation = mock(Operation.class);
        when(returnFalseOperation.perform()).thenReturn(or);
        when(operationFactory.getOperation(eq(UIOperation.class), eq("/fxml/pkcs11-params.fxml"),
                any(Object[].class))).thenReturn(returnOtherFalseOperation);




        final Operation<Object> successOperation = mock(Operation.class);
        when(successOperation.perform()).thenReturn(new OperationResult<Object>(BasicOperationStatus.SUCCESS));
        when(operationFactory.getOperation(eq(UIOperation.class), eq("/fxml/provide-feedback.fxml"),
                any(Object[].class))).thenReturn(successOperation);

        final GetCertificateFlow flow = new GetCertificateFlow(display, api);
        flow.setOperationFactory(operationFactory);

        final GetCertificateRequest req = new GetCertificateRequest();
        final Execution<GetCertificateResponse> resp = flow.process(api, req);
        Assert.assertNotNull(resp);
        Assert.assertFalse(resp.isSuccess());
        Assert.assertEquals(CoreOperationStatus.UNSUPPORTED_PRODUCT.getCode(), resp.getError());
    }

    @Test
    public void testCardRecognized() throws Exception {
        final UIDisplay display = mock(UIDisplay.class);
        final ProductAdapter adapter = mock(ProductAdapter.class);

        final SignatureTokenConnection token = new JKSSignatureToken(
                this.getClass().getResourceAsStream("/keystore.jks"), new PasswordProtection("password".toCharArray()));

        final NexuAPI api = mock(NexuAPI.class);
        final AppConfig appConfig = new AppConfig();
        appConfig.setEnablePopUps(true);
        when(api.getAppConfig()).thenReturn(appConfig);
        final DetectedCard detectedCard = new DetectedCard("atr", 0);
        when(adapter.getConfigurationOperation(api, detectedCard))
        .thenReturn(new NoOpFutureOperationInvocation<Product>(detectedCard));
        when(adapter.getSaveOperation(api, detectedCard)).thenReturn(new NoOpFutureOperationInvocation<Boolean>(true));

        when(api.detectCards()).thenReturn(Arrays.asList(detectedCard));
        when(api.matchingProductAdapters(detectedCard)).thenReturn(Arrays.asList(new Match(adapter, detectedCard)));
        when(api.registerTokenConnection(token)).thenReturn(new TokenId("id"));
        when(api.getTokenConnection(new TokenId("id"))).thenReturn(token);
        when(adapter.connect(eq(api), eq(detectedCard), any())).thenReturn(token);

        final OperationFactory operationFactory = new NoUIOperationFactory(detectedCard, null);
        ((NoUIOperationFactory) operationFactory).setDisplay(display);

        final GetCertificateFlow flow = new GetCertificateFlow(display, api);
        flow.setOperationFactory(operationFactory);
        final Execution<GetCertificateResponse> resp = flow.process(api, new GetCertificateRequest());
        final SignatureTokenConnection testToken = new JKSSignatureToken(
                this.getClass().getResourceAsStream("/keystore.jks"), new PasswordProtection("password".toCharArray()));
        Assert.assertNotNull(resp);
        Assert.assertTrue(resp.isSuccess());
        Assert.assertNotNull(resp.getResponse());
        Assert.assertEquals(testToken.getKeys().get(0).getCertificate(), resp.getResponse().getCertificate());
        Assert.assertEquals(testToken.getKeys().get(0).getEncryptionAlgorithm(),
                resp.getResponse().getEncryptionAlgorithm());
        Assert.assertEquals(new TokenId("id"), resp.getResponse().getTokenId());
        Assert.assertEquals(testToken.getKeys().get(0).getCertificate().getDSSIdAsString(),
                resp.getResponse().getKeyId());
        Assert.assertNull(resp.getResponse().getPreferredDigest());
        Assert.assertNull(resp.getResponse().getSupportedDigests());
    }

    private static class NoUIOperationFactory extends BasicOperationFactory {

        @SuppressWarnings("rawtypes")
        private final Operation successOperation;
        @SuppressWarnings("rawtypes")
        private final Operation selectedProductOperation;
        @SuppressWarnings("rawtypes")
        private final Operation configureProductOperation;

        public NoUIOperationFactory(final Product selectedProduct, final Product configuredProduct) {
            this.successOperation = mock(Operation.class);
            when(this.successOperation.perform()).thenReturn(new OperationResult<Void>(BasicOperationStatus.SUCCESS));
            this.selectedProductOperation = mock(Operation.class);
            when(this.selectedProductOperation.perform()).thenReturn(new OperationResult<Product>(selectedProduct));

            if (configuredProduct != null) {
                this.configureProductOperation = mock(Operation.class);
                when(this.configureProductOperation.perform()).thenReturn(new OperationResult<Product>(configuredProduct));
            } else {
                this.configureProductOperation = null;
            }
        }

        @Override
        @SuppressWarnings("unchecked")
        public <R, T extends Operation<R>> Operation<R> getOperation(final Class<T> clazz, final Object... params) {
            if (UIOperation.class.isAssignableFrom(clazz)) {
                switch ((String) params[0]) {
                    case "/fxml/product-selection.fxml":
                        return this.selectedProductOperation;
                    case "/fxml/configure-keystore.fxml":
                        return this.configureProductOperation;
                    default:
                        return this.successOperation;
                }
            } else {
                return super.getOperation(clazz, params);
            }
        }
    }
}
