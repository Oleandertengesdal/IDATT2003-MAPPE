package idi.edu.idatt.mappe;

import idi.edu.idatt.mappe.views.HomeView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.logging.Logger;

/**
 * Main application class for the Boardgame.
 * Initializes services and sets up the main window.
 */
public class BoardgameMain extends Application {

    private static final Logger logger = Logger.getLogger(BoardgameMain.class.getName());

    @Override
    public void start(Stage primaryStage) {
        logger.info("Starting Boardgame application");

        primaryStage.setTitle("Brettspill - Startmeny");
        primaryStage.setMinHeight(900);
        primaryStage.setMinWidth(1100);

        primaryStage.setWidth(1100);
        primaryStage.setHeight(900);
        primaryStage.setResizable(false);

        HomeView homeView = new HomeView(primaryStage);
        Scene scene = new Scene(homeView.getRoot());

        scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());

        primaryStage.setScene(scene);
        primaryStage.show();

        primaryStage.setOnCloseRequest(event -> {
            logger.info("Closing Boardgame application");
            System.exit(0);
        });

        logger.info("Boardgame application started");
    }

    public static void main(String[] args) {
        launch(args);
    }
}