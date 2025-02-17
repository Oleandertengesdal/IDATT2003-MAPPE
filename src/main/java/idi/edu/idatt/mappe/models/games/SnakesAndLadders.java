package idi.edu.idatt.mappe.models.games;

import idi.edu.idatt.mappe.models.Board;
import idi.edu.idatt.mappe.models.BoardGame;
import idi.edu.idatt.mappe.models.Player;
import idi.edu.idatt.mappe.models.Tile;
import idi.edu.idatt.mappe.models.dice.Dice;
import idi.edu.idatt.mappe.models.tileaction.LadderTileAction;
import idi.edu.idatt.mappe.models.tileaction.SnakeTileAction;
import idi.edu.idatt.mappe.models.tileaction.TileAction;

import java.text.MessageFormat;
import java.util.Random;
import java.util.logging.Logger;

public class SnakesAndLadders extends BoardGame {

    private final Dice dice;
    private final Random random;

    private static final int BOARD_SIZE = 90;
    private static final int NUMBER_OF_DICE = 2;

    private static Logger logger = Logger.getLogger(SnakesAndLadders.class.getName());
    /**
     * Creates a new snakes and ladders game
     */
    public SnakesAndLadders() {
        super();
        createBoard(BOARD_SIZE); // Standard board size
        createDice(NUMBER_OF_DICE); // Two dice
        setupBoard();
        dice = getDice();
        random = new Random();
        logger.info("Created a new Snakes and Ladders game");
    }

    /**
     * Sets up the board with snakes and ladders
     */
    private void setupBoard() {
        int numberOfSnakes = 10;
        int numberOfLadders = 10;

       logger.info("Setting up the board with snakes and ladders");
        for (int i = 0; i < numberOfSnakes; i++) {
            int start = random.nextInt(80) + 10; // Ensure snakes start within the board range
            int end = random.nextInt(start - 1) + 1; // Ensure end is before start
            setSnake(start, end);
        }

        for (int i = 0; i < numberOfLadders; i++) {
            int start = random.nextInt(80) + 1; // Ensure ladders start within the board range
            int end = random.nextInt(90 - start) + start + 1; // Ensure end is after start
            setLadder(start, end);
        }
    }

    /**
     * Sets a snake on the board
     *
     * @param start The start of the snake
     * @param end The end of the snake
     */
    private void setSnake(int start, int end) {
        Tile startTile = getBoard().getTile(start - 1);
        Tile endTile = getBoard().getTile(end - 1);
        startTile.setLandAction(new SnakeTileAction(
                endTile.getIndex(),
                "Snake from " + startTile.getIndex() + " to " + endTile.getIndex(), getBoard()));
    }

    /**
     * Sets a ladder on the board
     *
     * @param start The start of the ladder
     * @param end The end of the ladder
     */
    private void setLadder(int start, int end) {
        Tile startTile = getBoard().getTile(start - 1);
        Tile endTile = getBoard().getTile(end - 1);
        startTile.setLandAction(new LadderTileAction(
                endTile.getIndex(),
                "Ladder from " + startTile.getIndex() + " to " + endTile.getIndex(), getBoard()
        ));
    }


    /**
     * Plays a round of the game
     */
    public void playRound() {
        for (Player player : getPlayers()) {
            int steps = dice.roll();
            movePlayer(player, steps);
            player.getCurrentTile().landPlayer(player);
            System.out.println("Player " + player.getName() + " on tile " + player.getCurrentTile().getIndex());
        }
    }

    /**
     * Moves a player a given number of steps
     */
    public void addPlayerBoard() {
        for (Player player : getPlayers()) {
            player.setGame(this);
            player.placeOnTile(getBoard().getTile(0));
        }
    }
}