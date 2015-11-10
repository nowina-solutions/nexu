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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.esig.dss.token.SignatureTokenConnection;
import lu.nowina.nexu.api.CardAdapter;
import lu.nowina.nexu.api.DetectedCard;
import lu.nowina.nexu.api.EnvironmentInfo;
import lu.nowina.nexu.api.Execution;
import lu.nowina.nexu.api.GetCertificateRequest;
import lu.nowina.nexu.api.GetCertificateResponse;
import lu.nowina.nexu.api.Match;
import lu.nowina.nexu.api.NexuAPI;
import lu.nowina.nexu.api.ScAPI;
import lu.nowina.nexu.api.SignatureRequest;
import lu.nowina.nexu.api.SignatureResponse;
import lu.nowina.nexu.api.TokenId;
import lu.nowina.nexu.api.plugin.HttpPlugin;
import lu.nowina.nexu.flow.FlowRegistry;
import lu.nowina.nexu.generic.ConnectionInfo;
import lu.nowina.nexu.generic.DatabaseWebLoader;
import lu.nowina.nexu.generic.GenericCardAdapter;
import lu.nowina.nexu.generic.SCDatabase;
import lu.nowina.nexu.generic.SCInfo;
import lu.nowina.nexu.view.core.UIDisplay;
import lu.nowina.nexu.view.core.UIFlow;

/**
 * Implementation of the NexuAPI
 * 
 * @author David Naramski
 *
 */
public class InternalAPI implements NexuAPI {

	private Logger logger = LoggerFactory.getLogger(InternalAPI.class.getName());

	private UserPreferences prefs;

	private CardDetector detector;

	private List<CardAdapter> adapters = new ArrayList<>();

	private Map<TokenId, SignatureTokenConnection> connections = new HashMap<>();

	private Map<String, HttpPlugin> httpPlugins = new HashMap<>();

	private UIDisplay display;

	private SCDatabase myDatabase;

	private DatabaseWebLoader webDatabase;
	
	private FlowRegistry flowRegistry;

	public InternalAPI(UIDisplay display, UserPreferences prefs, SCDatabase store, CardDetector detector, DatabaseWebLoader webLoader, FlowRegistry flowRegistry) {
		this.display = display;
		this.prefs = prefs;
		this.myDatabase = store;
		this.detector = detector;
		this.webDatabase = webLoader;
		this.flowRegistry = flowRegistry;
	}

	@Override
	public List<DetectedCard> detectCards() {
		return detector.detectCard();
	}

	@Override
	public List<Match> matchingCardAdapters(DetectedCard d) {
		if (d == null) {
			logger.warn("DetectedCard argument should not be null");
			return Collections.emptyList();
		}
		List<Match> cards = new ArrayList<>();
		for (CardAdapter card : adapters) {
			if (card.accept(d)) {
				logger.info("Card is instance of " + card.getClass().getSimpleName());
				cards.add(new Match(card, d));
			}
		}
		if (cards.isEmpty()) {
			SCInfo info = null;
			if (webDatabase != null && webDatabase.getDatabase() != null) {
				info = myDatabase.getInfo(d.getAtr());
				if (info == null) {
					logger.warn("Card " + d.getAtr() + " is not in the web database");
				} else {
					cards.add(new Match(new GenericCardAdapter(info), d));
				}

			}
			if (info == null && myDatabase != null) {
				info = myDatabase.getInfo(d.getAtr());
				if (info == null) {
					logger.warn("Card " + d.getAtr() + " is not in the personal database");
				} else {
					cards.add(new Match(new GenericCardAdapter(info), d));
				}
			}
		}
		return cards;
	}

	@Override
	public void registerCardAdapter(CardAdapter adapter) {
		adapters.add(adapter);
	}

	@Override
	public EnvironmentInfo getEnvironmentInfo() {

		EnvironmentInfo info = EnvironmentInfo.buildFromSystemProperties(System.getProperties());
		return info;
	}

	@Override
	public TokenId registerTokenConnection(SignatureTokenConnection connection) {
		TokenId id = new TokenId();
		connections.put(id, connection);
		return id;
	}

	@Override
	public SignatureTokenConnection getTokenConnection(TokenId tokenId) {
		return connections.get(tokenId);
	}

	private <I, O> Execution<O> executeRequest(UIFlow<I, O> flow, I request) {

		Execution<O> resp = new Execution<>();
		try {

			O response = flow.execute(this, request);

			if (response != null) {
				resp.setSuccess(true);
				resp.setResponse(response);
			} else {
				resp.setSuccess(false);
				resp.setError("no_response");
				resp.setErrorMessage("No response");
			}

		} catch (Exception e) {
			logger.error("Cannot execute get certificates", e);
			resp.setSuccess(false);
			resp.setError("exception");
			resp.setErrorMessage("Exception during execution");
		}

		return resp;
	}

	@Override
	public Execution<GetCertificateResponse> getCertificate(GetCertificateRequest request) {

		UIFlow<GetCertificateRequest, GetCertificateResponse> flow = flowRegistry.getFlow(FlowRegistry.CERTIFICATE_FLOW, display);
		return executeRequest(flow, request);
	}

	@Override
	public Execution<SignatureResponse> sign(SignatureRequest request) {

		UIFlow<SignatureRequest, SignatureResponse> flow = flowRegistry.getFlow(FlowRegistry.CERTIFICATE_FLOW, display);
		return executeRequest(flow, request);

	}

	public HttpPlugin getPlugin(String context) {
		return httpPlugins.get(context);
	}

	public void registerHttpContext(String context, HttpPlugin plugin) {
		httpPlugins.put(context, plugin);
	}

	public void store(String detectedAtr, ScAPI selectedApi, String apiParam) {
		if (myDatabase != null) {

			EnvironmentInfo env = getEnvironmentInfo();
			ConnectionInfo cInfo = new ConnectionInfo();
			cInfo.setSelectedApi(selectedApi);
			cInfo.setEnv(env);
			cInfo.setApiParam(apiParam);

			myDatabase.add(detectedAtr, cInfo);
		}
	}

}
