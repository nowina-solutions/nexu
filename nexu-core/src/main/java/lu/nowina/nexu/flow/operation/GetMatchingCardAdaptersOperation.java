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

import lu.nowina.nexu.api.DetectedCard;
import lu.nowina.nexu.api.Feedback;
import lu.nowina.nexu.api.FeedbackStatus;
import lu.nowina.nexu.api.Match;
import lu.nowina.nexu.api.NexuAPI;
import lu.nowina.nexu.api.flow.OperationResult;
import lu.nowina.nexu.api.flow.OperationStatus;
import lu.nowina.nexu.view.core.UIOperation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This {@link CompositeOperation} allows to get a list of {@link Match}.
 *
 * <p>Expected parameter: {@link NexuAPI}.
 *
 * @author Jean Lepropre (jean.lepropre@nowina.lu)
 */
public class GetMatchingCardAdaptersOperation extends AbstractCompositeOperation<List<Match>> {

	private static final Logger LOG = LoggerFactory.getLogger(GetMatchingCardAdaptersOperation.class.getName());

	private NexuAPI api;
	
	public GetMatchingCardAdaptersOperation() {
		super();
	}

	@Override
	public void setParams(Object... params) {
		try {
			this.api = (NexuAPI) params[0];
		} catch(final ArrayIndexOutOfBoundsException | ClassCastException e) {
			throw new IllegalArgumentException("Expected parameter: NexuAPI");
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public OperationResult<List<Match>> perform() {
		final List<DetectedCard> detectedCards = api.detectCards();
		LOG.info(detectedCards.size() + " card detected");

		if (detectedCards.size() == 0) {
			final Feedback feedback = new Feedback();
			feedback.setFeedbackStatus(FeedbackStatus.NO_PRODUCT_FOUND);
			operationFactory.getOperation(UIOperation.class, display, "/fxml/provide-feedback.fxml",
					new Object[]{feedback}).perform();
			return new OperationResult<List<Match>>(OperationStatus.FAILED);
		} else {
			if(detectedCards.size() > 1) {
				LOG.warn("More than one card. Not supported yet. We will take the first one having a matching adapter.");
			}
			return getMatchingCardAdapters(detectedCards);
		}
	}
	
	private OperationResult<List<Match>> getMatchingCardAdapters(final List<DetectedCard> detectedCards) {
		final List<Match> matchingCardAdapters = new ArrayList<Match>();
		for (final DetectedCard d : detectedCards) {
			matchingCardAdapters.addAll(api.matchingCardAdapters(d));
		}
		return new OperationResult<List<Match>>(matchingCardAdapters);
	}
}
