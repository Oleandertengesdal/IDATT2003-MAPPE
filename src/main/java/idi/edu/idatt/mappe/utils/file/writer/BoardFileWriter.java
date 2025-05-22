package idi.edu.idatt.mappe.utils.file.writer;

import com.google.gson.JsonParseException;
import idi.edu.idatt.mappe.exceptions.JsonParsingException;
import idi.edu.idatt.mappe.exceptions.TileActionNotFoundException;
import idi.edu.idatt.mappe.models.Board;

import java.io.IOException;

/**
 * Interface for writing a board to a file
 * <p>
 *     This interface defines methods for writing a game board to a file.
 *     The board is serialized to JSON and written to the specified file.
 * </p>
 *
 * @see Board
 * @version 1.0
 */
public interface BoardFileWriter {
    /**
     * Writes a board to a JSON file
     *
     * @param board The board to write to the file
     * @param filePath The path to the file to write to
     * @throws JsonParsingException If a JSON parsing error occurs
     * @throws IOException If an I/O error occurs
     * @throws TileActionNotFoundException If a tileaction is not found
     */
    public void writeBoard(Board board, String filePath) throws JsonParsingException, IOException, TileActionNotFoundException;

    /**
     * Writes a board to a JSON file with a custom name and description
     *
     * @param board The board to write to the file
     * @param fileName The name of the file to write to
     * @param name The name of the board
     * @param description The description of the board
     * @throws JsonParseException If a JSON parsing error occurs
     * @throws IOException If an I/O error occurs
     * @throws TileActionNotFoundException If a tileaction is not found
     */
    public void writeBoard(Board board, String fileName, String name, String description) throws JsonParseException, IOException, TileActionNotFoundException;
}