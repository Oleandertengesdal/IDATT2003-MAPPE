/*package idi.edu.idatt.mappe.handlers.reader;

import idi.edu.idatt.mappe.exceptions.JsonParsingException;
import idi.edu.idatt.mappe.models.Board;
import idi.edu.idatt.mappe.models.Tile;
import idi.edu.idatt.mappe.models.tileaction.TileAction;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;


public class BoardFileReaderGson  implements BoardFileReader {

    @Override
    public Board readBoard(String filePath) throws JsonParsingException {
        try (JsonReader reader = new JsonReader(new FileReader(filePath))) {
            JsonObject boardJson = JsonParser.parseReader(reader).getAsJsonObject();
            JsonArray tilesJsonArray = boardJson.getAsJsonArray("tiles");

            List<Tile> tiles = new ArrayList<>();
            for (JsonElement tileElement : tilesJsonArray) {
                JsonObject tileJson = tileElement.getAsJsonObject();
                int index = tileJson.get("index").getAsInt();
                String type = tileJson.get("type").getAsString();

                JsonArray actionsJsonArray = tileJson.getAsJsonArray("actions");
                List<TileAction> actions = new ArrayList<>();
                for (JsonElement actionElement : actionsJsonArray) {
                    JsonObject actionJson = actionElement.getAsJsonObject();
                    String actionType = actionJson.get("actionType").getAsString();
                    int value = actionJson.get("value").getAsInt();
                    actions.add(new TileAction(actionType, value));
                }

                tiles.add(new Tile(index, type, actions));
            }

            return new Board(tiles);
        } catch (IOException | IllegalStateException e) {
            throw new JsonParsingException("Error parsing JSON file", e);
        }
    }
}
*/