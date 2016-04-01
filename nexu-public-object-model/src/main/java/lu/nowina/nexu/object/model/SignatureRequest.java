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
 * POJO defining a request to sign something.
 *
 * <p>Digest algorithm is a constant defined in
 * <a href="https://github.com/esig/dss/blob/master/dss-model/src/main/java/eu/europa/esig/dss/DigestAlgorithm.java">DSS</a>.
 *
 * @author Jean Lepropre (jean.lepropre@nowina.lu)
 */
public class SignatureRequest extends NexuRequest {

	private TokenId tokenId;
	private ToBeSigned toBeSigned;
	private String digestAlgorithm;
	private String keyId;
	
	public SignatureRequest() {
		super();
	}

	public TokenId getTokenId() {
		return tokenId;
	}

	public void setTokenId(TokenId tokenId) {
		this.tokenId = tokenId;
	}

	public ToBeSigned getToBeSigned() {
		return toBeSigned;
	}

	public void setToBeSigned(ToBeSigned toBeSigned) {
		this.toBeSigned = toBeSigned;
	}

	public String getDigestAlgorithm() {
		return digestAlgorithm;
	}

	public void setDigestAlgorithm(String digestAlgorithm) {
		this.digestAlgorithm = digestAlgorithm;
	}

	public String getKeyId() {
		return keyId;
	}

	public void setKeyId(String keyId) {
		this.keyId = keyId;
	}

}
