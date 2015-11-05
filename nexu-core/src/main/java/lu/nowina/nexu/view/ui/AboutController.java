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
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import lu.nowina.nexu.generic.DatabaseWebLoader;
import lu.nowina.nexu.view.core.UIDisplay;

public class AboutController implements Initializable {

	@FXML
	private Button ok;

	@FXML
	private Label dbVersion;

	@FXML
	private Label dbFile;

	private UIDisplay display;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		ok.setOnAction((e) -> {
			display.close();
		});
	}

	public void setDataLoader(DatabaseWebLoader loader) {
		String digest = loader.digestDatabase();
		dbVersion.setText(digest != null ? digest : "no_database");
		dbFile.setText(loader.getDatabaseFile().getAbsolutePath());
	}

	public void setDisplay(UIDisplay display) {
		this.display = display;
	}

}
