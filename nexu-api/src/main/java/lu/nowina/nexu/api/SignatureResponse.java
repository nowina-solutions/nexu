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

import javax.xml.bind.DatatypeConverter;

import eu.europa.esig.dss.SignatureValue;
import eu.europa.esig.dss.x509.CertificateToken;

public class SignatureResponse {

	private String signatureValue;
	private String signingCertificate;
	private String[] certificateChain;

	public SignatureResponse(SignatureValue value, CertificateToken signingCertificate, CertificateToken[] certificateChain) {
		this.signatureValue = new String(DatatypeConverter.printBase64Binary(value.getValue()));
		this.signingCertificate = signingCertificate.getBase64Encoded();
		
		this.certificateChain = new String[certificateChain.length];
		for(int i=0; i<certificateChain.length; i++) {
			this.certificateChain[i] = certificateChain[i].getBase64Encoded();
		}
	}

	public String getSignatureValue() {
		return signatureValue;
	}

	public String getSigningCertificate() {
		return signingCertificate;
	}

	public String[] getCertificateChain() {
		return certificateChain;
	}

}
