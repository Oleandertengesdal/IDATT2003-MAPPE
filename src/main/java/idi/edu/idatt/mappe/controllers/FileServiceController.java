package idi.edu.idatt.mappe.controllers;

import idi.edu.idatt.mappe.exceptions.JsonParsingException;
import idi.edu.idatt.mappe.exceptions.TileActionNotFoundException;
import idi.edu.idatt.mappe.models.Board;
import idi.edu.idatt.mappe.models.Player;
import idi.edu.idatt.mappe.utils.file.reader.BoardFileReader;
import idi.edu.idatt.mappe.utils.file.reader.BoardFileReaderGson;
import idi.edu.idatt.mappe.utils.file.reader.PlayerFileReader;
import idi.edu.idatt.mappe.utils.file.reader.PlayerFileReaderCVS;
import idi.edu.idatt.mappe.utils.file.writer.BoardFileWriter;
import idi.edu.idatt.mappe.utils.file.writer.BoardFileWriterGson;
import idi.edu.idatt.mappe.utils.file.writer.PlayerFileWriter;
import idi.edu.idatt.mappe.utils.file.writer.PlayerFileWriterCVS;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

/**
 * Implementation of the FileService interface for all file operations.
 * This class delegates to specific reader and writer implementations.
 */
public class FileServiceController implements FileService {
    private static final Logger logger = Logger.getLogger(FileServiceController.class.getName());

    // File readers and writers
    private final PlayerFileReader playerFileReader;
    private final PlayerFileWriter playerFileWriter;
    private final BoardFileReader boardFileReader;
    private final BoardFileWriter boardFileWriter;

    /**
     * Constructor for FileServiceController.
     * Initializes the file readers and writers.
     */
    public FileServiceController() {
        this.playerFileReader = new PlayerFileReaderCVS();
        this.playerFileWriter = new PlayerFileWriterCVS();
        this.boardFileReader = new BoardFileReaderGson();
        this.boardFileWriter = new BoardFileWriterGson();
        logger.info("FileServiceController initialized with default readers and writers");
    }

    /**
     * Constructor for FileServiceController with custom reader and writer implementations.
     *
     * @param playerFileReader The player file reader to use
     * @param playerFileWriter The player file writer to use
     * @param boardFileReader The board file reader to use
     * @param boardFileWriter The board file writer to use
     */
    public FileServiceController(
            PlayerFileReader playerFileReader,
            PlayerFileWriter playerFileWriter,
            BoardFileReader boardFileReader,
            BoardFileWriter boardFileWriter) {
        this.playerFileReader = playerFileReader;
        this.playerFileWriter = playerFileWriter;
        this.boardFileReader = boardFileReader;
        this.boardFileWriter = boardFileWriter;
        logger.info("FileServiceController initialized with custom readers and writers");
    }


    /**
     * Loads players from a CSV file.
     *
     * @param file The file to load players from
     * @return List of players loaded from the file
     * @throws IOException If there is an error reading the file
     */
    @Override
    public List<Player> loadPlayersFromFile(File file) throws IOException {
        logger.info("Loading players from file: " + file.getAbsolutePath());
        return loadPlayersFromPath(file.getAbsolutePath());
    }

    /**
     * Loads players from a CSV file path.
     *
     * @param filePath The file path to load players from
     * @return List of players loaded from the file
     * @throws IOException If there is an error reading the file
     */
    @Override
    public List<Player> loadPlayersFromPath(String filePath) throws IOException {
        try {
            logger.info("Loading players from path: " + filePath);
            List<Player> players = playerFileReader.loadPlayers(filePath);
            logger.info("Loaded " + players.size() + " players");
            return players;
        } catch (IOException e) {
            logger.severe("Error loading players from file: " + filePath);
            throw e;
        }
    }

    /**
     * Saves players to a CSV file.
     *
     * @param file The file to save players to
     * @param players The list of players to save
     * @throws IOException If there is an error writing to the file
     */
    @Override
    public void savePlayersToFile(File file, List<Player> players) throws IOException {
        logger.info("Saving " + players.size() + " players to file: " + file.getAbsolutePath());
        savePlayersToPath(file.getAbsolutePath(), players);
    }

    /**
     * Saves players to a CSV file path.
     *
     * @param filePath The file path to save players to
     * @param players The list of players to save
     * @throws IOException If there is an error writing to the file
     */
    @Override
    public void savePlayersToPath(String filePath, List<Player> players) throws IOException {
        try {
            logger.info("Saving " + players.size() + " players to path: " + filePath);
            playerFileWriter.savePlayers(players, filePath);
            logger.info("Players saved successfully");
        } catch (IOException e) {
            logger.severe("Error saving players to file: " + filePath);
            throw e;
        }
    }

    /**
     * Gets the default directory for player files.
     *
     * @return The default directory for player files
     */
    @Override
    public File getDefaultPlayerDirectory() {
        File resourcesDir = new File("src/main/resources/players");
        if (!resourcesDir.exists()) {
            boolean created = resourcesDir.mkdirs();
            if (!created) {
                logger.warning("Failed to create default player directory");
            }
        }
        return resourcesDir;
    }


    /**
     * Gets the default directory for board files.
     *
     * @return The default directory for board files
     */
    @Override
    public File getDefaultBoardDirectory() {
        File resourcesDir = new File("src/main/resources/boards");
        if (!resourcesDir.exists()) {
            boolean created = resourcesDir.mkdirs();
            if (!created) {
                logger.warning("Failed to create default board directory");
            }
        }
        return resourcesDir;
    }

    /**
     * Loads a board from a JSON file.
     *
     * @param file The JSON file to load
     * @return The loaded Board object
     * @throws JsonParsingException If there's an error parsing the JSON
     */
    @Override
    public Board loadBoardFromFile(File file) throws JsonParsingException {
        logger.info("Loading board from file: " + file.getAbsolutePath());
        return loadBoardFromPath(file.getAbsolutePath());
    }

    /**
     * Loads a board from a JSON file path.
     *
     * @param filePath The file path to load the board from
     * @return The loaded Board object
     * @throws JsonParsingException If there's an error parsing the JSON
     */
    @Override
    public Board loadBoardFromPath(String filePath) throws JsonParsingException {
        try {
            logger.info("Loading board from path: " + filePath);
            Board board = boardFileReader.readBoard(filePath);
            logger.info("Board loaded successfully");
            return board;
        } catch (JsonParsingException e) {
            logger.severe("Error loading board from file: " + filePath);
            throw e;
        }
    }

    /**
     * Saves a board to a JSON file.
     *
     * @param board The board to save
     * @param file The file to save to
     * @throws IOException If there's an error writing to the file
     * @throws JsonParsingException If there's an error creating the JSON
     * @throws TileActionNotFoundException If a tile action cannot be found
     */
    @Override
    public void saveBoardToFile(Board board, File file) throws IOException, JsonParsingException, TileActionNotFoundException {
        logger.info("Saving board to file: " + file.getAbsolutePath());
        saveBoardToPath(board, file.getAbsolutePath());
    }

    /**
     * Saves a board to a JSON file path.
     *
     * @param board The board to save
     * @param filePath The file path to save to
     * @throws IOException If there's an error writing to the file
     * @throws JsonParsingException If there's an error creating the JSON
     * @throws TileActionNotFoundException If a tile action cannot be found
     */
    @Override
    public void saveBoardToPath(Board board, String filePath) throws IOException, JsonParsingException, TileActionNotFoundException {
        try {
            logger.info("Saving board to path: " + filePath);
            boardFileWriter.writeBoard(board, filePath);
            logger.info("Board saved successfully");
        } catch (IOException | JsonParsingException | TileActionNotFoundException e) {
            logger.severe("Error saving board to file: " + filePath);
            throw e;
        }
    }

    /**
     * Saves a board to a JSON file path with a custom name and description.
     *
     * @param board The board to save
     * @param filePath The file path to save to
     * @param name The name of the board
     * @param description The description of the board
     * @throws IOException If there's an error writing to the file
     * @throws JsonParsingException If there's an error creating the JSON
     * @throws TileActionNotFoundException If a tile action cannot be found
     */
    @Override
    public void saveBoardToPath(Board board, String filePath, String name, String description) throws IOException, TileActionNotFoundException {
        try {
            logger.info("Saving board to path: " + filePath + " with name: " + name);
            boardFileWriter.writeBoard(board, filePath, name, description);
            logger.info("Board saved successfully");
        } catch (IOException | TileActionNotFoundException e) {
            logger.severe( "Error saving board to file: " + filePath);
            throw e;
        }
    }
}