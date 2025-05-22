package idi.edu.idatt.mappe.utils.file.reader;

import idi.edu.idatt.mappe.exceptions.JsonParsingException;
import idi.edu.idatt.mappe.models.GameRules;

/**
 * Interface for reading game rules from a file
 * <p>
 *     This interface defines methods for reading game rules from a file
 *     The game rules are deserialized from JSON and returned as a GameRules object
 * </p>
 * @see GameRules
 * @version 1.0
 */
public interface GameRulesReader {

    /**
     * Reads game rules from a file
     *
     * @param filePath The path to the file to read from
     * @return The game rules read from the file
     * @throws JsonParsingException If there's an error parsing the file
     */
    GameRules readGameRules(String filePath) throws JsonParsingException;
}