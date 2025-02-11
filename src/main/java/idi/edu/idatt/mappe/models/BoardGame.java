package idi.edu.idatt.mappe.models;

import idi.edu.idatt.mappe.models.dice.Dice;

import java.util.ArrayList;
import java.util.List;

public class BoardGame {
    private Board board;
    private Player currentPlayer;
    private List<Player> players;
    private Dice dice;

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
    }


    /**
     *  Plays the game
     */
    public void play() {
        int steps = dice.roll();
        movePlayer(currentPlayer, steps);
    }

    /**
     * Gets the winner of the game
     */
    public void getWinner() {
        for (Player player : players) {
            if (player.getCurrentTile().getIndex() == board.getTiles().size() - 1) {
                System.out.println(player.getName() + " is the winner!");
            }
        }
    }


    public void startGame() {
        for (Player player : players) {
            player.placeOnTile(board.getTile(0));
        }
    }

    public void movePlayer(Player player, int steps) {
        Tile currentTile = player.getCurrentTile();
        int currentIndex = 0;
        for (int i = 0; i < board.getTiles().size(); i++) {
            if (board.getTile(i).equals(currentTile)) {
                currentIndex = i;
                break;
            }
        }
        int newIndex = currentIndex + steps;
        if (newIndex >= board.getTiles().size()) {
            newIndex = board.getTiles().size() - 1;
        }
        player.placeOnTile(board.getTile(newIndex));
    }
}
