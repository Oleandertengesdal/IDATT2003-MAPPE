package idi.edu.idatt.mappe;

import idi.edu.idatt.mappe.controllers.GameController;
import idi.edu.idatt.mappe.exceptions.JsonParsingException;
import idi.edu.idatt.mappe.exceptions.TileActionNotFoundException;
import idi.edu.idatt.mappe.factory.BoardGameFactory;
import idi.edu.idatt.mappe.files.reader.BoardFileReaderGson;
import idi.edu.idatt.mappe.files.writer.BoardFileWriterGson;
import idi.edu.idatt.mappe.models.Board;
import idi.edu.idatt.mappe.models.BoardGame;
import idi.edu.idatt.mappe.views.GameView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Main class for the Board Game application.
 */
public class BoardgameMain extends Application {

    private BorderPane root;
    private VBox rightControls;
    private TextField nameField;
    private Button addPlayerBtn, startGameBtn, rollBtn;
    private GameController controller;
    private GameView view;
    private BoardGame currentGame;

    private Logger logger = Logger.getLogger(BoardgameMain.class.getName());

    @Override
    public void start(Stage primaryStage) {

        root = new BorderPane();

        logger.info("Starting application...");

        VBox startMenu = createStartMenu(primaryStage);
        root.setCenter(startMenu);
        root.setTop(createMenuBar(primaryStage));

        // initialize the right controls
        createRightControls();
        root.setRight(rightControls);
        root.setStyle("-fx-background-color: #f0f0f0; -fx-padding: 20");

        Scene scene = new Scene(root, 1000, 1000);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Brettspill - Startmeny");
        primaryStage.show();
    }

    /**
     * Creates the start menu
     */
    private VBox createStartMenu(Stage stage) {
        logger.info("Creating start menu...");

        VBox menuBox = new VBox(20);
        menuBox.setStyle("-fx-alignment: center; -fx-padding: 100");

        Label welcome = new Label("Velkommen til brettspill!");
        welcome.setStyle("-fx-font-size: 24px; -fx-font-weight: bold");

        Button newGameBtn = new Button("Start Stigespill");
        newGameBtn.setOnAction(e -> {
            loadPredefinedGame(BoardGameFactory.createClassicGame());
        });

        Button openJsonBtn = new Button("Åpne brett fra JSON");
        openJsonBtn.setOnAction(e -> loadBoardFromJson(stage));

        menuBox.getChildren().addAll(welcome, newGameBtn, openJsonBtn);
        return menuBox;
    }


    /**
     * Creates the top menu bar
     */
    private MenuBar createMenuBar(Stage stage) {
        logger.info("Creating menu bar...");

        MenuBar menuBar = new MenuBar();

        // === FIL-MENY ===
        Menu fileMenu = new Menu("Fil");

        MenuItem openJson = new MenuItem("Åpne JSON-brett...");
        openJson.setOnAction(e -> loadBoardFromJson(stage));

        MenuItem saveJson = new MenuItem("Lagre brett som JSON...");
        saveJson.setOnAction(e -> saveBoardToJson(stage));

        fileMenu.getItems().addAll(openJson, saveJson);

        // === SPILL-MENY ===
        Menu gameMenu = new Menu("Nytt spill");

        MenuItem laddersAndSnakes = new MenuItem("Stigespill");
        laddersAndSnakes.setOnAction(e -> loadPredefinedGame(BoardGameFactory.createClassicGame()));

        gameMenu.getItems().addAll(laddersAndSnakes);

        Menu playerMenu = new Menu("Spillere");

        MenuItem loadPlayers = new MenuItem("Last spillere fra CSV...");
        MenuItem savePlayers = new MenuItem("Lagre spillere til CSV...");

        loadPlayers.setOnAction(e -> loadPlayersFromCsv(stage));
        savePlayers.setOnAction(e -> savePlayersToCsv(stage));

        playerMenu.getItems().addAll(loadPlayers, savePlayers);

        menuBar.getMenus().addAll(fileMenu, gameMenu, playerMenu);
        return menuBar;
    }

    private void loadBoardFromJson(Stage stage) {
        logger.info("Loading board from JSON.");
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Åpne brett fra JSON");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON filer", "*.json"));
        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            try {
                logger.info("Loading board from file: " + file.getAbsolutePath());
                // Load the board from a JSON file
                controller.loadBoardFromFile(file);

                // Updates GUI
                view = controller.getView();
                updateUI();
            } catch (JsonParsingException ex) {
                showAlert("Feil ved lesing av brett", ex.getMessage());
            }
        }
    }

    private void saveBoardToJson(Stage stage) {
        logger.info("Saving board to JSON.");
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Lagre brett som JSON");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON filer", "*.json"));
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            try {
                // Save the board to a JSON file
                logger.info("Saving board to file: " + file.getAbsolutePath());
                controller.saveBoardToFile(file);
                showAlert("Lagret", "Brettet ble lagret til " + file.getName());
            } catch (IOException | TileActionNotFoundException | JsonParsingException ex) {
                showAlert("Feil ved lagring", ex.getMessage());
            }
        }
    }

    /**
     * Laster inn et forhåndsdefinert spill
     */
    private void loadPredefinedGame(BoardGame game) {
        currentGame = game;
        view = new GameView(game.getBoard());
        controller = new GameController(game, view);
        updateUI();
    }

    /**
     * Oppdaterer GUI med nytt spill og view
     */
    private void updateUI() {
        root.setCenter(view);
        rightControls.getChildren().clear();
        rightControls.getChildren().addAll(
                new Label("Player name:"), nameField,
                addPlayerBtn, startGameBtn, rollBtn
        );
    }

    /**
     * Oppretter kontrollpanel på høyre side
     */
    private VBox createRightControls() {
        logger.info("Creating right controls.");
        rightControls = new VBox(10);
        rightControls.setStyle("-fx-padding: 10");

        nameField = new TextField();
        addPlayerBtn = new Button("Legg til spiller");
        startGameBtn = new Button("Start spill");
        rollBtn = new Button("Kast terning");

        addPlayerBtn.setOnAction(e -> {
            String name = nameField.getText();
            if (!name.isBlank()) {
                controller.addPlayer(name);
                nameField.clear();
            }
        });

        startGameBtn.setOnAction(e -> controller.startGame());
        rollBtn.setOnAction(e -> controller.playTurn());

        rightControls.getChildren().addAll(
                new Label("Player name:"), nameField,
                addPlayerBtn, startGameBtn, rollBtn
        );

        return rightControls;
    }

    /**
     * Viser feilmelding i popup
     */
    private void showAlert(String title, String message) {
        logger.info("Error: " +  title + " - " + message);
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Laster inn spillere fra CSV
     */
    private void loadPlayersFromCsv(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Åpne CSV med spillere");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV filer", "*.csv"));
        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            try {
                logger.info("Loading players from file: " + file.getAbsolutePath());
                controller.loadPlayersFromCsv(file);
                showAlert("Spillere lastet", "Spillere lagt til fra " + file.getName());
            } catch (IOException ex) {
                showAlert("Feil ved lesing", ex.getMessage());
            }
        }
    }

    /**
     * Lagrer spillere til CSV
     */
    private void savePlayersToCsv(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Lagre spillere til CSV");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV filer", "*.csv"));
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            try {
                logger.info("Saving players to file: " + file.getAbsolutePath());
                controller.savePlayersToCsv(file);
                showAlert("Lagret", "Spillere lagret til " + file.getName());
            } catch (IOException ex) {
                showAlert("Feil ved lagring", ex.getMessage());
            }
        }
    }

    /**
     * Main method to launch the application
     *
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        launch();
    }
}
