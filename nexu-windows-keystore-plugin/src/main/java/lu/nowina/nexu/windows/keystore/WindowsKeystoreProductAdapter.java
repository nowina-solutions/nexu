/**
 * © Nowina Solutions, 2015-2016
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
package lu.nowina.nexu.windows.keystore;

import java.util.ArrayList;
import java.util.List;

import eu.europa.esig.dss.DigestAlgorithm;
import eu.europa.esig.dss.token.DSSPrivateKeyEntry;
import eu.europa.esig.dss.token.MSCAPISignatureToken;
import eu.europa.esig.dss.token.PasswordInputCallback;
import eu.europa.esig.dss.token.SignatureTokenConnection;
import lu.nowina.nexu.api.CertificateFilter;
import lu.nowina.nexu.api.CertificateFilterHelper;
import lu.nowina.nexu.api.GetIdentityInfoResponse;
import lu.nowina.nexu.api.MessageDisplayCallback;
import lu.nowina.nexu.api.NexuAPI;
import lu.nowina.nexu.api.Product;
import lu.nowina.nexu.api.ProductAdapter;
import lu.nowina.nexu.api.SystrayMenuItem;
import lu.nowina.nexu.api.flow.FutureOperationInvocation;
import lu.nowina.nexu.api.flow.NoOpFutureOperationInvocation;

/**
 * 
 * Product adapter for {@link WindowsKeystore}.
 * 
 * @author simon.ghisalberti
 *
 */
public class WindowsKeystoreProductAdapter implements ProductAdapter {

	public WindowsKeystoreProductAdapter() {
		super();
	}

	@Override
	public boolean accept(Product product) {
		return (product instanceof WindowsKeystore);
	}

	@Override
	public String getLabel(NexuAPI api, Product product, PasswordInputCallback callback) {
		return product.getLabel();
	}

	@Override
	public String getLabel(NexuAPI api, Product product, PasswordInputCallback callback, MessageDisplayCallback messageCallback) {
		throw new IllegalStateException("This product adapter does not support message display callback.");
	}

	@Override
	public boolean supportMessageDisplayCallback(Product product) {
		return false;
	}

	@Override
	public SignatureTokenConnection connect(NexuAPI api, Product product, PasswordInputCallback callback) {
		return new MSCAPISignatureToken();
	}

	@Override
	public SignatureTokenConnection connect(NexuAPI api, Product product, PasswordInputCallback callback, MessageDisplayCallback messageCallback) {
		throw new IllegalStateException("This product adapter does not support message display callback.");
	}

	@Override
	public boolean canReturnIdentityInfo(Product product) {
		return false;
	}

	@Override
	public GetIdentityInfoResponse getIdentityInfo(SignatureTokenConnection token) {
		throw new IllegalStateException("This product adapter cannot return identity information.");
	}

	@Override
	public boolean supportCertificateFilter(Product product) {
		return true;
	}

	@Override
	public List<DSSPrivateKeyEntry> getKeys(SignatureTokenConnection token, CertificateFilter certificateFilter) {
		return new CertificateFilterHelper().filterKeys(token, certificateFilter);
	}

	@Override
	public boolean canReturnSuportedDigestAlgorithms(Product product) {
		return false;
	}

	@Override
	public List<DigestAlgorithm> getSupportedDigestAlgorithms(Product product) {
		throw new IllegalStateException("This product adapter cannot return list of supported digest algorithms.");
	}

	@Override
	public DigestAlgorithm getPreferredDigestAlgorithm(Product product) {
		throw new IllegalStateException("This product adapter cannot return list of supported digest algorithms.");
	}

	@Override
	public FutureOperationInvocation<Product> getConfigurationOperation(NexuAPI api, Product product) {
		return new NoOpFutureOperationInvocation<Product>(product);
	}

	@Override
	public FutureOperationInvocation<Boolean> getSaveOperation(NexuAPI api, Product product) {
		return new NoOpFutureOperationInvocation<Boolean>(true);
	}

	@Override
	public SystrayMenuItem getExtensionSystrayMenuItem() {
		return null;
	}

	@Override
	public List<Product> detectProducts() {
		final List<Product> products = new ArrayList<>();
		products.add(new WindowsKeystore());
		return products;
	}

}
