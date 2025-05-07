package idi.edu.idatt.mappe.utils.factory;

import idi.edu.idatt.mappe.exceptions.InvalidGameTypeException;
import idi.edu.idatt.mappe.exceptions.JsonParsingException;
import idi.edu.idatt.mappe.models.Tile;
import idi.edu.idatt.mappe.utils.file.reader.BoardFileReaderGson;
import idi.edu.idatt.mappe.models.Board;
import idi.edu.idatt.mappe.models.BoardGame;
import idi.edu.idatt.mappe.models.Player;
import idi.edu.idatt.mappe.models.enums.GameType;
import idi.edu.idatt.mappe.models.tileaction.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Factory class for creating different variants of board games
 */
public class BoardGameFactory {

    private static final Logger logger = Logger.getLogger(BoardGameFactory.class.getName());
    private static final String BOARD_FILES_DIRECTORY = "boards";

    /**
     * Creates a classic board game with 100 tiles
     *
     * @return A classic board game
     */
    public static BoardGame createClassicGame() {
        BoardGame game = new BoardGame(GameType.SNAKES_AND_LADDERS);

        Board board = new Board(9, 10, GameType.SNAKES_AND_LADDERS);
        game.setBoard(board);

        board.getTileByIndex(2).setLandAction(new LadderTileAction(38, "Ladder from 2 to 38", board));
        board.getTileByIndex(4).setLandAction(new LadderTileAction(14, "Ladder from 4 to 14", board));
        board.getTileByIndex(9).setLandAction(new LadderTileAction(31, "Ladder from 9 to 31", board));
        board.getTileByIndex(21).setLandAction(new LadderTileAction(42, "Ladder from 21 to 42", board));
        board.getTileByIndex(28).setLandAction(new LadderTileAction(84, "Ladder from 28 to 84", board));
        board.getTileByIndex(36).setLandAction(new LadderTileAction(44, "Ladder from 36 to 44", board));
        board.getTileByIndex(51).setLandAction(new LadderTileAction(67, "Ladder from 51 to 67", board));
        board.getTileByIndex(71).setLandAction(new LadderTileAction(84, "Ladder from 71 to 84", board));


        board.getTileByIndex(16).setLandAction(new SnakeTileAction(6, "Snake from 16 to 6", board));
        board.getTileByIndex(47).setLandAction(new SnakeTileAction(26, "Snake from 47 to 26", board));
        board.getTileByIndex(49).setLandAction(new SnakeTileAction(11, "Snake from 49 to 11", board));
        board.getTileByIndex(56).setLandAction(new SnakeTileAction(53, "Snake from 56 to 53", board));
        board.getTileByIndex(62).setLandAction(new SnakeTileAction(19, "Snake from 62 to 19", board));
        board.getTileByIndex(65).setLandAction(new SnakeTileAction(60, "Snake from 65 to 60", board));
        board.getTileByIndex(77).setLandAction(new SnakeTileAction(24, "Snake from 77 to 24", board));
        board.getTileByIndex(83).setLandAction(new SnakeTileAction(72, "Snake from 83 to 72", board));
        board.getTileByIndex(85).setLandAction(new SnakeTileAction(64, "Snake from 85 to 64", board));

        game.createDice(2, 6);

        return game;
    }

    /**
     * Creates a simple board game with 50 tiles
     *
     * @return A simple board game
     */
    public static BoardGame createGameOneDice() {
        BoardGame game = new BoardGame(GameType.SNAKES_AND_LADDERS);

        game.createBoard(90);
        Board board = game.getBoard();

        board.getTileByIndex(3).setLandAction(new LadderTileAction(17, "Ladder from 3 to 17", board));
        board.getTileByIndex(8).setLandAction(new LadderTileAction(31, "Ladder from 8 to 31", board));
        board.getTileByIndex(21).setLandAction(new LadderTileAction(42, "Ladder from 21 to 42", board));
        board.getTileByIndex(28).setLandAction(new LadderTileAction(45, "Ladder from 28 to 45", board));
        board.getTileByIndex(36).setLandAction(new LadderTileAction(48, "Ladder from 36 to 48", board));

        board.getTileByIndex(41).setLandAction(new RandomTeleportTileAction(board, "Random teleport from 41"));
        board.getTileByIndex(46).setLandAction(new RandomTeleportTileAction(board, "Random teleport from 46"));

        board.getTileByIndex(14).setLandAction(new SnakeTileAction(4, "Snake from 14 to 4", board));
        board.getTileByIndex(34).setLandAction(new SnakeTileAction(20, "Snake from 34 to 20", board));
        board.getTileByIndex(38).setLandAction(new SnakeTileAction(9, "Snake from 38 to 9", board));
        board.getTileByIndex(48).setLandAction(new SnakeTileAction(26, "Snake from 48 to 26", board));
        board.getTileByIndex(49).setLandAction(new SnakeTileAction(11, "Snake from 49 to 11", board));

        game.createDice(1, 6);

        return game;
    }

    /**
     * Creates a mini board game with 30 tiles
     *
     * @return A mini board game
     */
    public static BoardGame createSimpleGameOneDice() {
        BoardGame game = new BoardGame(GameType.SNAKES_AND_LADDERS);

        game.createBoard(90);
        Board board = game.getBoard();

        board.getTileByIndex(2).setLandAction(new LadderTileAction(12, "Ladder from 2 to 12", board));
        board.getTileByIndex(5).setLandAction(new LadderTileAction(15, "Ladder from 5 to 15", board));
        board.getTileByIndex(19).setLandAction(new LadderTileAction(28, "Ladder from 19 to 28", board));
        board.getTileByIndex(22).setLandAction(new LadderTileAction(57, "Ladder from 22 to 57", board));
        board.getTileByIndex(25).setLandAction(new LadderTileAction(78, "Ladder from 25 to 78", board));

        board.getTileByIndex(17).setLandAction(new SnakeTileAction(7, "Snake from 17 to 7", board));
        board.getTileByIndex(24).setLandAction(new SnakeTileAction(16, "Snake from 24 to 16", board));
        board.getTileByIndex(27).setLandAction(new SnakeTileAction(9, "Snake from 27 to 9", board));
        board.getTileByIndex(85).setLandAction(new SnakeTileAction(22, "Snake from 85 to 22", board));
        board.getTileByIndex(88).setLandAction(new SnakeTileAction(51, "Snake from 88 to 51", board));

        game.createDice(1, 6);

        return game;
    }

    /**
     * Lists available board game files from the boards directory
     *
     * @return List of available board game file names
     */
    public static List<String> listAvailableBoardFiles() {
        List<String> availableBoards = new ArrayList<>();

        File boardsDir = new File(BOARD_FILES_DIRECTORY);
        if (!boardsDir.exists()) {
            boardsDir.mkdir();
            logger.info("Created boards directory");
            return availableBoards;
        }

        try (Stream<Path> paths = Files.walk(Paths.get(BOARD_FILES_DIRECTORY))) {
            availableBoards = paths
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".json"))
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            logger.log(Level.WARNING, "Failed to list board files", e);
        }

        return availableBoards;
    }

    /**
     * Loads a board game from a file
     *
     * @param fileName The name of the file to load from
     * @return A board game loaded from the file
     * @throws IOException If an I/O error occurs
     */
    public static BoardGame loadGameFromFile(String fileName) throws JsonParsingException {
        BoardGame game = new BoardGame(GameType.SNAKES_AND_LADDERS);

        String filePath = BOARD_FILES_DIRECTORY + File.separator + fileName;

        BoardFileReaderGson reader = new BoardFileReaderGson();
        Board board = reader.readBoard(filePath);

        game.setBoard(board);

        game.createDice(2, 6);

        return game;
    }

    /**
     * Sets up the game with players
     *
     * @param game The game to set up
     * @param playerNames The names of the players
     * @return The set-up game
     */
    public static BoardGame setupGame(BoardGame game, String... playerNames) {
        for (String playerName : playerNames) {
            Player player = new Player(playerName);
            game.addPlayer(player);
        }

        if (!game.getPlayers().isEmpty()) {
            game.setCurrentPlayer(game.getPlayers().get(0));
        }

        return game;
    }

    public static BoardGame createGame(String gameType) throws InvalidGameTypeException {
        switch (gameType) {
            case "SNAKES_LADDERS":
                return createClassicGame();
            case "Simple":
                return createSimpleGameOneDice();
            case "OneDice":
                return createGameOneDice();
            default:
                throw new InvalidGameTypeException("Invalid game type: " + gameType);
        }
    }

    /**
     * Creates a simple Ludo game
     *
     * @return A simple Ludo game
     */
    public static BoardGame createSimpleLudoGame() {
        BoardGame game = new BoardGame(GameType.LUDO);
        game.setGameType(GameType.LUDO);

        Board board = new Board(15, 15, GameType.LUDO);
        game.setBoard(board);


        createStartingArea(board, 1, 1, 4, 4, "Green Starting Area");  // Top-left (Green)
        createStartingArea(board, 1, 10, 4, 13, "Blue Starting Area"); // Top-right (Blue)
        createStartingArea(board, 10, 1, 13, 4, "Yellow Starting Area"); // Bottom-left (Yellow)
        createStartingArea(board, 10, 10, 13, 13, "Red Starting Area"); // Bottom-right (Red)

        createMainPath(board);
        createSafeZones(board);
        createHomePaths(board);

        // Create the center win tile
        Tile centerTile = board.getTileByCoordinates(7, 7);
        centerTile.setLandAction(new WinTileAction("Win the game!", board));

        // Add capture actions to regular tiles
        for (Tile tile : board.getTiles().values()) {
            if (tile != null && !tile.isSafeZone() &&
                    !(tile.getLandAction() instanceof StartingAreaTileAction) &&
                    !(tile.getLandAction() instanceof WinTileAction)) {
                tile.setLandAction(new CaptureTileAction("Capture opponent pieces", board));
            }
        }

        // Create the dice (Ludo uses 1 six-sided die)
        game.createDice(1, 6);

        return game;
    }

    /**
     * Creates a starting area for a player
     *
     * @param board The board to create the starting area on
     * @param startRow the starting row
     * @param startCol the starting column
     * @param endRow the ending row
     * @param endCol the ending column
     * @param description the description of the starting area
     */
    private static void createStartingArea(Board board, int startRow, int startCol, int endRow, int endCol, String description) {
        for (int row = startRow; row <= endRow; row++) {
            for (int col = startCol; col <= endCol; col++) {
                Tile tile = board.getTileByCoordinates(row, col);
                if (tile != null) {
                    tile.setLandAction(new StartingAreaTileAction(description, board));
                }
            }
        }
    }

    /**
     * Creates the main path for the Ludo game
     *
     * @param board The board to create the main path on
     */
    /**
     * Creates the main path for the Ludo game
     *
     * @param board The board to create the main path on
     */
    private static void createMainPath(Board board) {
        Tile previousTile = null;
        Tile firstTile = null;

        // Top row (left to right)
        for (int col = 0; col <= 14; col++) {
            if (col == 7) continue; // Skip the center column
            Tile tile = board.getTileByCoordinates(0, col);
            if (tile != null) {
                if (previousTile != null) {
                    previousTile.setNextTile(tile);
                } else {
                    firstTile = tile; // Save the first tile for linking later
                }
                previousTile = tile;
            }
        }

        // Right column (top to bottom)
        for (int row = 1; row <= 14; row++) {
            if (row == 7) continue; // Skip the center row
            Tile tile = board.getTileByCoordinates(row, 14);
            if (tile != null) {
                previousTile.setNextTile(tile);
                previousTile = tile;
            }
        }

        // Bottom row (right to left)
        for (int col = 13; col >= 0; col--) {
            if (col == 7) continue; // Skip the center column
            Tile tile = board.getTileByCoordinates(14, col);
            if (tile != null) {
                previousTile.setNextTile(tile);
                previousTile = tile;
            }
        }

        // Left column (bottom to top)
        for (int row = 13; row >= 0; row--) {
            if (row == 7) continue; // Skip the center row
            Tile tile = board.getTileByCoordinates(row, 0);
            if (tile != null) {
                previousTile.setNextTile(tile);
                previousTile = tile;
            }
        }

        // Link the last tile back to the first tile to complete the loop
        if (previousTile != null && firstTile != null) {
            previousTile.setNextTile(firstTile);
        }
    }

    /**
     * Creates safe zones on the board
     *
     * @param board The board to create the safe zones on
     */
    private static void createSafeZones(Board board) {
        // Example safe zone locations
        board.getTileByCoordinates(2, 6).setSafeZone(true);
        board.getTileByCoordinates(6, 12).setSafeZone(true);
        board.getTileByCoordinates(8, 12).setSafeZone(true);
        board.getTileByCoordinates(12, 8).setSafeZone(true);
        board.getTileByCoordinates(12, 6).setSafeZone(true);
        board.getTileByCoordinates(8, 2).setSafeZone(true);
    }

    /**
     * Creates home paths for each player
     *
     * @param board The board to create the home paths on
     */
    private static void createHomePaths(Board board) {
        // Green home path (from top-left)
        for (int row = 1; row <= 6; row++) {
            Tile tile = board.getTileByCoordinates(row, 7);
            if (tile != null) {
                tile.setSafeZone(true);
            }
        }

        // Blue home path (from top-right)
        for (int col = 13; col >= 8; col--) {
            Tile tile = board.getTileByCoordinates(7, col);
            if (tile != null) {
                tile.setSafeZone(true);
            }
        }

        // Yellow home path (from bottom-left)
        for (int col = 1; col <= 6; col++) {
            Tile tile = board.getTileByCoordinates(7, col);
            if (tile != null) {
                tile.setSafeZone(true);
            }
        }

        // Red home path (from bottom-right)
        for (int row = 13; row >= 8; row--) {
            Tile tile = board.getTileByCoordinates(row, 7);
            if (tile != null) {
                tile.setSafeZone(true);
            }
        }
    }
}