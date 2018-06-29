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
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import lu.nowina.nexu.api.EnvironmentInfo;
import lu.nowina.nexu.api.OS;
import lu.nowina.nexu.api.ScAPI;
import lu.nowina.nexu.flow.StageHelper;
import lu.nowina.nexu.view.core.AbstractUIOperationController;

public class APISelectionController extends AbstractUIOperationController<ScAPI> implements Initializable {

	private static final boolean IS_WINDOWS =
			EnvironmentInfo.buildFromSystemProperties(System.getProperties()).getOs().equals(OS.WINDOWS);
	
	@FXML
	private Button select;

	@FXML
	private Button cancel;

	@FXML
	private RadioButton mscapi;

	@FXML
	private RadioButton pkcs11;

	@FXML
	private RadioButton mocca;

	@FXML
	private ToggleGroup api;

	@FXML
	private Label message;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		select.setOnAction((e) -> {
			signalEnd(getSelectedAPI());
		});
		cancel.setOnAction((e) -> {
			signalUserCancel();
		});
		
		select.disableProperty().bind(api.selectedToggleProperty().isNull());
		
		if(!IS_WINDOWS) {
			mscapi.setVisible(false);
			mscapi.setManaged(false);
		}
	}

	private ScAPI getSelectedAPI() {
		if (mscapi.isSelected()) {
			if(!IS_WINDOWS) {
				throw new IllegalStateException("MSCAPI not supported on platforms other than Windows!");
			}
			return ScAPI.MSCAPI;
		} else if (pkcs11.isSelected()) {
			return ScAPI.PKCS_11;
		} else if (mocca.isSelected()) {
			return ScAPI.MOCCA;
		}
		return null;
	}

	@Override
	public final void init(Object... params) {
		StageHelper.getInstance().setTitle((String) params[0], "api.selection.title");
		Platform.runLater(() -> 
			message.setText(MessageFormat.format(
					ResourceBundle.getBundle("bundles/nexu").getString("api.selection.header"),
					params[0]))
		);
	}
}
