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

import java.util.ArrayList;
import java.util.List;

import lu.nowina.nexu.api.Match;
import lu.nowina.nexu.api.NexuAPI;
import lu.nowina.nexu.api.Product;
import lu.nowina.nexu.api.flow.OperationResult;

/**
 * This {@link CompositeOperation} allows to get a list of {@link Match}.
 *
 * <p>Expected parameters:
 * <ol>
 * <li>List of {@link Product}.</li>
 * <li>{@link NexuAPI}</li>
 * </ol>
 *
 * @author Jean Lepropre (jean.lepropre@nowina.lu)
 */
public class GetMatchingProductAdaptersOperation extends AbstractCompositeOperation<List<Match>> {

	private List<Product> products;
	private NexuAPI api;
	
	public GetMatchingProductAdaptersOperation() {
		super();
	}

	@Override
	@SuppressWarnings("unchecked")
	public void setParams(Object... params) {
		try {
			this.products = (List<Product>) params[0];
			this.api = (NexuAPI) params[1];
		} catch(final ArrayIndexOutOfBoundsException | ClassCastException e) {
			throw new IllegalArgumentException("Expected parameters: list of Product, NexuAPI");
		}
	}

	@Override
	public OperationResult<List<Match>> perform() {
		if (products.size() == 0) {
			return new OperationResult<List<Match>>(CoreOperationStatus.NO_PRODUCT_FOUND);
		} else {
			return getMatchingCardAdapters(products);
		}
	}
	
	private OperationResult<List<Match>> getMatchingCardAdapters(final List<Product> products) {
		final List<Match> matchingCardAdapters = new ArrayList<Match>();
		for (final Product p : products) {
			matchingCardAdapters.addAll(api.matchingProductAdapters(p));
		}
		return new OperationResult<List<Match>>(matchingCardAdapters);
	}
}
