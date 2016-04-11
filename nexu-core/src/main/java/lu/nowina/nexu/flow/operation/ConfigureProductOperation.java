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

import java.net.URL;

import lu.nowina.nexu.api.Product;
import lu.nowina.nexu.api.ProductAdapter;
import lu.nowina.nexu.api.flow.OperationResult;
import lu.nowina.nexu.view.core.UIOperation;

/**
 * This {@link CompositeOperation} allows a {@link ProductAdapter} to configure a {@link Product}.
 *
 * <p>Expected parameters:
 * <ol>
 * <li>{@link Product}</li>
 * <li>{@link ProductAdapter}</li>
 * </ol>
 *
 * @author Jean Lepropre (jean.lepropre@nowina.lu)
 */
public class ConfigureProductOperation extends AbstractCompositeOperation<Product> {

	private Product product;
	private ProductAdapter productAdapter;
	
	@Override
	public void setParams(Object... params) {
		try {
			this.product = (Product) params[0];
			this.productAdapter = (ProductAdapter) params[1];
		} catch(final ArrayIndexOutOfBoundsException | ClassCastException e) {
			throw new IllegalArgumentException("Expected parameters: Product, ProductAdapter");
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public OperationResult<Product> perform() {
		final URL url = productAdapter.getFXMLConfigurationURL(product);
		if(url != null) {
			return operationFactory.getOperation(UIOperation.class, url.toString(), product).perform();
		} else {
			return new OperationResult<Product>(product);
		}
	}
}
