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

import java.util.List;

/**
 * POJO defining the response of NexU to a {@link GetCertificateRequest} request.
 * 
 * <p>Certificate and certificate chain are encoded in base 64.
 * 
 * <p>Encryption algorithm is a constant defined in
 * <a href="https://github.com/esig/dss/blob/master/dss-model/src/main/java/eu/europa/esig/dss/EncryptionAlgorithm.java">DSS</a>.
 *
 * <p>Digest algorithms are constants defined in
 * <a href="https://github.com/esig/dss/blob/master/dss-model/src/main/java/eu/europa/esig/dss/DigestAlgorithm.java">DSS</a>.
 *
 * @author Jean Lepropre (jean.lepropre@nowina.lu)
 */
public class GetCertificateResponse {

	private TokenId tokenId;

	private String keyId;

	private String certificate;

	private String[] certificateChain;

	private String encryptionAlgorithm;

	private List<String> supportedDigests;
	
	private String preferredDigest;

	public GetCertificateResponse() {
		super();
	}
	
	public TokenId getTokenId() {
		return tokenId;
	}

	public void setTokenId(TokenId tokenId) {
		this.tokenId = tokenId;
	}

	public String getKeyId() {
		return keyId;
	}

	public void setKeyId(String keyId) {
		this.keyId = keyId;
	}

	public String getCertificate() {
		return certificate;
	}

	public void setCertificate(String certificate) {
		this.certificate = certificate;
	}

	public String[] getCertificateChain() {
		return certificateChain;
	}

	public void setCertificateChain(String[] certificateChain) {
		this.certificateChain = certificateChain;
	}

	public String getEncryptionAlgorithm() {
		return encryptionAlgorithm;
	}

	public void setEncryptionAlgorithm(String encryptionAlgorithm) {
		this.encryptionAlgorithm = encryptionAlgorithm;
	}

	public List<String> getSupportedDigests() {
		return supportedDigests;
	}

	public void setSupportedDigests(List<String> supportedDigests) {
		this.supportedDigests = supportedDigests;
	}

	public String getPreferredDigest() {
		return preferredDigest;
	}

	public void setPreferredDigest(String preferredDigest) {
		this.preferredDigest = preferredDigest;
	}
}
