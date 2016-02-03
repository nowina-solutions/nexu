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

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import lu.nowina.nexu.api.ScAPI;
import lu.nowina.nexu.view.core.AbstractUIOperationController;

public class ProductSelectionController extends AbstractUIOperationController<ScAPI> implements Initializable {

	@FXML
	private Button select;

	@FXML
	private Button cancel;

	@FXML
	private RadioButton mscapi;

	@FXML
	private RadioButton pkcs11;

	@FXML
	private RadioButton pkcs12;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		select.setOnAction((e) -> {
			signalEnd(getSelectedAPI());
		});
		cancel.setOnAction((e) -> {
			signalUserCancel();
		});
	}

	public ScAPI getSelectedAPI() {
		if (mscapi.isSelected()) {
			return ScAPI.MSCAPI;
		} else if (pkcs11.isSelected()) {
			return ScAPI.PKCS_11;
		} else if (pkcs12.isSelected()) {
			return ScAPI.PKCS_12;
		}
		return null;
	}

}
