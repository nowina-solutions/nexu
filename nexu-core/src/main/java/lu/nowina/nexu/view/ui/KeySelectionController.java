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
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.esig.dss.token.DSSPrivateKeyEntry;
import eu.europa.esig.dss.x509.CertificateToken;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import lu.nowina.nexu.view.core.AbstractUIOperationController;

public class KeySelectionController extends AbstractUIOperationController<DSSPrivateKeyEntry> implements Initializable {

	private static final Logger logger = LoggerFactory.getLogger(KeySelectionController.class.getName());

	@FXML
	private Button select;

	@FXML
	private Button cancel;

	@FXML
	private ListView<DSSPrivateKeyEntry> listView;
	
	@FXML
	private TextArea taX500Principal;
	
	@FXML
	private Label startDate;
	
	@FXML
	private Label endDate;
	
	@FXML
	private Label usage;

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
			signalUserCancel();
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
		
		listView.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				DSSPrivateKeyEntry key = listView.getSelectionModel().getSelectedItem();
				if(key != null) {
					CertificateToken token = listView.getSelectionModel().getSelectedItem().getCertificate();
					taX500Principal.setText(token.getSubjectX500Principal().toString().replace(", ", "\n"));
					
					SimpleDateFormat format = new SimpleDateFormat("dd MMMMMM yyyy");
					startDate.setText(format.format(token.getNotBefore()));
					endDate.setText(format.format(token.getNotAfter()));
					usage.setText(createKeyUsageString(token, resources));
				}
			}
			
		});
		
		taX500Principal.setEditable(false);
		taX500Principal.setMouseTransparent(true);
		taX500Principal.setFocusTraversable(false);
	}
	
	private String createKeyUsageString(CertificateToken token, ResourceBundle resources) {
		StringBuilder builder = new StringBuilder();
		boolean[] keyUsages = token.getCertificate().getKeyUsage();
		if(keyUsages[0]) {
			builder.append(resources.getString("keyUsage.digitalSignature") + "\n");
		}
		if(keyUsages[1]) {
			builder.append(resources.getString("keyUsage.nonRepudiation") + "\n");
		}
		if(keyUsages[2]) {
			builder.append(resources.getString("keyUsage.keyEncipherment") + "\n");
		}
		if(keyUsages[3]) {
			builder.append(resources.getString("keyUsage.dataEncipherment") + "\n");
		}
		if(keyUsages[4]) {
			builder.append(resources.getString("keyUsage.keyAgreement") + "\n");
		}
		if(keyUsages[5]) {
			builder.append(resources.getString("keyUsage.keyCertSign") + "\n");
		}
		if(keyUsages[6]) {
			builder.append(resources.getString("keyUsage.crlSign") + "\n");
		}
		if(keyUsages[7]) {
			builder.append(resources.getString("keyUsage.encipherOnly") + "\n");
		}
		if(keyUsages[8]) {
			builder.append(resources.getString("keyUsage.decipherOnly") + "\n");
		}
		return builder.toString();
	}

	@Override
	@SuppressWarnings("unchecked")
	public void init(Object... params) {
		List<DSSPrivateKeyEntry> keys = (List<DSSPrivateKeyEntry>) params[0];
		ObservableList<DSSPrivateKeyEntry> items = FXCollections.observableArrayList(keys);
		listView.setItems(items);
	}

}
