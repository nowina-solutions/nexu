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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import lu.nowina.nexu.generic.DatabaseWebLoader;
import lu.nowina.nexu.view.ui.AboutController;
import lu.nowina.nexu.view.ui.PreferencesController;

public class SystrayMenu {

	private static final Logger logger = LoggerFactory.getLogger(SystrayMenu.class.getName());

	private TrayIcon trayIcon;

	private MenuItem exitItem;

	private MenuItem aboutItem;

	private MenuItem preferencesItem;

	public SystrayMenu(NexUApp display, DatabaseWebLoader webLoader) {

		if (SystemTray.isSupported()) {

			SystemTray tray = SystemTray.getSystemTray();
			Image image = Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("/tray-icon.png"));

			ActionListener actionListener = new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (e.getSource() == exitItem) {
						System.out.println("Exiting...");
						System.exit(0);
					} else if (e.getSource() == aboutItem) {

						FXMLLoader loader = new FXMLLoader();
						try {
							loader.load(getClass().getResourceAsStream("/fxml/about.fxml"));

							Parent root = loader.getRoot();
							AboutController controller = loader.getController();
							controller.setDisplay(display);
							controller.setDataLoader(webLoader);

							Platform.runLater(() -> {
								display.display(root);
							});

						} catch (IOException ex) {
							throw new RuntimeException(ex);
						}
					} else if (e.getSource() == preferencesItem) {

						FXMLLoader loader = new FXMLLoader();
						try {
							loader.load(getClass().getResourceAsStream("/fxml/preferences.fxml"));

							Parent root = loader.getRoot();
							PreferencesController controller = loader.getController();
							controller.setDisplay(display);

							Platform.runLater(() -> {
								display.display(root);
							});

						} catch (IOException ex) {
							throw new RuntimeException(ex);
						}
					}
				}
			};

			PopupMenu popup = new PopupMenu();
			aboutItem = new MenuItem("About");
			aboutItem.addActionListener(actionListener);
			popup.add(aboutItem);
			preferencesItem = new MenuItem("Preferences");
			preferencesItem.addActionListener(actionListener);
			popup.add(preferencesItem);
			exitItem = new MenuItem("Exit");
			exitItem.addActionListener(actionListener);
			popup.add(exitItem);

			trayIcon = new TrayIcon(image, "NexU", popup);

			trayIcon.setImageAutoSize(true);

			try {
				tray.add(trayIcon);
			} catch (AWTException e) {
				logger.error("Cannot add TrayIcon", e);
				System.err.println("TrayIcon could not be added.");
			}

		} else {
			System.err.println("System tray is currently not supported.");
		}
	}

}
