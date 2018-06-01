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
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import lu.nowina.nexu.NexuException;
import lu.nowina.nexu.api.ConfiguredKeystore;
import lu.nowina.nexu.api.KeystoreType;
import lu.nowina.nexu.view.core.AbstractUIOperationController;
import lu.nowina.nexu.view.core.ExtensionFilter;

public class ConfigureKeystoreController extends AbstractUIOperationController<ConfiguredKeystore>
		implements Initializable {

	@FXML
	private Button ok;

	@FXML
	private Button cancel;

	@FXML
	private Button selectFile;

	@FXML
	private ComboBox<KeystoreType> keystoreType;

	@FXML
	private Label slotIndexLabel;

	@FXML
	private TextField slotIndexField;

	private File keystoreFile;
	private final BooleanProperty keystoreFileSpecified;

	private final BooleanProperty pkcs11Selected;
	private final BooleanProperty validSlotNumberEntered;

	public ConfigureKeystoreController() {
		keystoreFileSpecified = new SimpleBooleanProperty(false);
		pkcs11Selected = new SimpleBooleanProperty(false);
		validSlotNumberEntered = new SimpleBooleanProperty(false);
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		// Slot number management
		slotIndexLabel.visibleProperty().bind(pkcs11Selected);
		slotIndexField.visibleProperty().bind(pkcs11Selected);

		// Accept only numbers!
		slotIndexField.textProperty().addListener( //
				(ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
					if (!newValue.matches("\\d*")) {
						slotIndexField.setText(newValue.replaceAll("[^\\d]", ""));
					}
					if (slotIndexField.getText().trim().isEmpty()) {
						validSlotNumberEntered.setValue(Boolean.FALSE);
					} else {
						validSlotNumberEntered.setValue(Boolean.TRUE);
					}
				});

		keystoreType.setOnAction((ActionEvent e) -> {
			ComboBox<KeystoreType> combobox = (ComboBox<KeystoreType>) e.getSource();
			if (combobox.getValue().equals(KeystoreType.PKCS11)) {
				pkcs11Selected.setValue(Boolean.TRUE);
			} else {
				pkcs11Selected.setValue(Boolean.FALSE);
			}
		});

		ok.setOnAction((event) -> {
			final ConfiguredKeystore result = new ConfiguredKeystore();
			try {
				result.setUrl(keystoreFile.toURI().toURL().toString());
				result.setSlotNumber(Integer.parseInt(slotIndexField.getText()));
			} catch (Exception e1) {
				throw new NexuException(e1);
			}
			result.setType(keystoreType.getValue());
			result.setToBeSaved(true);
			signalEnd(result);
		});
		ok.disableProperty().bind(Bindings.not(keystoreFileSpecified) //
				.or(Bindings.and(pkcs11Selected, Bindings.not(validSlotNumberEntered))));

		cancel.setOnAction((e) -> {
			signalUserCancel();
		});
		selectFile.setOnAction((e) -> {
			final ExtensionFilter extensionFilter;
			switch (keystoreType.getValue()) {
			case JKS:
				extensionFilter = new ExtensionFilter("JKS", "*.jks", "*.JKS");
				break;
			case PKCS12:
				extensionFilter = new ExtensionFilter("PKCS12", "*.p12", "*.pfx", "*.P12", "*.PFX");
				break;
			case PKCS11:
				extensionFilter = new ExtensionFilter("PKCS11", "*.dll", "*.so", "*.DLL", "*.SO");
				break;
			default:
				throw new IllegalArgumentException("Unknown keystore type: " + keystoreType.getValue());
			}
			keystoreFile = getDisplay().displayFileChooser(extensionFilter);
			keystoreFileSpecified.set(keystoreFile != null);
		});
		selectFile.disableProperty().bind(keystoreType.valueProperty().isNull());

		keystoreType.getItems().setAll(KeystoreType.values());
	}
}