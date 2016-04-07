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
 * POJO defining the response of NexU to a {@link SignatureRequest} request.
 * 
 * <p>Signature value and certificates are encoded in base 64.
 * 
 * <p>Signature algorithm is a constant defined in
 * <a href="https://github.com/esig/dss/blob/master/dss-model/src/main/java/eu/europa/esig/dss/SignatureAlgorithm.java">DSS</a>.
 *
 * @author Jean Lepropre (jean.lepropre@nowina.lu)
 */
public class SignatureResponse {

	private String signatureValue;
	private String signatureAlgorithm;
	private String certificate;
	private String[] certificateChain;

	public SignatureResponse() {
		super();
	}

	public String getSignatureValue() {
		return signatureValue;
	}

	public String getSignatureAlgorithm() {
		return signatureAlgorithm;
	}

	public String getCertificate() {
		return certificate;
	}

	public String[] getCertificateChain() {
		return certificateChain;
	}

	public void setSignatureValue(String signatureValue) {
		this.signatureValue = signatureValue;
	}

	public void setSignatureAlgorithm(String signatureAlgorithm) {
		this.signatureAlgorithm = signatureAlgorithm;
	}

	public void setCertificate(String certificate) {
		this.certificate = certificate;
	}

	public void setCertificateChain(String[] certificateChain) {
		this.certificateChain = certificateChain;
	}
}
