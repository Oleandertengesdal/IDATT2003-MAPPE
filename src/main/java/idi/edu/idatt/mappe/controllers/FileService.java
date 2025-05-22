package idi.edu.idatt.mappe.controllers;

import idi.edu.idatt.mappe.exceptions.JsonParsingException;
import idi.edu.idatt.mappe.exceptions.TileActionNotFoundException;
import idi.edu.idatt.mappe.models.Board;
import idi.edu.idatt.mappe.models.GameRules;
import idi.edu.idatt.mappe.models.Player;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Service for handling file operations related to players, boards, and game rules.
 * This interface provides methods for loading and saving players, boards, and game rules
 * from/to files in various formats (CSV, JSON).
 *
 * @see Player, Board, GameRules
 * @version 1.0
 */
public interface FileService {

    /**
     * Loads players from a CSV file.
     *
     * @param file The file to load players from
     * @return List of players loaded from the file
     * @throws IOException If an error occurs during file reading
     */
    List<Player> loadPlayersFromFile(File file) throws IOException;

    /**
     * Saves players to a CSV file.
     *
     * @param file The file to save players to
     * @param players The list of players to save
     * @throws IOException If an error occurs during file writing
     */
    void savePlayersToFile(File file, List<Player> players) throws IOException;

    /**
     * Gets the default directory for player files.
     *
     * @return The default directory for player files
     */
    File getDefaultPlayerDirectory();


    /**
     * Loads the Board From Json file.
     *
     * @param file The file to load the board from
     * @return The loaded board
     */
    Board loadBoardFromFile(File file) throws JsonParsingException;

    /**
     * Saves the board to a JSON file.
     *
     * @param board The board to save
     * @param file The file to save the board to
     * @throws IOException If an error occurs during file writing
     */
    void saveBoardToFile(Board board, File file) throws IOException, JsonParsingException, TileActionNotFoundException;

    /**
     * Gets the default directory for board files.
     *
     * @return The default directory for board files
     */
    File getDefaultBoardDirectory();

    /**
     * Loads players from a specified file path.
     *
     * @param filePath The path to the file where the players are saved
     * @return List of players loaded from the file
     * @throws IOException If an error occurs during file reading
     */
    List<Player> loadPlayersFromPath(String filePath) throws IOException;

    /**
     * Saves players to a specified file path.
     *
     * @param filePath The path to the file where the players will be saved
     * @param players The list of players to save
     * @throws IOException If an error occurs during file writing
     */
    void savePlayersToPath(String filePath, List<Player> players) throws IOException;

    /**
     * Loads the Board from a specified file path.
     *
     * @param filePath The path to the file where the board is saved
     * @return The loaded board
     * @throws JsonParsingException If an error occurs during JSON parsing
     */
    Board loadBoardFromPath(String filePath) throws JsonParsingException;

    /**
     * Saves the board to a specified file path.
     *
     * @param board The board to save
     * @param filePath The path to the file where the board will be saved
     * @throws IOException If an error occurs during file writing
     */
    void saveBoardToPath(Board board, String filePath) throws IOException, JsonParsingException, TileActionNotFoundException;

    /**
     * Saves the board to a specified file path with additional metadata.
     *
     * @param board The board to save
     * @param filePath The path to the file where the board will be saved
     * @param name The name of the board
     * @param description A description of the board
     * @throws IOException If an error occurs during file writing
     */
    void saveBoardToPath(Board board, String filePath, String name, String description) throws IOException, JsonParsingException, TileActionNotFoundException;

    /**
     * Loads game rules from a JSON file.
     *
     * @param file The file to load game rules from
     * @return The loaded game rules
     * @throws JsonParsingException If an error occurs during JSON parsing
     */
    GameRules loadGameRulesFromFile(File file) throws JsonParsingException;

    /**
     * Loads game rules from a specified file path.
     *
     * @param filePath The path to the file where the game rules are saved
     * @return The loaded game rules
     * @throws JsonParsingException If an error occurs during JSON parsing
     */
    GameRules loadGameRulesFromPath(String filePath) throws JsonParsingException;

    /**
     * Saves game rules to a JSON file.
     *
     * @param rules The game rules to save
     * @param file The file to save the game rules to
     * @throws IOException If an error occurs during file writing
     * @throws JsonParsingException If an error occurs during JSON creation
     */
    void saveGameRulesToFile(GameRules rules, File file) throws IOException, JsonParsingException;

    /**
     * Saves game rules to a specified file path.
     *
     * @param rules The game rules to save
     * @param filePath The path to the file where the game rules will be saved
     * @throws IOException If an error occurs during file writing
     * @throws JsonParsingException If an error occurs during JSON creation
     */
    void saveGameRulesToPath(GameRules rules, String filePath) throws IOException, JsonParsingException;

    /**
     * Saves game rules to a specified file path with a custom name.
     *
     * @param rules The game rules to save
     * @param filePath The path to the file where the game rules will be saved
     * @param name The name to use for the rules
     * @throws IOException If an error occurs during file writing
     * @throws JsonParsingException If an error occurs during JSON creation
     */
    void saveGameRulesToPath(GameRules rules, String filePath, String name) throws IOException, JsonParsingException;

    /**
     * Gets the default directory for game rules files.
     *
     * @return The default directory for game rules files
     */
    File getDefaultRulesDirectory();
}