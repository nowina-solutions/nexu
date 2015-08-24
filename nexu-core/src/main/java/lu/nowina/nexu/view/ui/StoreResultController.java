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

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import lu.nowina.nexu.api.Feedback;
import lu.nowina.nexu.view.core.UIOperation;

public class StoreResultController extends UIOperation<Feedback>implements Initializable {

	private static final Logger logger = Logger.getLogger(StoreResultController.class.getName());

	@FXML
	private Button store;

	@FXML
	private Button forget;

	@FXML
	private Label label;

	@FXML
	private CheckBox publish;

	private Feedback feedback;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		store.setOnAction((e) -> {
			logger.info("Store for " + feedback.getSelectedCard().getAtr() + " parameters: " + feedback.getSelectedAPI()
					+ " - " + feedback.getApiParameter());
			logger.info(new File(".").getAbsolutePath());
			if (publish.isSelected()) {
				try {
					signalEnd(feedback);
				} catch (Exception ex) {
					logger.log(Level.SEVERE, "Cannot send feedback", ex);
					signalEnd(feedback);
				}
			}
		});
		forget.setOnAction((e) -> {
			signalEnd(null);
		});
		publish.setSelected(true);
	}

	@Override
	public void init(Object... params) {

		if (params.length != 1) {
			throw new IllegalArgumentException("Feedback object expected");
		}

		Feedback feedback = (Feedback) params[0];
		if (feedback == null) {
			throw new IllegalArgumentException("Feedback object expected");
		}

		if (feedback.getSelectedCard() == null || feedback.getSelectedAPI() == null) {
			throw new IllegalArgumentException(
					"Invalid Feedback (card:" + feedback.getSelectedCard() + ",api:" + feedback.getSelectedAPI() + ")");
		}

		this.feedback = feedback;
		Platform.runLater(() -> {
			label.setText(feedback.getFeedbackStatus().toString());
		});

	}

}
