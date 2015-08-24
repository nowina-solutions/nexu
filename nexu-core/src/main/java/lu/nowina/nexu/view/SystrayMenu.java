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
package lu.nowina.nexu.view;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import lu.nowina.nexu.UserPreferences;

public class SystrayMenu {

	private UserPreferences config;

	private TrayIcon trayIcon;

	private MenuItem exitItem;

	private MenuItem configItem;

	public SystrayMenu() {

		if (SystemTray.isSupported()) {

			SystemTray tray = SystemTray.getSystemTray();
			Image image = Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("/tray-icon.png"));

			ActionListener actionListener = new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (e.getSource() == exitItem) {
						System.out.println("Exiting...");
						System.exit(0);
					} else if (e.getSource() == configItem) {
						ConfigFrame frame = new ConfigFrame(config);
						frame.setVisible(true);
					}
				}
			};

			PopupMenu popup = new PopupMenu();
			configItem = new MenuItem("Config");
			configItem.addActionListener(actionListener);
			popup.add(configItem);
			exitItem = new MenuItem("Exit");
			exitItem.addActionListener(actionListener);
			popup.add(exitItem);

			trayIcon = new TrayIcon(image, "Standup", popup);

			trayIcon.setImageAutoSize(true);

			try {
				tray.add(trayIcon);
			} catch (AWTException e) {
				System.err.println("TrayIcon could not be added.");
			}

		} else {
			System.err.println("System tray is currently not supported.");
		}
	}

	public void setConfig(UserPreferences config) {
		this.config = config;
	}

}
