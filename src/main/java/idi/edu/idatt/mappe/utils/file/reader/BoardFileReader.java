package idi.edu.idatt.mappe.utils.file.reader;


import idi.edu.idatt.mappe.exceptions.JsonParsingException;
import idi.edu.idatt.mappe.models.Board;

/**
 * Interface for reading a board from a file
 * <p>
 *     This interface defines methods for reading a board from a file
 *     The board is deserialized from JSON and returned as a Board object
 * </p>
 *
 * @see Board
 * @version 1.0
 */
public interface BoardFileReader {
    /**
     * Reads a board from a JSON file
     *
     * @param filePath The path to the file to read from
     * @return The board read from the file
     * @throws JsonParsingException If there's an error parsing the file
     */
    public Board readBoard(String filePath) throws JsonParsingException;
}