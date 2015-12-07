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
import eu.europa.esig.dss.EncryptionAlgorithm;
import eu.europa.esig.dss.SignatureValue;

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
	private final String certificate;
	private final List<String> certificateChain;
	private final EncryptionAlgorithm encryptionAlgorithm;
	private final DigestAlgorithm digestAlgorithm;
	
	private final SignatureValue signatureValue;

	public AuthenticateResponse(String keyId, String certificate, List<String> certificateChain,
			EncryptionAlgorithm encryptionAlgorithm, DigestAlgorithm digestAlgorithm, SignatureValue signatureValue) {
		super();
		this.keyId = keyId;
		this.certificate = certificate;
		this.certificateChain = certificateChain;
		this.encryptionAlgorithm = encryptionAlgorithm;
		this.digestAlgorithm = digestAlgorithm;
		this.signatureValue = signatureValue;
	}

	public String getKeyId() {
		return keyId;
	}

	public String getCertificate() {
		return certificate;
	}

	public List<String> getCertificateChain() {
		return certificateChain;
	}

	public EncryptionAlgorithm getEncryptionAlgorithm() {
		return encryptionAlgorithm;
	}
	
	public DigestAlgorithm getDigestAlgorithm() {
		return digestAlgorithm;
	}

	public SignatureValue getSignatureValue() {
		return signatureValue;
	}
}
