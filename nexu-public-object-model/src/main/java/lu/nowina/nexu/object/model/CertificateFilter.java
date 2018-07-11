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
 * POJO defining a filter to select particular certificates. 
 *
 * <p><code>certificateSHA1</code> is encoded in base 64.
 *
 * @author Jean Lepropre (jean.lepropre@nowina.lu)
 */
public class CertificateFilter {

	private Purpose purpose;
	
	private String certificateSHA1;
	
	private boolean nonRepudiationBit;

	public CertificateFilter() {
		super();
	}
	
	public Purpose getPurpose() {
		return purpose;
	}

	public void setPurpose(Purpose purpose) {
		this.purpose = purpose;
	}

	public String getCertificateSHA1() {
		return certificateSHA1;
	}

	public void setCertificateSHA1(String certificateSHA1) {
		this.certificateSHA1 = certificateSHA1;
	}

	public boolean getNonRepudiationBit() {
		return nonRepudiationBit;
	}

	public void setNonRepudiationBit(boolean nonRepudiationBit) {
		this.nonRepudiationBit = nonRepudiationBit;
	}
}
