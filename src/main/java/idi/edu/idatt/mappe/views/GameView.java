package idi.edu.idatt.mappe.views;

import idi.edu.idatt.mappe.models.*;
import idi.edu.idatt.mappe.models.enums.GameState;
import idi.edu.idatt.mappe.services.AnimationService;
import idi.edu.idatt.mappe.services.ColorService;
import idi.edu.idatt.mappe.services.TokenService;
import idi.edu.idatt.mappe.views.game.*;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * Main view for the board game.
 * Coordinates all sub-views and provides interaction with the game controller.
 * This view implements the BoardGameObserver interface to receive notifications of game events.
 */
public class GameView extends BorderPane implements BoardGameObserver {
    private static final Logger logger = Logger.getLogger(GameView.class.getName());

    private final double MIN_BOARD_WIDTH = 720;
    private final double MIN_BOARD_HEIGHT = 650;
    private final double CONTROL_PANEL_WIDTH = 280;

    private final TokenService tokenService;
    private final ColorService colorService;
    private final AnimationService animationService;

    private final Pane boardPane;
    private final BoardView boardView;
    private final PlayerTokenView playerTokenView;
    private final DiceView diceView;
    private final PlayerStatusPanelView playerStatusView;
    private final GameLogView gameLogView;

    private Button rollDiceButton;

    private final Board board;

    /**
     * Creates a new GameView.
     *
     * @param board The game board to display
     */
    public GameView(Board board) {
        this.board = board;

        this.tokenService = new TokenService();
        this.colorService = new ColorService();
        this.animationService = new AnimationService();

        boardPane = new Pane();
        boardPane.getStyleClass().add("board-pane");

        boardView = new BoardView(boardPane, colorService, board, MIN_BOARD_WIDTH, MIN_BOARD_HEIGHT);
        playerTokenView = new PlayerTokenView(boardPane, tokenService, animationService, MIN_BOARD_WIDTH, MIN_BOARD_HEIGHT);

        playerTokenView.setBoard(board);
        playerTokenView.setBoardView(boardView);

        diceView = new DiceView(animationService);
        playerStatusView = new PlayerStatusPanelView(tokenService);
        gameLogView = new GameLogView();

        VBox rightControlPanel = createRightControlPanel();

        setCenter(boardPane);
        setRight(rightControlPanel);

        gameLogView.logGameEvent("Welcome to the Board Game!");

        logger.info("GameView initialized");
    }

    /**
     * Creates the right control panel with game controls, player status, and game log.
     *
     * @return The right control panel as a VBox
     */
    private VBox createRightControlPanel() {
        VBox controlPanel = new VBox(15);
        controlPanel.setPadding(new Insets(20));
        controlPanel.setPrefWidth(CONTROL_PANEL_WIDTH);
        controlPanel.getStyleClass().add("game-controls");

        Label gameTitleLabel = new Label(board.getGameType().getName());
        gameTitleLabel.getStyleClass().add("game-title");

        Label controlsLabel = new Label("Game Controls");
        controlsLabel.getStyleClass().add("game-controls-label");

        rollDiceButton = new Button("Roll Dice & Move");
        rollDiceButton.setPrefWidth(CONTROL_PANEL_WIDTH - 40);
        rollDiceButton.getStyleClass().add("button-primary");
        rollDiceButton.setDisable(true);

        Label playerStatusLabel = new Label("Players");
        playerStatusLabel.getStyleClass().add("game-controls-label");

        Label diceLabel = new Label("Dice");
        diceLabel.getStyleClass().add("game-controls-label");

        controlPanel.getChildren().addAll(
                gameTitleLabel,
                controlsLabel,
                rollDiceButton,
                new Separator(),
                playerStatusLabel,
                playerStatusView,
                new Separator(),
                diceLabel,
                diceView,
                new Separator(),
                gameLogView
        );

        return controlPanel;
    }

    /**
     * Sets the roll dice button action.
     *
     * @param action The action to perform when the button is clicked
     */
    public void setRollDiceAction(Runnable action) {
        rollDiceButton.setOnAction(e -> {
            rollDiceButton.setDisable(true);
            action.run();
        });
    }

    /**
     * Enables or disables the roll dice button.
     *
     * @param enabled Whether the button should be enabled
     */
    public void setRollDiceButtonEnabled(boolean enabled) {
        rollDiceButton.setDisable(!enabled);
    }

    /**
     * Adds a player to the game view.
     *
     * @param player The player to add
     */
    public void addPlayer(Player player) {
        Color playerColor = colorService.getPlayerColor(player);

        playerTokenView.addPlayerToken(player, playerColor);

        playerStatusView.addPlayer(player, playerColor);

        gameLogView.logGameEvent(player.getName() + " joined the game with token: " + player.getToken());

        logger.info("Added player: " + player.getName());
    }

    /**
     * Updates the dice display with new values.
     *
     * @param diceValues The values of the dice to display
     */
    public void updateDiceDisplay(List<Integer> diceValues) {
        diceView.updateDiceDisplay(diceValues, () -> {
            setRollDiceButtonEnabled(true);
        });
    }

    /**
     * Creates dice with a specific number of dice and sides.
     *
     * @param numberOfDice The number of dice to create
     * @param sides The number of sides on each die
     */
    public void createDice(int numberOfDice, int sides) {
        diceView.createDice(numberOfDice, sides);
    }

    /**
     * Updates a player's position on the board.
     *
     * @param player The player to update
     */
    public void updatePlayerPosition(Player player) {
        playerTokenView.updatePlayerPosition(player);
        playerStatusView.updatePlayerStatus(player);
    }

    /**
     * Adds a message to the game log.
     *
     * @param message The message to add
     */
    public void logGameEvent(String message) {
        gameLogView.logGameEvent(message);
    }

    /**
     * Shows a winner dialog.
     *
     * @param winner The winning player
     */
    public void showWinnerDialog(Player winner) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Winner!");
            alert.setHeaderText("We have a winner!");
            alert.setContentText(winner.getName() + " has won the game!");

            ButtonType returnToMainButton = new ButtonType("Return to Main Menu");
            ButtonType replayButton = new ButtonType("Play Again");

            alert.getButtonTypes().setAll(returnToMainButton, replayButton);

            Optional<ButtonType> result = alert.showAndWait();
        });
    }

    /**
     * Gets the board.
     *
     * @return The board
     */
    public Board getBoard() {
        return board;
    }

    /**
     * Gets the color service.
     *
     * @return The color service
     */
    public ColorService getColorService() {
        return colorService;
    }


    // BoardGameObserver implementation

    @Override
    public void onPlayerMoved(Player player, int steps) {
        logGameEvent(player.getName() + " moved " + steps + " steps to tile " +
                (player.getCurrentTile() != null ? player.getCurrentTile().getIndex() : "unknown"));

        Platform.runLater(() -> updatePlayerPosition(player));
    }

    @Override
    public void onGameStateChanged(GameState gameState) {
        switch (gameState) {
            case STARTED -> {
                logGameEvent("Game has started!");
                setRollDiceButtonEnabled(true);
            }
            case FINISHED -> {
                logGameEvent("Game has ended!");
                setRollDiceButtonEnabled(false);
            }
            case NOT_STARTED -> {
                logGameEvent("Game is ready to start.");
                setRollDiceButtonEnabled(false);
            }
        }
    }

    @Override
    public void onGameWinner(Player winner) {
        logGameEvent("🏆 " + winner.getName() + " has won the game! 🏆");
        showWinnerDialog(winner);
    }

    @Override
    public void onPlayerCaptured(Player captor, Player victim) {
        // TODO: implement later.
    }
}