package idi.edu.idatt.mappe.utils.file.writer;

import idi.edu.idatt.mappe.models.Player;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import static java.util.logging.Logger.getLogger;

/**
 * PlayerFileWriterCVS is a class that implements the PlayerFileWriter interface.
 * It provides functionality to save player data to a CSV file.
 * <p>
 *     This class is responsible for writing player data to a file in CSV format.
 * </p>
 *
 * @see PlayerFileWriter
 * @version 1.0
 */
public class PlayerFileWriterCVS implements PlayerFileWriter {

    private final Logger logger = getLogger(PlayerFileWriterCVS.class.getName());

    /**
     * Writes a list of players to a file in CSV format.
     *
     * @param players The list of players to write.
     * @param filePath The path to the file where the players will be saved.
     * @throws IOException If an I/O error occurs.
     */
    @Override
    public void savePlayers(List<Player> players, String filePath) throws IOException {
        try (FileWriter writer = new FileWriter(filePath)) {
            for (Player player : players) {
                writer.write(player.getName() + "," + player.getToken() + "\n");
            }
        } catch (IOException e) {
            logger.warning("Could not write to file: " + e.getMessage());
            throw new IOException("Could not write to file");
        }
    }
}
