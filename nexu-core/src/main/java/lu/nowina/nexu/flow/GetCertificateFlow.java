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

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.esig.dss.token.DSSPrivateKeyEntry;
import eu.europa.esig.dss.token.SignatureTokenConnection;
import eu.europa.esig.dss.x509.CertificateToken;
import lu.nowina.nexu.api.Execution;
import lu.nowina.nexu.api.GetCertificateRequest;
import lu.nowina.nexu.api.GetCertificateResponse;
import lu.nowina.nexu.api.Match;
import lu.nowina.nexu.api.NexuAPI;
import lu.nowina.nexu.api.Product;
import lu.nowina.nexu.api.ProductAdapter;
import lu.nowina.nexu.api.TokenId;
import lu.nowina.nexu.api.flow.BasicOperationStatus;
import lu.nowina.nexu.api.flow.Operation;
import lu.nowina.nexu.api.flow.OperationResult;
import lu.nowina.nexu.flow.operation.AdvancedCreationFeedbackOperation;
import lu.nowina.nexu.flow.operation.ConfigureProductOperation;
import lu.nowina.nexu.flow.operation.CoreOperationStatus;
import lu.nowina.nexu.flow.operation.CreateTokenOperation;
import lu.nowina.nexu.flow.operation.GetMatchingProductAdaptersOperation;
import lu.nowina.nexu.flow.operation.GetTokenConnectionOperation;
import lu.nowina.nexu.flow.operation.SaveProductOperation;
import lu.nowina.nexu.flow.operation.SelectPrivateKeyOperation;
import lu.nowina.nexu.flow.operation.TokenOperationResultKey;
import lu.nowina.nexu.view.core.UIDisplay;
import lu.nowina.nexu.view.core.UIOperation;

class GetCertificateFlow extends AbstractCoreFlow<GetCertificateRequest, GetCertificateResponse> {

    static final Logger logger = LoggerFactory.getLogger(GetCertificateFlow.class);

    public GetCertificateFlow(final UIDisplay display, final NexuAPI api) {
        super(display, api);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Execution<GetCertificateResponse> process(final NexuAPI api, final GetCertificateRequest req) throws Exception {
    	SignatureTokenConnection token = null;
    	try {
    		Product defaultProduct = api.getAppConfig().getDefaultProduct();
    		while (true) {
    			final Product selectedProduct;
    			if(defaultProduct != null) {
    				selectedProduct = defaultProduct;
    				defaultProduct = null;
    			} else {
    				final Object[] params = {
    						api.getAppConfig().getApplicationName(), api.detectCards(), api.detectProducts(), api
    				};
    				final Operation<Product> operation = this.getOperationFactory().getOperation(UIOperation.class, "/fxml/product-selection.fxml", params);
    				final OperationResult<Product> selectProductOperationResult = operation.perform();
    				if (selectProductOperationResult.getStatus().equals(BasicOperationStatus.SUCCESS)) {
    					selectedProduct = selectProductOperationResult.getResult();
    				} else {
    					return this.handleErrorOperationResult(selectProductOperationResult);
    				}
    			}

    			final OperationResult<List<Match>> getMatchingCardAdaptersOperationResult = this.getOperationFactory()
    					.getOperation(GetMatchingProductAdaptersOperation.class, Arrays.asList(selectedProduct), api).perform();
    			if (getMatchingCardAdaptersOperationResult.getStatus().equals(BasicOperationStatus.SUCCESS)) {
    				List<Match> matchingProductAdapters = getMatchingCardAdaptersOperationResult.getResult();

    				final OperationResult<List<Match>> configureProductOperationResult = this.getOperationFactory()
    						.getOperation(ConfigureProductOperation.class, matchingProductAdapters, api).perform();
    				if (configureProductOperationResult.getStatus().equals(BasicOperationStatus.SUCCESS)) {
    					matchingProductAdapters = configureProductOperationResult.getResult();
    					final OperationResult<Map<TokenOperationResultKey, Object>> createTokenOperationResult = this.getOperationFactory()
    							.getOperation(CreateTokenOperation.class, api, matchingProductAdapters).perform();
    					if (createTokenOperationResult.getStatus().equals(BasicOperationStatus.SUCCESS)) {
    						final Map<TokenOperationResultKey, Object> map = createTokenOperationResult.getResult();
    						final TokenId tokenId = (TokenId) map.get(TokenOperationResultKey.TOKEN_ID);

    						final OperationResult<SignatureTokenConnection> getTokenConnectionOperationResult = this.getOperationFactory()
    								.getOperation(GetTokenConnectionOperation.class, api, tokenId).perform();
    						if (getTokenConnectionOperationResult.getStatus().equals(BasicOperationStatus.SUCCESS)) {
    							token = getTokenConnectionOperationResult.getResult();

    							final Product product = (Product) map.get(TokenOperationResultKey.SELECTED_PRODUCT);
    							final ProductAdapter productAdapter = (ProductAdapter) map.get(TokenOperationResultKey.SELECTED_PRODUCT_ADAPTER);
    							final OperationResult<DSSPrivateKeyEntry> selectPrivateKeyOperationResult = this.getOperationFactory()
    									.getOperation(SelectPrivateKeyOperation.class, token, api, product, productAdapter, req.getCertificateFilter()).perform();
    							if (selectPrivateKeyOperationResult.getStatus().equals(BasicOperationStatus.SUCCESS)) {
    								final DSSPrivateKeyEntry key = selectPrivateKeyOperationResult.getResult();

    								if ((Boolean) map.get(TokenOperationResultKey.ADVANCED_CREATION)) {
    									this.getOperationFactory().getOperation(AdvancedCreationFeedbackOperation.class, api, map).perform();
    								}

    								this.getOperationFactory().getOperation(SaveProductOperation.class, productAdapter, product, api).perform();

    								final GetCertificateResponse resp = new GetCertificateResponse();
    								resp.setTokenId(tokenId);

    								final CertificateToken certificate = key.getCertificate();
    								resp.setCertificate(certificate);
    								resp.setKeyId(certificate.getDSSIdAsString());
    								resp.setEncryptionAlgorithm(certificate.getEncryptionAlgorithm());

    								final CertificateToken[] certificateChain = key.getCertificateChain();
    								if (certificateChain != null) {
    									resp.setCertificateChain(certificateChain);
    								}

    								if (productAdapter.canReturnSuportedDigestAlgorithms(product)) {
    									resp.setSupportedDigests(productAdapter.getSupportedDigestAlgorithms(product));
    									resp.setPreferredDigest(productAdapter.getPreferredDigestAlgorithm(product));
    								}

    								if (api.getAppConfig().isEnablePopUps() && api.getAppConfig().isEnableInformativePopUps()) {
    									this.getOperationFactory().getOperation(UIOperation.class, "/fxml/message.fxml", new Object[] {
    											"certificates.flow.finished"
    									}).perform();
    								}
    								return new Execution<GetCertificateResponse>(resp);
    							} else if (selectPrivateKeyOperationResult.getStatus().equals(CoreOperationStatus.BACK)) {
    								continue;
    							} else {
    								return this.handleErrorOperationResult(selectPrivateKeyOperationResult);
    							}
    						} else {
    							return this.handleErrorOperationResult(getTokenConnectionOperationResult);
    						}
    					} else {
    						return this.handleErrorOperationResult(createTokenOperationResult);
    					}
    				} else {
    					return this.handleErrorOperationResult(configureProductOperationResult);
    				}
    			} else {
    				return this.handleErrorOperationResult(getMatchingCardAdaptersOperationResult);
    			}
    		}
    	} catch (final Exception e) {
    		logger.error("Flow error", e);
    		throw this.handleException(e);
    	} finally {
    		if (token != null) {
    			if (req.isCloseToken()) {
    				try {
    					token.close();
    				} catch (final Exception e) {
    					logger.error("Exception when closing token", e);
    				}
    			}
    		}
    	}
    }
}
