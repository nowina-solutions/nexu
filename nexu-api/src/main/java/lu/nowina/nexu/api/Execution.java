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
package lu.nowina.nexu.api;

import lu.nowina.nexu.api.flow.OperationStatus;

/**
 * 
 * @author david.naramski
 *
 * @param <T>
 */
public class Execution<T> {

	private final boolean success;

	private final T response;

	private final String error;

	private final String errorMessage;

	private Feedback feedback;

	public Execution(T response) {
		this.response = response;
		this.success = true;
		this.error = null;
		this.errorMessage = null;
	}

	public Execution(final OperationStatus errorOperationStatus) {
		this.success = false;
		this.error = errorOperationStatus.getCode();
		this.errorMessage = errorOperationStatus.getLabel();
		this.response = null;
	}
	
	public T getResponse() {
		return response;
	}

	public boolean isSuccess() {
		return success;
	}

	public String getError() {
		return error;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public Feedback getFeedback() {
		return feedback;
	}

	public void setFeedback(Feedback feedback) {
		this.feedback = feedback;
	}

}
