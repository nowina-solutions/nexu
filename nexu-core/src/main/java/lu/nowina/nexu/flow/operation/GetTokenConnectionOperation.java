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
package lu.nowina.nexu.flow.operation;

import lu.nowina.nexu.api.NexuAPI;
import lu.nowina.nexu.api.TokenId;
import lu.nowina.nexu.api.flow.Operation;
import lu.nowina.nexu.api.flow.OperationResult;
import eu.europa.esig.dss.token.SignatureTokenConnection;

/**
 * This {@link Operation} allows to retrieve a {@link SignatureTokenConnection} associated
 * to a {@link TokenId}.
 *
 * <p>Expected parameters:
 * <ol>
 * <li>{@link NexuAPI}</li>
 * <li>{@link TokenId}</li>
 * </ol>
 *
 * @author Jean Lepropre (jean.lepropre@nowina.lu)
 */
public class GetTokenConnectionOperation implements Operation<SignatureTokenConnection> {

	private NexuAPI api;
	private TokenId tokenId;
	
	public GetTokenConnectionOperation() {
		super();
	}

	@Override
	public void setParams(Object... params) {
		try {
			this.api = (NexuAPI) params[0];
			this.tokenId = (TokenId) params[1];
		} catch(final ClassCastException | ArrayIndexOutOfBoundsException e) {
			throw new IllegalArgumentException("Expected parameters: NexuAPI, TokenId");
		}
	}

	@Override
	public OperationResult<SignatureTokenConnection> perform() {
		final SignatureTokenConnection token = api.getTokenConnection(tokenId);
		if(token != null) {
			return new OperationResult<SignatureTokenConnection>(token);
		} else {
			return new OperationResult<SignatureTokenConnection>(CoreOperationStatus.UNKNOWN_TOKEN_ID);
		}
	}
	
}
