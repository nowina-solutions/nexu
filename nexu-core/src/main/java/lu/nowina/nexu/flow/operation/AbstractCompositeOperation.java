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

import lu.nowina.nexu.api.flow.OperationFactory;
import lu.nowina.nexu.view.core.UIDisplay;

/**
 * Convenient base class for {@link CompositeOperation}.
 *
 * @author Jean Lepropre (jean.lepropre@nowina.lu)
 */
public abstract class AbstractCompositeOperation<R> implements CompositeOperation<R> {

	protected UIDisplay display;
	protected OperationFactory operationFactory;
	
	public AbstractCompositeOperation() {
		super();
	}

	@Override
	public final void setOperationFactory(OperationFactory operationFactory) {
		this.operationFactory = operationFactory;
	}

	@Override
	public final void setDisplay(UIDisplay display) {
		this.display = display;
	}

}
