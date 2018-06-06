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

import java.awt.Desktop;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.cert.CertificateEncodingException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.esig.dss.DSSASN1Utils;
import eu.europa.esig.dss.token.DSSPrivateKeyEntry;
import eu.europa.esig.dss.x509.CertificateToken;
import javafx.application.HostServices;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lu.nowina.nexu.view.core.AbstractUIOperationController;

public class KeySelectionController extends AbstractUIOperationController<DSSPrivateKeyEntry> implements Initializable {

	private static final Logger logger = LoggerFactory.getLogger(KeySelectionController.class.getName());

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
			signalUserCancel();
		});
		listView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		listView.setCellFactory((param) -> {
			ListCell<DSSPrivateKeyEntry> cell = new ListCell<DSSPrivateKeyEntry>() {

				@Override
				protected void updateItem(DSSPrivateKeyEntry k, boolean bln) {
					super.updateItem(k, bln);
					if (k != null) {
						CertificateToken certificateToken = k.getCertificate();
						Label lSubject = new Label();
						lSubject.setText(DSSASN1Utils.getSubjectCommonName(k.getCertificate()));
						lSubject.setStyle("-fx-font-weight: bold;");
						Label lEmitter = new Label();
						lEmitter.setText(String.format("Issuer: %s - Usage: %s", DSSASN1Utils.get(certificateToken.getIssuerX500Principal()).get("2.5.4.3"), createKeyUsageString(certificateToken, resources)));
						Label lValidity = new Label();
						SimpleDateFormat format = new SimpleDateFormat("dd MMMMMM yyyy");
						String startDate = format.format(certificateToken.getNotBefore());
						String endDate = format.format(certificateToken.getNotAfter());
						lValidity.setText(String.format("Valid from: %s to: %s", startDate, endDate));
						
						Button button = new Button("Open certificate");
						button.setOnAction(actionEvent ->{
							Path path = null;
					        try {
					            path = Paths.get("c:", "Tmp","certificate.der");
					            Files.write(path, certificateToken.getCertificate().getTBSCertificate());
					            Desktop.getDesktop().open(path.toFile());
					        } catch (IOException | CertificateEncodingException e) {
					            e.printStackTrace();
					        }
								
						});
						
						VBox vBox = new VBox(lSubject, lEmitter, lValidity, button);
						
						Label lTest = new Label();
						lTest.setText("test");
						VBox vBoxLeft = new VBox(lTest);
								
						HBox hBox = new HBox(vBoxLeft, vBox);
						setGraphic(hBox);
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
					
				}
			}
			
		});
	}
	
	private String createKeyUsageString(CertificateToken token, ResourceBundle resources) {
		final boolean[] keyUsages = token.getCertificate().getKeyUsage();
		if(keyUsages == null) {
			return "";
		}
		final StringBuilder builder = new StringBuilder();
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
