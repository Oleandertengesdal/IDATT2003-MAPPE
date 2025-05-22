package idi.edu.idatt.mappe.utils.file.writer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import idi.edu.idatt.mappe.exceptions.JsonParsingException;
import idi.edu.idatt.mappe.models.GameRules;

import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Implementation of GameRulesWriter using GSON
 *
 * @see GameRulesWriter
 * @version 1.0
 */
public class GameRulesWriterGson implements GameRulesWriter {
    private static final Logger logger = Logger.getLogger(GameRulesWriterGson.class.getName());

    /**
     * Writes game rules to a file
     *
     * @param rules The game rules to write
     * @param filePath The path to the file to write to
     * @throws JsonParsingException If there's an error creating the JSON
     * @throws IOException If there's an error writing to the file
     */
    @Override
    public void writeGameRules(GameRules rules, String filePath) throws JsonParsingException, IOException {
        try {
            JsonObject rulesJson = serializeRules(rules);

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String jsonString = gson.toJson(rulesJson);

            try (FileWriter writer = new FileWriter(filePath)) {
                writer.write(jsonString);
            }

            logger.info("Successfully wrote game rules to file: " + filePath);

        } catch (Exception e) {
            logger.severe( "Error writing game rules to file: " + filePath);
            throw new JsonParsingException("Error writing game rules", e);
        }
    }

    /**
     * Serializes game rules to a JsonObject
     *
     * @param rules The game rules to serialize
     * @return A JsonObject representing the game rules
     */
    private JsonObject serializeRules(GameRules rules) {
        JsonObject rulesJson = new JsonObject();

        rulesJson.addProperty("gameType", rules.getGameType().name());

        rulesJson.addProperty("numberOfDice", rules.getNumberOfDice());
        rulesJson.addProperty("diceSides", rules.getDiceSides());

        rulesJson.addProperty("extraThrowOnMax", rules.isExtraThrowOnMax());
        rulesJson.addProperty("startOnlyWithMax", rules.isStartOnlyWithMax());
        rulesJson.addProperty("skipTurnOnSnake", rules.isSkipTurnOnSnake());
        rulesJson.addProperty("extraTurnOnLadder", rules.isExtraTurnOnLadder());
        rulesJson.addProperty("consecutiveSixesLimit", rules.getConsecutiveSixesLimit());
        return rulesJson;
    }

}