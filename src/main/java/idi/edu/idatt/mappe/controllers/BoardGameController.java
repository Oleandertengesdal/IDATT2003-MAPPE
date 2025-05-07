package idi.edu.idatt.mappe.controllers;

import idi.edu.idatt.mappe.exceptions.JsonParsingException;
import idi.edu.idatt.mappe.exceptions.TileActionNotFoundException;
import idi.edu.idatt.mappe.models.Board;
import idi.edu.idatt.mappe.models.BoardGame;
import idi.edu.idatt.mappe.models.Player;
import idi.edu.idatt.mappe.models.PlayerSelectionEntry;
import idi.edu.idatt.mappe.models.enums.GameState;
import idi.edu.idatt.mappe.controllers.FileService;
import idi.edu.idatt.mappe.views.GameView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
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
        boardGame.addPlayer(player);
        gameView.addPlayer(player);

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
        gameView.logGameEvent("Next turn: " + nextPlayer.getName());
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
     * Loads players from a CSV file using FileService.
     *
     * @param file The CSV file to load players from
     * @throws IOException If there is an error reading the file
     */
    public void loadPlayersFromCsv(File file) throws IOException {
        logger.info("Loading players from CSV file: " + file.getAbsolutePath());

        List<Player> loadedPlayers = fileService.loadPlayersFromFile(file);
        gameView.logGameEvent("Loading " + loadedPlayers.size() + " players from " + file.getName());

        // Add each player to the game if they don't already exist
        int addedCount = 0;
        for (Player player : loadedPlayers) {
            if (boardGame.getPlayers().stream().noneMatch(p -> p.getName().equals(player.getName()))) {
                boardGame.addPlayer(player);
                gameView.addPlayer(player);
                addedCount++;
            }
        }

        gameView.logGameEvent("Added " + addedCount + " new players to the game.");
    }

    /**
     * Saves players to a CSV file using FileService.
     *
     * @param file The CSV file to save players to
     * @throws IOException If there is an error writing to the file
     */
    public void savePlayersToCsv(File file) throws IOException {
        logger.info("Saving players to CSV file: " + file.getAbsolutePath());

        fileService.savePlayersToFile(file, boardGame.getPlayers());
        gameView.logGameEvent("Saved " + boardGame.getPlayers().size() + " players to " + file.getName());
    }

    /**
     * Loads a board from a JSON file using FileService.
     *
     * @param file The JSON file to load the board from
     * @throws JsonParsingException If there is an error parsing the JSON file
     */
    public void loadBoardFromFile(File file) throws JsonParsingException {
        logger.info("Loading board from JSON file: " + file.getAbsolutePath());

        try {
            Board board = fileService.loadBoardFromFile(file);
            boardGame.setBoard(board);

            if (boardGame.getDice() == null) {
                boardGame.createDice(2, 6);
            }

            this.gameView = new GameView(board);
            boardGame.addObserver(gameView);

            setupDiceDisplay();

            gameView.setRollDiceAction(this::playTurn);

            gameView.logGameEvent("Loaded board from " + file.getName());
        } catch (JsonParsingException e) {
            logger.log(Level.SEVERE, "Error loading board from file", e);
            gameView.logGameEvent("Error loading board: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Saves the current board to a JSON file using FileService.
     *
     * @param file The JSON file to save the board to
     * @throws IOException If there is an error writing to the file
     * @throws JsonParsingException If there is an error formatting the JSON data
     * @throws TileActionNotFoundException If a tile action cannot be found
     */
    public void saveBoardToFile(File file) throws IOException, JsonParsingException, TileActionNotFoundException {
        logger.info("Saving board to JSON file: " + file.getAbsolutePath());

        try {
            fileService.saveBoardToFile(boardGame.getBoard(), file);
            gameView.logGameEvent("Saved board to " + file.getName());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error saving board to file", e);
            gameView.logGameEvent("Error saving board: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Saves the current board to a JSON file with a custom name and description using FileService.
     *
     * @param file The JSON file to save the board to
     * @param name The name of the board
     * @param description The description of the board
     * @throws IOException If there is an error writing to the file
     * @throws JsonParsingException If there is an error formatting the JSON data
     * @throws TileActionNotFoundException If a tile action cannot be found
     */
    public void saveBoardToFile(File file, String name, String description) throws IOException, JsonParsingException, TileActionNotFoundException {
        logger.info("Saving board to JSON file: " + file.getAbsolutePath());

        try {
            fileService.saveBoardToPath(boardGame.getBoard(), file.getAbsolutePath(), name, description);
            gameView.logGameEvent("Saved board to " + file.getName());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error saving board to file", e);
            gameView.logGameEvent("Error saving board: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Resets the game, clearing all players and setting game state to NOT_STARTED.
     */
    public void resetGame() {
        logger.info("Resetting game");

        boardGame.getPlayers().clear();
        boardGame.startGame();

        this.gameView = new GameView(boardGame.getBoard());
        boardGame.addObserver(gameView);
        setupDiceDisplay();
        gameView.setRollDiceAction(this::playTurn);

        gameView.logGameEvent("Game has been reset. Add players to start a new game.");
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