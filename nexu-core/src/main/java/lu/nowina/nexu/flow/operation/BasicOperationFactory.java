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
package lu.nowina.nexu.flow.operation;

import lu.nowina.nexu.api.flow.Operation;
import lu.nowina.nexu.api.flow.OperationFactory;
import lu.nowina.nexu.view.core.UIDisplay;

/**
 * Basic implementation of {@link OperationFactory} that uses reflection.
 *
 * @author Jean Lepropre (jean.lepropre@nowina.lu)
 */
public class BasicOperationFactory implements OperationFactory {

    private UIDisplay display;

    public BasicOperationFactory() {
        super();
    }

    @Override
    public <R, T extends Operation<R>> Operation<R> getOperation(final Class<T> clazz, final Object... params) {
        try {
            final T operation = clazz.newInstance();
            if (operation instanceof CompositeOperation) {
                final CompositeOperation<R> compositeOperation = (CompositeOperation<R>) operation;
                compositeOperation.setOperationFactory(this);
                compositeOperation.setDisplay(this.display);
            } else if (operation instanceof UIDisplayAwareOperation) {
                final UIDisplayAwareOperation<R> uiDisplayAwareOperation = (UIDisplayAwareOperation<R>) operation;
                uiDisplayAwareOperation.setDisplay(this.display);
            }
            operation.setParams(params);
            return operation;
        } catch (final InstantiationException e) {
            throw new IllegalArgumentException(e);
        } catch (final IllegalAccessException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public void setDisplay(final UIDisplay display) {
        this.display = display;
    }
}
