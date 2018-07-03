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
import javafx.scene.control.ComboBox;
import lu.nowina.nexu.NexuException;
import lu.nowina.nexu.api.ConfiguredKeystore;
import lu.nowina.nexu.api.KeystoreType;
import lu.nowina.nexu.flow.StageHelper;
import lu.nowina.nexu.view.core.AbstractUIOperationController;
import lu.nowina.nexu.view.core.ExtensionFilter;

public class ConfigureKeystoreController extends AbstractUIOperationController<ConfiguredKeystore> implements Initializable {

	@FXML
	private Button ok;

	@FXML
	private Button cancel;

	@FXML
	private Button selectFile;

	@FXML
	private ComboBox<KeystoreType> keystoreType;
	
	private File keystoreFile;
	private final BooleanProperty keystoreFileSpecified;

	public ConfigureKeystoreController() {
		keystoreFileSpecified = new SimpleBooleanProperty(false);
	}
	
	@Override
	public void init(Object... params) {
		StageHelper.getInstance().setTitle((String)params[0], "save.keystore.title");
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {


		ok.setOnAction((event) -> {
			final ConfiguredKeystore result = new ConfiguredKeystore();
			try {
				result.setUrl(keystoreFile.toURI().toURL().toString());
			} catch (Exception e1) {
				throw new NexuException(e1);
			}
			result.setType(keystoreType.getValue());
			result.setToBeSaved(true);
			signalEnd(result);
		});
		ok.disableProperty().bind(Bindings.not(keystoreFileSpecified));
		cancel.setOnAction(e -> signalUserCancel());
		selectFile.setOnAction(e -> {
			final ExtensionFilter extensionFilter;
			switch(keystoreType.getValue()) {
			case JKS:
				extensionFilter = new ExtensionFilter("JKS", "*.jks", "*.JKS");
				break;
			case PKCS12:
				extensionFilter = new ExtensionFilter("PKCS12", "*.p12", "*.pfx", "*.P12", "*.PFX");
				break;
			default:
				throw new IllegalArgumentException("Unknown keystore type: " +
						keystoreType.getValue());
			}
			keystoreFile = getDisplay().displayFileChooser(extensionFilter);
			keystoreFileSpecified.set(keystoreFile != null);
		});
		selectFile.disableProperty().bind(keystoreType.valueProperty().isNull());
		
		keystoreType.getItems().setAll(KeystoreType.values());
	}

}
