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
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import lu.nowina.nexu.flow.StageHelper;
import lu.nowina.nexu.view.core.AbstractUIOperationController;

public class MessageController extends AbstractUIOperationController<Void> implements Initializable {

	@FXML
	private Label message;

	@FXML
	private Button ok;

	private String defaultErrorText;

	private ResourceBundle resources;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		if (ok != null) {
			ok.setOnAction(e -> signalEnd(null));
		}
		defaultErrorText = resources.getString("error");
		this.resources = resources;
	}

	@Override
	public void init(Object... params) {
		if (params.length > 0) {
			final String value = (String) params[0];
			if (value != null) {
				message.setText(
						MessageFormat.format(resources.getString(value), Arrays.copyOfRange(params, 1, params.length)));
			}
			StageHelper.getInstance().setTitle((String) params[1], "message.title");
		} else {
			StageHelper.getInstance().setTitle("", "message.title");
			message.setText(defaultErrorText);
		}
	}

}
