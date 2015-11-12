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

/**
 * An <code>Operation</code> is an element of a {@link Flow}.
 * 
 * <p>An <code>Operation</code> can be composed of several <code>Operation</code>s.
 *
 * @param <R> The return type of the operation.
 *
 * @author Jean Lepropre (jean.lepropre@nowina.lu)
 */
public interface Operation<R> {

	/**
	 * Performs the operation and returns its result.
	 * @return The result of the operation.
	 * @throws InterruptedException If the thread was interrupted when performing
	 * the operation.
	 */
	OperationResult<R> perform() throws InterruptedException;
	
}
