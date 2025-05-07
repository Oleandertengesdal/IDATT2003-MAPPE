package idi.edu.idatt.mappe.utils.file.writer;

import com.google.gson.JsonParseException;
import idi.edu.idatt.mappe.exceptions.JsonParsingException;
import idi.edu.idatt.mappe.exceptions.TileActionNotFoundException;
import idi.edu.idatt.mappe.models.Board;

import java.io.IOException;

public interface BoardFileWriter {
    public void writeBoard(Board board, String filePath) throws JsonParsingException, IOException, TileActionNotFoundException;

    public void writeBoard(Board board, String fileName, String name, String description) throws JsonParseException, IOException, TileActionNotFoundException;
}