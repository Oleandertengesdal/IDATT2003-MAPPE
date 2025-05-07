package idi.edu.idatt.mappe.controllers;

import idi.edu.idatt.mappe.models.Board;
import idi.edu.idatt.mappe.models.BoardGame;
import idi.edu.idatt.mappe.models.Player;
import idi.edu.idatt.mappe.models.Tile;
import idi.edu.idatt.mappe.models.enums.GameState;
import idi.edu.idatt.mappe.models.tileaction.CaptureTileAction;
import idi.edu.idatt.mappe.models.tileaction.StartingAreaTileAction;
import idi.edu.idatt.mappe.models.tileaction.TileAction;
import idi.edu.idatt.mappe.models.tileaction.WinTileAction;
import idi.edu.idatt.mappe.views.GameView;

import java.util.List;

/**
 * Controller specifically for Ludo type games.
 * Implements the game-specific logic for this game type.
 */
public class LudoController extends BoardGameController {

    /**
     * Creates a new LudoController with the given board game, view, and file service.
     *
     * @param boardGame The board game model
     * @param gameView The game view
     * @param fileService The file service for file operations
     */
    public LudoController(BoardGame boardGame, GameView gameView, FileService fileService) {
        super(boardGame, gameView, fileService);
        logger.info("LudoController initialized");
    }

    /**
     * Plays a turn in the Ludo game with proper rules
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

        logger.info("Playing a turn in Ludo");

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
        int diceValue = diceValues.getFirst();

        gameView.updateDiceDisplay(diceValues);

        handlePlayerTurn(currentPlayer, diceValue);
    }

    /**
     * Handles a player's turn based on their current state and the dice roll
     */
    private void handlePlayerTurn(Player player, int diceValue) {
        if (isPlayerInStartingArea(player)) {
            if (diceValue == 6) {
                moveFromStartingArea(player, () -> {
                    gameView.logGameEvent(player.getName() + " brought a piece onto the board and gets another roll!");
                    animationInProgress = false;
                    gameView.setRollDiceButtonEnabled(true);
                });
            } else {
                gameView.logGameEvent(player.getName() + " rolled a " + diceValue +
                        " and cannot move. Need a 6 to exit the starting area.");
                advanceToNextPlayer();
                animationInProgress = false;
            }
            return;
        }

        if (diceValue == 6) {
            if (hasRemainingPiecesInStartingArea(player)) {
                promptPlayerForMoveChoice(player, diceValue);
            } else {
                movePlayerPiece(player, diceValue, () -> {
                    gameView.logGameEvent(player.getName() + " gets another turn for rolling a 6!");
                    animationInProgress = false;
                    gameView.setRollDiceButtonEnabled(true);
                });
            }
        } else {
            movePlayerPiece(player, diceValue, () -> {
                advanceToNextPlayer();
                animationInProgress = false;
            });
        }
    }

    /**
     * Checks if all of a player's pieces are in the starting area
     */
    private boolean isPlayerInStartingArea(Player player) {
        Tile currentTile = player.getCurrentTile();
        return currentTile != null && currentTile.getLandAction() instanceof StartingAreaTileAction;
    }

    /**
     * Checks if a player has any remaining pieces in the starting area
     */
    private boolean hasRemainingPiecesInStartingArea(Player player) {
        return false;
    }

    /**
     * Moves a player's piece from the starting area to the first position on the board
     */
    private void moveFromStartingArea(Player player, Runnable callback) {
        Tile startTile = getPlayerStartTile(player);

        player.setCurrentTile(startTile);
        gameView.updatePlayerPosition(player);

        gameView.logGameEvent(player.getName() + " moved a piece onto the board at tile " +
                startTile.getIndex());

        if (callback != null) {
            callback.run();
        }
    }

    /**
     * Gets the starting tile for a player based on their color/position
     */
    private Tile getPlayerStartTile(Player player) {
        return null;
    }

    /**
     * Prompts the player to choose whether to bring a new piece onto the board
     * or move an existing piece
     */
    private void promptPlayerForMoveChoice(Player player, int diceValue) {


        movePlayerPiece(player, diceValue, () -> {
            gameView.logGameEvent(player.getName() + " gets another turn for rolling a 6!");
            animationInProgress = false;
            gameView.setRollDiceButtonEnabled(true);
        });
    }

    /**
     * Moves a player's piece a specific number of steps
     */
    private void movePlayerPiece(Player player, int steps, Runnable callback) {
        Tile currentTile = player.getCurrentTile();
        if (currentTile == null) {
            gameView.logGameEvent("Error: Player " + player.getName() + " is not on the board.");
            if (callback != null) {
                callback.run();
            }
            return;
        }

        Tile destinationTile = currentTile;
        for (int i = 0; i < steps; i++) {
            if (destinationTile.getNextTile() != null) {
                destinationTile = destinationTile.getNextTile();
            } else {
                break;
            }
        }

        player.setCurrentTile(destinationTile);

        TileAction action = destinationTile.getLandAction();
        if (action != null) {
            gameView.logGameEvent(player.getName() + " landed on a special tile: " +
                    action.getClass().getSimpleName().replace("TileAction", "") +
                    " - " + action.getDescription());

            action.perform(player);
        }

        if (action instanceof CaptureTileAction) {
            checkForCapture(player, destinationTile);
        }

        if (action instanceof WinTileAction) {
            gameView.logGameEvent(player.getName() + " has won the game!");
            boardGame.setGameState(GameState.FINISHED);
            boardGame.notifyObserversOfWinner(player);
        }

        gameView.updatePlayerPosition(player);

        if (callback != null) {
            callback.run();
        }
    }

    /**
     * Checks if a player can capture any opponent pieces on the same tile
     */
    private void checkForCapture(Player player, Tile tile) {
        for (Player otherPlayer : boardGame.getPlayers()) {
            if (otherPlayer != player && otherPlayer.getCurrentTile() == tile) {sendToStartingArea(otherPlayer);
                gameView.logGameEvent(player.getName() + " captured " + otherPlayer.getName() + "'s piece!");
                boardGame.notifyObserversOfCapture(player, otherPlayer);
            }
        }
    }

    /**
     * Sends a player's piece back to the starting area
     */
    private void sendToStartingArea(Player player) {
        int playerIndex = boardGame.getPlayers().indexOf(player);
        Board board = boardGame.getBoard();

        Tile startingAreaTile = null;

        switch (playerIndex) {
            case 0:
                startingAreaTile = board.getTileByCoordinates(2, 2);
                break;
            case 1:
                startingAreaTile = board.getTileByCoordinates(2, 12);
                break;
            case 2:
                startingAreaTile = board.getTileByCoordinates(12, 2);
                break;
            case 3:
                startingAreaTile = board.getTileByCoordinates(12, 12);
                break;
        }

        if (startingAreaTile != null) {
            player.setCurrentTile(startingAreaTile);
            gameView.updatePlayerPosition(player);
            gameView.logGameEvent(player.getName() + "'s piece was sent back to the starting area.");
        }
    }

    @Override
    protected void movePlayer(Player player, int steps, Runnable callback) {

        if (isPlayerInStartingArea(player)) {
            if (steps == 6) {
                moveFromStartingArea(player, callback);
            } else {
                if (callback != null) {
                    callback.run();
                }
            }
        } else {
            movePlayerPiece(player, steps, callback);
        }
    }

}