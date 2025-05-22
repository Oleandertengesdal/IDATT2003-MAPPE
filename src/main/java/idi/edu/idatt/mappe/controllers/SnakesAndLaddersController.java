package idi.edu.idatt.mappe.controllers;

import idi.edu.idatt.mappe.models.BoardGame;
import idi.edu.idatt.mappe.models.GameRules;
import idi.edu.idatt.mappe.models.Player;
import idi.edu.idatt.mappe.models.Tile;
import idi.edu.idatt.mappe.models.enums.GameState;
import idi.edu.idatt.mappe.views.GameView;

import java.util.List;
import java.util.logging.Logger;

/**
 * Controller for Snakes and Ladders game logic.
 */
public class SnakesAndLaddersController extends BoardGameController {
    private static final Logger logger = Logger.getLogger(SnakesAndLaddersController.class.getName());

    private boolean[] skipNextTurn;
    private int consecutiveSixes;

    /**
     * Creates a new SnakesAndLaddersController.
     *
     * @param boardGame The board game model
     * @param gameView The game view
     * @param fileService The file service for file operations
     */
    public SnakesAndLaddersController(BoardGame boardGame, GameView gameView, FileService fileService) {
        super(boardGame, gameView, fileService);
        logger.info("Initialized SnakesAndLaddersController");
    }

    /**
     * Starts the game by initializing player positions and enabling the roll dice button.
     */
    @Override
    public void startGame() {
        super.startGame();

        skipNextTurn = new boolean[boardGame.getPlayers().size()];
        consecutiveSixes = 0;

        List<Player> players = boardGame.getPlayers();
        for (Player player : players) {
            player.setCurrentTile(boardGame.getBoard().getTileByIndex(1));
        }

        players.forEach(player -> gameView.updatePlayerPosition(player));

        gameView.logGameEvent("Game started. All players positioned at the starting tile.");

        gameView.setRollDiceButtonEnabled(true);
    }

    /**
     * Handles the player's turn by rolling the dice and moving the player.
     * This method is called when the player clicks the "Roll Dice" button.
     */
    @Override
    public void playTurn() {
        if (isAnimationInProgressOrGameNotStarted()) return;

        Player currentPlayer = boardGame.getPlayers().get(currentPlayerIndex);
        boardGame.setCurrentPlayer(currentPlayer);
        gameView.setRollDiceButtonEnabled(false);

        if (handleSkippedTurn(currentPlayer)) return;

        GameRules rules = boardGame.getGameRules();
        List<Integer> diceValues = rollDice();
        if (diceValues.isEmpty()) {
            handleEmptyDiceRoll();
            return;
        }

        int totalRoll = diceValues.stream().mapToInt(Integer::intValue).sum();
        gameView.logGameEvent(currentPlayer.getName() + " rolled " +
                formatDiceValues(diceValues) + " (Total: " + totalRoll + ")");

        if (handleStartOnlyWithSixRule(currentPlayer, rules, diceValues)) return;

        boolean isMaxRoll = checkMaxRoll(rules, totalRoll);
        animationInProgress = true;

        movePlayer(currentPlayer, totalRoll, () -> handlePostMoveActions(currentPlayer, rules, isMaxRoll));
    }

    /**
     * Checks if the animation is in progress or if the game has not started.
     *
     * @return true if animation is in progress or game has not started, false otherwise
     */
    private boolean isAnimationInProgressOrGameNotStarted() {
        if (animationInProgress) {
            logger.info("Animation in progress, ignoring button click");
            return true;
        }
        if (boardGame.getGameState() != GameState.STARTED) {
            gameView.logGameEvent("Game has not started yet. Please start the game first.");
            return true;
        }
        return false;
    }

    /**
     * Handles the case where the current player needs to skip their turn.
     *
     * @param currentPlayer The current player
     * @return true if the turn is skipped, false otherwise
     */
    private boolean handleSkippedTurn(Player currentPlayer) {
        if (skipNextTurn[currentPlayerIndex] || currentPlayer.isMissingTurn()) {
            gameView.logGameEvent(currentPlayer.getName() + " skips this turn.");
            skipNextTurn[currentPlayerIndex] = false;
            currentPlayer.setMissingTurn(false);
            advanceToNextPlayer();
            return true;
        }
        return false;
    }

    /**
     * Handles the case where no dice values are returned.
     */
    private void handleEmptyDiceRoll() {
        logger.warning("No dice values returned, cannot continue turn");
        gameView.setRollDiceButtonEnabled(true);
    }

    /**
     * Handles the case where the player needs to roll a 6 to start.
     *
     * @param currentPlayer The current player
     * @param rules The game rules
     * @param diceValues The rolled dice values
     * @return true if the player cannot start, false otherwise
     */
    private boolean handleStartOnlyWithSixRule(Player currentPlayer, GameRules rules, List<Integer> diceValues) {
        if (rules != null && rules.isStartOnlyWithMax() && currentPlayer.getCurrentTile().getIndex() == 1) {
            int maxRoll = rules.getNumberOfDice() * rules.getDiceSides();
            boolean canStart = diceValues.stream().mapToInt(Integer::intValue).sum() == maxRoll;

            if (!canStart) {
                gameView.logGameEvent(currentPlayer.getName() + " needs a 6 to start. Staying at the starting position.");
                advanceToNextPlayer();
                return true;
            }
            gameView.logGameEvent(currentPlayer.getName() + " rolled a 6 and can start the game!");
        }
        return false;
    }

    /**
     * Checks if the player rolled the maximum value and handles the consequences.
     *
     * @param rules The game rules
     * @param totalRoll The total roll value
     * @return true if the player rolled the maximum value, false otherwise
     */
    private boolean checkMaxRoll(GameRules rules, int totalRoll) {
        if (rules == null) return false;

        int maxRoll = rules.getMaxRoll();
        if (totalRoll == maxRoll) {
            consecutiveSixes++;
            gameView.logGameEvent(boardGame.getCurrentPlayer().getName() + " rolled the maximum value of " + maxRoll + "!");
            if (rules.getConsecutiveSixesLimit() > 0 && consecutiveSixes >= rules.getConsecutiveSixesLimit()) {
                gameView.logGameEvent(boardGame.getCurrentPlayer().getName() + " rolled maximum " +
                        consecutiveSixes + " times in a row. Forfeit turn!");
                consecutiveSixes = 0;
                advanceToNextPlayer();
                return false;
            }
            return true;
        }
        consecutiveSixes = 0;
        return false;
    }

    /**
     * Handles actions after the player has moved.
     *
     * @param currentPlayer The current player
     * @param rules The game rules
     * @param isMaxRoll Whether the player rolled the maximum value
     */
    private void handlePostMoveActions(Player currentPlayer, GameRules rules, boolean isMaxRoll) {
        try {
            if (checkWinCondition(currentPlayer)) return;

            boolean extraTurn = checkExtraTurnConditions(currentPlayer, rules, isMaxRoll);
            if (!extraTurn) advanceToNextPlayer();
        } finally {
            animationInProgress = false;
        }
    }

    /**
     * Checks if the current player has won the game.
     *
     * @param currentPlayer The current player
     * @return true if the player has won, false otherwise
     */
    private boolean checkWinCondition(Player currentPlayer) {
        if (currentPlayer.getCurrentTile().getIndex() >= boardGame.getBoard().getTiles().size()) {
            gameView.logGameEvent(currentPlayer.getName() + " wins the game!");
            boardGame.notifyObserversOfWinner(currentPlayer);
            boardGame.setGameState(GameState.FINISHED);
            return true;
        }
        return false;
    }

    /**
     * Checks if the player meets the conditions for an extra turn or to skip their next turn.
     *
     * @param currentPlayer The current player
     * @param rules The game rules
     * @param isMaxRoll Whether the player rolled the maximum value
     * @return true if the player gets an extra turn, false otherwise
     */
    private boolean checkExtraTurnConditions(Player currentPlayer, GameRules rules, boolean isMaxRoll) {
        boolean extraTurn = false;

        if (currentPlayer.hasExtraThrow()) {
            gameView.logGameEvent(currentPlayer.getName() + " gets an extra turn from special tile!");
            currentPlayer.setExtraThrow(false);
            extraTurn = true;
        }


        if (rules != null && rules.isExtraThrowOnMax() && isMaxRoll) {
            gameView.logGameEvent(currentPlayer.getName() + " rolled maximum value and gets an extra turn!");
            extraTurn = true;
        }

        Tile currentTile = currentPlayer.getCurrentTile();
        if (rules != null && rules.isExtraTurnOnLadder() &&
                currentTile.getLandAction() != null &&
                currentTile.getLandAction().getClass().getSimpleName().contains("LadderTileAction")) {
            gameView.logGameEvent(currentPlayer.getName() + " climbed a ladder and gets an extra turn!");
            extraTurn = true;
        }

        if (rules != null && rules.isSkipTurnOnSnake() &&
                currentTile.getLandAction() != null &&
                currentTile.getLandAction().getClass().getSimpleName().contains("SnakeTileAction")) {
            gameView.logGameEvent(currentPlayer.getName() + " was bitten by a snake and will skip next turn!");
            skipNextTurn[currentPlayerIndex] = true;
        }

        if (extraTurn) boardGame.notifyObserversOfExtraTurn(currentPlayer);
        return extraTurn;
    }

    /**
     * Formats dice values for display in game log.
     *
     * @param diceValues List of dice values
     * @return Formatted string representation
     */
    private String formatDiceValues(List<Integer> diceValues) {
        if (diceValues == null || diceValues.isEmpty()) {
            return "?";
        }

        if (diceValues.size() == 1) {
            return String.valueOf(diceValues.getFirst());
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < diceValues.size(); i++) {
            sb.append(diceValues.get(i));
            if (i < diceValues.size() - 1) {
                sb.append(" + ");
            }
        }
        return sb.toString();
    }

    @Override
    protected void movePlayer(Player player, int steps, Runnable callback) {
        int currentIndex = player.getCurrentTile().getIndex();
        int targetIndex = Math.min(currentIndex + steps, boardGame.getBoard().getTiles().size());

        gameView.logGameEvent(player.getName() + " moves " + steps + " steps from " +
                currentIndex + " to " + targetIndex);

        player.setCurrentTile(boardGame.getBoard().getTileByIndex(targetIndex));
        gameView.updatePlayerPosition(player);

        Tile currentTile = player.getCurrentTile();
        if (currentTile.getLandAction() != null) {
            currentTile.performLandAction(player);

            int newIndex = player.getCurrentTile().getIndex();
            if (newIndex != targetIndex) {
                String actionName = currentTile.getLandAction().getClass().getSimpleName();
                if (actionName.contains("RandomTeleportTileAction")) {
                    gameView.logGameEvent(player.getName() + " was teleported from " +
                            targetIndex + " to " + newIndex + "!");
                } else if (actionName.contains("LadderTileAction")) {
                    gameView.logGameEvent(player.getName() + " climbed a ladder from " +
                            targetIndex + " to " + newIndex + "!");
                } else if (actionName.contains("SnakeTileAction")) {
                    gameView.logGameEvent(player.getName() + " slid down a snake from " +
                            targetIndex + " to " + newIndex + "!");
                } else if (actionName.contains("SwapAction")) {
                    gameView.logGameEvent(player.getName() + " landed on a swap tile and swapped positions with another player!");
                } else {
                    gameView.logGameEvent(player.getName() + " moved from " +
                            targetIndex + " to " + newIndex + " due to a special tile!");
                }
            }

            gameView.updatePlayerPosition(player);
        }

        try {
            if (callback != null) {
                callback.run();
            }
        } catch (Exception e) {
            logger.severe("Error in movePlayer callback: " + e.getMessage());
            animationInProgress = false;
            gameView.setRollDiceButtonEnabled(true);
        }
    }
}