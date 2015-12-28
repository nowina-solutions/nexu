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

import lu.nowina.nexu.api.flow.OperationStatus;

/**
 * This enum defines {@link OperationStatus}es applicable to the core version of NexU.
 *
 * @author Jean Lepropre (jean.lepropre@nowina.lu)
 */
public enum CoreOperationStatus implements OperationStatus {

	UNSUPPORTED_PRODUCT("unsupported_product", "The provided product is not supported by this version of NexU."),
	NO_TOKEN("no_token", "The card adapter did not return any token."),
	NO_TOKEN_ID("no_token_id", "No token ID was returned after having registered the token."),
	NO_PRODUCT_FOUND("no_product_found", "No product was found."),
	UNKNOWN_TOKEN_ID("unknown_token_id", "There is no registered token for the given token ID."),
	NO_KEY("no_key", "No key was retrieved from the given token."),
	CANNOT_SELECT_KEY("cannot_select_key", "Cannot automatically select key based on given key filter."),
	NO_KEY_SELECTED("no_key_selected", "No key was selected by the user.");
	
	private final String code;
	private final String label;
	
	private CoreOperationStatus(final String code, final String label) {
		this.code = code;
		this.label = label;
	}

	@Override
	public String getCode() {
		return code;
	}

	@Override
	public String getLabel() {
		return label;
	}
}
