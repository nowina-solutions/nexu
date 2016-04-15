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
 * Allows to get instances of {@link Operation}.
 *
 * @author Jean Lepropre (jean.lepropre@nowina.lu)
 */
public interface OperationFactory {

	/**
	 * Returns an instance of {@link Operation} initialized with <code>params</code>.
	 * <p>The type of the returned instance is guaranteed to implement {@link Operation}
	 * or one of its sub-interface but its concrete type could be different from <code>clazz</code>.
	 * @param clazz The target operation type.
	 * @param params The parameters to use to initialize the operation.
	 * @return An instance of {@link Operation} initialized with <code>params</code>.
	 */
	<R, T extends Operation<R>> Operation<R> getOperation(Class<T> clazz, Object... params);
	
}
