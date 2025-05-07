package idi.edu.idatt.mappe.utils.file.reader;

import idi.edu.idatt.mappe.models.Player;

import java.io.IOException;
import java.util.List;

/**
 * PlayerFileReader is an interface for reading player data from a file.
 * It provides a method to load a list of players from a specified file path.
 */
public interface PlayerFileReader {
    public List<Player> loadPlayers(String filePath) throws IOException;
}