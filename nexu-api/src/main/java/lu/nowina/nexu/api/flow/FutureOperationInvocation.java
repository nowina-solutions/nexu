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
package lu.nowina.nexu.api.flow;

/**
 * Represents the future invocation of an {@link Operation}.
 *
 * @author Jean Lepropre (jean.lepropre@nowina.lu)
 * @param <R> The result type returned by this FutureOperationInvocation's {@link #call(OperationFactory)} method.
 */
public interface FutureOperationInvocation<R> {

	/**
	 * Calls the operation corresponding to this object and returns its result.
	 * @param operationFactory The object that will be responsible to call the {@link Operation}.
	 * @return The result of the operation corresponding to this object.
	 */
	OperationResult<R> call(OperationFactory operationFactory);
	
}
