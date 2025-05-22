package idi.edu.idatt.mappe.utils.file.reader;

import idi.edu.idatt.mappe.models.Player;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Class for reading player data from a CSV file.
 * <p>
 *     This class implements the PlayerFileReader interface and provides
 *     functionality to load player data from a CSV file.
 * </p>
 *
 * @see PlayerFileReader
 * @version 1.0
 */
public class PlayerFileReaderCVS implements PlayerFileReader {

    /**
     * Loads a list of players from a file in CSV format.
     *
     * @param filePath The path to the file from which the players will be loaded.
     * @return A list of players.
     * @throws IOException If an I/O error occurs.
     */
    @Override
    public List<Player> loadPlayers(String filePath) throws IOException {
        List<Player> players = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    players.add(new Player(parts[0], parts[1]));
                }
            }
        } catch (IOException e) {
            throw new IOException("Could not read file");
        }
        return players;
    }

}
