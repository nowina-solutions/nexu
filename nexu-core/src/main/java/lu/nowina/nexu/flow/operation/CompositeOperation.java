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

import lu.nowina.nexu.api.flow.Operation;
import lu.nowina.nexu.api.flow.OperationFactory;
import lu.nowina.nexu.view.core.UIDisplay;
import lu.nowina.nexu.view.core.UIOperation;

/**
 * A <code>CompositeOperation</code> is composed of several {@link Operation}s.
 * Hence it has a {@link #setOperationFactory(OperationFactory)} method to get the
 * {@link OperationFactory} to use to create other {@link Operation}s.
 *
 * @author Jean Lepropre (jean.lepropre@nowina.lu)
 */
public interface CompositeOperation<R> extends Operation<R> {

	/**
	 * Sets the {@link OperationFactory} to use to create other {@link Operation}s.
	 * @param operationFactory The {@link OperationFactory} to use to create other
	 * {@link Operation}s.
	 */
	void setOperationFactory(OperationFactory operationFactory);
	
	/**
	 * Sets the {@link UIDisplay} for {@link UIOperation}.
	 * @param display The {@link UIDisplay} for {@link UIOperation}.
	 */
	void setDisplay(UIDisplay display);
	
}
