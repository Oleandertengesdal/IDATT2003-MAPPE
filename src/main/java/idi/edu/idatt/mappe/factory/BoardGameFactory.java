package idi.edu.idatt.mappe.factory;

import idi.edu.idatt.mappe.exceptions.JsonParsingException;
import idi.edu.idatt.mappe.files.reader.BoardFileReaderGson;
import idi.edu.idatt.mappe.models.Board;
import idi.edu.idatt.mappe.models.BoardGame;
import idi.edu.idatt.mappe.models.Player;
import idi.edu.idatt.mappe.models.tileaction.LadderTileAction;
import idi.edu.idatt.mappe.models.tileaction.SnakeTileAction;

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
        BoardGame game = new BoardGame();

        game.createBoard(90);
        Board board = game.getBoard();

        board.getTileByIndex(1).setLandAction(new LadderTileAction(38, "Ladder from 1 to 38", board));
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
        board.getTileByIndex(64).setLandAction(new SnakeTileAction(60, "Snake from 64 to 60", board));
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
    public static BoardGame createSimpleGame() {
        BoardGame game = new BoardGame();

        game.createBoard(50);
        Board board = game.getBoard();

        board.getTileByIndex(3).setLandAction(new LadderTileAction(17, "Ladder from 3 to 17", board));
        board.getTileByIndex(8).setLandAction(new LadderTileAction(31, "Ladder from 8 to 31", board));
        board.getTileByIndex(21).setLandAction(new LadderTileAction(42, "Ladder from 21 to 42", board));
        board.getTileByIndex(28).setLandAction(new LadderTileAction(45, "Ladder from 28 to 45", board));

        board.getTileByIndex(14).setLandAction(new SnakeTileAction(4, "Snake from 14 to 4", board));
        board.getTileByIndex(34).setLandAction(new SnakeTileAction(20, "Snake from 34 to 20", board));
        board.getTileByIndex(38).setLandAction(new SnakeTileAction(9, "Snake from 38 to 9", board));
        board.getTileByIndex(48).setLandAction(new SnakeTileAction(26, "Snake from 48 to 26", board));

        game.createDice(1, 6);

        return game;
    }

    /**
     * Creates a mini board game with 30 tiles
     *
     * @return A mini board game
     */
    public static BoardGame createMiniGame() {
        BoardGame game = new BoardGame();

        game.createBoard(30);
        Board board = game.getBoard();

        board.getTileByIndex(2).setLandAction(new LadderTileAction(12, "Ladder from 2 to 12", board));
        board.getTileByIndex(5).setLandAction(new LadderTileAction(15, "Ladder from 5 to 15", board));
        board.getTileByIndex(19).setLandAction(new LadderTileAction(28, "Ladder from 19 to 28", board));

        board.getTileByIndex(17).setLandAction(new SnakeTileAction(7, "Snake from 17 to 7", board));
        board.getTileByIndex(24).setLandAction(new SnakeTileAction(16, "Snake from 24 to 16", board));

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
        BoardGame game = new BoardGame();

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
}