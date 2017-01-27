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
package lu.nowina.nexu;

/**
 * This exception is thrown when an operation is cancelled because
 * of any reasons (cancelled by user, timeout, etc.).
 *
 * @author Jean Lepropre (jean.lepropre@nowina.lu)
 */
public class CancelledOperationException extends NexuException {

	private static final long serialVersionUID = 1L;

	public CancelledOperationException() {
		super();
	}

	public CancelledOperationException(String message) {
		super(message);
	}

	public CancelledOperationException(Throwable cause) {
		super(cause);
	}

	public CancelledOperationException(String message, Throwable cause) {
		super(message, cause);
	}
	
}
