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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Represents the information collected on the user environment
 * 
 * @author David Naramski
 *
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "environment", propOrder = {
    "jreVendor",
    "osName",
    "osArch",
    "osVersion",
    "arch",
    "os"
})
public class EnvironmentInfo {

	private JREVendor jreVendor;
	
	private String osName;
	
	private String osArch;

	private String osVersion;

	private Arch arch;

	private OS os;

	public EnvironmentInfo() {
    }
	
	public static EnvironmentInfo get() {
	    EnvironmentInfo info = new EnvironmentInfo();

	    String osArch = System.getProperty("os.arch");
        info.setOsArch(osArch);
        info.setArch(Arch.forOSArch(osArch));
	    
        info.setJreVendor(JREVendor.forJREVendor(System.getProperty("java.vendor")));
	    
        String osName = System.getProperty("os.name");
        info.setOsName(osName);
        info.setOs(OS.forOSName(osName));

        String osVersion = System.getProperty("os.version");
        info.setOsVersion(osVersion);
        
	    return info;
	}
	
	public JREVendor getJreVendor() {
		return jreVendor;
	}

	public void setJreVendor(JREVendor jreVendor) {
		this.jreVendor = jreVendor;
	}

	public String getOsVersion() {
		return osVersion;
	}

	public void setOsVersion(String osVersion) {
		this.osVersion = osVersion;
	}

	public Arch getArch() {
		return arch;
	}

	public void setArch(Arch arch) {
		this.arch = arch;
	}

	public OS getOs() {
		return os;
	}

	public void setOs(OS os) {
		this.os = os;
	}

    public String getOsName() {
        return osName;
    }

    public void setOsName(String osName) {
        this.osName = osName;
    }

    public String getOsArch() {
        return osArch;
    }

    public void setOsArch(String osArch) {
        this.osArch = osArch;
    }

}