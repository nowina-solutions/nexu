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

/**
 * Represents the information collected on the user environment
 * 
 * @author David Naramski
 *
 */
public class EnvironmentInfo {

	/**
	 * The JRE vendor.
	 */
	public JREVendor jreVendor = JREVendor.NOT_RECOGNIZED;

	/**
	 * The JRE version.
	 */
	public String jreVersion;

	/**
	 * The OS version.
	 */
	public String osVersion;

	/**
	 * The arch.
	 */
	public String arch;

	/**
	 * The OS.
	 */
	public OS os = OS.NOT_RECOGNIZED;

	public JREVendor getJreVendor() {
		return jreVendor;
	}

	public void setJreVendor(JREVendor jreVendor) {
		this.jreVendor = jreVendor;
	}

	public String getJreVersion() {
		return jreVersion;
	}

	public void setJreVersion(String jreVersion) {
		this.jreVersion = jreVersion;
	}

	public String getOsVersion() {
		return osVersion;
	}

	public void setOsVersion(String osVersion) {
		this.osVersion = osVersion;
	}

	public String getArch() {
		return arch;
	}

	public void setArch(String arch) {
		this.arch = arch;
	}

	public OS getOs() {
		return os;
	}

	public void setOs(OS os) {
		this.os = os;
	}

}