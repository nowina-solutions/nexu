package lu.nowina.nexu.flow;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.esig.dss.SignatureValue;
import eu.europa.esig.dss.token.DSSPrivateKeyEntry;
import eu.europa.esig.dss.token.SignatureTokenConnection;
import eu.europa.esig.dss.x509.CertificateToken;
import lu.nowina.nexu.NexuException;
import lu.nowina.nexu.api.AuthenticateRequest;
import lu.nowina.nexu.api.AuthenticateResponse;
import lu.nowina.nexu.api.Execution;
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
import lu.nowina.nexu.flow.operation.CreateTokenOperation;
import lu.nowina.nexu.flow.operation.GetMatchingProductAdaptersOperation;
import lu.nowina.nexu.flow.operation.GetTokenConnectionOperation;
import lu.nowina.nexu.flow.operation.SelectPrivateKeyOperation;
import lu.nowina.nexu.flow.operation.SignOperation;
import lu.nowina.nexu.flow.operation.TokenOperationResultKey;
import lu.nowina.nexu.view.core.UIDisplay;
import lu.nowina.nexu.view.core.UIOperation;

public class AuthenticateFlow extends AbstractCoreFlow<AuthenticateRequest, AuthenticateResponse> {

	static final Logger logger = LoggerFactory.getLogger(AuthenticateFlow.class);

	public AuthenticateFlow(UIDisplay display, NexuAPI api) {
		super(display, api);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Execution<AuthenticateResponse> process(NexuAPI api, AuthenticateRequest req) throws Exception {

		// Challenge is required!
		if (req.getChallenge() == null) {
			throw new NexuException("Challenge is null");
		}

		SignatureTokenConnection token = null;
		try {
			
			
			Object[] params = { api.getAppConfig().getApplicationName(), api.detectCards(), api.detectProducts(), api };
			Operation<Product> operation = getOperationFactory().getOperation(UIOperation.class, "/fxml/product-selection.fxml", params);
			
			final OperationResult<Product> selectProductOperationResult = operation.perform();
			
			if (selectProductOperationResult.getStatus().equals(BasicOperationStatus.SUCCESS)) {
				
				// (1) Select product
				final Product selectedProduct = selectProductOperationResult.getResult();
				
				final OperationResult<List<Match>> getMatchingCardAdaptersOperationResult = 
						getOperationFactory().getOperation(GetMatchingProductAdaptersOperation.class, Arrays.asList(selectedProduct), api).perform();
				if (getMatchingCardAdaptersOperationResult.getStatus().equals(BasicOperationStatus.SUCCESS)) {
					
					// (2) Matching products
					List<Match> matchingProductAdapters = getMatchingCardAdaptersOperationResult.getResult();

					final OperationResult<List<Match>> configureProductOperationResult =
							getOperationFactory().getOperation(ConfigureProductOperation.class, matchingProductAdapters, api).perform();
					if (configureProductOperationResult.getStatus().equals(BasicOperationStatus.SUCCESS)) {
						
						matchingProductAdapters = configureProductOperationResult.getResult();

						final OperationResult<Map<TokenOperationResultKey, Object>> createTokenOperationResult =
								getOperationFactory().getOperation(CreateTokenOperation.class, api, matchingProductAdapters).perform();
						
						if (createTokenOperationResult.getStatus().equals(BasicOperationStatus.SUCCESS)) {
							
							final Map<TokenOperationResultKey, Object> map = createTokenOperationResult.getResult();
							final TokenId tokenId = (TokenId) map.get(TokenOperationResultKey.TOKEN_ID);

							final OperationResult<SignatureTokenConnection> getTokenConnectionOperationResult =
									getOperationFactory().getOperation(GetTokenConnectionOperation.class, api, tokenId).perform();
							if (getTokenConnectionOperationResult.getStatus().equals(BasicOperationStatus.SUCCESS)) {
								token = getTokenConnectionOperationResult.getResult();

								final Product product = (Product) map.get(TokenOperationResultKey.SELECTED_PRODUCT);
								final ProductAdapter productAdapter = (ProductAdapter) map.get(TokenOperationResultKey.SELECTED_PRODUCT_ADAPTER);
								final OperationResult<DSSPrivateKeyEntry> selectPrivateKeyOperationResult =
										getOperationFactory().getOperation(SelectPrivateKeyOperation.class, token, api, product, productAdapter).perform();
								
								if (selectPrivateKeyOperationResult.getStatus().equals(BasicOperationStatus.SUCCESS)) {

									final DSSPrivateKeyEntry key = selectPrivateKeyOperationResult.getResult();
									logger.info("Key " + key + " " + key.getCertificate().getSubjectDN() + " from " + key.getCertificate().getIssuerDN());
									
									// Sign challenge now
									final OperationResult<SignatureValue> signOperationResult = getOperationFactory().getOperation(SignOperation.class, token, req.getChallenge(), key.getCertificate().getDigestAlgorithm(), key).perform();
									
									if (signOperationResult.getStatus().equals(BasicOperationStatus.SUCCESS)) {
									
										final SignatureValue value = signOperationResult.getResult();
										logger.info("Authentication performed " + value);

										if ((Boolean) map.get(TokenOperationResultKey.ADVANCED_CREATION)) {
											getOperationFactory().getOperation(AdvancedCreationFeedbackOperation.class,
													api, map).perform();
										}
										
										if(api.getAppConfig().isEnablePopUps()) {
											getOperationFactory().getOperation(UIOperation.class, "/fxml/message.fxml", new Object[]{"authenticate.flow.finished"}).perform();
										}
										
										final CertificateToken certificate = key.getCertificate();
										
										return new Execution<AuthenticateResponse>(new AuthenticateResponse(certificate.getDSSIdAsString(), certificate, key.getCertificateChain(), value));
									} else {
										return handleErrorOperationResult(signOperationResult);
									}
								} else {
									return handleErrorOperationResult(selectPrivateKeyOperationResult);
								}
							} else {
								return handleErrorOperationResult(getTokenConnectionOperationResult);
							}
						} else {
							return handleErrorOperationResult(createTokenOperationResult);
						}
					} else {
						return handleErrorOperationResult(configureProductOperationResult);
					}
				} else {
					return handleErrorOperationResult(getMatchingCardAdaptersOperationResult);
				}
			} else {
				return handleErrorOperationResult(selectProductOperationResult);				
			}
			
		} catch (Exception e) {
			logger.error("Flow error", e);
			throw handleException(e);
		} finally {
			if (token != null) {
				try {
					token.close();
				} catch (final Exception e) {
					logger.error("Exception when closing token", e);
				}
			}
		}

	}

}
