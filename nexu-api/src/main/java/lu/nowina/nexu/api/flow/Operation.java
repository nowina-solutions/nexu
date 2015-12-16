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
package lu.nowina.nexu.api.flow;

/**
 * An <code>Operation</code> is an element of a {@link Flow}.
 * 
 * <p>Each sub-class is expected to provide a no-arg constructor (parameters will
 * be provided thanks to the {@link #setParams(Object...)} method).
 *
 * @param <R> The return type of the operation.
 *
 * @author Jean Lepropre (jean.lepropre@nowina.lu)
 * 
 * @see CompositeOperation
 */
public interface Operation<R> {

	/**
	 * Sets the parameters of the operation.
	 * @param params The parameters of the operation. It can be <code>null</null>
	 * if the operation does not accept any parameter.
	 * @throws IllegalArgumentException If <code>params</code> are not the expected parameters.
	 */
	void setParams(Object... params);
	
	/**
	 * Performs the operation and returns its result.
	 * @return The result of the operation.
	 */
	OperationResult<R> perform();
	
}
