package idi.edu.idatt.mappe.models;

import idi.edu.idatt.mappe.models.dice.Dice;

import java.util.ArrayList;
import java.util.List;

public class BoardGame {
    private Board board;
    private Player currentPlayer;
    private List<Player> players;
    private Dice dice;
    private GameState gameState = GameState.NOT_STARTED;
    private boolean finished = false;

    //List to store observers
    private List<BoardGameObserver> observers = new ArrayList<>();


    /**
     * Creates a new board game with no players
     */
    public BoardGame() {
        players = new ArrayList<>();
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
     *  Creates a new board with the given size
     *
     * @param size The size of the board
     */
    public void createBoard(int size) {
        board = new Board(size);
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
     *  Plays the game
     */
    public void play() {
        int steps = dice.roll();
        movePlayer(currentPlayer, steps);
    }

    /**
     * Plays one round of the game
     */
    public void playOneRound() {
        for (Player player : players) {
            int steps = dice.roll();
            movePlayer(player, steps);

            if (player.getCurrentTile().getIndex() == board.getTiles().size()) {
                finished = true;
                break;
            }
        }
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
        for (Player player : players) {
            player.placeOnTile(board.getTileByIndex(1));
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

        // Notify observers about the move
        notifyPlayerMoved(player, steps);

        // Check for game completion
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
    private void notifyGameWinner(Player winner) {
        for (BoardGameObserver observer : observers) {
            observer.onGameWinner(winner);
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
}
