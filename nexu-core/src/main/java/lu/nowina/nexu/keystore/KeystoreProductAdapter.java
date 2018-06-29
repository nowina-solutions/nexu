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
package lu.nowina.nexu.keystore;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyStore.PasswordProtection;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import eu.europa.esig.dss.DSSException;
import eu.europa.esig.dss.DigestAlgorithm;
import eu.europa.esig.dss.MaskGenerationFunction;
import eu.europa.esig.dss.SignatureValue;
import eu.europa.esig.dss.ToBeSigned;
import eu.europa.esig.dss.token.DSSPrivateKeyEntry;
import eu.europa.esig.dss.token.JKSSignatureToken;
import eu.europa.esig.dss.token.PasswordInputCallback;
import eu.europa.esig.dss.token.Pkcs12SignatureToken;
import eu.europa.esig.dss.token.SignatureTokenConnection;
import lu.nowina.nexu.NexuException;
import lu.nowina.nexu.ProductDatabaseLoader;
import lu.nowina.nexu.api.CertificateFilter;
import lu.nowina.nexu.api.ConfiguredKeystore;
import lu.nowina.nexu.api.GetIdentityInfoResponse;
import lu.nowina.nexu.api.MessageDisplayCallback;
import lu.nowina.nexu.api.NewKeystore;
import lu.nowina.nexu.api.NexuAPI;
import lu.nowina.nexu.api.Product;
import lu.nowina.nexu.api.ProductAdapter;
import lu.nowina.nexu.api.SystrayMenuItem;
import lu.nowina.nexu.api.flow.FutureOperationInvocation;
import lu.nowina.nexu.api.flow.NoOpFutureOperationInvocation;
import lu.nowina.nexu.view.core.NonBlockingUIOperation;
import lu.nowina.nexu.view.core.UIOperation;

/**
 * Product adapter for {@link ConfiguredKeystore}.
 *
 * @author Jean Lepropre (jean.lepropre@nowina.lu)
 */
public class KeystoreProductAdapter implements ProductAdapter {

	private final File nexuHome;
	
	public KeystoreProductAdapter(final File nexuHome) {
		super();
		this.nexuHome = nexuHome;
	}

	@Override
	public boolean accept(Product product) {
		return (product instanceof ConfiguredKeystore) || (product instanceof NewKeystore);
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
		if (product instanceof NewKeystore) {
			throw new IllegalArgumentException("Given product was not configured!");
		}
		final ConfiguredKeystore configuredKeystore = (ConfiguredKeystore) product;
		return new KeystoreTokenProxy(configuredKeystore, callback);
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
		return false;
	}

	@Override
	public List<DSSPrivateKeyEntry> getKeys(SignatureTokenConnection token, CertificateFilter certificateFilter) {
		throw new IllegalStateException("This product adapter does not support certificate filter.");
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
	@SuppressWarnings("unchecked")
	public FutureOperationInvocation<Product> getConfigurationOperation(NexuAPI api, Product product) {
		if (product instanceof NewKeystore) {
			return UIOperation.getFutureOperationInvocation(UIOperation.class, "/fxml/configure-keystore.fxml", api.getAppConfig().getApplicationName());
		} else {
			return new NoOpFutureOperationInvocation<Product>(product);
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public FutureOperationInvocation<Boolean> getSaveOperation(NexuAPI api, Product product) {
		if (product instanceof NewKeystore) {
			throw new IllegalArgumentException("Given product was not configured!");
		} else {
			final ConfiguredKeystore keystore = (ConfiguredKeystore) product;
			if(keystore.isToBeSaved()) {
				return UIOperation.getFutureOperationInvocation(UIOperation.class, "/fxml/save-keystore.fxml",
					api.getAppConfig().getApplicationName(), this, keystore);
			} else {
				return new NoOpFutureOperationInvocation<Boolean>(true);
			}
		}
	}

	@Override
	public SystrayMenuItem getExtensionSystrayMenuItem() {
		return new SystrayMenuItem() {
			@Override
			public String getLabel() {
				return ResourceBundle.getBundle("bundles/nexu").getString("systray.menu.manage.keystores");
			}
			
			@Override
			public FutureOperationInvocation<Void> getFutureOperationInvocation() {
				return UIOperation.getFutureOperationInvocation(NonBlockingUIOperation.class, "/fxml/manage-keystores.fxml",
						getDatabase());
			}
		};
	}
	
	@Override
	public List<Product> detectProducts() {
		final List<Product> products = new ArrayList<>();
		products.addAll(getDatabase().getKeystores());
		products.add(new NewKeystore());
		return products;
	}

	private KeystoreDatabase getDatabase() {
		return ProductDatabaseLoader.load(KeystoreDatabase.class, new File(nexuHome, "keystore-database.xml"));
	}
	
	public void saveKeystore(final ConfiguredKeystore keystore) {
		getDatabase().add(keystore);
	}
	
	private static class KeystoreTokenProxy implements SignatureTokenConnection {

		private SignatureTokenConnection proxied;
		private final ConfiguredKeystore configuredKeystore;
		private final PasswordInputCallback callback;
				
		public KeystoreTokenProxy(ConfiguredKeystore configuredKeystore, PasswordInputCallback callback) {
			super();
			this.configuredKeystore = configuredKeystore;
			this.callback = callback;
		}

		private void initSignatureTokenConnection() {
			if(proxied != null) {
				return;
			}
			try {
				switch(configuredKeystore.getType()) {
				case PKCS12:
					proxied = new Pkcs12SignatureToken(new URL(configuredKeystore.getUrl()).openStream(),
							new PasswordProtection(callback.getPassword()));
					break;
				case JKS:
					proxied = new JKSSignatureToken(new URL(configuredKeystore.getUrl()).openStream(),
							new PasswordProtection(callback.getPassword()));
					break;
				default:
					throw new IllegalStateException("Unhandled keystore type: " + configuredKeystore.getType());
				}
			} catch (MalformedURLException e) {
				throw new NexuException(e);
			} catch (IOException e) {
				throw new NexuException(e);
			}
		}
		
		@Override
		public void close() {
			final SignatureTokenConnection stc = proxied;
			// Always nullify proxied even in case of exception when calling close()
			proxied = null;
			if(stc != null) {
				stc.close();
			}
		}

		@Override
		public List<DSSPrivateKeyEntry> getKeys() throws DSSException {
			initSignatureTokenConnection();
			return proxied.getKeys();
		}

		@Override
		public SignatureValue sign(ToBeSigned toBeSigned, DigestAlgorithm digestAlgorithm, DSSPrivateKeyEntry keyEntry)
				throws DSSException {
			initSignatureTokenConnection();
			return proxied.sign(toBeSigned, digestAlgorithm, keyEntry);
		}

		@Override
		public SignatureValue sign(ToBeSigned toBeSigned, DigestAlgorithm digestAlgorithm, MaskGenerationFunction mgf, DSSPrivateKeyEntry keyEntry) throws DSSException {
			initSignatureTokenConnection();
			return proxied.sign(toBeSigned, digestAlgorithm, mgf, keyEntry);
		}
	}
}
