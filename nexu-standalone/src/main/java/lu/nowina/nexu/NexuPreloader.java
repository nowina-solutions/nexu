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
package lu.nowina.nexu;

import javafx.animation.PauseTransition;
import javafx.application.Preloader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

/**
 * <p>
 * Displays splash screen at Nexu's startup.
 * </p>
 * <p>
 * Splash screen is activated with runtime parameter :
 * -Djavafx.preloader=lu.nowina.nexu.NexuPreloader
 * </p>
 * 
 * @author Landry Soules
 *
 */
public class NexuPreloader extends Preloader {

	@Override
	public void start(Stage primaryStage) throws Exception {

		ImageView splash = new ImageView(new Image(NexuPreloader.class.getResourceAsStream("/images/splash.jpg")));
		StackPane background = new StackPane(splash);
		Scene splashScene = new Scene(background, 600, 300);
		primaryStage.setScene(splashScene);
		primaryStage.initStyle(StageStyle.UNDECORATED);
		primaryStage.show();
		PauseTransition delay = new PauseTransition(Duration.seconds(3));
		delay.setOnFinished(event -> primaryStage.close());
		delay.play();
	}

}
