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
package lu.nowina.nexu.api;

import java.util.List;

import eu.europa.esig.dss.DigestAlgorithm;
import eu.europa.esig.dss.token.PasswordInputCallback;
import eu.europa.esig.dss.token.SignatureTokenConnection;

/**
 * Convenient base class for {@link ProductAdapter}s supporting {@link DetectedCard}s.
 *
 * @author Jean Lepropre (jean.lepropre@nowina.lu)
 */
public abstract class AbstractCardProductAdapter implements ProductAdapter {

	public AbstractCardProductAdapter() {
		super();
	}

	@Override
	public final boolean accept(Product product) {
		return (product instanceof DetectedCard) && accept((DetectedCard) product);
	}

	protected abstract boolean accept(DetectedCard card);
	
	@Override
	public final SignatureTokenConnection connect(NexuAPI api, Product product, PasswordInputCallback callback) {
		return connect(api, (DetectedCard) product, callback);
	}

	protected abstract SignatureTokenConnection connect(NexuAPI api, DetectedCard card, PasswordInputCallback callback);
	
	@Override
	public final boolean canReturnIdentityInfo(Product product) {
		return (product instanceof DetectedCard) && canReturnIdentityInfo((DetectedCard) product);
	}

	protected abstract boolean canReturnIdentityInfo(DetectedCard card);

	@Override
	public final boolean supportCertificateFilter(Product product) {
		return (product instanceof DetectedCard) && supportCertificateFilter((DetectedCard) product);
	}

	protected abstract boolean supportCertificateFilter(DetectedCard card);

	@Override
	public final boolean canReturnSuportedDigestAlgorithms(Product product) {
		return (product instanceof DetectedCard) && canReturnSuportedDigestAlgorithms((DetectedCard) product);
	}

	protected abstract boolean canReturnSuportedDigestAlgorithms(DetectedCard card);

	@Override
	public final List<DigestAlgorithm> getSupportedDigestAlgorithms(Product product) {
		return getSupportedDigestAlgorithms((DetectedCard) product);
	}

	protected abstract List<DigestAlgorithm> getSupportedDigestAlgorithms(DetectedCard card);
	
	@Override
	public final DigestAlgorithm getPreferredDigestAlgorithm(Product product) {
		return getPreferredDigestAlgorithm((DetectedCard) product);
	}

	protected abstract DigestAlgorithm getPreferredDigestAlgorithm(DetectedCard card);
}
