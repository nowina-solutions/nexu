/**
 * © Nowina Solutions, 2015-2016
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
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import lu.nowina.nexu.api.ConfiguredKeystore;
import lu.nowina.nexu.api.KeystoreType;
import lu.nowina.nexu.keystore.KeystoreDatabase;
import lu.nowina.nexu.view.core.AbstractUIOperationController;

/**
 * Allow to manage saved keystores.
 *
 * @author Jean Lepropre (jean.lepropre@nowina.lu)
 */
public class ManageKeystoresController extends AbstractUIOperationController<Void> implements Initializable {

	@FXML
	private Button remove;

	@FXML
	private TableView<ConfiguredKeystore> keystoresTable;
	
	@FXML
	private TableColumn<ConfiguredKeystore, String> keystoreNameTableColumn;
	
	@FXML
	private TableColumn<ConfiguredKeystore, KeystoreType> keystoreTypeTableColumn;
	
	@FXML
	private Label keystoreURL;

	private final ObservableList<ConfiguredKeystore> observableKeystores;
	
	private KeystoreDatabase database;
	
	public ManageKeystoresController() {
		super();
		observableKeystores = FXCollections.observableArrayList();
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		keystoresTable.setPlaceholder(new Label(resources.getString("table.view.no.content")));
		keystoresTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		keystoreNameTableColumn.setCellValueFactory((param) -> {
			final String url = param.getValue().getUrl();
			return new ReadOnlyStringWrapper(url.substring(url.lastIndexOf('/') + 1));
		});
		keystoreTypeTableColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
		keystoresTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			if(newValue != null) {
				keystoreURL.setText(newValue.getUrl());
			} else {
				keystoreURL.setText(null);
			}
		});
		keystoresTable.setItems(observableKeystores);

		remove.disableProperty().bind(keystoresTable.getSelectionModel().selectedItemProperty().isNull());
		remove.setOnAction((event) -> {
			observableKeystores.remove(keystoresTable.getSelectionModel().getSelectedItem());
		});
		
		observableKeystores.addListener((ListChangeListener<ConfiguredKeystore>)(c) -> {
			while(c.next()) {
				for(final ConfiguredKeystore removed : c.getRemoved()) {
					database.remove(removed);
				}
			}
		});
	}
	
	@Override
	public void init(Object... params) {
		database = (KeystoreDatabase) params[0];
		Platform.runLater(() -> {
			observableKeystores.setAll(database.getKeystores());
		});
	}

}
