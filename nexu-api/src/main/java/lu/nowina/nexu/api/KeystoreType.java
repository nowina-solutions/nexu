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

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

/**
 * This enum gathers various keystore types supported by NexU.
 *
 * @author Jean Lepropre (jean.lepropre@nowina.lu)
 */
@XmlType(name = "keystoreType")
@XmlEnum
public enum KeystoreType {

	@XmlEnumValue("JKS") JKS("JKS"),
	@XmlEnumValue("PKCS12") PKCS12("PKCS#12");
	
	private String label;
	
	private KeystoreType(final String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}
}
