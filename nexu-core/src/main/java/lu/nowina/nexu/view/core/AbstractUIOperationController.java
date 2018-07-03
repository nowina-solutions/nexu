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

import lu.nowina.nexu.api.flow.OperationResult;
import lu.nowina.nexu.api.flow.OperationStatus;

/**
 * Convenient base class for {@link UIOperationController}.
 *
 * @author Jean Lepropre (jean.lepropre@nowina.lu)
 */
public abstract class AbstractUIOperationController<R> implements UIOperationController<R> {

	private UIOperation<R> uiOperation;
	private UIDisplay display;
	
	public AbstractUIOperationController() {
		super();
	}

	@Override
	public final void setUIOperation(final UIOperation<R> uiOperation) {
		this.uiOperation = uiOperation;
	}
	
	@Override
	public final void setDisplay(UIDisplay display) {
		this.display = display;
	}

	protected final void signalEnd(R result) {
		uiOperation.signalEnd(result);
	}
	
	/**
	 * Provides the flow alternative actions (other than next or cancel).
	 * @param operationStatus
	 * Status the flow will check before dispatching to an action.	
	 */
	protected final void signalEndWithStatus(final OperationStatus operationStatus) {
		uiOperation.signalEnd(operationStatus);
	}

	protected final void signalUserCancel() {
		uiOperation.signalUserCancel();
	}
	
	/**
	 * This implementation does nothing.
	 */
	public void init(Object... params) {
		// Do nothing by contract
	}
	
	protected final UIDisplay getDisplay() {
		return display;
	}
}
