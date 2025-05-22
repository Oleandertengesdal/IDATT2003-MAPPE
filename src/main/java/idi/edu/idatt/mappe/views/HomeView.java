package idi.edu.idatt.mappe.views;

import idi.edu.idatt.mappe.controllers.BoardGameController;
import idi.edu.idatt.mappe.controllers.BoardGameControllerFactory;
import idi.edu.idatt.mappe.controllers.GameRulesController;
import idi.edu.idatt.mappe.controllers.PlayerController;
import idi.edu.idatt.mappe.exceptions.InvalidGameTypeException;
import idi.edu.idatt.mappe.exceptions.JsonParsingException;
import idi.edu.idatt.mappe.models.Board;
import idi.edu.idatt.mappe.models.BoardGame;
import idi.edu.idatt.mappe.controllers.FileService;
import idi.edu.idatt.mappe.controllers.FileServiceController;
import idi.edu.idatt.mappe.models.GameRules;
import idi.edu.idatt.mappe.models.Player;
import idi.edu.idatt.mappe.models.enums.GameType;
import idi.edu.idatt.mappe.utils.factory.BoardGameFactory;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
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
    private GameType lastPlayedGameType;
    private List<Player> lastPlayers = new ArrayList<>();

    private GameRules lastGameRules;
    private File lastLoadedBoardFile;
    private Board lastLoadedBoard;

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

        this.fileService = new FileServiceController();
        this.playerController = new PlayerController(fileService);

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

        Label welcomeLabel = new Label("Welcome to the board game application!");
        welcomeLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold");

        Button snakesAndLaddersButton = createGameButton("Start Snakes and Ladders",
                () -> openGameRulesView(GameType.SNAKES_AND_LADDERS));
        snakesAndLaddersButton.setStyle("-fx-background-color: #d3c6c5; -fx-text-fill: white; " +
                "-fx-font-weight: bold; -fx-padding: 10px;");

        Button lostDiamondButton = createGameButton("Start The Lost Diamond",
                () -> openGameRulesView(GameType.THE_LOST_DIAMOND));
        lostDiamondButton.setStyle("-fx-background-color: #d3c6c5; -fx-text-fill: white; " +
                "-fx-font-weight: bold; -fx-padding: 10px;");


        Button openJsonButton = new Button("Open board from JSON");
        openJsonButton.setOnAction(e -> loadBoardFromJson());

        menuBox.getChildren().addAll(
                welcomeLabel,
                snakesAndLaddersButton,
                lostDiamondButton,
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
            lastPlayers = new ArrayList<>(gameController.getBoardGame().getPlayers());

            lastGameRules = gameController.getBoardGame().getGameRules();

            LOGGER.info("Saved " + lastPlayers.size() + " players and game configuration for potential reset");

            gameView.setOnResetGame(this::resetCurrentGame);
            gameView.setOnReturnToMainMenu(this::returnToMainMenu);

            gameController.startGame();
            root.setCenter(gameView);
        } else {
            root.setCenter(createStartMenu());
        }
    }

    /**
     * Resets the current game with the same players and settings.
     */
    private void resetCurrentGame() {
        LOGGER.info("Resetting current game");

        if (gameController == null) {
            LOGGER.warning("Cannot reset game: no active game controller");
            returnToMainMenu();
            return;
        }

        try {
            BoardGame newGame = null;

            if (lastLoadedBoardFile != null && lastLoadedBoardFile.exists()) {
                LOGGER.info("Resetting game with board from JSON file: " + lastLoadedBoardFile.getName());

                try {
                    Board loadedBoard = fileService.loadBoardFromFile(lastLoadedBoardFile);

                    if (loadedBoard != null) {
                        newGame = new BoardGame(loadedBoard.getGameType());
                        newGame.setBoard(loadedBoard);

                        if (lastGameRules != null) {
                            newGame.setGameRules(lastGameRules);
                        } else {
                            GameRules defaultRules = new GameRules(loadedBoard.getGameType());
                            newGame.setGameRules(defaultRules);
                        }

                        newGame.createDice(
                                newGame.getGameRules().getNumberOfDice(),
                                newGame.getGameRules().getDiceSides()
                        );
                    }
                } catch (Exception e) {
                    LOGGER.warning( "Error loading board from file, falling back to rules-based reset");
                }
            }

            if (newGame == null && lastGameRules != null) {
                LOGGER.info("Creating game with rules: " + lastGameRules.getRuleName());
                newGame = BoardGameFactory.createCustomGame(lastGameRules);
            }

            if (newGame == null && lastPlayedGameType != null) {
                LOGGER.info("Fallback: Creating basic game of type: " + lastPlayedGameType);
                newGame = BoardGameFactory.createGame(lastPlayedGameType);
            }

            if (newGame == null) {
                LOGGER.warning("Cannot reset game: failed to create a new game instance");
                showAlert("Error", "Could not reset game: failed to create a new game instance");
                returnToMainMenu();
                return;
            }

            gameView = new GameView(newGame.getBoard());
            gameController = BoardGameControllerFactory.createController(newGame, gameView, fileService);

            gameView.setOnResetGame(this::resetCurrentGame);
            gameView.setOnReturnToMainMenu(this::returnToMainMenu);

            if (!lastPlayers.isEmpty()) {
                for (Player player : lastPlayers) {
                    Player newPlayer = new Player(player.getName(), player.getToken());
                    newPlayer.reset();
                    gameController.addPlayer(newPlayer);
                }

                gameController.startGame();
                root.setCenter(gameView);

                LOGGER.info("Game reset successful with " + lastPlayers.size() + " players");
            } else {
                LOGGER.warning("No players available from last game");
                showAlert("Error", "Could not reset game: no players available");
                returnToMainMenu();
            }
        } catch (Exception e) {
            LOGGER.warning("Error resetting game");
            showAlert("Error", "Could not reset game: " + e.getMessage());
            returnToMainMenu();
        }
    }

    /**
     * Returns to the main menu.
     */
    private void returnToMainMenu() {
        LOGGER.info("Returning to main menu");
        root.setCenter(createStartMenu());
    }

    /**
     * Opens the game rules view for the selected game type.
     *
     * @param gameType The selected game type
     */
    private void openGameRulesView(GameType gameType) {
        LOGGER.info("Opening game rules view for: " + gameType);

        lastPlayedGameType = gameType;
        lastLoadedBoardFile = null;
        lastLoadedBoard = null;

        GameRulesView rulesView = new GameRulesView(gameType, stage);

        GameRulesController rulesController = new GameRulesController(
                gameType,
                rulesView,
                fileService,
                this::handleRulesSelection
        );

        rulesView.setController(rulesController);
        root.setCenter(rulesView);
    }

    /**
     * Handles the selection of game rules.
     *
     * @param rules The selected game rules
     */
    private void handleRulesSelection(GameRules rules) {
        if (rules == null) {
            LOGGER.info("Rules selection cancelled, returning to main menu");
            root.setCenter(createStartMenu());
            return;
        }

        LOGGER.info("Rules selected: " + rules.getRuleName() + ", creating game");

        lastGameRules = rules;

        try {
            BoardGame game;

            if (lastLoadedBoard != null) {
                game = new BoardGame(lastLoadedBoard.getGameType());
                game.setBoard(lastLoadedBoard);
                game.setGameRules(rules);
                game.createDice(rules.getNumberOfDice(), rules.getDiceSides());
            } else {
                game = BoardGameFactory.createCustomGame(rules);
            }

            gameView = new GameView(game.getBoard());

            gameController = BoardGameControllerFactory.createController(game, gameView, fileService);

            LOGGER.info("Game created, showing player selection");
            PlayerSelectionView playerSelectionView = new PlayerSelectionView(
                    gameController,
                    playerController,
                    fileService,
                    stage,
                    this::handlePlayerSelectionResult
            );

            root.setCenter(playerSelectionView);

        } catch (InvalidGameTypeException e) {
            LOGGER.log(Level.SEVERE, "Invalid game type", e);
            showAlert("Error", "Could not create game controller: " + e.getMessage());
            root.setCenter(createStartMenu());
        }
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

        File boardsDir = new File(BOARDS_DIRECTORY);
        if (boardsDir.exists() && boardsDir.isDirectory()) {
            fileChooser.setInitialDirectory(boardsDir);
        }

        File file = fileChooser.showOpenDialog(stage);
        if (file == null) {
            return;
        }

        try {
            Board loadedBoard = fileService.loadBoardFromFile(file);

            if (loadedBoard == null) {
                showAlert("Error", "Could not load board from file");
                return;
            }

            lastLoadedBoardFile = file;
            lastLoadedBoard = loadedBoard;

            GameType gameType = loadedBoard.getGameType();

            GameRulesView rulesView = new GameRulesView(gameType, stage);

            GameRulesController rulesController = new GameRulesController(
                    gameType,
                    rulesView,
                    fileService,
                    this::handleRulesSelection
            );

            rulesController.loadBoardFromFile(file);

            rulesView.setController(rulesController);
            root.setCenter(rulesView);

            LOGGER.info("Board loaded from JSON: " + file.getName() + ", showing rules configuration");

        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error loading board from JSON", ex);
            showAlert("Feil ved lesing av brett", ex.getMessage());
        }
    }

    /**
     * Shows an alert dialog.
     *
     * @param title The title of the alert
     * @param message The message to display
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();    }
}