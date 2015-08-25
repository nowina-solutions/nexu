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
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import eu.europa.esig.dss.token.DSSPrivateKeyEntry;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import lu.nowina.nexu.view.core.UIOperation;

public class KeySelectionController extends UIOperation<DSSPrivateKeyEntry>implements Initializable {

	private static final Logger logger = Logger.getLogger(KeySelectionController.class.getName());

	@FXML
	private Button select;

	@FXML
	private Button cancel;

	@FXML
	private ListView<DSSPrivateKeyEntry> listView;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		select.setOnAction((event) -> {
			DSSPrivateKeyEntry selectedItem = listView.getSelectionModel().getSelectedItem();
			logger.info("Selected item " + selectedItem);
			if (selectedItem != null) {
				signalEnd(selectedItem);
			} else {
				signalEnd(null);
			}
		});
		cancel.setOnAction((e) -> {
			signalEnd(null);
		});
		listView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		listView.setCellFactory((param) -> {
			ListCell<DSSPrivateKeyEntry> cell = new ListCell<DSSPrivateKeyEntry>() {

				@Override
				protected void updateItem(DSSPrivateKeyEntry k, boolean bln) {
					super.updateItem(k, bln);
					if (k != null) {
						setText(k.getCertificate().getSubjectShortName());
					}
				}

			};

			return cell;
		});
	}

	@Override
	public void init(Object... params) {
		List<DSSPrivateKeyEntry> keys = (List<DSSPrivateKeyEntry>) params[0];
		ObservableList<DSSPrivateKeyEntry> items = FXCollections.observableArrayList(keys);
		listView.setItems(items);
	}

}
