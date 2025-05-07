package idi.edu.idatt.mappe.controllers;

import idi.edu.idatt.mappe.models.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Controller class for managing players in the game.
 * Handles loading, saving, adding, updating, and removing players.
 * Uses FileService for file operations.
 */
public class PlayerController {

    private final List<Player> players = new ArrayList<>();
    private final FileService fileService;
    private final static String DEFAULT_PLAYER_FILENAME = "players.csv";

    private final Logger logger = Logger.getLogger(PlayerController.class.getName());

    /**
     * Constructor for PlayerController with provided FileService.
     *
     * @param fileService The file service to use for loading/saving players
     */
    public PlayerController(FileService fileService) {
        this.fileService = fileService;
        loadDefaultPlayers();
    }

    /**
     * Attempts to load players from the default player file.
     */
    private void loadDefaultPlayers() {
        try {
            File defaultFile = new File(fileService.getDefaultPlayerDirectory(), DEFAULT_PLAYER_FILENAME);
            if (defaultFile.exists()) {
                List<Player> loadedPlayers = fileService.loadPlayersFromFile(defaultFile);
                players.addAll(loadedPlayers);
                logger.info("Loaded " + loadedPlayers.size() + " players from default file");
            }
        } catch (IOException e) {
            logger.warning("Could not load default players: " + e.getMessage());
        }
    }

    /**
     * Gets all players.
     *
     * @return List of all players
     */
    public List<Player> getPlayers() {
        return new ArrayList<>(players);
    }

    /**
     * Adds a player to the collection.
     *
     * @param player The player to add
     * @return True if the player was added successfully
     */
    public boolean addPlayer(Player player) {
        if (player == null) {
            logger.warning("Attempted to add null player");
            return false;
        }

        // Check if player already exists
        if (players.stream().anyMatch(p ->
                p.getName().equalsIgnoreCase(player.getName()))) {
            logger.warning("Player with name " + player.getName() + " already exists");
            return false;
        }

        players.add(player);
        logger.info("Added player: " + player.getName());
        return true;
    }

    /**
     * Gets a player by name.
     *
     * @param name The name of the player to get
     * @return The player with the given name, or null if not found
     */
    public Player getPlayerByName(String name) {
        return players.stream()
                .filter(p -> p.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    /**
     * Removes a player from the collection.
     *
     * @param player The player to remove
     * @return True if the player was removed successfully
     */
    public boolean removePlayer(Player player) {
        boolean removed = players.remove(player);
        if (removed) {
            logger.info("Removed player: " + player.getName());
        } else {
            logger.warning("Failed to remove player: " + player.getName() + " (player not found)");
        }
        return removed;
    }

    /**
     * Removes a player from the collection by name.
     *
     * @param playerName The name of the player to remove
     * @return True if the player was removed successfully
     */
    public boolean removePlayer(String playerName) {
        boolean removed = players.removeIf(player -> player.getName().equalsIgnoreCase(playerName));
        if (removed) {
            logger.info("Player removed: " + playerName);
            return true;
        }
        logger.warning("Player not found: " + playerName);
        return false;
    }

    /**
     * Clears all players from the collection.
     */
    public void clearPlayers() {
        players.clear();
        logger.info("Cleared all players");
    }

    /**
     * Loads players from a file using the FileService.
     *
     * @param file The file to load players from
     * @throws IOException If an error occurs during file reading
     */
    public void loadPlayersFromFile(File file) throws IOException {
        logger.info("Loading players from file: " + file.getAbsolutePath());

        List<Player> loadedPlayers = fileService.loadPlayersFromFile(file);

        // Update our collection
        players.clear();
        players.addAll(loadedPlayers);

        logger.info("Loaded " + loadedPlayers.size() + " players from file");
    }

    /**
     * Saves players to the default file.
     *
     * @throws IOException If an error occurs during file writing
     */
    public void savePlayersToDefaultFile() throws IOException {
        File defaultFile = new File(fileService.getDefaultPlayerDirectory(), DEFAULT_PLAYER_FILENAME);
        savePlayersToFile(defaultFile);
    }

    /**
     * Saves players to a file using the FileService.
     *
     * @param file The file to save players to
     * @throws IOException If an error occurs during file writing
     */
    public void savePlayersToFile(File file) throws IOException {
        logger.info("Saving " + players.size() + " players to file: " + file.getAbsolutePath());

        fileService.savePlayersToFile(file, new ArrayList<>(players));

        logger.info("Players saved successfully");
    }
}