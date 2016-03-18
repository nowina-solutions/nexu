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

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import lu.nowina.nexu.NexuLauncher;
import lu.nowina.nexu.ProxyConfigurer;
import lu.nowina.nexu.UserPreferences;
import lu.nowina.nexu.api.EnvironmentInfo;
import lu.nowina.nexu.api.OS;
import lu.nowina.nexu.view.core.AbstractUIOperationController;

public class PreferencesController extends AbstractUIOperationController<Void> implements Initializable {

	@FXML
	private Button ok;

	@FXML
	private Button cancel;

	@FXML
	private Button reset;
	
	@FXML
	private Label useSystemProxyLabel;
	
	@FXML
	private CheckBox useSystemProxy;

	@FXML
	private TextField proxyServer;

	@FXML
	private TextField proxyPort;

	@FXML
	private CheckBox proxyAuthentication;

	@FXML
	private TextField proxyUsername;
	
	@FXML
	private CheckBox useHttps;

	@FXML
	private PasswordField proxyPassword;
	
	private UserPreferences userPreferences;
	
	private BooleanProperty readOnly;
	
	private static final boolean isWindows;
	
	static {
		isWindows = EnvironmentInfo.buildFromSystemProperties(System.getProperties()).getOs().equals(OS.WINDOWS);
	}
	
	private void init(final ProxyConfigurer proxyConfigurer) {
		if(isWindows) {
			useSystemProxy.setSelected(proxyConfigurer.isUseSystemProxy());
		} else {
			useSystemProxy.setVisible(false);
			useSystemProxy.setManaged(false);
			useSystemProxyLabel.setVisible(false);
			useSystemProxyLabel.setManaged(false);
		}
		
		useHttps.setSelected(proxyConfigurer.isProxyUseHttps());
		proxyServer.setText(proxyConfigurer.getProxyServer());
		final Integer proxyPortInt = proxyConfigurer.getProxyPort();
		proxyPort.setText((proxyPortInt != null) ? proxyPortInt.toString() : "");
		proxyAuthentication.setSelected(proxyConfigurer.isProxyAuthentication());
		proxyUsername.setText(proxyConfigurer.getProxyUsername());
		proxyPassword.setText(proxyConfigurer.getProxyPassword());
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		readOnly = new SimpleBooleanProperty(false);
		ok.disableProperty().bind(readOnly);
		reset.disableProperty().bind(readOnly);
		useSystemProxy.disableProperty().bind(readOnly);
		
		proxyServer.disableProperty().bind(
				readOnly.or(
						useSystemProxy.selectedProperty()));
		
		proxyPort.disableProperty().bind(
				proxyServer.textProperty().length().lessThanOrEqualTo(0).or(
						proxyServer.disabledProperty()));
		
		proxyAuthentication.disableProperty().bind(
				readOnly.or(
						proxyServer.textProperty().length().lessThanOrEqualTo(0).and(
								useSystemProxy.selectedProperty().not())));
		
		useHttps.disableProperty().bind(
				readOnly.or(
						proxyServer.textProperty().length().lessThanOrEqualTo(0).and(
								useSystemProxy.selectedProperty().not())));
		
		proxyUsername.disableProperty().bind(proxyAuthentication.disabledProperty().or(
						proxyAuthentication.selectedProperty().not()));
		
		proxyPassword.disableProperty().bind(proxyAuthentication.disabledProperty().or(
				proxyAuthentication.selectedProperty().not()));
		
		ok.setOnAction((evt) -> {
			final Integer port;
			try {
				if(proxyPort.isDisabled()) {
					port = null;
				} else {
					port = Integer.parseInt(proxyPort.getText());
				}
			} catch(NumberFormatException e) {
				proxyPort.setTooltip(new Tooltip(resources.getString("preferences.controller.invalid.port")));
				proxyPort.setStyle("-fx-text-box-border: red; -fx-focus-color: red;");
	    		return;
			}
			
			userPreferences.setUseSystemProxy(useSystemProxy.isDisabled() ? null : useSystemProxy.isSelected());
			userPreferences.setProxyServer(proxyServer.isDisabled() ? null : proxyServer.getText());
			userPreferences.setProxyPort(port);
			userPreferences.setProxyAuthentication(proxyAuthentication.isDisabled() ? null : proxyAuthentication.isSelected());
			userPreferences.setProxyUsername(proxyUsername.isDisabled() ? null : proxyUsername.getText());
			userPreferences.setProxyPassword(proxyPassword.isDisabled() ? null : proxyPassword.getText());
			userPreferences.setProxyUseHttps(useHttps.isDisabled() ? null : useHttps.isSelected());
			
			NexuLauncher.getProxyConfigurer().updateValues(NexuLauncher.getConfig(), userPreferences);

			signalEnd(null);
		});
		cancel.setOnAction((e) -> {
			signalEnd(null);
		});
		reset.setOnAction((e) -> {
			userPreferences.clear();
			NexuLauncher.getProxyConfigurer().updateValues(NexuLauncher.getConfig(), userPreferences);
			signalEnd(null);
		});
	}

	@Override
	public void init(Object... params) {
		final ProxyConfigurer proxyConfigurer = (ProxyConfigurer) params[0];
		init(proxyConfigurer);
		this.userPreferences = (UserPreferences) params[1];
		this.readOnly.set((boolean) params[2]);
	}
}
