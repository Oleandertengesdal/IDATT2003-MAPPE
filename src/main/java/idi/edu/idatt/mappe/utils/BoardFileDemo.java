package idi.edu.idatt.mappe.utils;

import idi.edu.idatt.mappe.exceptions.JsonParsingException;
import idi.edu.idatt.mappe.exceptions.TileActionNotFoundException;
import idi.edu.idatt.mappe.files.reader.BoardFileReader;
import idi.edu.idatt.mappe.files.reader.BoardFileReaderGson;
import idi.edu.idatt.mappe.files.writer.BoardFileWriter;
import idi.edu.idatt.mappe.files.writer.BoardFileWriterGson;
import idi.edu.idatt.mappe.models.Board;
import idi.edu.idatt.mappe.models.Tile;
import idi.edu.idatt.mappe.models.tileaction.LadderTileAction;
import idi.edu.idatt.mappe.models.tileaction.SnakeTileAction;

import java.io.IOException;

/**
 * Demo class for board file operations
 */
public class BoardFileDemo {
    public static void main(String[] args) {
        try {
            Board board = new Board(90);

            LadderTileAction ladderAction = new LadderTileAction(8, "Ladder from 3 to 8", board);
            board.getTileByIndex(3).setLandAction(ladderAction);

            SnakeTileAction snakeAction = new SnakeTileAction(2, "Snake from 7 to 2", board);
            board.getTileByIndex(7).setLandAction(snakeAction);

            BoardFileWriter writer = new BoardFileWriterGson();
            writer.writeBoard(board, "boards/game_board.json");

            BoardFileReader reader = new BoardFileReaderGson();
            Board loadedBoard = reader.readBoard("boards/game_board.json");

            Tile tile3 = loadedBoard.getTileByIndex(3);
            if (tile3.getLandAction() != null && tile3.getLandAction() instanceof LadderTileAction) {
                LadderTileAction action = (LadderTileAction) tile3.getLandAction();
            }

            Tile tile7 = loadedBoard.getTileByIndex(7);
            if (tile7.getLandAction() != null && tile7.getLandAction() instanceof SnakeTileAction) {
                SnakeTileAction action = (SnakeTileAction) tile7.getLandAction();
            }

        } catch (IOException | JsonParsingException | TileActionNotFoundException e) {
            e.printStackTrace();
        }
    }
}