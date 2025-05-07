package idi.edu.idatt.mappe.views;

import idi.edu.idatt.mappe.controllers.BoardGameController;
import idi.edu.idatt.mappe.controllers.BoardGameControllerFactory;
import idi.edu.idatt.mappe.controllers.PlayerController;
import idi.edu.idatt.mappe.exceptions.InvalidGameTypeException;
import idi.edu.idatt.mappe.exceptions.JsonParsingException;
import idi.edu.idatt.mappe.models.BoardGame;
import idi.edu.idatt.mappe.controllers.FileService;
import idi.edu.idatt.mappe.controllers.FileServiceController;
import idi.edu.idatt.mappe.utils.factory.BoardGameFactory;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * HomeView class that handles UI components for the home screen and navigation.
 */
public class HomeView {
    private static final Logger LOGGER = Logger.getLogger(HomeView.class.getName());
    private static final String BOARDS_DIRECTORY = "src/main/resources/boards";

    private final BorderPane root;
    private final Stage stage;

    private GameView gameView;
    private BoardGame currentGame;

    private BoardGameController gameController;
    private final PlayerController playerController;
    private final FileService fileService;

    /**
     * Constructor for HomeView.
     *
     * @param stage The main application stage
     */
    public HomeView(Stage stage) {
        this.stage = stage;
        this.root = new BorderPane();
        root.getStyleClass().add("home-view");

        // Initialize services
        this.fileService = new FileServiceController();
        this.playerController = new PlayerController(fileService);

        // Setup UI
        root.setTop(createMenuBar());
        root.setCenter(createStartMenu());

        LOGGER.info("HomeView initialized");
    }

    /**
     * Returns the root pane of this view.
     *
     * @return The BorderPane containing this view
     */
    public BorderPane getRoot() {
        return root;
    }

    /**
     * Returns the current game controller.
     *
     * @return The current BoardGameController
     */
    public BoardGameController getController() {
        return gameController;
    }

    /**
     * Returns the current game view.
     *
     * @return The current GameView
     */
    public GameView getView() {
        return gameView;
    }

    /**
     * Creates and returns the start menu.
     *
     * @return VBox containing the start menu components
     */
    private VBox createStartMenu() {
        LOGGER.info("Creating start menu");

        VBox menuBox = new VBox(20);
        menuBox.setStyle("-fx-alignment: center; -fx-padding: 100");

        Label welcomeLabel = new Label("Velkommen til brettspill!");
        welcomeLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold");

        Button snakesAndLaddersButton = createGameButton("Start Stigespill",
                () -> startNewGame(BoardGameFactory.createClassicGame()));

        Button ludoButton = createGameButton("Start Ludo",
                () -> startNewGame(BoardGameFactory.createSimpleLudoGame()));

        Button openJsonButton = new Button("Åpne brett fra JSON");
        openJsonButton.setOnAction(e -> loadBoardFromJson());

        menuBox.getChildren().addAll(
                welcomeLabel,
                snakesAndLaddersButton,
                ludoButton,
                openJsonButton
        );

        return menuBox;
    }

    /**
     * Creates a button for starting a specific game type.
     *
     * @param text Button text
     * @param gameStarter Runnable to execute when button is clicked
     * @return The configured button
     */
    private Button createGameButton(String text, GameStarter gameStarter) {
        Button button = new Button(text);
        button.setOnAction(e -> {
            try {
                gameStarter.start();
            } catch (InvalidGameTypeException ex) {
                LOGGER.log(Level.SEVERE, "Invalid game type", ex);
                showAlert("Feil ved oppstart av spill", "Kunne ikke starte spillet: " + ex.getMessage());
            }
        });
        return button;
    }

    /**
     * Functional interface for game starters.
     */
    @FunctionalInterface
    private interface GameStarter {
        void start() throws InvalidGameTypeException;
    }

    /**
     * Handles the result of player selection.
     *
     * @param startGame Whether to start the game (true) or return to main menu (false)
     */
    private void handlePlayerSelectionResult(boolean startGame) {
        if (startGame) {
            gameController.startGame();
            root.setCenter(gameView);
        } else {
            root.setCenter(createStartMenu());
        }
    }

    /**
     * Starts a new game with player selection.
     *
     * @param game The game to start
     * @throws InvalidGameTypeException If the game type is invalid
     */
    private void startNewGame(BoardGame game) throws InvalidGameTypeException {
        LOGGER.info("Starting new game: " + game.getGameType());

        currentGame = game;
        gameView = new GameView(game.getBoard());
        gameController = BoardGameControllerFactory.createController(game, gameView, fileService);

        PlayerSelectionView playerSelectionView = new PlayerSelectionView(
                gameController,
                playerController,
                fileService,
                stage,
                this::handlePlayerSelectionResult
        );

        root.setCenter(playerSelectionView);
    }

    /**
     * Creates the menu bar.
     *
     * @return MenuBar containing the application menus
     */
    private MenuBar createMenuBar() {
        LOGGER.info("Creating menu bar");

        MenuBar menuBar = new MenuBar();

        Menu homeMenu = new Menu("Home");
        MenuItem goToHomeItem = new MenuItem("Go to Home");
        goToHomeItem.setOnAction(e -> root.setCenter(createStartMenu()));
        homeMenu.getItems().add(goToHomeItem);

        menuBar.getMenus().addAll(homeMenu);
        return menuBar;
    }

    /**
     * Loads a board from a JSON file.
     */
    private void loadBoardFromJson() {
        LOGGER.info("Loading board from JSON");

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Åpne brett fra JSON");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("JSON filer", "*.json"));

        fileChooser.setInitialDirectory(new File(BOARDS_DIRECTORY));

        File file = fileChooser.showOpenDialog(stage);
        if (file == null) {
            return;
        }

        try {
            BoardGame game = BoardGameFactory.createClassicGame();
            gameView = new GameView(game.getBoard());
            gameController = BoardGameControllerFactory.createController(game, gameView, fileService);

            gameController.loadBoardFromFile(file);
            gameView = gameController.getView();
            currentGame = gameController.getBoardGame();

            PlayerSelectionView playerSelectionView = new PlayerSelectionView(
                    gameController,
                    playerController,
                    fileService,
                    stage,
                    this::handlePlayerSelectionResult
            );

            root.setCenter(playerSelectionView);

            LOGGER.info("Board loaded from JSON, showing player selection");

        } catch (JsonParsingException ex) {
            LOGGER.log(Level.SEVERE, "Error parsing JSON", ex);
            showAlert("Feil ved lesing av brett", ex.getMessage());
        } catch (InvalidGameTypeException ex) {
            LOGGER.log(Level.SEVERE, "Invalid game type", ex);
            showAlert("Feil", "Ugyldig spilltype: " + ex.getMessage());
        }
    }

    /**
     * Updates the UI after loading a game or board.
     */
    private void updateUI() {
        root.setCenter(gameView);
    }

    /**
     * Shows an alert dialog.
     *
     * @param title The title of the alert
     * @param message The message to display
     */
    private void showAlert(String title, String message) {
        showAlert(Alert.AlertType.ERROR, title, message);
    }

    /**
     * Shows an alert dialog with the specified type.
     *
     * @param alertType The type of alert to show
     * @param title The title of the alert
     * @param message The message to display
     */
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        LOGGER.info("Showing alert: " + title + " - " + message);

        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}