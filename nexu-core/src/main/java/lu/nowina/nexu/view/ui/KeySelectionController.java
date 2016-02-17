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
import java.util.Locale;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.MouseButton;
import lu.nowina.nexu.view.core.AbstractUIOperationController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.esig.dss.token.DSSPrivateKeyEntry;
import eu.europa.esig.dss.x509.CertificateToken;

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
					
					SimpleDateFormat format = new SimpleDateFormat("dd MMMMMM yyyy", Locale.ENGLISH);
					startDate.setText(format.format(token.getNotBefore()));
					endDate.setText(format.format(token.getNotAfter()));
					usage.setText(createKeyUsageString(token));
				}
			}
			
		});
		
		taX500Principal.setEditable(false);
		taX500Principal.setMouseTransparent(true);
		taX500Principal.setFocusTraversable(false);
	}
	
	private String createKeyUsageString(CertificateToken token) {
		StringBuilder builder = new StringBuilder();
		boolean[] keyUsages = token.getCertificate().getKeyUsage();
		if(keyUsages[0]) {
			builder.append("Digital Signature\n");
		}
		if(keyUsages[1]) {
			builder.append("Non Repudiation\n");
		}
		if(keyUsages[2]) {
			builder.append("Key Encipherment\n");
		}
		if(keyUsages[3]) {
			builder.append("Data Encipherment\n");
		}
		if(keyUsages[4]) {
			builder.append("Key Agreement\n");
		}
		if(keyUsages[5]) {
			builder.append("Key Certificate Signature\n");
		}
		if(keyUsages[6]) {
			builder.append("CRL Signing\n");
		}
		if(keyUsages[7]) {
			builder.append("Encipher Only\n");
		}
		if(keyUsages[8]) {
			builder.append("Decipher Only\n");
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
