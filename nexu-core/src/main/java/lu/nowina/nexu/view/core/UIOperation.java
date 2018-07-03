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

import java.io.IOException;
import java.util.Arrays;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import lu.nowina.nexu.api.flow.AbstractFutureOperationInvocation;
import lu.nowina.nexu.api.flow.BasicOperationStatus;
import lu.nowina.nexu.api.flow.FutureOperationInvocation;
import lu.nowina.nexu.api.flow.OperationResult;
import lu.nowina.nexu.api.flow.OperationStatus;
import lu.nowina.nexu.flow.Flow;
import lu.nowina.nexu.flow.operation.UIDisplayAwareOperation;

/**
 * An <code>UIOperation</code> controls the user interaction with the {@link Flow}.
 * The {@link Flow} triggers the <code>UIOperation</code> and calls the method {@link #perform()}.
 * When the user finished the operation, the {@link UIOperationController} notifies the <code>UIOperation</code>
 * through the method {@link #signalEnd(Object)}.
 * 
 * <p>Expected parameters:
 * <ol>
 * <li>FXML</li>
 * <li>Controller parameters (optional): array of {@link Object}.</li>
 * </ol>
 * 
 * @author david.naramski
 * @author Jean Lepropre (jean.lepropre@nowina.lu)
 * @param <R> The return type of the operation.
 */
public class UIOperation<R> implements UIDisplayAwareOperation<R> {

	private static final Logger LOGGER = LoggerFactory.getLogger(UIOperation.class.getName());

	private transient Object lock = new Object();
	private transient volatile OperationResult<R> result = null;

	private UIDisplay display;
	private String fxml;
	private Object[] params;
	
	private transient Parent root;
	private transient UIOperationController<R> controller;
	
	public UIOperation() {
		super();
	}
	
	public void setParams(final Object... params) {
		if(params.length < 1) {
			throw new IllegalArgumentException("An UIOperation needs at least the fxml.");
		}
		try {
			this.fxml = (String) params[0];
			if(params.length > 1) {
				if(params[1] instanceof Object[]) {
					this.params = (Object[]) params[1];
				} else {
					this.params = Arrays.copyOfRange(params, 1, params.length);
				}
			}
		} catch(ClassCastException e) {
			throw new IllegalArgumentException("Expected parameters: fxml, controller params.");
		}
	}
	
	@Override
	public final OperationResult<R> perform() {
		LOGGER.info("Loading " + fxml + " view");
		final FXMLLoader loader = new FXMLLoader();
		try {
			loader.setResources(ResourceBundle.getBundle("bundles/nexu"));
			loader.load(getClass().getResourceAsStream(fxml));
		} catch(final IOException e) {
			throw new RuntimeException(e);
		}

	    root = loader.getRoot();
		controller = loader.getController();
		controller.init(params);
		controller.setUIOperation(this);
		controller.setDisplay(display);

		display();
		
		return result;
	}
	
	public void waitEnd() throws InterruptedException {
		String name = getOperationName();
		LOGGER.info("Thread " + Thread.currentThread().getName() + " wait on " + name);
		synchronized (lock) {
			lock.wait();
		}
		LOGGER.info("Thread " + Thread.currentThread().getName() + " resumed on " + name);
	}

	/**
	 * When the user has finished performing the operation, the UIOperation must call the "signalEnd()" method to resume the UIFlow.
	 * 
	 * @param result
	 */
	public final void signalEnd(R result) {
		String name = getOperationName();
		LOGGER.info("Notify from " + Thread.currentThread().getName() + " on " + name);
		notifyResult(new OperationResult<>(result));
		hide();
	}

	public final void signalEnd(final OperationStatus operationStatus) {
		notifyResult(new OperationResult<>(operationStatus));
	}
	
	private void notifyResult(OperationResult<R> result) {
		this.result = result;
		synchronized (lock) {
			lock.notify();
		}
	}

	public final void signalUserCancel() {
		notifyResult(new OperationResult<>(BasicOperationStatus.USER_CANCEL));
	}

	private String getOperationName() {
		return this.controller.getClass().getSimpleName();
	}

	public Parent getRoot() {
		return root;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((display == null) ? 0 : display.hashCode());
		result = prime * result + ((fxml == null) ? 0 : fxml.hashCode());
		result = prime * result + Arrays.hashCode(params);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UIOperation<?> other = (UIOperation<?>) obj;
		if (display == null) {
			if (other.display != null)
				return false;
		} else if (!display.equals(other.display))
			return false;
		if (fxml == null) {
			if (other.fxml != null)
				return false;
		} else if (!fxml.equals(other.fxml))
			return false;
		if (!Arrays.equals(params, other.params))
			return false;
		return true;
	}

	@Override
	public final void setDisplay(UIDisplay display) {
		this.display = display;
	}
	
	protected final UIDisplay getDisplay() {
		return display;
	}
	
	protected void display() {
		display.displayAndWaitUIOperation(this);
	}
	
	protected void hide() {
		display.close(true);
	}
	
	public static <R, T extends UIOperation<R>> FutureOperationInvocation<R> getFutureOperationInvocation(
			final Class<T> operationClass, final String fxml, final Object... controllerParams) {
		return new UIFutureOperationInvocation<>(operationClass, fxml, controllerParams);
	}
	
	private static class UIFutureOperationInvocation<R, T extends UIOperation<R>> extends AbstractFutureOperationInvocation<R> {
		private final Class<T> operationClass;
		private final String fxml;
		private final Object[] controllerParams;
		
		public UIFutureOperationInvocation(final Class<T> operationClass, final String fxml, final Object... controllerParams) {
			this.operationClass = operationClass;
			this.fxml = fxml;
			this.controllerParams = controllerParams;
		}

		@Override
		@SuppressWarnings({"unchecked"})
		protected Class<T> getOperationClass() {
			return operationClass;
		}

		@Override
		protected Object[] getOperationParams() {
			return (controllerParams != null) ? new Object[]{fxml, controllerParams} : new Object[]{fxml};
		}
	}
}
