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
package lu.nowina.nexu.generic;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import eu.europa.esig.dss.DigestAlgorithm;
import lu.nowina.nexu.api.EnvironmentInfo;

/**
 * SCInfo contains information about a SmartCard, such as known ATR, label, downloadUrl for drivers, infoUrl for information, ...
 * 
 * @author david.naramski
 *
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class SCInfo {

	private String atr;

	@XmlElementWrapper(name = "connectionInfos")
	@XmlElement(name = "connectionInfo")
	private List<ConnectionInfo> infos;

	private String label;

	private String downloadUrl;

	private String infoUrl;

	@XmlElementWrapper(name = "supportedDigestAlgo")
	@XmlElement(name = "digestAlgo")
	private List<DigestAlgorithm> supportedDigestAlgorithm;

	public List<DigestAlgorithm> getSupportedDigestAlgorithm() {
		if (supportedDigestAlgorithm == null) {
			supportedDigestAlgorithm = new ArrayList<>();
		}
		return supportedDigestAlgorithm;
	}

	public ConnectionInfo getConnectionInfo(EnvironmentInfo env) {
		for (ConnectionInfo info : getInfos()) {
			if (info.getEnv().matches(env)) {
				return info;
			}
		}
		return null;
	}

	public List<ConnectionInfo> getInfos() {
		if (infos == null) {
			infos = new ArrayList<>();
		}
		return infos;
	}

	public void setInfos(List<ConnectionInfo> infos) {
		this.infos = infos;
	}

	public String getAtr() {
		return atr;
	}

	public void setAtr(String atr) {
		this.atr = atr;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getDownloadUrl() {
		return downloadUrl;
	}

	public void setDownloadUrl(String downloadUrl) {
		this.downloadUrl = downloadUrl;
	}

	public String getInfoUrl() {
		return infoUrl;
	}

	public void setInfoUrl(String infoUrl) {
		this.infoUrl = infoUrl;
	}

	public void setSupportedDigestAlgorithm(List<DigestAlgorithm> supportedDigtestAlgorithm) {
		this.supportedDigestAlgorithm = supportedDigtestAlgorithm;
	}

}
