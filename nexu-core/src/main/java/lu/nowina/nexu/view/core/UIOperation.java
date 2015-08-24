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

import java.util.logging.Logger;

import lu.nowina.nexu.InternalAPI;

public abstract class UIOperation<R> {

	private static final Logger logger = Logger.getLogger(UIOperation.class.getName());

	private Object lock = new Object();

	private volatile R result = null;
	
	public final R waitEnd() throws InterruptedException {
		String name = getOperationName();

		logger.info("Thread " + Thread.currentThread().getName() + " wait on " + name);
		synchronized (lock) {
			lock.wait();
		}
		logger.info("Thread " + Thread.currentThread().getName() + " resumed on " + name);
		return result;
	}

	protected final void signalEnd(R result) {
		String name = getOperationName();

		this.result = result;
		logger.info("Notify from " + Thread.currentThread().getName() + " on " + name);
		synchronized (lock) {
			lock.notify();
		}
	}

	private String getOperationName() {
		return this.getClass().getSimpleName();
	}

	public void init(Object... params) {

	}

}
