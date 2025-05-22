package idi.edu.idatt.mappe.models;

import idi.edu.idatt.mappe.models.dice.Dice;
import idi.edu.idatt.mappe.models.enums.Direction;
import idi.edu.idatt.mappe.models.enums.GameState;
import idi.edu.idatt.mappe.models.enums.GameType;
import idi.edu.idatt.mappe.models.enums.TokenType;
import idi.edu.idatt.mappe.views.GameView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.IntStream;

public class BoardGame {
    private Board board;
    private Player currentPlayer;
    private List<Player> players;
    private Dice dice;
    private GameRules gameRules;

    private GameState gameState = GameState.NOT_STARTED;
    private boolean finished = false;
    private GameType gameType;

    private List<BoardGameObserver> observers = new ArrayList<>();

    private static final Logger logger = Logger.getLogger(BoardGame.class.getName());

    /**
     * Creates a new board game with no players
     */
    public BoardGame(GameType gameType) {
        players = new ArrayList<>();
        this.gameType = gameType;
    }

    /**
     * Returns the board of the game
     *
     * @return The board of the game
     */
    public Board getBoard() {
        return board;
    }

    /**
     * Returns the current player
     *
     * @return The current player
     */
    public List<Player> getPlayers() {
        return players;
    }

    /**
     * Returns the dice of the game
     *
     * @return The dice of the game
     */
    public Dice getDice() {
        return dice;
    }

    /**
     * Sets the current player
     *
     * @param currentPlayer The current player
     */
    public void setCurrentPlayer(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    /**
     * Returns the current player
     *
     * @return The current player
     */
    public Player getCurrentPlayer() {
        return currentPlayer;
    }


    /**
     * Sets the board of the game
     *
     * @param board The board of the game
     */
    public void setBoard(Board board) {
        this.board = board;
    }

    /**
     * Creates a new board with the given size
     *
     * @param size The size of the board
     */
    public void createBoard(int size) {

        board = new Board(size, gameType);
    }

    /**
     * Creates Dice with the given number of dice
     *
     * @param numberOfDice The tiles of the board
     */
    public void createDice(int numberOfDice) {
        dice = new Dice(numberOfDice);
    }

    /**
     * Creates Dice with the given number of dice and sides
     *
     * @param numberOfDice The number of dice
     * @param numberOfSides The number of sides on the dice
     */
    public void createDice(int numberOfDice, int numberOfSides) {
        dice = new Dice(numberOfDice, numberOfSides);
    }

    /**
     * Adds a player to the game
     *
     * @param player A player to add to the game
     */
    public void addPlayer(Player player) {
        players.add(player);
        player.setGame(this);
        player.placeOnTile(board.getTileByIndex(1));
    }

    /**
     * Plays the game
     */
    public void play() {
        int steps = dice.roll();

        // Check for extra turn rule
        if (gameRules != null && gameRules.isExtraThrowOnMax() && steps == gameRules.getMaxRoll()) {
            notifyExtraTurn(currentPlayer);
        }

        movePlayer(currentPlayer, steps);
    }

    /**
     * Gets the winner of the game
     */
    public Player getWinner() {
        for (Player player : players) {
            if (player.getCurrentTile().getIndex() == board.getTiles().size()) {
                return player;
            }
        }
        return null;
    }

    /**
     * Starts the game
     */
    public void startGame() {
        switch (gameType) {
            case SNAKES_AND_LADDERS:
                players.forEach(player -> player.setCurrentTile(board.getTileByIndex(1)));
                break;
            case THE_LOST_DIAMOND:
                // Players can choose between Cairo (tile 1) and Tangier (tile 2)
                // For simplicity, alternate between the two starting cities
                for (int i = 0; i < players.size(); i++) {
                    Player player = players.get(i);
                    if (i % 2 == 0) {
                        player.setCurrentTile(board.getTileByIndex(1)); // Cairo
                        logger.info(player.getName() + " starts in Cairo");
                    } else {
                        player.setCurrentTile(board.getTileByIndex(2)); // Tangier
                        logger.info(player.getName() + " starts in Tangier");
                    }
                }
                break;
            default:
                players.forEach(player -> player.setCurrentTile(board.getTileByIndex(1)));
                break;
        }
        notifyGameStateChanged(GameState.STARTED);
    }

    /**
     * Moves a player a given number of steps
     *
     * @param player The player to move
     * @param steps The number of steps to move
     */
    public void movePlayer(Player player, int steps) {
        int currentIndex = player.getCurrentTile().getIndex();
        int newIndex = currentIndex + steps;
        if (newIndex > board.getTiles().size()) {
            newIndex = board.getTiles().size();
        }
        player.placeOnTile(board.getTileByIndex(newIndex));

        notifyPlayerMoved(player, steps);

        // Apply special rules if applicable
        if (gameRules != null) {
            Tile currentTile = player.getCurrentTile();

            // Check for ladder action with extra turn rule
            if (gameRules.isExtraTurnOnLadder() &&
                    currentTile.getLandAction() != null &&
                    currentTile.getLandAction().getClass().getSimpleName().contains("LadderTileAction")) {
                notifyExtraTurn(player);
            }

            // Check for snake action with skip turn rule
            if (gameRules.isSkipTurnOnSnake() &&
                    currentTile.getLandAction() != null &&
                    currentTile.getLandAction().getClass().getSimpleName().contains("SnakeTileAction")) {
                notifySkipTurn(player);
            }
        }

        // Check if game is finished
        if (player.getCurrentTile().getIndex() == board.getTiles().size()) {
            finished = true;
            Player winner = getWinner();
            if (winner != null) {
                notifyGameWinner(winner);
                notifyGameStateChanged(GameState.FINISHED);
            }
        }
    }

    /**
     * Returns whether the game is finished
     *
     * @return Whether the game is finished
     */
    public boolean isFinished() {
        return finished;
    }

    /**
     * Register an observer to receive game updates
     * @param observer The observer to register
     */
    public void addObserver(BoardGameObserver observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    /**
     * Remove an observer from the game
     * @param observer The observer to remove
     */
    public void removeObserver(BoardGameObserver observer) {
        observers.remove(observer);
    }

    /**
     * Notify all observers about a player's move
     * @param player The player who moved
     * @param steps The number of steps moved
     */
    private void notifyPlayerMoved(Player player, int steps) {
        for (BoardGameObserver observer : observers) {
            observer.onPlayerMoved(player, steps);
        }
    }

    /**
     * Notify all observers about game state changes
     * @param newState The new game state
     */
    private void notifyGameStateChanged(GameState newState) {
        gameState = newState;
        for (BoardGameObserver observer : observers) {
            observer.onGameStateChanged(newState);
        }
    }

    /**
     * Notify all observers about the game winner
     * @param winner The winning player
     */
    public void notifyGameWinner(Player winner) {
        for (BoardGameObserver observer : observers) {
            observer.onGameWinner(winner);
        }
    }

    /**
     * Notify all observers that a player gets an extra turn
     * @param player The player who gets an extra turn
     */
    private void notifyExtraTurn(Player player) {
        for (BoardGameObserver observer : observers) {
            observer.onPlayerExtraTurn(player);
        }
    }

    /**
     * Notify all observers that a player must skip their next turn
     * @param player The player who must skip their turn
     */
    private void notifySkipTurn(Player player) {
        for (BoardGameObserver observer : observers) {
            observer.onPlayerSkipTurn(player);
        }
    }

    /**
     * Returns the current game state
     *
     * @return The current game state
     */
    public GameState getGameState() {
        return gameState;
    }

    /**
     * Sets the game state
     *
     * @param gameState The game state to set
     */
    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    /**
     * Returns the game type
     *
     * @return The game type
     */
    public GameType getGameType() {
        return gameType;
    }

    /**
     * Sets the game type
     *
     * @param gameType The game type to set
     */
    public void setGameType(GameType gameType) {
        this.gameType = gameType;
    }

    /**
     * Gets the game rules configuration
     *
     * @return The current game rules
     */
    public GameRules getGameRules() {
        return gameRules;
    }

    /**
     * Sets the game rules configuration
     *
     * @param gameRules The rules to apply to this game
     */
    public void setGameRules(GameRules gameRules) {
        this.gameRules = gameRules;
    }


    /**
     * Notify observers of a player winning
     * @param player The player who won the game
     */
    public void notifyObserversOfWinner(Player player) {
        for (BoardGameObserver observer : observers) {
            observer.onGameWinner(player);
        }
    }

    /**
     * Notify observers of a capture event
     * @param player The player who captured
     * @param otherPlayer The player who was captured
     */
    public void notifyObserversOfCapture(Player player, Player otherPlayer) {
        for (BoardGameObserver observer : observers) {
            observer.onPlayerCaptured(player, otherPlayer);
        }
    }

    /**
     * Notify observers of a player getting an extra tur
     *
     * @param currentPlayer The player who gets an extra turn
     */
    public void notifyObserversOfExtraTurn(Player currentPlayer) {
        for (BoardGameObserver observer : observers) {
            observer.onPlayerExtraTurn(currentPlayer);
        }
    }

    public void notifyObserversOfSwap(Player player, Player otherPlayer, int playerTileIndex, int otherPlayerTileIndex) {
        for (BoardGameObserver observer : observers) {
            if (observer instanceof GameView) {
                ((GameView) observer).logGameEvent(
                        player.getName() + " swapped positions with " +
                                otherPlayer.getName() + " (from " + playerTileIndex +
                                " to " + otherPlayerTileIndex + ")!"
                );
            }
        }
    }

    /**
     * Processes a player's move to a specific destination in The Lost Diamond
     *
     * @param player The player making the move
     * @param destinationTile The destination tile
     * @return True if the move was successful
     */
    public boolean processLostDiamondMove(Player player, Tile destinationTile) {
        if (gameType != GameType.THE_LOST_DIAMOND) {
            throw new IllegalStateException("This method is only for The Lost Diamond game");
        }

        Tile currentTile = player.getCurrentTile();

        if (!currentTile.getConnections().containsValue(destinationTile)) {
            return false;
        }

        Direction direction = null;
        for (Map.Entry<Direction, Tile> entry : currentTile.getConnections().entrySet()) {
            if (entry.getValue().equals(destinationTile)) {
                direction = entry.getKey();
                break;
            }
        }

        int travelCost = currentTile.getTravelCost(direction);

        if (player.getMoney() < travelCost) {
            return false;
        }

        if (travelCost > 0) {
            player.spendMoney(travelCost);
        }

        player.placeOnTile(destinationTile);

        notifyPlayerMoved(player, 0);

        checkLostDiamondWinCondition(player);

        return true;
    }

    /**
     * Processes a token reveal action
     *
     * @param player The player revealing the token
     * @return The revealed token, or null if the action couldn't be performed
     */
    public TokenType processTokenReveal(Player player) {
        if (gameType != GameType.THE_LOST_DIAMOND) {
            throw new IllegalStateException("This method is only for The Lost Diamond game");
        }

        Tile currentTile = player.getCurrentTile();

        if (!currentTile.isCity() || !currentTile.hasToken()) {
            return null;
        }

        TokenType revealedToken = player.revealToken();

        if (revealedToken != null) {
            notifyTokenRevealed(player, revealedToken);

            if (revealedToken == TokenType.DIAMOND) {
                notifyDiamondFound(player);
            }
        }

        return revealedToken;
    }

    /**
     * Checks win condition for The Lost Diamond game
     *
     * @param player The player to check
     */
    private void checkLostDiamondWinCondition(Player player) {
        if (player.hasDiamond() && player.getCurrentTile().isStartingCity()) {
            finished = true;
            notifyGameWinner(player);
            notifyGameStateChanged(GameState.FINISHED);
        }
    }

    /**
     * Notify observers that a token was revealed
     *
     * @param player The player who revealed the token
     * @param tokenType The type of token revealed
     */
    private void notifyTokenRevealed(Player player, TokenType tokenType) {
        for (BoardGameObserver observer : observers) {
            if (observer instanceof LostDiamondObserver) {
                ((LostDiamondObserver) observer).onTokenRevealed(player, tokenType);
            }
        }
    }

    /**
     * Notify observers that the diamond was found
     *
     * @param player The player who found the diamond
     */
    private void notifyDiamondFound(Player player) {
        for (BoardGameObserver observer : observers) {
            if (observer instanceof LostDiamondObserver) {
                ((LostDiamondObserver) observer).onDiamondFound(player);
            }
        }
    }
}