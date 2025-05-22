package idi.edu.idatt.mappe.views;

import idi.edu.idatt.mappe.models.*;
import idi.edu.idatt.mappe.models.enums.GameState;
import idi.edu.idatt.mappe.models.enums.GameType;
import idi.edu.idatt.mappe.services.AnimationController;
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

    private final ColorService colorService;

    private final BoardView boardView;
    private final PlayerTokenView playerTokenView;
    private final DiceView diceView;
    private final PlayerStatusPanelView playerStatusView;
    private final GameLogView gameLogView;

    private Button actionButton;

    private final Board board;

    private Runnable onResetGame;
    private Runnable onReturnToMainMenu;


    /**
     * Creates a new GameView.
     *
     * @param board The game board to display
     */
    public GameView(Board board) {
        this.board = board;

        TokenService tokenService = new TokenService();
        this.colorService = new ColorService();
        AnimationController animationController = new AnimationController();

        Pane boardPane = new Pane();
        boardPane.getStyleClass().add("board-pane");

        double MIN_BOARD_WIDTH = 720;
        double MIN_BOARD_HEIGHT = 650;
        boardView = new BoardView(boardPane, colorService, board, MIN_BOARD_WIDTH, MIN_BOARD_HEIGHT);
        playerTokenView = new PlayerTokenView(boardPane, tokenService, animationController, MIN_BOARD_WIDTH, MIN_BOARD_HEIGHT);

        playerTokenView.setBoard(board);
        playerTokenView.setBoardView(boardView);

        diceView = new DiceView(animationController);
        playerStatusView = new PlayerStatusPanelView(tokenService);
        gameLogView = new GameLogView();

        VBox rightControlPanel = createRightControlPanel();

        setCenter(boardPane);
        setRight(rightControlPanel);

        gameLogView.logGameEvent("Welcome to the Board Game!");

        logger.info("GameView initialized");
    }

    /**
     * Sets the callback to handle game reset requests.
     *
     * @param onResetGame Callback to execute when a game reset is requested
     */
    public void setOnResetGame(Runnable onResetGame) {
        this.onResetGame = onResetGame;
    }

    /**
     * Sets the callback to handle returning to the main menu.
     *
     * @param onReturnToMainMenu Callback to execute when a return to main menu is requested
     */
    public void setOnReturnToMainMenu(Runnable onReturnToMainMenu) {
        this.onReturnToMainMenu = onReturnToMainMenu;
    }

    /**
     * Creates the right control panel with game controls, player status, and game log.
     *
     * @return The right control panel as a VBox
     */
    private VBox createRightControlPanel() {
        VBox controlPanel = new VBox(15);
        controlPanel.setPadding(new Insets(20));
        double CONTROL_PANEL_WIDTH = 280;
        controlPanel.setPrefWidth(CONTROL_PANEL_WIDTH);
        controlPanel.getStyleClass().add("game-controls");

        Label gameTitleLabel = new Label(board.getGameType().getName());
        gameTitleLabel.getStyleClass().add("game-title");

        Label controlsLabel = new Label("Game Controls");
        controlsLabel.getStyleClass().add("game-controls-label");

        // Update button text based on game type
        String buttonText = getActionButtonText();
        actionButton = new Button(buttonText);
        actionButton.setPrefWidth(CONTROL_PANEL_WIDTH - 40);
        actionButton.getStyleClass().add("button-primary");
        actionButton.setDisable(true); // Start with button disabled

        Label playerStatusLabel = new Label("Players");
        playerStatusLabel.getStyleClass().add("game-controls-label");

        Label diceLabel = new Label("Dice");
        diceLabel.getStyleClass().add("game-controls-label");

        controlPanel.getChildren().addAll(
                gameTitleLabel,
                controlsLabel,
                actionButton,
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
     * Gets the appropriate action button text based on the game type
     *
     * @return The button text
     */
    private String getActionButtonText() {
        if (board.getGameType() == GameType.THE_LOST_DIAMOND) {
            return "Take Turn";
        } else {
            return "Roll Dice & Move";
        }
    }

    /**
     * Sets the action button action.
     *
     * @param action The action to perform when the button is clicked
     */
    public void setRollDiceAction(Runnable action) {
        actionButton.setOnAction(e -> {
            setRollDiceButtonEnabled(false);
            action.run();
        });
    }

    /**
     * Enables or disables the action button.
     * This method is thread-safe and will ensure UI updates happen on the JavaFX thread.
     *
     * @param enabled Whether the button should be enabled
     */
    public void setRollDiceButtonEnabled(boolean enabled) {
        if (Platform.isFxApplicationThread()) {
            actionButton.setDisable(!enabled);
            logger.fine("Action button " + (enabled ? "enabled" : "disabled") + " on FX thread");
        } else {
            Platform.runLater(() -> {
                actionButton.setDisable(!enabled);
                logger.fine("Action button " + (enabled ? "enabled" : "disabled") + " via runLater");
            });
        }
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
        diceView.updateDiceDisplay(diceValues);
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
     * Shows a winner dialog with options to return to main menu or play again.
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

            if (result.isPresent()) {
                ButtonType selectedButton = result.get();

                if (selectedButton == returnToMainButton) {
                    logger.info("User chose to return to main menu");
                    if (onReturnToMainMenu != null) {
                        onReturnToMainMenu.run();
                    } else {
                        logger.warning("Return to main menu callback not set");
                    }
                } else if (selectedButton == replayButton) {
                    logger.info("User chose to play again");
                    if (onResetGame != null) {
                        onResetGame.run();
                    } else {
                        logger.warning("Reset game callback not set");
                    }
                }
            }
        });
    }

    /**
     * Updates the player's money display.
     *
     * @param player The player to update
     */
    public void updatePlayerMoney(Player player) {
        if (player == null) return;

        if (board.getGameType() == GameType.THE_LOST_DIAMOND) {
            playerStatusView.updatePlayerMoney(player);
        } else {
            playerStatusView.updatePlayerStatus(player);
        }
    }

    /**
     * Initializes the game view based on the game type.
     * This method should be called after the game view is created.
     */
    public void initializeForGameType() {
        if (board.getGameType() == GameType.THE_LOST_DIAMOND) {
            playerStatusView.showMoneyInsteadOfPosition(true);
            actionButton.setText("Take Turn");
        } else {
            playerStatusView.showMoneyInsteadOfPosition(false);
            actionButton.setText("Roll Dice & Move");
        }
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

    public void refreshBoardView() {
        boardView.updateTokens();
    }


    /**
     * On player moved event.
     */
    @Override
    public void onPlayerMoved(Player player, int steps) {
        String message;
        if (board.getGameType() == GameType.THE_LOST_DIAMOND) {
            message = player.getName() + " moved to " +
                    (player.getCurrentTile() != null ? player.getCurrentTile().getName() : "unknown location");
        } else {
            message = player.getName() + " moved " + steps + " steps to tile " +
                    (player.getCurrentTile() != null ? player.getCurrentTile().getIndex() : "unknown");
        }

        logGameEvent(message);
        Platform.runLater(() -> updatePlayerPosition(player));
    }

    /**
     * On game state changed event.
     */
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

    /**
     * On player turn changed event.
     */
    @Override
    public void onGameWinner(Player winner) {
        logGameEvent("🏆 " + winner.getName() + " has won the game! 🏆");
        showWinnerDialog(winner);
    }

    /**
     * On player captured event.
     */
    @Override
    public void onPlayerCaptured(Player captor, Player victim) {
    }

    /**
     * On player extra turn event.
     */
    @Override
    public void onPlayerExtraTurn(Player player) {
        logGameEvent(player.getName() + " gets an extra turn!");
        setRollDiceButtonEnabled(true);
    }

    /**
     * On player skip turn event.
     */
    @Override
    public void onPlayerSkipTurn(Player player) {
        logGameEvent(player.getName() + " loses a turn!");
    }
}