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
package lu.nowina.nexu.view.core;

import eu.europa.esig.dss.token.PasswordInputCallback;

/**
 * Représente une interface graphique visible par l'utilisateur. On peut demander l'affichage d'un panel spécifique ou montrer des panels pré-définis.
 * 
 */
public interface UIDisplay {

	void close();

	<T extends Object> OperationResult<T> displayAndWaitUIOperation(String fxml, Object... params);

	PasswordInputCallback getPasswordInputCallback();

}
