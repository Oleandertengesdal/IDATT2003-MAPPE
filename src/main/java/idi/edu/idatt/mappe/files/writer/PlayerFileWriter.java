package idi.edu.idatt.mappe.files.writer;

import idi.edu.idatt.mappe.models.Player;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * PlayerFileWriter is an interface for writing player data to a file.
 * It provides a method to save a list of players to a specified file path.
 */
public interface PlayerFileWriter {
    public void savePlayers(List<Player> players, String filePath) throws IOException;
}