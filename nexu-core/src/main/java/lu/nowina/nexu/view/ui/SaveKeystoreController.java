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
import lu.nowina.nexu.api.ConfiguredKeystore;
import lu.nowina.nexu.keystore.KeystoreProductAdapter;
import lu.nowina.nexu.view.core.AbstractUIOperationController;

public class SaveKeystoreController extends AbstractUIOperationController<Boolean> implements Initializable {

	@FXML
	private Button store;

	@FXML
	private Button forget;

	@FXML
	private Label message;
	
	@FXML
	private Label keystoreFilename;

	@FXML
	private Label keystoreType;
	
	private ResourceBundle resources;
	
	private KeystoreProductAdapter productAdapter;
	private ConfiguredKeystore keystore;
	
	public SaveKeystoreController() {
		super();
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.resources = resources;
		store.setOnAction((event) -> {
			productAdapter.saveKeystore(keystore);
			signalEnd(true);
		});
		forget.setOnAction((e) -> {
			signalEnd(false);
		});
	}

	@Override
	public void init(Object... params) {
		this.productAdapter = (KeystoreProductAdapter) params[1];
		this.keystore = (ConfiguredKeystore) params[2];
		Platform.runLater(() -> {
			message.setText(MessageFormat.format(
					resources.getString("save.keystore.header"),
					params[0]));
			keystoreType.setText(keystore.getType().getLabel());
			keystoreFilename.setText(keystore.getUrl().substring(keystore.getUrl().lastIndexOf('/')+1));
		});
	}
}
