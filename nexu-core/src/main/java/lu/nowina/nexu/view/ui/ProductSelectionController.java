/**
 * © Nowina Solutions, 2015-2016
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
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.Pane;
import lu.nowina.nexu.api.DetectedCard;
import lu.nowina.nexu.api.NexuAPI;
import lu.nowina.nexu.api.Product;
import lu.nowina.nexu.flow.StageHelper;
import lu.nowina.nexu.view.core.AbstractUIOperationController;

public class ProductSelectionController extends AbstractUIOperationController<Product> implements Initializable {

	@FXML
	private Label message;

	@FXML
	private Pane productsContainer;

	@FXML
	private Button select;

	@FXML
	private Button cancel;

	private ToggleGroup product;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		select.setOnAction(e -> signalEnd(getSelectedProduct()));
		cancel.setOnAction(e -> signalUserCancel());

		product = new ToggleGroup();
		select.disableProperty().bind(product.selectedToggleProperty().isNull());
	}

	private Product getSelectedProduct() {
		return (Product) product.getSelectedToggle().getUserData();
	}

	@Override
	public final void init(Object... params) {
		final NexuAPI api = (NexuAPI) params[3];
		StageHelper.getInstance().setTitle(api.getAppConfig().getApplicationName(),
				"product.selection.title");

		Platform.runLater(() -> {
			message.setText(MessageFormat
					.format(ResourceBundle.getBundle("bundles/nexu").getString("product.selection.header"), params[0]));
			@SuppressWarnings("unchecked")
			final List<DetectedCard> cards = (List<DetectedCard>) params[1];
			@SuppressWarnings("unchecked")
			final List<Product> products = (List<Product>) params[2];
			final List<RadioButton> radioButtons = new ArrayList<>(cards.size() + products.size());

			for (final DetectedCard card : cards) {
				final RadioButton button = new RadioButton(api.getLabel(card));
				button.setToggleGroup(product);
				button.setUserData(card);
				button.setMnemonicParsing(false);
				radioButtons.add(button);
			}

			for (final Product p : products) {
				final RadioButton button = new RadioButton(api.getLabel(p));
				button.setToggleGroup(product);
				button.setUserData(p);
				button.setMnemonicParsing(false);
				radioButtons.add(button);
			}

			productsContainer.getChildren().addAll(radioButtons);
		});
	}
}
