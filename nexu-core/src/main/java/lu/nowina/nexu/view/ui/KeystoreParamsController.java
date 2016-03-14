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

import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import lu.nowina.nexu.model.KeystoreParams;
import lu.nowina.nexu.model.KeystoreType;
import lu.nowina.nexu.view.core.AbstractUIOperationController;
import lu.nowina.nexu.view.core.ExtensionFilter;

public class KeystoreParamsController extends AbstractUIOperationController<KeystoreParams> implements Initializable {

	@FXML
	private Button ok;

	@FXML
	private Button cancel;

	@FXML
	private Button selectFile;

	@FXML
	private PasswordField password;

	private File keystoreFile;
	private BooleanProperty keystoreFileSpecified;

	public KeystoreParamsController() {
		keystoreFileSpecified = new SimpleBooleanProperty(false);
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		ok.setOnAction((event) -> {
			final KeystoreParams result = new KeystoreParams(keystoreFile, password.getText(),
					(keystoreFile.getName().toLowerCase().endsWith(".jks") ? KeystoreType.JKS : KeystoreType.PKCS12)
			);
			signalEnd(result);
		});
		ok.disableProperty().bind(Bindings.not(keystoreFileSpecified));
		cancel.setOnAction((e) -> {
			signalUserCancel();
		});
		selectFile.setOnAction((e) -> {
			keystoreFile = getDisplay().displayFileChooser(
					new ExtensionFilter("PKCS12", "*.p12", "*.pfx", "*.P12", "*.PFX"),
					new ExtensionFilter("JKS", "*.jks", "*.JKS")
			);
			keystoreFileSpecified.set(keystoreFile != null);
		});
	}

}
