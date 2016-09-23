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
package lu.nowina.nexu.generic;

import java.util.List;

import eu.europa.esig.dss.DigestAlgorithm;
import eu.europa.esig.dss.token.DSSPrivateKeyEntry;
import eu.europa.esig.dss.token.MSCAPISignatureToken;
import eu.europa.esig.dss.token.PasswordInputCallback;
import eu.europa.esig.dss.token.Pkcs11SignatureToken;
import eu.europa.esig.dss.token.SignatureTokenConnection;
import eu.europa.esig.dss.token.mocca.MOCCASignatureTokenConnection;
import lu.nowina.nexu.api.AbstractCardProductAdapter;
import lu.nowina.nexu.api.CertificateFilter;
import lu.nowina.nexu.api.DetectedCard;
import lu.nowina.nexu.api.GetIdentityInfoResponse;
import lu.nowina.nexu.api.MessageDisplayCallback;
import lu.nowina.nexu.api.NexuAPI;
import lu.nowina.nexu.api.ScAPI;

public class GenericCardAdapter extends AbstractCardProductAdapter {

	private SCInfo info;

	public GenericCardAdapter(SCInfo info) {
		super();
		this.info = info;
	}

	@Override
	protected boolean accept(DetectedCard card) {
		return info.getAtr().equals(card.getAtr());
	}

	@Override
	protected boolean supportMessageDisplayCallback(DetectedCard card) {
		return false;
	}
	
	@Override
	protected SignatureTokenConnection connect(NexuAPI api, DetectedCard card, PasswordInputCallback callback) {
		ConnectionInfo cInfo = info.getConnectionInfo(api.getEnvironmentInfo());
		ScAPI scApi = cInfo.getSelectedApi();
		switch (scApi) {
		case MSCAPI:
			// Cannot intercept cancel and timeout for MSCAPI (too generic error).
			return new MSCAPISignatureToken();
		case PKCS_11:
			String absolutePath = cInfo.getApiParam();
			return new Pkcs11SignatureTokenAdapter(new Pkcs11SignatureToken(absolutePath, callback, card.getTerminalIndex()));
		case MOCCA:
			return new MOCCASignatureTokenConnectionAdapter(new MOCCASignatureTokenConnection(callback));
		default:
			throw new RuntimeException("API not supported");
		}
	}

	@Override
	protected SignatureTokenConnection connect(NexuAPI api, DetectedCard card, PasswordInputCallback callback,
			MessageDisplayCallback messageCallback) {
		throw new IllegalStateException("This product adapter does not support message display callback.");
	}
	
	@Override
	protected boolean canReturnIdentityInfo(DetectedCard card) {
		return false;
	}

	@Override
	public GetIdentityInfoResponse getIdentityInfo(SignatureTokenConnection token) {
		throw new IllegalStateException("This card adapter cannot return identity information.");
	}
	
	@Override
	protected boolean supportCertificateFilter(DetectedCard card) {
		return false;
	}

	@Override
	public List<DSSPrivateKeyEntry> getKeys(SignatureTokenConnection token, CertificateFilter certificateFilter) {
		throw new IllegalStateException("This card adapter does not support certificate filter.");
	}

	@Override
	protected boolean canReturnSuportedDigestAlgorithms(DetectedCard card) {
		return false;
	}

	@Override
	protected List<DigestAlgorithm> getSupportedDigestAlgorithms(DetectedCard card) {
		throw new IllegalStateException("This card adapter cannot return list of supported digest algorithms.");
	}

	@Override
	protected DigestAlgorithm getPreferredDigestAlgorithm(DetectedCard card) {
		throw new IllegalStateException("This card adapter cannot return list of supported digest algorithms.");
	}
}
