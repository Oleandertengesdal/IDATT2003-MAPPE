package idi.edu.idatt.mappe.files.reader;

import idi.edu.idatt.mappe.models.Player;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PlayerFileReader {
    public static List<Player> loadPlayersFromCSV(String filePath) throws IOException {
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