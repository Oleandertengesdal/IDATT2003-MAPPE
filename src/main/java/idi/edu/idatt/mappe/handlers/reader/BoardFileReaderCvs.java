package idi.edu.idatt.mappe.handlers.reader;

import idi.edu.idatt.mappe.exceptions.JsonParsingException;
import idi.edu.idatt.mappe.models.Board;
import idi.edu.idatt.mappe.models.Tile;
import idi.edu.idatt.mappe.models.tileaction.TileAction;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BoardFileReaderCsv implements BoardFileReader {

    @Override
    public Board readBoard(String filePath) throws IOException {
        List<Tile> tiles = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line = reader.readLine(); // Ignorer header
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                int id = Integer.parseInt(parts[0]);
                int nextTile = Integer.parseInt(parts[1]);
                TileAction action = null;
                if (!parts[2].isEmpty()) {
                    String type = parts[2];
                    int destinationTileId = Integer.parseInt(parts[3]);
                    String description = parts[4];
                    action = new TileAction(type, destinationTileId, description);
                }
                tiles.add(new Tile(id, nextTile, action));
            }
        }
        return new Board("CSV Board", "Brett lest fra CSV-fil", tiles);
    }

}