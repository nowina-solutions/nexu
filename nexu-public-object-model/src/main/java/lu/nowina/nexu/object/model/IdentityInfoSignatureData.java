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
 * This class holds data regarding signature of some identity information.
 * 
 * <p>Raw data and certificate chain are encoded in base 64.
 * 
 * @author Jean Lepropre (jean.lepropre@nowina.lu)
 */
public class IdentityInfoSignatureData {

	private String rawData;
	private SignatureValue signatureValue;
	private String[] certificateChain;
	
	public IdentityInfoSignatureData() {
		super();
	}
		
	public IdentityInfoSignatureData(String rawData, SignatureValue signatureValue, String[] certificateChain) {
		super();
		this.rawData = rawData;
		this.signatureValue = signatureValue;
		this.certificateChain = certificateChain;
	}
	
	public String getRawData() {
		return rawData;
	}
	
	public SignatureValue getSignatureValue() {
		return signatureValue;
	}
	
	public String[] getCertificateChain() {
		return certificateChain;
	}

	public void setRawData(String rawData) {
		this.rawData = rawData;
	}

	public void setSignatureValue(SignatureValue signatureValue) {
		this.signatureValue = signatureValue;
	}

	public void setCertificateChain(String[] certificateChain) {
		this.certificateChain = certificateChain;
	}
}
