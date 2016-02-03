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
package lu.nowina.nexu.stats;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import lu.nowina.nexu.api.EnvironmentInfo;

/**
 * POJO that holds anonymous data about the platform of the user.
 *
 * @author Jean Lepropre (jean.lepropre@nowina.lu)
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "platformStatistic", propOrder = { "applicationVersion", "environmentInfo" })
public class PlatformStatistic {

	public static final String APPLICATION_VERSION = "application.version";
	public static final String JRE_VENDOR = "jre.vendor";
	public static final String OS_NAME = "os.name";
	public static final String OS_ARCH = "os.arch";
	public static final String OS_VERSION = "os.version";
	
	private String applicationVersion;
	private EnvironmentInfo environmentInfo;
	
	public PlatformStatistic() {
		super();
	}

	public String getApplicationVersion() {
		return applicationVersion;
	}

	public void setApplicationVersion(String applicationVersion) {
		this.applicationVersion = applicationVersion;
	}

	public EnvironmentInfo getEnvironmentInfo() {
		return environmentInfo;
	}

	public void setEnvironmentInfo(EnvironmentInfo environmentInfo) {
		this.environmentInfo = environmentInfo;
	}
}
