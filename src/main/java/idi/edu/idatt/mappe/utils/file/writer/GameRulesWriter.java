package idi.edu.idatt.mappe.utils.file.writer;

import idi.edu.idatt.mappe.exceptions.JsonParsingException;
import idi.edu.idatt.mappe.models.GameRules;

import java.io.IOException;

/**
 * Interface for writing game rules to a file
 * <p>
 *     This interface defines methods for writing game rules to a file
 *     The game rules are serialized to JSON and written to the specified file
 * </p>
 *
 * @see GameRules
 * @version 1.0
 */
public interface GameRulesWriter {

    /**
     * Writes game rules to a file
     *
     * @param rules The game rules to write
     * @param filePath The path to the file to write to
     * @throws JsonParsingException If there's an error creating the JSON
     * @throws IOException If there's an error writing to the file
     */
    void writeGameRules(GameRules rules, String filePath) throws JsonParsingException, IOException;

}
