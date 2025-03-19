package idi.edu.idatt.mappe.handlers.writer;

import idi.edu.idatt.mappe.models.Board;
import idi.edu.idatt.mappe.models.Tile;

import java.io.FileWriter;
import java.io.IOException;

public class BoardFileWriterCsv implements BoardFileWriter {

    @Override
    public void writeBoard(Board board, String filePath) throws IOException {
        try (FileWriter file = new FileWriter(filePath)) {
            file.write("id,nextTile,actionType,destinationTileId,actionDescription\n");
            for (Tile tile : board.getTiles()) {
                file.write(tile.getIndex() + ",");
                file.write(tile.getNextTile() + ",");
                if (tile.getLandAction() != null) {
                    file.write(tile.getLandAction().getType() + ",");
                    file.write(tile.getLandAction().getDestinationTileId() + ",");
                    file.write(tile.getLandAction().getDescription() + "\n");
                } else {
                    file.write(",,,\n");
                }
            }
        }
    }
}