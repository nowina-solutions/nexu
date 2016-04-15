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
package lu.nowina.nexu;

/**
 * Handler when an event occurs on a database. 
 *
 * @author Jean Lepropre (jean.lepropre@nowina.lu)
 */
public interface DatabaseEventHandler {

	/**
	 * Executes the action associated to this handler on the given database.
	 * @param db The database on which the action associated to this handler must be performed.
	 * @param <T> The type of the database on which the action must be performed.
	 */
	<T extends ProductDatabase> void execute(T db);

}
