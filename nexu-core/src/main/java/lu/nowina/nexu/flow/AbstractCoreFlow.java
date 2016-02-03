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
package lu.nowina.nexu.flow;

import lu.nowina.nexu.api.Execution;
import lu.nowina.nexu.api.NexuAPI;
import lu.nowina.nexu.api.flow.BasicOperationStatus;
import lu.nowina.nexu.api.flow.OperationResult;
import lu.nowina.nexu.view.core.UIDisplay;

/**
 * Abstract base class for flows of the core version of NexU.
 *
 * @author Jean Lepropre (jean.lepropre@nowina.lu)
 */
abstract class AbstractCoreFlow<I, O> extends Flow<I, O> {

	public AbstractCoreFlow(UIDisplay display, NexuAPI api) {
		super(display, api);
	}

	/**
	 * Builds and returns an {@link Execution} object from an <b>error</b> {@link OperationResult}.
	 * @param operationResult The {@link OperationResult} to handle.
	 * @return An {@link Execution} object built from the given {@link OperationResult}.
	 * @throws IllegalArgumentException If given <code>operationResult</code> is a success operation result.
	 * @throws Exception If given <code>operationResult</code> is an exception operation result.
	 */
	protected final Execution<O> handleErrorOperationResult(final OperationResult<?> operationResult) throws Exception {
		if(operationResult.getStatus().equals(BasicOperationStatus.SUCCESS)) {
			throw new IllegalArgumentException("Cannot handle success operation result!");
		}
		
		if(operationResult.getStatus().equals(BasicOperationStatus.EXCEPTION)) {
			throw operationResult.getException();
		} else {
			return new Execution<>(operationResult.getStatus());
		}
	}
}
