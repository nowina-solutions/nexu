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

import eu.europa.esig.dss.SignatureValue;
import eu.europa.esig.dss.x509.CertificateToken;

/**
 * Holds the data representing the result of the authentication:
 * <ul>
 * <li>Signature value of the challenge (see {@link AuthenticateRequest#getChallenge()}.</li>
 * <li>Certificate and certificate chain used to sign the challenge.</li>
 * </ul>
 *
 * @author Jean Lepropre (jean.lepropre@nowina.lu)
 */
public class AuthenticateResponse {

	private final String keyId;
	private final CertificateToken certificate;
	private final CertificateToken[] certificateChain;
	
	private final SignatureValue signatureValue;

	public AuthenticateResponse(String keyId, CertificateToken certificate, CertificateToken[] certificateChain, SignatureValue signatureValue) {
		super();
		this.keyId = keyId;
		this.certificate = certificate;
		this.certificateChain = certificateChain;
		this.signatureValue = signatureValue;
	}

	public String getKeyId() {
		return keyId;
	}

	public CertificateToken getCertificate() {
		return certificate;
	}

	public CertificateToken[] getCertificateChain() {
		return certificateChain;
	}

	public SignatureValue getSignatureValue() {
		return signatureValue;
	}
}
