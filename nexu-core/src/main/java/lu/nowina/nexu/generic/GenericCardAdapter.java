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

import eu.europa.esig.dss.token.MSCAPISignatureToken;
import eu.europa.esig.dss.token.PasswordInputCallback;
import eu.europa.esig.dss.token.Pkcs11SignatureToken;
import eu.europa.esig.dss.token.SignatureTokenConnection;
import lu.nowina.nexu.api.CardAdapter;
import lu.nowina.nexu.api.DetectedCard;
import lu.nowina.nexu.api.NexuAPI;
import lu.nowina.nexu.api.ScAPI;

public class GenericCardAdapter implements CardAdapter {

	private SCInfo info;

	public GenericCardAdapter(SCInfo info) {
		this.info = info;
	}

	@Override
	public boolean accept(DetectedCard card) {
		return info.getAtr().equals(card.getAtr());
	}

	@Override
	public SignatureTokenConnection connect(NexuAPI api, DetectedCard card, PasswordInputCallback callback) {
		ConnectionInfo cInfo = info.getConnectionInfo(api.getEnvironmentInfo());
		ScAPI scApi = cInfo.getSelectedApi();
		switch (scApi) {
		case MSCAPI:
			return new MSCAPISignatureToken();
		case PKCS_11:
			String absolutePath = cInfo.getApiParam();
			return new Pkcs11SignatureToken(absolutePath, callback);
		default:
		    throw new RuntimeException("API not supported");
		}
	}

}
