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

import lu.nowina.nexu.NexuException;
import lu.nowina.nexu.api.Feedback;
import lu.nowina.nexu.api.NexuAPI;
import lu.nowina.nexu.flow.operation.Operation;
import lu.nowina.nexu.flow.operation.OperationFactory;
import lu.nowina.nexu.view.core.UIDisplay;
import lu.nowina.nexu.view.core.UIOperation;

/**
 * A flow is a sequence of {@link Operation}.
 * 
 * @author David Naramski
 */
public abstract class Flow<I, O> {

	private UIDisplay display;

	private OperationFactory operationFactory;

	public Flow(UIDisplay display) {
		if (display == null) {
			throw new IllegalArgumentException("display cannot be null");
		}
		this.display = display;
	}

	public final void setOperationFactory(final OperationFactory operationFactory) {
		this.operationFactory = operationFactory;
	}

	protected final OperationFactory getOperationFactory() {
		return operationFactory;
	}

	public final O execute(NexuAPI api, I input) {
		final O out = process(api, input);
		display.close();
		return out;
	}

	protected abstract O process(NexuAPI api, I input) throws NexuException;

	protected final UIDisplay getDisplay() {
		return display;
	}
	
	@SuppressWarnings("unchecked")
	protected void handleException(final Exception e) {
		final Feedback feedback = new Feedback(e);
		getOperationFactory().getOperation(
				UIOperation.class, getDisplay(), "/fxml/provide-feedback.fxml",
				new Object[]{feedback}).perform();
		getOperationFactory().getOperation(UIOperation.class, getDisplay(), "/fxml/message.fxml",
				new Object[]{"Failure"}).perform();
	}
}
