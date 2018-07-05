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
import lu.nowina.nexu.view.core.AbstractUIOperationController;

public class AboutController extends AbstractUIOperationController<Void> implements Initializable {

	@FXML
	private Label aboutTitle;
	
	@FXML
	private Button ok;

	@FXML
	private Label applicationVersion;
	
	@FXML
	private Label dbVersion;

	@FXML
	private Label dbFile;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		ok.setOnAction(e -> signalEnd(null));
	}

	@Override
	public void init(Object... params) {
		final String applicationName = (String) params[0];
		this.aboutTitle.setText(aboutTitle.getText() + " " + applicationName);
		
		final String applicationVersion = (String) params[1];
		this.applicationVersion.setText(applicationVersion);
	}
}
