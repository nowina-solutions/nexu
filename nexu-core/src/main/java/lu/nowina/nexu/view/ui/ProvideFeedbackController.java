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

import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import lu.nowina.nexu.api.Feedback;
import lu.nowina.nexu.api.FeedbackClient;
import lu.nowina.nexu.api.FeedbackStatus;
import lu.nowina.nexu.view.core.AbstractUIOperationController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProvideFeedbackController extends AbstractUIOperationController<Feedback> implements Initializable {

	private static final Logger logger = LoggerFactory.getLogger(ProvideFeedbackController.class.getName());

	@FXML
	private Button ok;

	@FXML
	private Button cancel;

	@FXML
	private Label label;

	@FXML
	private Label what;

	@FXML
	private TextArea userComment;

	private Feedback feedback;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		ok.setOnAction((e) -> {
			if (feedback != null && feedback.getFeedbackStatus() != FeedbackStatus.SUCCESS) {
				try {
					feedback.setUserComment(userComment.getText());
					FeedbackClient client = new FeedbackClient("http://lab.nowina.solutions/nexu/");
					client.reportError(feedback);
					signalEnd(feedback);
				} catch (Exception ex) {
					logger.error("Cannot send feedback", ex);
					signalEnd(null);
				}
			}
		});
		cancel.setOnAction((e) -> {
			signalUserCancel();
		});
		if (feedback != null && feedback.getFeedbackStatus() == FeedbackStatus.SUCCESS) {
			cancel.setVisible(false);
			userComment.setVisible(false);
			what.setVisible(false);
		}
	}

	@Override
	public void init(Object... params) {
		this.feedback = null;
		if (params.length > 0 && params[0] != null) {
			Feedback feedback = (Feedback) params[0];
			this.feedback = feedback;
			Platform.runLater(() -> {
				label.setText(feedback.getFeedbackStatus().toString());
			});
		}
	}

}
