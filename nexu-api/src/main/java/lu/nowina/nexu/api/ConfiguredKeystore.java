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
package lu.nowina.nexu.api;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * Represents a configured keystore.
 *
 * @author Jean Lepropre (jean.lepropre@nowina.lu)
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "configuredKeystore", propOrder = { "url", "type" })
public class ConfiguredKeystore implements Product {

	private String url;
	private KeystoreType type;
	
	public ConfiguredKeystore() {
		super();
	}

	/**
	 * Returns the URL towards the configured keystore.
	 * @return The URL towards the configured keystore.
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * Sets the URL towards the configured keystore.
	 * @param url The URL towards the configured keystore.
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * Returns the type of the configured keystore.
	 * @return The type of the configured keystore.
	 */
	public KeystoreType getType() {
		return type;
	}

	/**
	 * Sets the type of the configured keystore.
	 * @param type The type of the configured keystore.
	 */
	public void setType(KeystoreType type) {
		this.type = type;
	}
}
