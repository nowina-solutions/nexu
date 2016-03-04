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

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import lu.nowina.nexu.model.Pkcs11Params;
import lu.nowina.nexu.view.core.AbstractUIOperationController;

public class Pkcs11ParamsController extends AbstractUIOperationController<Pkcs11Params> implements Initializable {

	@FXML
	private Button ok;

	@FXML
	private Button cancel;

	@FXML
	private Button selectFile;

	private File pkcs11File;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		ok.setOnAction((event) -> {
			Pkcs11Params result = new Pkcs11Params();
			result.setPkcs11Lib(pkcs11File);
			signalEnd(result);
		});
		cancel.setOnAction((e) -> {
			signalUserCancel();
		});
		selectFile.setOnAction((e) -> {
			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle(resources.getString("fileChooser.title.openResourceFile"));
			fileChooser.getExtensionFilters().addAll(new ExtensionFilter("DLL", "*.dll"));
			pkcs11File = fileChooser.showOpenDialog(null);
		});
	}

}
