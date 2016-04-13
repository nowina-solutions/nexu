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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import eu.europa.esig.dss.DigestAlgorithm;
import eu.europa.esig.dss.token.DSSPrivateKeyEntry;
import eu.europa.esig.dss.token.JKSSignatureToken;
import eu.europa.esig.dss.token.PasswordInputCallback;
import eu.europa.esig.dss.token.Pkcs12SignatureToken;
import eu.europa.esig.dss.token.SignatureTokenConnection;
import lu.nowina.nexu.NexuException;
import lu.nowina.nexu.api.CertificateFilter;
import lu.nowina.nexu.api.ConfiguredKeystore;
import lu.nowina.nexu.api.GetIdentityInfoResponse;
import lu.nowina.nexu.api.NewKeystore;
import lu.nowina.nexu.api.NexuAPI;
import lu.nowina.nexu.api.Product;
import lu.nowina.nexu.api.ProductAdapter;

/**
 * Product adapter for {@link ConfiguredKeystore}.
 *
 * @author Jean Lepropre (jean.lepropre@nowina.lu)
 */
public class KeystoreProductAdapter implements ProductAdapter {

	public KeystoreProductAdapter() {
		super();
	}

	@Override
	public boolean accept(Product product) {
		return (product instanceof ConfiguredKeystore) || (product instanceof NewKeystore);
	}

	@Override
	public SignatureTokenConnection connect(NexuAPI api, Product product, PasswordInputCallback callback) {
		if (product instanceof NewKeystore) {
			throw new IllegalArgumentException("Given product was not configured!");
		}
		final ConfiguredKeystore configuredKeystore = (ConfiguredKeystore) product;
		try {
			switch(configuredKeystore.getType()) {
			case PKCS12:
				return new Pkcs12SignatureToken(new String(callback.getPassword()),
						new URL(configuredKeystore.getUrl()).openStream());
			case JKS:
				return new JKSSignatureToken(new URL(configuredKeystore.getUrl()).openStream(),
						new String(callback.getPassword()));
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
	public URL getFXMLConfigurationURL(Product product) {
		if (product instanceof NewKeystore) {
			return this.getClass().getResource("/fxml/configure-keystore.fxml");
		} else {
			return null;
		}
	}

	@Override
	public void saveProductConfiguration(NexuAPI api, Product product) {
		// TODO Auto-generated method stub
	}
}
