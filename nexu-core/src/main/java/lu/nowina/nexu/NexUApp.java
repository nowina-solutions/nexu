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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;

import eu.europa.esig.dss.token.PasswordInputCallback;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lu.nowina.nexu.api.plugin.HttpPlugin;
import lu.nowina.nexu.api.plugin.SignaturePlugin;
import lu.nowina.nexu.generic.DatabaseWebLoader;
import lu.nowina.nexu.generic.HttpDataLoader;
import lu.nowina.nexu.generic.SCDatabase;
import lu.nowina.nexu.generic.SCDatabaseLoader;
import lu.nowina.nexu.jetty.JettyServer;
import lu.nowina.nexu.smartcard.dector.CardDetector;
import lu.nowina.nexu.view.SystrayMenu;
import lu.nowina.nexu.view.core.UIDisplay;
import lu.nowina.nexu.view.core.UIOperation;

public class NexUApp extends Application implements UIDisplay {

    private static final Logger logger = Logger.getLogger(NexUApp.class.getName());

    private static Properties props;
    
    private static AppConfig config;
    
    private Stage stage;

    public static void main(String[] args) throws Exception {
    	
        logger.config("Read configuration");
        props = loadPropertiesFile();
        config = loadAppConfig(props);

        URL url = new URL("http://" + config.getBindingIP() + ":" + config.getBindingPort() + "/info");
        try(InputStream in = url.openStream()) {
        	String info = IOUtils.toString(in);
        	logger.severe("NexU already started. Version '" + info + "'");
        } catch(Exception e) {
        	logger.info("no " + url.toString() + " detected, " + e.getMessage());
        	// If we cannot connect, most likely NexU is not started yet
        }

        launch(NexUApp.class, args);
        
    }

    @Override
    public void start(Stage primaryStage) {
        Platform.setImplicitExit(false);

        this.stage = new Stage();


        try {

            File store = new File("./store.xml");
            SCDatabase db = SCDatabaseLoader.load(store);

            UserPreferences prefs = new UserPreferences();
            CardDetector detector = new CardDetector();
            
            DatabaseWebLoader loader = new DatabaseWebLoader(config, new HttpDataLoader());
            loader.start();
            
            InternalAPI api = new InternalAPI(this, prefs, db, detector, loader);
            
            for (String key : props.stringPropertyNames()) {
                if (key.startsWith("plugin_")) {

                    String pluginClassName = props.getProperty(key);
                    String pluginId = key.substring("plugin_".length());
                    logger.config(" + Plugin " + pluginClassName);

                    Class<?> clazz = Class.forName(pluginClassName);
                    for (Class<?> i : clazz.getInterfaces()) {
                        Object plugin = clazz.newInstance();
                        if (SignaturePlugin.class.equals(i)) {
                            SignaturePlugin p = (SignaturePlugin) plugin;
                            p.init(pluginId, api);
                        }
                        if (HttpPlugin.class.equals(i)) {
                            HttpPlugin p = (HttpPlugin) plugin;
                            p.init(pluginId, api);
                            api.registerHttpContext(pluginId, p);
                        }
                    }

                }
            }

            new SystrayMenu(this, loader);

            logger.info("Start Jetty");

            new Thread(() -> {
                JettyServer server = new JettyServer();
                server.setConfig(api, prefs, config);
                try {
                    server.start();
                } catch (Exception e) {
                    logger.log(Level.SEVERE, "Cannot Jetty", e);
                }
            }).start();

            logger.info("Start finished");

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Cannot start", e);
        }
    }

    public static Properties loadPropertiesFile() throws IOException {
        
        InputStream configFile = NexUApp.class.getClassLoader().getResourceAsStream("nexu-config.properties");
        Properties props = new Properties();
        if (configFile != null) {
            props.load(configFile);
        }
        
        return props;

    }
    
    public static AppConfig loadAppConfig(Properties props) {
        AppConfig config = new AppConfig();

        config.setBindingPort(Integer.parseInt(props.getProperty("binding_port", "9876")));
        config.setBindingIP(props.getProperty("binding_ip", "127.0.0.1"));
        config.setServerUrl(props.getProperty("server_url", "http://lab.nowina.solutions/nexu"));

        return config;
    }

    @Override
    public void display(Parent panel) {
        logger.info("Display " + panel + " in display " + this + " from Thread " + Thread.currentThread().getName());
        Platform.runLater(() -> {
            logger.info(
                    "Display " + panel + " in display " + this + " from Thread " + Thread.currentThread().getName());
            if (!stage.isShowing()) {
                stage = createStage();
                logger.info("Loading ui " + panel + " is a new Stage " + stage);
            } else {
                logger.info("Stage still showing, display " + panel);
            }
            stage.setScene(new Scene(panel, 300, 250));
            stage.show();
        });
    }

    private Stage createStage() {
        Stage newStage = new Stage();
        newStage.setAlwaysOnTop(true);
        newStage.setOnCloseRequest((e) -> {
            logger.info("Closing stage " + stage + " from " + Thread.currentThread().getName());
            stage.hide();
            e.consume();
        });
        return newStage;
    }

    @Override
    public void displayWaitingPane() {
    }

    @Override
    public void close() {

        Platform.runLater(() -> {
            Stage oldStage = stage;
            logger.info("Hide stage " + stage + " and create new stage");
            stage = createStage();
            oldStage.hide();
        });
    }

    @Override
    public void stop() throws Exception {
        logger.warning("Can only happen with explicite user request");
    }

    public <T extends Object> T displayAndWaitUIOperation(String fxml, Object... params) {

        logger.info("Loading " + fxml + " view");
        FXMLLoader loader = new FXMLLoader();
        try {
            loader.load(getClass().getResourceAsStream(fxml));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Parent root = loader.getRoot();
        UIOperation<T> controller = loader.getController();

        display(root);
        return waitForUser(controller, params);
    }

    private <T> T waitForUser(UIOperation<T> controller, Object... params) {
        try {
            logger.info("Wait on Thread " + Thread.currentThread().getName());
            controller.init(params);
            T r = controller.waitEnd();
            displayWaitingPane();
            return r;
        } catch (InterruptedException e) {
            throw new RuntimeException();
        }
    }

    private final class FlowPasswordCallback implements PasswordInputCallback {
        @Override
        public char[] getPassword() {
            logger.info("Request password");
            return displayAndWaitUIOperation("/fxml/password-input.fxml");
        }
    }

    public PasswordInputCallback getPasswordInputCallback() {
        return new FlowPasswordCallback();
    }

}
