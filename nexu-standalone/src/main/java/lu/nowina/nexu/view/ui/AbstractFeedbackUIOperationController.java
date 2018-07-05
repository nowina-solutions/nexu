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
package lu.nowina.nexu.view.ui;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lu.nowina.nexu.api.AppConfig;
import lu.nowina.nexu.api.Feedback;
import lu.nowina.nexu.view.core.AbstractUIOperationController;
import lu.nowina.nexu.view.core.UIOperationController;

/**
 * Convenient base class for {@link UIOperationController} whose result is a feedback that can be provided to {@link FeedbackClient}. 
 *
 * @author Jean Lepropre (jean.lepropre@nowina.lu)
 */
public abstract class AbstractFeedbackUIOperationController extends AbstractUIOperationController<Feedback> {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractFeedbackUIOperationController.class);
	
	private Feedback feedback;
	private String serverUrl;
	private String applicationVersion;
	private String applicationName;
	// Needed by ProvideFeedbackController, in order to get JIRA's url as well as other potentially useful information
	private AppConfig appConfig;
	
	@Override
	public final void init(Object... params) {
		try {
			feedback = (Feedback) params[0];
			serverUrl = (String) params[1];
			applicationVersion = (String) params[2];
			applicationName = (String) params[3];
			appConfig = (AppConfig) params[4];
		} catch(final ClassCastException | ArrayIndexOutOfBoundsException e) {
			throw new IllegalArgumentException("Expected parameters: Feedback, serverUrl (String), application version (String), application name (String), and application configuration (AppConfig)");
		}
		
		if((feedback == null) || (serverUrl == null) || (applicationVersion == null) || (applicationName == null)) {
			throw new IllegalArgumentException("Expected parameters: Feedback, serverUrl (String), application version (String), application name (String), and application configuration (AppConfig)");
		}

		if(params.length > 5) {
			doInit(Arrays.copyOfRange(params, 5, params.length));
		} else {
			doInit((Object) null); 
		}
	}

	/**
	 * Allows subclasses to use additional parameters or perform some specific initialization.
	 * 
	 * <p>This implementation does nothing.
	 * 
	 * @param params Additional parameters of the concrete controller.
	 */
	protected void doInit(Object... params) {
		// Do nothing by contract
	}
	
	protected Feedback getFeedback() {
		return feedback;
	}
	
	protected String getApplicationName() {
		return applicationName;
	}

	public AppConfig getAppConfig() {
		return appConfig;
	}

}
