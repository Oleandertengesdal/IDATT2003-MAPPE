package idi.edu.idatt.mappe.controllers;

import idi.edu.idatt.mappe.models.BoardGame;
import idi.edu.idatt.mappe.models.Player;
import idi.edu.idatt.mappe.models.Tile;
import idi.edu.idatt.mappe.models.enums.GameState;
import idi.edu.idatt.mappe.models.tileaction.TileAction;
import idi.edu.idatt.mappe.views.GameView;

import java.util.List;

/**
 * Controller specifically for Snakes and Ladders type games.
 * Implements the game-specific logic for this game type.
 */
public class SnakesAndLaddersController extends BoardGameController {

    /**
     * Creates a new SnakesAndLaddersController with the given board game, view, and file service.
     *
     * @param boardGame The board game model
     * @param gameView The game view
     * @param fileService The file service for file operations
     */
    public SnakesAndLaddersController(BoardGame boardGame, GameView gameView, FileService fileService) {
        super(boardGame, gameView, fileService);
        logger.info("SnakesAndLaddersController initialized");
    }

    /**
     * Plays a turn of the Snakes and Ladders game.
     */
    @Override
    public void playTurn() {
        if (boardGame.getGameState() != GameState.STARTED) {
            gameView.logGameEvent("Game has not been started yet. Press 'Start Game' to begin.");
            return;
        }

        if (boardGame.isFinished()) {
            gameView.logGameEvent("Game is already finished. Start a new game to play again.");
            return;
        }

        if (animationInProgress) {
            gameView.logGameEvent("Animation in progress. Please wait.");
            return;
        }
        logger.info("Playing a turn in Snakes and Ladders");

        animationInProgress = true;

        List<Player> players = boardGame.getPlayers();
        if(players.isEmpty()) {
            gameView.logGameEvent("No players in the game. Please add players to start.");
            animationInProgress = false;
            return;
        }

        Player currentPlayer = players.get(currentPlayerIndex);
        gameView.logGameEvent("Current player: " + currentPlayer.getName());

        List<Integer> diceValues = rollDice();
        int totalSteps = diceValues.stream().mapToInt(Integer::intValue).sum();

        gameView.updateDiceDisplay(diceValues);

        movePlayer(currentPlayer, totalSteps, () -> {
            if (boardGame.isFinished()) {
                gameView.logGameEvent(currentPlayer.getName() + " has won the game!");
                boardGame.setGameState(GameState.FINISHED);
            } else {
                advanceToNextPlayer();
                animationInProgress = false;
            }
        });
    }

    /**
     * Moves a player a specific number of steps in Snakes and Ladders.
     *
     * @param player The player to move
     * @param steps Number of steps to move
     * @param callback Callback to run after move completes
     */
    @Override
    protected void movePlayer(Player player, int steps, Runnable callback) {
        int startingTileIndex = 0;
        if (player.getCurrentTile() != null) {
            startingTileIndex = player.getCurrentTile().getIndex();
        }

        boardGame.movePlayer(player, steps);

        if (player.getCurrentTile() != null) {
            Tile landedTile = player.getCurrentTile();

            if (landedTile != null && landedTile.getLandAction() != null) {
                TileAction action = landedTile.getLandAction();

                gameView.logGameEvent(player.getName() + " landed on a special tile: " +
                        action.getClass().getSimpleName().replace("TileAction", "") +
                        " - " + action.getDescription());

                action.perform(player);

                gameView.logGameEvent(player.getName() + " moved to tile " +
                        (player.getCurrentTile() != null ? player.getCurrentTile().getIndex() : "unknown"));
            }

            gameView.updatePlayerPosition(player);

            if (boardGame.isFinished()) {
                gameView.logGameEvent(player.getName() + " has won the game!");
                boardGame.setGameState(GameState.FINISHED);
            }

            if (callback != null) {
                callback.run();
            }
        } else {
            gameView.updatePlayerPosition(player);
            if (callback != null) {
                callback.run();
            }
        }
    }
}