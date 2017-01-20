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
package lu.nowina.nexu.api;

import java.util.List;

import eu.europa.esig.dss.DigestAlgorithm;
import eu.europa.esig.dss.token.DSSPrivateKeyEntry;
import eu.europa.esig.dss.token.PasswordInputCallback;
import eu.europa.esig.dss.token.SignatureTokenConnection;
import lu.nowina.nexu.api.flow.Operation;
import lu.nowina.nexu.api.flow.FutureOperationInvocation;

/**
 * A <code>ProductAdapter</code> can manage some specific {@link Product}s.
 * 
 * @author David Naramski
 */
public interface ProductAdapter {

	/**
	 * Queries the <code>ProductAdapter</code> to know if he is able to manage the given {@link Product}.
	 * @param product The target product.
	 * @return <code>true</code> if the <code>ProductAdapter</code> can manage the given {@link Product}.
	 */
	boolean accept(Product product);

	/**
	 * Returns a label for the given <code>product</code>.
	 * @param api The unique instance of {@link NexuAPI}.
	 * @param product The target product.
	 * @param callback Password input callback.
	 * @return A label for the given <code>product</code>.
	 */
	String getLabel(NexuAPI api, Product product, PasswordInputCallback callback);
	
	/**
	 * Returns a label for the given <code>product</code>.
	 * @param api The unique instance of {@link NexuAPI}.
	 * @param product The target product.
	 * @param callback Password input callback.
	 * @param messageCallback Message display callback.
	 * @return A label for the given <code>product</code>.
	 */
	String getLabel(NexuAPI api, Product product, PasswordInputCallback callback, MessageDisplayCallback messageCallback);
	
	/**
	 * Returns <code>true</code> if this product adapter supports {@link MessageDisplayCallback} for the given <code>product</code>.
	 * @return <code>true</code> if this product adapter supports {@link MessageDisplayCallback} for the given <code>product</code>.
	 */
	boolean supportMessageDisplayCallback(Product product);
	
	/**
	 * Creates a {@link SignatureTokenConnection} for the given product.
	 * @param api The unique instance of {@link NexuAPI}.
	 * @param product The target product.
	 * @param callback Password input callback.
	 * @return A {@link SignatureTokenConnection} for the given product.
	 */
	SignatureTokenConnection connect(NexuAPI api, Product product, PasswordInputCallback callback);

	/**
	 * Creates a {@link SignatureTokenConnection} for the given product.
	 * @param api The unique instance of {@link NexuAPI}.
	 * @param product The target product.
	 * @param callback Password input callback.
	 * @param messageCallback Message display callback.
	 * @return A {@link SignatureTokenConnection} for the given product.
	 */
	SignatureTokenConnection connect(NexuAPI api, Product product, PasswordInputCallback callback,
			MessageDisplayCallback messageCallback);
	
	/**
	 * Returns <code>true</code> if this product adapter can return identity information for the given <code>product</code>.
	 * @return <code>true</code> if this product adapter can return identity information for the given <code>product</code>.
	 */
	boolean canReturnIdentityInfo(Product product);
	
	/**
	 * Returns the identity information using the given <code>token</code>.
	 * @param token The token to use to get the identity information.
	 * @return The identity information using the given <code>token</code>.
	 */
	GetIdentityInfoResponse getIdentityInfo(SignatureTokenConnection token);
	
	/**
	 * Returns <code>true</code> if this product adapter supports {@link CertificateFilter} for the given <code>product</code>.
	 * @return <code>true</code> if this product adapter supports {@link CertificateFilter} for the given <code>product</code>.
	 */
	boolean supportCertificateFilter(Product product);
	
	/**
	 * Returns the keys of <code>token</code> matching the <code>certificateFilter</code>.
	 * @param token The token to use to retrieve the keys.
	 * @param certificateFilter Filter that must be matched by returned keys.
	 * @return The keys of <code>token</code> matching the <code>certificateFilter</code>.
	 */
	List<DSSPrivateKeyEntry> getKeys(SignatureTokenConnection token, CertificateFilter certificateFilter);
	
	/**
	 * Returns <code>true</code> if this product adapter can return supported digest algorithms for the given <code>product</code>.
	 * @param product The product for which one would like to retrieve the supported digest algorithms.
	 * @return <code>true</code> if this product adapter can return supported digest algorithms for the given <code>product</code>.
	 */
	boolean canReturnSuportedDigestAlgorithms(Product product);
	
	/**
	 * Returns the list of supported digest algorithms for the given <code>product</code>.
	 * @param product The product for which one would like to retrieve the supported digest algorithms.
	 * @return The list of supported digest algorithms for the given <code>product</code>.
	 */
	List<DigestAlgorithm> getSupportedDigestAlgorithms(Product product);
	
	/**
	 * Returns the preferred digest algorithm for the given <code>product</code>.
	 * @param product The product for which one would like to retrieve the preferred digest algorithm.
	 * @return The preferred digest algorithm for the given <code>product</code>.
	 */
	DigestAlgorithm getPreferredDigestAlgorithm(Product product);
	
	/**
	 * Returns the specification of the operation to call to configure <code>product</code>.
	 * <p>Returned operation must return a configured product.
	 * @param api The unique instance of {@link NexuAPI}.
	 * @param product The product for which one would like to retrieve the configuration {@link Operation}.
	 * @return The specification of the operation to call to configure <code>product</code>.
	 */
	FutureOperationInvocation<Product> getConfigurationOperation(NexuAPI api, Product product);
	
	/**
	 * Returns the specification of the operation to call to save the configured <code>product</code>.
	 * <p>Returned operation must return a boolean indicating whether save operation was successful or not.
	 * @param api The unique instance of {@link NexuAPI}.
	 * @param product The product for which one would like to retrieve the save {@link Operation}.
	 * @return The specification of the operation to call to save the configured <code>product</code>.
	 */
	FutureOperationInvocation<Boolean> getSaveOperation(NexuAPI api, Product product);
	
	/**
	 * Each <code>ProductAdapter</code> is given the capability to enrich the systray menu with an item
	 * specific to it. This method is used to retrieve this item.
	 * @return The menu item specific to this <code>ProductAdapter</code> or <code>null</code> if none.
	 */
	SystrayMenuItem getExtensionSystrayMenuItem();
	
	/**
	 * Detects products that will <strong>maybe</strong> be accepted by this <code>ProductAdapter</code>.
	 * @return Products that will <strong>maybe</strong> be accepted by this <code>ProductAdapter</code>.
	 */
	List<? extends Product> detectProducts();
}
