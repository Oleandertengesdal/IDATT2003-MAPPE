package idi.edu.idatt.mappe.utils.file.reader;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import idi.edu.idatt.mappe.exceptions.JsonParsingException;
import idi.edu.idatt.mappe.models.GameRules;
import idi.edu.idatt.mappe.models.enums.GameType;

import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Implementation of GameRulesReader using GSON
 *
 * <p>
 *     This class reads game rules from a JSON file using GSON
 *     The game rules are deserialized from a JSON object and then returned as a GameRules object
 * </p>
 *
 * @see GameRulesReader
 * @version 1.0
 */
public class GameRulesReaderGson implements GameRulesReader {
    private static final Logger LOGGER = Logger.getLogger(GameRulesReaderGson.class.getName());

    /**
     * Reads game rules from a JSON file
     *
     * @param filePath The path to the file to read from
     * @return The game rules read from the file
     * @throws JsonParsingException If there's an error parsing the file
     */
    @Override
    public GameRules readGameRules(String filePath) throws JsonParsingException {
        try (FileReader fileReader = new FileReader(filePath)) {
            JsonObject rulesJson = JsonParser.parseReader(fileReader).getAsJsonObject();
            GameRules rules = new GameRules();

            setBasicAttributes(rules, rulesJson);
            setGameType(rules, rulesJson);

            LOGGER.info("Successfully read game rules from file: " + filePath);
            return rules;

        } catch (IOException e) {
            LOGGER.warning("Error reading game rules from file: " + filePath);
            throw new JsonParsingException("Error reading game rules file", e);
        } catch (Exception e) {
            LOGGER.warning("Error parsing game rules from file: " + filePath);
            throw new JsonParsingException("Error parsing game rules", e);
        }
    }

    /**
     * Sets basic attributes for the game rules from the JSON object.
     * If the key does not exist, it sets a default value.
     *
     * @param rules The GameRules object to set attributes on
     * @param rulesJson The JSON object containing the rules
     */
    private void setBasicAttributes(GameRules rules, JsonObject rulesJson) {
        rules.setNumberOfDice(getInt(rulesJson, "numberOfDice"));
        rules.setDiceSides(getInt(rulesJson, "diceSides"));
        rules.setExtraThrowOnMax(getBoolean(rulesJson, "extraThrowOnMax"));
        rules.setStartOnlyWithMax(getBoolean(rulesJson, "startOnlyWithSix"));
        rules.setSkipTurnOnSnake(getBoolean(rulesJson, "skipTurnOnSnake"));
        rules.setExtraTurnOnLadder(getBoolean(rulesJson, "extraTurnOnLadder"));
        rules.setConsecutiveSixesLimit(getInt(rulesJson, "consecutiveSixesLimit"));
    }

    private void setGameType(GameRules rules, JsonObject rulesJson) throws JsonParsingException {
        if (rulesJson.has("gameType")) {
            try {
                GameType gameType = GameType.valueOf(rulesJson.get("gameType").getAsString());
                rules.setGameType(gameType);
            } catch (IllegalArgumentException e) {
                LOGGER.warning("Invalid game type in rules file: " + rulesJson.get("gameType").getAsString());
                throw new JsonParsingException("Invalid game type: " + rulesJson.get("gameType").getAsString(), e);
            }
        }
    }

    /**
     * Returns a string value from the JSON object.
     * If the key does not exist, it returns null.
     *
     * @param json The JSON object
     * @param key The key to look for
     * @return The string value or null if the key does not exist
     */
    private String getString(JsonObject json, String key) {
        return json.has(key) ? json.get(key).getAsString() : null;
    }

    /**
     * Returns an integer value from the JSON object.
     * If the key does not exist, it returns 0.
     *
     * @param json The JSON object
     * @param key The key to look for
     * @return The integer value or 0 if the key does not exist
     */
    private int getInt(JsonObject json, String key) {
        return json.has(key) ? json.get(key).getAsInt() : 0;
    }

    /**
     * Returns a boolean value from the JSON object.
     * If the key does not exist, it returns false.
     *
     * @param json The JSON object
     * @param key The key to look for
     * @return The boolean value or false if the key does not exist
     */
    private boolean getBoolean(JsonObject json, String key) {
        return json.has(key) && json.get(key).getAsBoolean();
    }
}