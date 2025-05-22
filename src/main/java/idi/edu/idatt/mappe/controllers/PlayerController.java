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

        if (players.stream().anyMatch(p ->
                p.getName().equalsIgnoreCase(player.getName()))) {
            logger.warning("Player with name " + player.getName() + " already exists");
            return false;
        }

        players.add(player);
        logger.info("Added player: " + player.getName());
        return true;
    }
}