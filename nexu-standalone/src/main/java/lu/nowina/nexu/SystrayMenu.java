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
package lu.nowina.nexu;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.Platform;
import lu.nowina.nexu.api.NexuAPI;
import lu.nowina.nexu.flow.operation.OperationFactory;
import lu.nowina.nexu.generic.DatabaseWebLoader;
import lu.nowina.nexu.view.core.NonBlockingUIOperation;

public class SystrayMenu {

	private static final Logger LOGGER = LoggerFactory.getLogger(SystrayMenu.class.getName());

	private final TrayIcon trayIcon;
	
	public SystrayMenu(OperationFactory operationFactory, DatabaseWebLoader webLoader, NexuAPI api, UserPreferences prefs) {
		if (SystemTray.isSupported()) {
			final ResourceBundle resources = ResourceBundle.getBundle("bundles/nexu");
			final PopupMenu popup = new PopupMenu();
			
			final MenuItem aboutItem = new MenuItem(resources.getString("systray.menu.about"));
			aboutItem.addActionListener((l) -> about(operationFactory, api, webLoader));
			popup.add(aboutItem);
			
			final MenuItem preferencesItem = new MenuItem(resources.getString("systray.menu.preferences"));
			preferencesItem.addActionListener((l) -> preferences(operationFactory, api, prefs));
			popup.add(preferencesItem);
			
			final MenuItem exitItem = new MenuItem(resources.getString("systray.menu.exit"));
			exitItem.addActionListener((l) -> exit());
			popup.add(exitItem);

			final Image image = Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("/tray-icon.png"));
			trayIcon = new TrayIcon(image, api.getAppConfig().getApplicationName(), popup);
			trayIcon.setImageAutoSize(true);
			try {
				SystemTray.getSystemTray().add(trayIcon);
			} catch (AWTException e) {
				LOGGER.error("Cannot add TrayIcon", e);
			}
		} else {
			trayIcon = null;
			LOGGER.error("System tray is currently not supported.");
		}
	}
	
	private void about(final OperationFactory operationFactory, final NexuAPI api, final DatabaseWebLoader webLoader) {
		operationFactory.getOperation(NonBlockingUIOperation.class, "/fxml/about.fxml",
				api.getAppConfig().getApplicationName(), api.getAppConfig().getApplicationVersion(), webLoader).perform();
	}
	
	private void preferences(final OperationFactory operationFactory, final NexuAPI api, final UserPreferences prefs) {
		final ProxyConfigurer proxyConfigurer = new ProxyConfigurer(api.getAppConfig(), prefs);
		
		operationFactory.getOperation(NonBlockingUIOperation.class, "/fxml/preferences.fxml",
				proxyConfigurer, prefs, !api.getAppConfig().isUserPreferencesEditable()).perform();
	}
	
	private void exit() {
		LOGGER.info("Exiting...");
		if(trayIcon != null) {
			SystemTray.getSystemTray().remove(trayIcon);
		}
		Platform.exit();
	}
}
