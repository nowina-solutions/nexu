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

import org.apache.commons.lang.StringEscapeUtils;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import lu.nowina.nexu.flow.StageHelper;
import lu.nowina.nexu.view.core.AbstractUIOperationController;

/**
 * Return true if the user want to try "Advance mode"
 *
 * @author David Naramski
 *
 */
public class UnsupportedProductController extends AbstractUIOperationController<Void> implements Initializable {

    @FXML
    private Label message;

    @FXML
    private Button cancel;

    @FXML
    private Button hicSuntDracones;

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        this.hicSuntDracones.setOnAction(ev -> this.signalEnd(null));
        this.cancel.setOnAction(ev -> this.signalUserCancel());
    }

    @Override
    public final void init(final Object... params) {
        StageHelper.getInstance().setTitle((String) params[0], "unsuported.product.title");

        Platform.runLater(() -> this.message.setText(StringEscapeUtils.unescapeJava(MessageFormat
                .format(ResourceBundle.getBundle("bundles/nexu").getString("unsuported.product.header"), params[0]))));
    }
}
