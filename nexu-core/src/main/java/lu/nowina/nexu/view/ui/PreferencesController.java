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
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import lu.nowina.nexu.UserPreferences;
import lu.nowina.nexu.view.core.UIDisplay;

public class PreferencesController implements Initializable {

	@FXML
	private Button ok;

	@FXML
	private TextField proxyHost;

	@FXML
	private TextField proxyPort;

	@FXML
	private CheckBox authenticationRequired;

	@FXML
	private TextField proxyUsername;

	@FXML
	private PasswordField proxyPassword;

	@FXML
	private Label dbFile;

	private UIDisplay display;

	private UserPreferences preferences;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		ok.setOnAction((e) -> {
			display.close();
		});
	}

	public void setDisplay(UIDisplay display) {
		this.display = display;
	}

	public void setPreferences(UserPreferences preferences) {

		if (preferences == null) {
			throw new NullPointerException("preferences cannot be null");
		}

		this.preferences = preferences;

		this.authenticationRequired.setSelected(preferences.getProxyAuthentification());
		this.proxyUsername.setText(preferences.getProxyUsername());
		this.proxyPassword.setText(preferences.getProxyPassword());
		this.proxyPort.setText(preferences.getProxyPort());
		this.proxyHost.setText(preferences.getProxyServer());
	}

}
