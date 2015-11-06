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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A UIOperation control the user interaction with the UIFlow. The UIFlow trigger the UIOperation and call the method "waitEnd()". When the user finished the
 * operation, the UIOperation notify the UIFlow through the method "signalEnd()".
 * 
 * @author david.naramski
 *
 * @param <R>
 */
public abstract class UIOperation<R> {

	private static final Logger logger = LoggerFactory.getLogger(UIOperation.class.getName());

	private Object lock = new Object();

	private volatile OperationResult<R> result = null;

	/**
	 * Once the UIOperation has been instanciated and initialized, the UIFlow will call the "waitEnd()" method.
	 * 
	 * @return
	 * @throws InterruptedException
	 */
	public final OperationResult<R> waitEnd() throws InterruptedException {
		String name = getOperationName();

		logger.info("Thread " + Thread.currentThread().getName() + " wait on " + name);
		synchronized (lock) {
			lock.wait();
		}
		logger.info("Thread " + Thread.currentThread().getName() + " resumed on " + name);
		return result;
	}

	/**
	 * When the user has finished performing the operation, the UIOperation must call the "signalEnd()" method to resume the UIFlow.
	 * 
	 * @param result
	 */
	protected final void signalEnd(R result) {
		String name = getOperationName();
		logger.info("Notify from " + Thread.currentThread().getName() + " on " + name);
		notifyResult(new OperationResult<>(result));
	}

	private void notifyResult(OperationResult<R> result) {
		this.result = result;
		synchronized (lock) {
			lock.notify();
		}
	}
	
	public final void signalUserCancel() {
		notifyResult(new OperationResult<>(OperationStatus.USER_CANCEL));
	}

	private String getOperationName() {
		return this.getClass().getSimpleName();
	}

	public void init(Object... params) {

	}

}
