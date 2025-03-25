package idi.edu.idatt.mappe.factory;

import idi.edu.idatt.mappe.exceptions.JsonParsingException;
import idi.edu.idatt.mappe.exceptions.TileActionNotFoundException;
import idi.edu.idatt.mappe.files.writer.BoardFileWriter;
import idi.edu.idatt.mappe.files.writer.BoardFileWriterGson;
import idi.edu.idatt.mappe.models.Board;
import idi.edu.idatt.mappe.models.Player;
import idi.edu.idatt.mappe.models.Tile;
import idi.edu.idatt.mappe.models.tileaction.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Factory for creating different board game JSON files
 */
public class BoardGameFileFactory {
    private static final int BOARD_SIZE = 90;
    private static final int ROWS = 10;
    private static final int COLUMNS = 9;
    private static final Random random = new Random();

    /**
     * Main method to generate 6 different board game files
     */
    public static void main(String[] args) {
        BoardFileWriter writer = new BoardFileWriterGson();

        try {
            // Generate 6 different game boards
            writer.writeBoard(createClassicSnakesAndLadders(), "classic_board.json");
            writer.writeBoard(createSnakesOnlyBoard(), "snakes_only_board.json");
            writer.writeBoard(createLaddersOnlyBoard(), "ladders_only_board.json");
            writer.writeBoard(createRandomTeleportBoard(), "random_teleport_board.json");
            writer.writeBoard(createMixedActionBoard(), "mixed_action_board.json");
            writer.writeBoard(createChaosBoard(), "chaos_board.json");

            System.out.println("Successfully generated 6 board game JSON files.");
        } catch (IOException | TileActionNotFoundException | JsonParsingException e) {
            System.err.println("Error generating board game files: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Creates a classic Snakes and Ladders board
     */
    public static Board createClassicSnakesAndLadders() {
        Board board = new Board(ROWS, COLUMNS);

        // Create all tiles
        createStandardTiles(board);

        // Add ladders (upward movement)
        addTileAction(board, 4, 14, "Climb a small ladder", true);
        addTileAction(board, 9, 31, "Climb a tall ladder", true);
        addTileAction(board, 20, 38, "Climb a medium ladder", true);
        addTileAction(board, 28, 84, "Climb a very tall ladder", true);
        addTileAction(board, 40, 59, "Climb a rickety ladder", true);
        addTileAction(board, 51, 67, "Climb a sturdy ladder", true);
        addTileAction(board, 63, 81, "Climb a wooden ladder", true);

        // Add snakes (downward movement)
        addTileAction(board, 17, 7, "Slide down a small snake", false);
        addTileAction(board, 54, 34, "Slide down a green snake", false);
        addTileAction(board, 62, 19, "Slide down a cobra", false);
        addTileAction(board, 64, 60, "Slide down a garden snake", false);
        addTileAction(board, 87, 24, "Slide down a python", false);
        addTileAction(board, 93, 73, "Slide down a viper", false);
        addTileAction(board, 95, 75, "Slide down an anaconda", false);
        addTileAction(board, 99, 78, "Slide down a rattlesnake", false);

        return board;
    }

    /**
     * Creates a board with only snakes
     */
    public static Board createSnakesOnlyBoard() {
        Board board = new Board(ROWS, COLUMNS);

        // Create all tiles
        createStandardTiles(board);

        // Add many snakes (downward movement)
        addTileAction(board, 14, 4, "Slide down a small snake", false);
        addTileAction(board, 22, 11, "Slide down a green snake", false);
        addTileAction(board, 31, 16, "Slide down a cobra", false);
        addTileAction(board, 38, 9, "Slide down a garden snake", false);
        addTileAction(board, 48, 26, "Slide down a python", false);
        addTileAction(board, 56, 33, "Slide down a viper", false);
        addTileAction(board, 62, 42, "Slide down an anaconda", false);
        addTileAction(board, 74, 58, "Slide down a rattlesnake", false);
        addTileAction(board, 78, 69, "Slide down a tiny snake", false);
        addTileAction(board, 85, 61, "Slide down a huge snake", false);
        addTileAction(board, 88, 52, "Slide down a king cobra", false);

        return board;
    }

    /**
     * Creates a board with only ladders
     */
    public static Board createLaddersOnlyBoard() {
        Board board = new Board(ROWS, COLUMNS);

        // Create all tiles
        createStandardTiles(board);

        // Add many ladders (upward movement)
        addTileAction(board, 4, 23, "Climb a small ladder", true);
        addTileAction(board, 8, 34, "Climb a tall ladder", true);
        addTileAction(board, 18, 37, "Climb a medium ladder", true);
        addTileAction(board, 26, 51, "Climb a very tall ladder", true);
        addTileAction(board, 33, 49, "Climb a rickety ladder", true);
        addTileAction(board, 41, 63, "Climb a sturdy ladder", true);
        addTileAction(board, 54, 72, "Climb a wooden ladder", true);
        addTileAction(board, 67, 86, "Climb a steel ladder", true);
        addTileAction(board, 77, 89, "Climb a rope ladder", true);

        return board;
    }

    /**
     * Creates a board with random teleport actions
     */
    public static Board createRandomTeleportBoard() {
        Board board = new Board(ROWS, COLUMNS);

        // Create all tiles
        createStandardTiles(board);

        // Add random teleport tiles
        for (int i = 10; i < BOARD_SIZE; i += 10) {
            Tile tile = board.getTileByIndex(i);
            RandomTeleportTileAction action = new RandomTeleportTileAction(board, "Teleport to a random location!");
            tile.setLandAction(action);
        }

        // Add a few snakes and ladders too
        addTileAction(board, 15, 35, "Climb an emergency ladder", true);
        addTileAction(board, 65, 45, "Slide down a small snake", false);

        return board;
    }

    /**
     * Creates a board with a mix of actions
     */
    public static Board createMixedActionBoard() {
        Board board = new Board(ROWS, COLUMNS);

        // Create all tiles
        createStandardTiles(board);

        // Add ladders
        addTileAction(board, 5, 25, "Climb a small ladder", true);
        addTileAction(board, 30, 50, "Climb a medium ladder", true);
        addTileAction(board, 55, 75, "Climb a tall ladder", true);

        // Add snakes
        addTileAction(board, 40, 20, "Slide down a small snake", false);
        addTileAction(board, 70, 45, "Slide down a big snake", false);
        addTileAction(board, 85, 65, "Slide down a huge snake", false);

        // Add random teleport
        Tile tile1 = board.getTileByIndex(33);
        RandomTeleportTileAction action1 = new RandomTeleportTileAction(board, "Teleport to a random location!");
        tile1.setLandAction(action1);

        Tile tile2 = board.getTileByIndex(66);
        RandomTeleportTileAction action2 = new RandomTeleportTileAction(board, "Magic portal teleport!");
        tile2.setLandAction(action2);

        // Creating mock players for swap tile actions - in a real implementation, these would be passed from the game
        List<Player> mockPlayers = new ArrayList<>();
        mockPlayers.add(new Player("Player1"));
        mockPlayers.add(new Player("Player2"));

        // Add swap tile actions
        Tile tile3 = board.getTileByIndex(42);
        SwapTileAction action3 = new SwapTileAction(mockPlayers, "Swap positions with a random player!");
        tile3.setLandAction(action3);

        Tile tile4 = board.getTileByIndex(78);
        SwapTileAction action4 = new SwapTileAction(mockPlayers, "Cosmic swap! Trade places with someone else.");
        tile4.setLandAction(action4);

        return board;
    }

    /**
     * Creates a chaos board with new custom tile actions
     */
    public static Board createChaosBoard() {
        Board board = new Board(ROWS, COLUMNS);

        // Create all tiles
        createStandardTiles(board);

        // Creating mock players for swap tile actions
        List<Player> mockPlayers = new ArrayList<>();
        mockPlayers.add(new Player("Player1"));
        mockPlayers.add(new Player("Player2"));
        mockPlayers.add(new Player("Player3"));

        // Add a mix of all available actions
        // Ladders
        addTileAction(board, 3, 21, "Rocket ladder! Zoom up!", true);
        addTileAction(board, 24, 58, "Elevator going up!", true);

        // Snakes
        addTileAction(board, 50, 12, "Trap door! Fall down!", false);
        addTileAction(board, 71, 29, "Black hole! Get sucked in!", false);

        // Random teleports
        for (int i = 15; i < 80; i += 20) {
            Tile tile = board.getTileByIndex(i);
            RandomTeleportTileAction action = new RandomTeleportTileAction(board, "Quantum teleport! Who knows where you'll end up?");
            tile.setLandAction(action);
        }

        // Swap tile actions
        for (int i = 10; i < 85; i += 25) {
            Tile tile = board.getTileByIndex(i);
            SwapTileAction action = new SwapTileAction(mockPlayers, "Body swap! Trade places with someone else!");
            tile.setLandAction(action);
        }

        // Add bonus tile actions (these are placeholders as they would need implementation)
        Tile bonusTile1 = board.getTileByIndex(33);
        bonusTile1.setLandAction(new FreeTurnTileAction("Free turn! Roll again!"));

        Tile bonusTile2 = board.getTileByIndex(67);
        bonusTile2.setLandAction(new DoubleMoveActionTile("Double move! Move twice the dice value!"));

        Tile bonusTile3 = board.getTileByIndex(44);
        bonusTile3.setLandAction(new ReverseDirectionTileAction("Reverse! Start moving backwards!"));

        return board;
    }

    /**
     * Helper method to create standard tiles for a board
     */
    private static void createStandardTiles(Board board) {
        for (int i = 1; i <= BOARD_SIZE; i++) {
            // Calculate X and Y coordinates (assuming a snake-like pattern)
            int row = (i - 1) / COLUMNS;
            int col;

            // Implement snake-like pattern
            if (row % 2 == 0) {
                // Even rows go left to right
                col = (i - 1) % COLUMNS;
            } else {
                // Odd rows go right to left
                col = COLUMNS - 1 - ((i - 1) % COLUMNS);
            }

            Tile tile = new Tile(i, col, row);
            board.addTile(i, tile);

            // Connect tiles
            if (i > 1) {
                Tile prevTile = board.getTileByIndex(i - 1);
                prevTile.setNextTile(tile);
            }
        }
    }

    /**
     * Helper method to add tile actions
     */
    private static void addTileAction(Board board, int startTileId, int endTileId, String description, boolean isLadder) {
        Tile startTile = board.getTileByIndex(startTileId);
        if (startTile != null) {
            TileAction action;
            if (isLadder) {
                action = new LadderTileAction(endTileId, description, board);
            } else {
                action = new SnakeTileAction(endTileId, description, board);
            }
            startTile.setLandAction(action);
        }
    }

    /**
     * Custom tile action: Free turn
     */
    private static class FreeTurnTileAction implements TileAction {
        private String description;

        public FreeTurnTileAction(String description) {
            this.description = description;
        }

        @Override
        public void perform(Player player) {
            // In a real implementation, this would signal the game logic to grant another turn
            System.out.println(player.getName() + " gets a free turn!");
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * Custom tile action: Double Move
     */
    private static class DoubleMoveActionTile implements TileAction {
        private String description;

        public DoubleMoveActionTile(String description) {
            this.description = description;
        }

        @Override
        public void perform(Player player) {
            // In a real implementation, this would signal the game to move the player again
            System.out.println(player.getName() + " gets to move twice the dice value!");
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * Custom tile action: Reverse Direction
     */
    private static class ReverseDirectionTileAction implements TileAction {
        private String description;

        public ReverseDirectionTileAction(String description) {
            this.description = description;
        }

        @Override
        public void perform(Player player) {
            // In a real implementation, this would signal the game to reverse movement direction
            System.out.println(player.getName() + " now moves backwards!");
        }

        public String getDescription() {
            return description;
        }
    }
}