package idi.edu.idatt.mappe.files.writer;

import idi.edu.idatt.mappe.exceptions.JsonParsingException;
import idi.edu.idatt.mappe.exceptions.TileActionNotFoundException;
import idi.edu.idatt.mappe.models.Board;

import java.io.IOException;

public interface BoardFileWriter {
    public void writeBoard(Board board, String filePath) throws JsonParsingException, IOException, TileActionNotFoundException;
}