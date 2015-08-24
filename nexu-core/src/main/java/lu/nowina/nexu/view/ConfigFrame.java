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

import java.awt.Container;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import lu.nowina.nexu.UserPreferences;

@SuppressWarnings("serial")
public class ConfigFrame extends JFrame {

	private JTextField proxyServer;

	private JTextField proxyPort;

	private JButton saveButton;

	private JButton cancelButton;

	public ConfigFrame(final UserPreferences config) {
		Container panel = getContentPane();
		panel.setLayout(new FlowLayout());

		panel.add(new JLabel("Proxy Server"));
		panel.add(proxyServer = new JTextField(12));
		proxyServer.setText(config.getProxyServer());
		panel.add(new JLabel("Proxy Port"));
		panel.add(proxyPort = new JPasswordField(12));
		proxyPort.setText(config.getProxyPort());
		panel.add(saveButton = new JButton("Save"));

		saveButton.addActionListener((e) -> {
			config.setProxyPort(proxyServer.getText());
			config.setProxyServer(proxyPort.getText());
		});

		panel.add(cancelButton = new JButton("Cancel"));
		cancelButton.addActionListener((e) -> {
			setVisible(false);
		});

		setSize(320, 240);
	}

}
