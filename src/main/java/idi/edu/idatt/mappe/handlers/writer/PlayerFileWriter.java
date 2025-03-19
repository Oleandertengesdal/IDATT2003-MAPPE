package idi.edu.idatt.mappe.handlers.writer;

import idi.edu.idatt.mappe.models.Player;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class PlayerFileWriter {
    public static void savePlayersToCSV(List<Player> players, String filePath) throws IOException {
        try (FileWriter writer = new FileWriter(filePath)) {
            for (Player player : players) {
                writer.write(player.getName() + "," + player.getToken() + "\n");
            }
        } catch (IOException e) {
            throw new IOException("Could not write to file");
        }
    }
}