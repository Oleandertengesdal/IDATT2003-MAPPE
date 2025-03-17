/*package idi.edu.idatt.mappe.models.games;

import idi.edu.idatt.mappe.models.BoardGame;
import idi.edu.idatt.mappe.models.Player;
import idi.edu.idatt.mappe.models.Tile;
import idi.edu.idatt.mappe.models.dice.Dice;
import idi.edu.idatt.mappe.models.tileaction.LadderTileAction;
import idi.edu.idatt.mappe.models.tileaction.SnakeTileAction;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

public class SnakesAndLadders extends BoardGame {

    private final Dice dice;
    private final Random random;

    private static final int BOARD_SIZE = 90;
    private static final int NUMBER_OF_DICE = 2;
    private static final int NUMBER_OF_COLLUMS = 10;
    private static List<int[]> actionTilePositions;

    private static Logger logger = Logger.getLogger(SnakesAndLadders.class.getName());
    /**
     * Creates a new snakes and ladders game
     *//*
    public SnakesAndLadders() {
        super();
        createBoard(BOARD_SIZE); // Standard board size
        createDice(NUMBER_OF_DICE); // Two dice

        dice = getDice();
        random = new Random();
        actionTilePositions = new ArrayList<>();

        setupBoard();
        logger.info("Created a new Snakes and Ladders game");
    }

    /**
     * Sets up the board with snakes and ladders
     *//*
    private void setupBoard() {
        int numberOfSnakes = 6;
        int numberOfLadders = 6;

        logger.info("Setting up the board with snakes and ladders");

        List<int[]> snakePositions = generatePositions(numberOfSnakes, true);
        List<int[]> ladderPositions = generatePositions(numberOfLadders, false);

        for (int[] pos : snakePositions) {
            setSnake(pos[0], pos[1]);
        }

        for (int[] pos : ladderPositions) {
            setLadder(pos[0], pos[1]);
        }
    }

    /**
     * Generates positions for snakes and ladders
     *
     * @param count The number of positions to generate
     * @param isSnake Whether the positions are for snakes or ladders
     * @return The generated positions
     *//*
    private List<int[]> generatePositions(int count, boolean isSnake) {
        int maxRows = BOARD_SIZE / NUMBER_OF_COLLUMS;
        int minLength = 2;
        int maxLength = isSnake ? 6 : 8;

        while (actionTilePositions.size() < count) {
            int start = generateStartPosition(maxRows);
            int end = generateEndPosition(start, minLength, maxLength, isSnake);

            try {
                if (isValidPosition(start, end, actionTilePositions)) {
                    actionTilePositions.add(new int[]{start, end});
                }
            } catch (Exception e) {
                logger.warning("Failed to generate a position: " + e.getMessage() + "at start: " + start + " end: " + end + " -  try again");
            }
        }

        return actionTilePositions;
    }

    /**
     * Generates a random start position
     *
     * @param maxRows The maximum number of rows
     * @return The generated start position
     *//*
    private int generateStartPosition(int maxRows) {
        int startRow = random.nextInt(maxRows) + 1;
        int startCol = random.nextInt(NUMBER_OF_COLLUMS);
        return startRow * NUMBER_OF_COLLUMS + startCol;
    }

    /**
     * Generates a random end position
     *
     * @param start The start position
     * @param minLength The minimum length of the snake or ladder
     * @param maxLength The maximum length of the snake or ladder
     * @param isSnake Whether the position is for a snake or ladder
     * @return The generated end position
     *//*
    private int generateEndPosition(int start, int minLength, int maxLength, boolean isSnake) {
        int length;

        // 70% chance of choosing a small length, 30% chance of a large one
        if (random.nextDouble() < 0.7) {
            length = (random.nextInt((maxLength / 2) - minLength + 1) + minLength) * NUMBER_OF_COLLUMS
                    + random.nextInt(NUMBER_OF_COLLUMS) - NUMBER_OF_COLLUMS;  // Adds a random offset
        } else {
            length = (random.nextInt(maxLength - (maxLength / 2) + 1) + (maxLength / 2)) * NUMBER_OF_COLLUMS
                    + random.nextInt(NUMBER_OF_COLLUMS) - NUMBER_OF_COLLUMS;  // Adds a random offset
        }

        int endPosition = isSnake ? start - length : start + length;

        // Ensure the end position stays within bounds
        if (isSnake) {
            // A snake must not go below position 1
            endPosition = Math.max(1, endPosition);
        } else {
            // A ladder must not go above position 90
            endPosition = Math.min(BOARD_SIZE-1, endPosition);
        }

        return endPosition;
    }

    /**
     * Checks if a position is valid
     *
     * @param start The start position
     * @param end The end position
     * @param positions The existing positions
     * @return Whether the position is valid
     *//*
    private boolean isValidPosition(int start, int end, List<int[]> positions) {
        if (end < 1 || end > BOARD_SIZE || start == end) return false;

        for (int[] pos : positions) {
            if (pos[0] / 10 == start / 10 || pos[1] / 10 == end / 10 || pos[0] == start || pos[1] == end) {
                return false;
            }
        }

        return getBoard().getTile(start).getLandAction() == null && getBoard().getTile(end).getLandAction() == null;
    }

    /**
     * Sets a snake on the board
     *
     * @param start The start of the snake
     * @param end The end of the snake
     *//*
    private void setSnake(int start, int end) {
        Tile startTile = getBoard().getTile(start);
        Tile endTile = getBoard().getTile(end);
        startTile.setLandAction(new SnakeTileAction(
                endTile.getIndex(),
                "Snake from " + startTile.getIndex() + " to " + endTile.getIndex(), getBoard()
        ));
        logger.info("Set a snake from " + startTile.getIndex() + " to " + endTile.getIndex());
    }

    /**
     * Sets a ladder on the board
     *
     * @param start The start of the ladder
     * @param end The end of the ladder
     *//*
    private void setLadder(int start, int end) {
        Tile startTile = getBoard().getTile(start);
        Tile endTile = getBoard().getTile(end);
        startTile.setLandAction(new LadderTileAction(
                endTile.getIndex(),
                "Ladder from " + startTile.getIndex() + " to " + endTile.getIndex(), getBoard()
        ));
        logger.info("Set a ladder from " + startTile.getIndex() + " to " + endTile.getIndex());
    }


    /**
     * Plays a round of the game
     *//*
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
     *//*

    @Override
    public void addPlayerBoard() {
        Tile startTile = new Tile(0);
        for (Player player : getPlayers()) {
            player.setGame(this);
            player.placeOnTile(startTile);
        }
        startTile.setNextTile(getBoard().getTile(1));
    }
}
*/