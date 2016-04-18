/**
 * © Nowina Solutions, 2015-2016
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

import lu.nowina.nexu.api.NexuAPI;
import lu.nowina.nexu.api.Product;
import lu.nowina.nexu.api.ProductAdapter;
import lu.nowina.nexu.api.flow.OperationResult;

/**
 * This {@link CompositeOperation} allows a {@link ProductAdapter} to save a {@link Product}.
 *
 * <p>Expected parameters:
 * <ol>
 * <li>{@link ProductAdapter}</li>
 * <li>{@link Product}</li>
 * <li>{@link NexuAPI}</li>
 * </ol>
 *
 * @author Jean Lepropre (jean.lepropre@nowina.lu)
 */
public class SaveProductOperation extends AbstractCompositeOperation<Boolean> {

	private ProductAdapter adapter;
	private Product product;
	private NexuAPI api;
	
	@Override
	public void setParams(Object... params) {
		try {
			this.adapter = (ProductAdapter) params[0];
			this.product = (Product) params[1];
			this.api = (NexuAPI) params[2];
		} catch(final ArrayIndexOutOfBoundsException | ClassCastException e) {
			throw new IllegalArgumentException("Expected parameters: ProductAdapter, Product, NexuAPI");
		}
	}

	@Override
	public OperationResult<Boolean> perform() {
		return adapter.getSaveOperation(api, product).call(operationFactory);
	}
}
