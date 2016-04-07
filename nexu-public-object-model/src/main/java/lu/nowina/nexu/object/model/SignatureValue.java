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
 * POJO encapsulating signature algorithm and value.
 * 
 * <p>Signature algorithm is a constant defined in
 * <a href="https://github.com/esig/dss/blob/master/dss-model/src/main/java/eu/europa/esig/dss/SignatureAlgorithm.java">DSS</a>.
 * 
 * <p>Signature value is a base 64 encoded string of the signature.
 *
 * @author Jean Lepropre (jean.lepropre@nowina.lu)
 */
public class SignatureValue {

	private String algorithm;
	private String value;

	public SignatureValue() {
		super();
	}

	public String getAlgorithm() {
		return algorithm;
	}

	public void setAlgorithm(String algorithm) {
		this.algorithm = algorithm;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
