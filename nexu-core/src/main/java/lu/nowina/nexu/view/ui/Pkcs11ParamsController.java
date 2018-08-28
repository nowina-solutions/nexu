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
import lu.nowina.nexu.api.EnvironmentInfo;
import lu.nowina.nexu.api.OS;
import lu.nowina.nexu.flow.StageHelper;
import lu.nowina.nexu.model.Pkcs11Params;
import lu.nowina.nexu.view.core.AbstractUIOperationController;
import lu.nowina.nexu.view.core.ExtensionFilter;

public class Pkcs11ParamsController extends AbstractUIOperationController<Pkcs11Params> implements Initializable {

    private static final OS OS = EnvironmentInfo.buildFromSystemProperties(System.getProperties()).getOs();

    @FXML
    private Button ok;

    @FXML
    private Button cancel;

    @FXML
    private Button selectFile;

    private File pkcs11File;
    private final BooleanProperty pkcs11FileSpecified;

    public Pkcs11ParamsController() {
        this.pkcs11FileSpecified = new SimpleBooleanProperty(false);
    }

    @Override
    public void init(final Object... params) {
        StageHelper.getInstance().setTitle((String) params[0], "pkcs11.params.title");
    }

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        this.ok.setOnAction(event -> {
            final Pkcs11Params result = new Pkcs11Params();
            result.setPkcs11Lib(this.pkcs11File);
            this.signalEnd(result);
        });
        this.ok.disableProperty().bind(Bindings.not(this.pkcs11FileSpecified));
        this.cancel.setOnAction(e -> this.signalUserCancel());
        this.selectFile.setOnAction(e -> {
            this.pkcs11File = this.getDisplay().displayFileChooser(new ExtensionFilter(
                    OS.getNativeLibraryFileExtensionDescription(), OS.getNativeLibraryFileExtension()));
            this.pkcs11FileSpecified.set(this.pkcs11File != null);
        });
    }

}
