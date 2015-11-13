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

/**
 * Basic implementation of {@link OperationFactory} that uses reflection.
 *
 * @author Jean Lepropre (jean.lepropre@nowina.lu)
 */
public class BasicOperationFactory implements OperationFactory {

	public BasicOperationFactory() {
		super();
	}

	@Override
	public <R, T extends Operation<R>> Operation<R> getOperation(Class<T> clazz, Object... params) {
		try {
			final T operation = clazz.newInstance();
			operation.setParams(params);
			return operation;
		} catch (InstantiationException e) {
			throw new IllegalArgumentException(e);
		} catch (IllegalAccessException e) {
			throw new IllegalArgumentException(e);
		}
	}

	@Override
	public <R, T extends CompositeOperation<R>> CompositeOperation<R> getCompositeOperation(
			Class<T> clazz, Object... params) {
		try {
			final T operation = clazz.newInstance();
			operation.setParams(params);
			operation.setOperationFactory(this);
			return operation;
		} catch (InstantiationException e) {
			throw new IllegalArgumentException(e);
		} catch (IllegalAccessException e) {
			throw new IllegalArgumentException(e);
		}
	}

}
