/**
 * © Nowina Solutions, 2015-2017
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

import eu.europa.esig.dss.token.PasswordInputCallback;

/**
 * {@link PasswordInputCallback} whose password prompt can be modified.
 *
 * @author Jean Lepropre (jean.lepropre@nowina.lu)
 */
public interface NexuPasswordInputCallback extends PasswordInputCallback {

	/**
	 * Sets the password prompt of this {@link PasswordInputCallback}.
	 * @param passwordPrompt The new password prompt.
	 */
	void setPasswordPrompt(String passwordPrompt);
	
}
