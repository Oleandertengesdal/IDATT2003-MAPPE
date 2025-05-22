package idi.edu.idatt.mappe.controllers;

import idi.edu.idatt.mappe.models.BoardGame;
import idi.edu.idatt.mappe.models.Player;
import idi.edu.idatt.mappe.models.PlayerSelectionEntry;
import idi.edu.idatt.mappe.models.enums.GameState;
import idi.edu.idatt.mappe.views.GameView;


import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Abstract base controller for all board games.
 * Contains common functionality for all types of board games.
 */
public abstract class BoardGameController {

    protected final BoardGame boardGame;
    protected GameView gameView;
    protected final FileService fileService;
    protected static final Logger logger = Logger.getLogger(BoardGameController.class.getName());

    protected int currentPlayerIndex = 0;
    protected boolean animationInProgress = false;

    /**
     * Creates a new BoardGameController with the given board game, view, and file service.
     *
     * @param boardGame The board game model
     * @param gameView The game view
     * @param fileService The file service for file operations
     */
    public BoardGameController(BoardGame boardGame, GameView gameView, FileService fileService) {
        this.boardGame = boardGame;
        this.gameView = gameView;
        this.fileService = fileService;

        boardGame.addObserver(gameView);
        setupDiceDisplay();
        gameView.setRollDiceAction(this::playTurn);

        logger.info("BoardGameController initialized with board game, view, and file service");
    }

    /**
     * Sets up the dice display in the view based on the game's dice configuration.
     */
    protected void setupDiceDisplay() {
        if (boardGame.getDice() != null) {
            int numberOfDice = boardGame.getDice().getNumberOfDice();
            int sides = boardGame.getDice().getNumberOfSides(0);
            gameView.createDice(numberOfDice, sides);
        } else {
            gameView.createDice(2, 6);
        }
    }

    /**
     * Adds a player to the game with the given name.
     *
     * @param name The name of the player
     */
    public void addPlayer(String name) {
        logger.info("Adding player: " + name);
        Player player = new Player(name);
        boardGame.addPlayer(player);
        gameView.addPlayer(player);
    }

    /**
     * Adds a player to the game.
     *
     * @param player The player to add
     */
    public void addPlayer(Player player) {
        logger.info("Adding player: " + player.getName());
        boardGame.addPlayer(player);
        gameView.addPlayer(player);
    }

    /**
     * gets the players in the game.
     *
     * @return A list of players in the game
     */
    public List<Player> getPlayers() {
        return boardGame.getPlayers();
    }

    /**
     * Adds a player to the game with the given name and token.
     *
     * @param playerEntry The player entry containing player and color information
     */
    public void addPlayer(PlayerSelectionEntry playerEntry) {
        Player player = playerEntry.getPlayer();
        String colorHex = playerEntry.getColor();

        logger.info("Adding player with color: " + player.getName() +
                " token: " + player.getToken() +
                " color: " + colorHex);

        gameView.getColorService().setPlayerColorFromHex(player, colorHex);

        this.addPlayer(player);

        logger.info("Player fully added with color: " + colorHex);
    }

    /**
     * Starts the game.
     */
    public void startGame() {
        logger.info("Starting game");

        if (boardGame.getPlayers().isEmpty()) {
            gameView.logGameEvent("Cannot start game without players. Please add at least one player.");
            return;
        }
        currentPlayerIndex = 0;

        boardGame.startGame();
        boardGame.setCurrentPlayer(boardGame.getPlayers().get(currentPlayerIndex));
        String firstPlayerName = boardGame.getCurrentPlayer().getName();

        gameView.logGameEvent("Game started with " + boardGame.getPlayers().size() + " players. " +
                "Current player: " + firstPlayerName);

        gameView.setRollDiceButtonEnabled(true);
    }

    /**
     * Abstract method to play a turn of the game.
     * Each game type will implement this differently.
     */
    public abstract void playTurn();

    /**
     * Rolls the dice and returns the values.
     *
     * @return A list of dice values
     */
    protected List<Integer> rollDice() {
        if (boardGame.getGameState() != GameState.STARTED) {
            gameView.logGameEvent("Cannot roll dice. Game has not started yet.");
            return new ArrayList<>();
        }

        int total = boardGame.getDice().roll();
        logger.info("Dice rolled: " + total);

        List<Integer> values = boardGame.getDice().getValues();
        logger.info("Dice values: " + values);

        gameView.updateDiceDisplay(values);

        gameView.logGameEvent("Dice rolled: " + values.stream()
                .map(Object::toString)
                .collect(Collectors.joining(", ")) + " (Total: " + total + ")");

        return values;
    }

    /**
     * Advances to the next player's turn.
     */
    protected void advanceToNextPlayer() {
        List<Player> players = boardGame.getPlayers();

        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();

        Player nextPlayer = players.get(currentPlayerIndex);
        boardGame.setCurrentPlayer(nextPlayer);

        gameView.logGameEvent("Next turn: " + nextPlayer.getName());

        gameView.setRollDiceButtonEnabled(true);
    }

    /**
     * Gets the current game state.
     *
     * @return The current game state
     */
    public GameState getGameState() {
        return boardGame.getGameState();
    }

    /**
     * Gets the board game.
     *
     * @return The board game
     */
    public BoardGame getBoardGame() {
        return boardGame;
    }

    /**
     * Gets the game view.
     *
     * @return The game view
     */
    public GameView getView() {
        return gameView;
    }


    /**
     * Abstract method to move a player a specific number of steps.
     * Each game type will implement this differently.
     *
     * @param player The player to move
     * @param steps Number of steps to move
     * @param callback Callback to run after move completes
     */
    protected abstract void movePlayer(Player player, int steps, Runnable callback);
}