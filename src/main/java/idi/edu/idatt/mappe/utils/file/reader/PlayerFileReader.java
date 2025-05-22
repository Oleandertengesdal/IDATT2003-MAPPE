package idi.edu.idatt.mappe.utils.file.reader;

import idi.edu.idatt.mappe.models.Player;

import java.io.IOException;
import java.util.List;

/**
 * Interface for reading player data from a file
 * <p>
 *     This interface defines methods for reading player data from a file
 *     The players data are read form a CVS file.
 * </p>
 *
 * @see Player
 * @version 1.0
 */
public interface PlayerFileReader {

    /**
     * Loads a list of players from a file
     *
     * @param filePath The path to the file from which the players will be loaded
     * @return A list of players
     * @throws IOException If an I/O error occurs
     */
    public List<Player> loadPlayers(String filePath) throws IOException;
}