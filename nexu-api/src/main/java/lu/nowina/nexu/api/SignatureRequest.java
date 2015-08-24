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

import eu.europa.esig.dss.ToBeSigned;
import lu.nowina.nexu.api.signature.smartcard.TokenId;

public class SignatureRequest extends NexuRequest {

	private TokenId tokenId;
	
	private ToBeSigned tbs;
	
	private String keyId;
	
	public SignatureRequest() {
	}
	
	public SignatureRequest(ToBeSigned tbs) {
		this.tbs = tbs;
	}
	
	public ToBeSigned getToBeSigned() {
		return tbs;
	}

	public TokenId getTokenId() {
		return tokenId;
	}

	public void setTokenId(TokenId tokenId) {
		this.tokenId = tokenId;
	}

	public ToBeSigned getTbs() {
		return tbs;
	}

	public void setTbs(ToBeSigned tbs) {
		this.tbs = tbs;
	}

	public String getKeyId() {
		return keyId;
	}

	public void setKeyId(String keyId) {
		this.keyId = keyId;
	}
	
}
