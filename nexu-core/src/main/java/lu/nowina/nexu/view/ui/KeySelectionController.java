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
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.esig.dss.DSSASN1Utils;
import eu.europa.esig.dss.DSSUtils;
import eu.europa.esig.dss.QCStatementOids;
import eu.europa.esig.dss.token.DSSPrivateKeyEntry;
import eu.europa.esig.dss.x509.CertificateToken;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lu.nowina.nexu.flow.StageHelper;
import lu.nowina.nexu.flow.operation.CoreOperationStatus;
import lu.nowina.nexu.view.core.AbstractUIOperationController;

public class KeySelectionController extends AbstractUIOperationController<DSSPrivateKeyEntry> implements Initializable {

    private static final Logger logger = LoggerFactory.getLogger(KeySelectionController.class.getName());

    private static final String ICON_UNLOCKED = "/images/unlocked.png";
    private static final String ICON_QC = "/images/medal.png";
    private static final String ICON_QCSD = "/images/quality.png";

    @FXML
    private Button select;

    @FXML
    private Button cancel;

    @FXML
    private Button back;

    @FXML
    private ListView<DSSPrivateKeyEntry> listView;

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        this.select.setOnAction((event) -> {
            final DSSPrivateKeyEntry selectedItem = this.listView.getSelectionModel().getSelectedItem();
            logger.info("Selected item " + selectedItem);
            if (selectedItem != null) {
                this.signalEnd(selectedItem);
            } else {
                this.signalEnd(null);
            }
        });
        this.cancel.setOnAction((e) -> {
            this.signalUserCancel();
        });

        this.back.setOnAction(e -> this.signalEndWithStatus(CoreOperationStatus.BACK));

        this.listView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        this.listView.setCellFactory(param -> {
            return new ListCell<DSSPrivateKeyEntry>() {

                @Override
                protected void updateItem(final DSSPrivateKeyEntry k, final boolean bln) {
                    super.updateItem(k, bln);
                    if (k != null) {
                        final CertificateToken certificateToken = k.getCertificate();

                        final Label lSubject = new Label();
                        lSubject.setText(DSSASN1Utils.getSubjectCommonName(certificateToken));
                        lSubject.setStyle("-fx-font-weight: bold;");

                        final Label lEmitter = new Label();
                        lEmitter.setText(MessageFormat.format(resources.getString("key.selection.issuer.usage"),
                                DSSASN1Utils.get(certificateToken.getIssuerX500Principal()).get("2.5.4.3"),
                                KeySelectionController.this.createKeyUsageString(certificateToken, resources)));
                        final Label lValidity = new Label();
                        final SimpleDateFormat format = new SimpleDateFormat("dd MMMMMM yyyy");
                        final String startDate = format.format(certificateToken.getNotBefore());
                        final String endDate = format.format(certificateToken.getNotAfter());
                        lValidity.setText(
                                MessageFormat.format(resources.getString("key.selection.validity"), startDate, endDate));

                        final Hyperlink link = new Hyperlink(resources.getString("key.selection.certificate.open"));

                        link.setOnAction(actionEvent -> {
                            if (Desktop.isDesktopSupported()) {
                                try {
                                    final File tmpFile = File.createTempFile("certificate", ".crt");
                                    tmpFile.deleteOnExit();
                                    final String certificateStr = DSSUtils.convertToPEM(certificateToken);
                                    final FileWriter writer = new FileWriter(tmpFile);
                                    writer.write(certificateStr);
                                    writer.close();
                                    new Thread(() -> {
                                        try {
                                            Desktop.getDesktop().open(tmpFile);
                                        } catch (final IOException e) {
                                            logger.error(e.getMessage(), e);
                                        }
                                    }).start();
                                } catch (final Exception e) {
                                    logger.error(e.getMessage(), e);
                                }
                            }
                        });

                        final VBox vBox = new VBox(lSubject, lEmitter, lValidity, link);

                        VBox vBoxLeft;
                        try {
                            vBoxLeft = new VBox(KeySelectionController.this.getQCIcons(certificateToken).stream().toArray(ImageView[]::new));
                        } catch (final IOException e) {
                            logger.error(e.getMessage(), e);
                            vBoxLeft = new VBox();
                        }
                        vBoxLeft.setPadding(new Insets(0, 10, 0, 0));
                        vBoxLeft.setAlignment(Pos.CENTER);

                        final HBox hBox = new HBox(vBoxLeft, vBox);
                        this.setGraphic(hBox);
                    }
                }

            };

        });
    }

    private List<ImageView> getQCIcons(final CertificateToken certificateToken) throws IOException {
        final List<ImageView> qcIconsImages = new ArrayList<>();
        final List<String> qcStatements = DSSASN1Utils.getQCStatementsIdList(certificateToken);
        if (qcStatements.contains(QCStatementOids.QC_COMPLIANCE.getOid())) {
            qcIconsImages.add(this.fetchImage(ICON_QC));
        }
        if (qcStatements.contains(QCStatementOids.QC_SSCD.getOid())) {
            qcIconsImages.add(this.fetchImage(ICON_QCSD));
        }
        if (qcIconsImages.isEmpty()) {
            qcIconsImages.add(this.fetchImage(ICON_UNLOCKED));
        }
        return qcIconsImages;
    }

    private ImageView fetchImage(final String imagePath) throws IOException {
        return new ImageView(new Image(this.getClass().getResource(imagePath).openStream()));
    }

    private String createKeyUsageString(final CertificateToken token, final ResourceBundle resources) {
        final boolean[] keyUsages = token.getCertificate().getKeyUsage();
        if (keyUsages == null) {
            return "";
        }
        final StringBuilder builder = new StringBuilder();
        if (keyUsages[0]) {
            builder.append(resources.getString("keyUsage.digitalSignature") + "\n");
        }
        if (keyUsages[1]) {
            builder.append(resources.getString("keyUsage.nonRepudiation") + "\n");
        }
        if (keyUsages[2]) {
            builder.append(resources.getString("keyUsage.keyEncipherment") + "\n");
        }
        if (keyUsages[3]) {
            builder.append(resources.getString("keyUsage.dataEncipherment") + "\n");
        }
        if (keyUsages[4]) {
            builder.append(resources.getString("keyUsage.keyAgreement") + "\n");
        }
        if (keyUsages[5]) {
            builder.append(resources.getString("keyUsage.keyCertSign") + "\n");
        }
        if (keyUsages[6]) {
            builder.append(resources.getString("keyUsage.crlSign") + "\n");
        }
        if (keyUsages[7]) {
            builder.append(resources.getString("keyUsage.encipherOnly") + "\n");
        }
        if (keyUsages[8]) {
            builder.append(resources.getString("keyUsage.decipherOnly") + "\n");
        }
        return builder.toString();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void init(final Object... params) {
        StageHelper.getInstance().setTitle((String) params[1], "key.selection.title");
        final Boolean displayBackButton = (Boolean) params[2];
        this.back.setManaged(displayBackButton);
        this.back.setVisible(displayBackButton);
        final List<DSSPrivateKeyEntry> keys = (List<DSSPrivateKeyEntry>) params[0];
        final ObservableList<DSSPrivateKeyEntry> items = FXCollections.observableArrayList(keys);
        this.listView.setItems(items);
        if(items.size()<=5) {
        	this.listView.setPrefHeight(100.0*items.size());	
        }else {
        	this.listView.setPrefHeight(500);
        }
        
    }

}
