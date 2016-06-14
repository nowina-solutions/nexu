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
package lu.nowina.nexu.object.model;

/**
 * Holds the data representing the result of the authentication:
 * <ul>
 * <li>Signature value of the challenge (see {@link AuthenticateRequest#getChallenge()}.</li>
 * <li>Certificate and certificate chain used to sign the challenge. These are encoded in base 64.</li>
 * </ul>
 *
 * @author Jean Lepropre (jean.lepropre@nowina.lu)
 */
public class AuthenticateResponse {

	private String keyId;
	private String certificate;
	private String[] certificateChain;
	
	private SignatureValue signatureValue;

	public AuthenticateResponse() {
		super();
	}
	
	public AuthenticateResponse(String keyId, String certificate, String[] certificateChain, SignatureValue signatureValue) {
		super();
		this.keyId = keyId;
		this.certificate = certificate;
		this.certificateChain = certificateChain;
		this.signatureValue = signatureValue;
	}

	public String getKeyId() {
		return keyId;
	}

	public String getCertificate() {
		return certificate;
	}

	public String[] getCertificateChain() {
		return certificateChain;
	}

	public SignatureValue getSignatureValue() {
		return signatureValue;
	}

	public void setKeyId(String keyId) {
		this.keyId = keyId;
	}

	public void setCertificate(String certificate) {
		this.certificate = certificate;
	}

	public void setCertificateChain(String[] certificateChain) {
		this.certificateChain = certificateChain;
	}

	public void setSignatureValue(SignatureValue signatureValue) {
		this.signatureValue = signatureValue;
	}
}
