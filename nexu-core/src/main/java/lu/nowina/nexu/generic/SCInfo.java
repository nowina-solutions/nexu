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

import javax.xml.bind.annotation.XmlRootElement;

import lu.nowina.nexu.api.ScAPI;

@XmlRootElement
public class SCInfo {

	private String detectedAtr;
	
	private ScAPI selectedApi;
	
	private String apiParam;

	public String getDetectedAtr() {
		return detectedAtr;
	}

	public void setDetectedAtr(String detectedAtr) {
		this.detectedAtr = detectedAtr;
	}

	public ScAPI getSelectedApi() {
		return selectedApi;
	}

	public void setSelectedApi(ScAPI selectedApi) {
		this.selectedApi = selectedApi;
	}

	public String getApiParam() {
		return apiParam;
	}

	public void setApiParam(String apiParam) {
		this.apiParam = apiParam;
	}
	
}
