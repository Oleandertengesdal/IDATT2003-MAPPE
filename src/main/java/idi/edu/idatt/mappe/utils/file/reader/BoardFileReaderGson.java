package idi.edu.idatt.mappe.utils.file.reader;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import idi.edu.idatt.mappe.exceptions.JsonParsingException;
import idi.edu.idatt.mappe.models.Board;
import idi.edu.idatt.mappe.models.BoardGame;
import idi.edu.idatt.mappe.models.Tile;
import idi.edu.idatt.mappe.models.enums.GameType;
import idi.edu.idatt.mappe.models.tileaction.*;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of BoardFileReader using GSON
 *
 * <p>
 *     This class reads a board from a JSON file using GSON
 *     The board is deserialized from a JSON object and then returned as a Board object
 *     The board is deserialized as follows:
 *     {
 *     "name": "Game Board",
 *     "description": "A game board with tiles and actions",
 *     "rows": 10,
 *     "columns": 10,
 *     "tiles": [
 *     {
 *     "id": 1,
 *     "x": 0,
 *     "y": 0,
 *     "nextTile": 2,
 *     "action": {
 *     "type": "LadderAction",
 *     "destinationTileId": 5,
 *     "description": "Climb the ladder"
 *     }
 *     },
 *     .....
 *     }
 *     </p>
 *
 * @see BoardFileReader
 * @version 1.0
 */
public class BoardFileReaderGson implements BoardFileReader {

    /**
     * Reads a board from a JSON file
     *
     * @param fileName The name of the file to read from
     * @return The board read from the file
     * @throws JsonParsingException If a JSON parsing error occurs
     */
    @Override
    public Board readBoard(String fileName) throws JsonParsingException {
        try (FileReader fileReader = new FileReader(fileName)) {
            JsonObject boardJson = JsonParser.parseReader(fileReader).getAsJsonObject();

            int rows = boardJson.has("rows") ? boardJson.get("rows").getAsInt() : 10;
            int columns = boardJson.has("columns") ? boardJson.get("columns").getAsInt() : 10;


            GameType gameType = GameType.SNAKES_AND_LADDERS;
            if (boardJson.has("gameType")) {
                try {
                    gameType = GameType.valueOf(boardJson.get("gameType").getAsString());
                } catch (IllegalArgumentException e) {
                    throw new JsonParsingException("Invalid game type: " + boardJson.get("gameType").getAsString(), e);
                }
            }

            Board board = new Board(rows, columns, gameType);


            // Get the tiles array from the JSON
            JsonArray tilesJsonArray = boardJson.getAsJsonArray("tiles");

            Map<Integer, Tile> tilesMap = new HashMap<>();

            for (JsonElement tileElement : tilesJsonArray) {
                JsonObject tileJson = tileElement.getAsJsonObject();
                int tileId = tileJson.get("id").getAsInt();

                // Read the coordinates
                int x = tileJson.has("x") ? tileJson.get("x").getAsInt() : 0;
                int y = tileJson.has("y") ? tileJson.get("y").getAsInt() : 0;

                // Create tile with coordinates
                Tile tile = new Tile(tileId, x, y);
                tilesMap.put(tileId, tile);
            }

            tilesMap.forEach((key, tile) -> board.addTile(tile.getIndex(), tile));

            // Second pass: Set next tiles and actions
            for (JsonElement tileElement : tilesJsonArray) {
                JsonObject tileJson = tileElement.getAsJsonObject();
                int tileId = tileJson.get("id").getAsInt();
                Tile tile = tilesMap.get(tileId);

                if (tileJson.has("nextTile")) {
                    int nextTileId = tileJson.get("nextTile").getAsInt();
                    tile.setNextTile(tilesMap.get(nextTileId));
                }

                if (tileJson.has("action")) {
                    JsonObject actionJson = tileJson.getAsJsonObject("action");
                    String actionType = actionJson.get("type").getAsString();
                    int destinationTileId = actionJson.get("destinationTileId").getAsInt();
                    String description = actionJson.get("description").getAsString();

                    TileAction action = switch (actionType) {
                        case "LadderAction" -> new LadderTileAction(destinationTileId, description, board);
                        case "SnakeAction" -> new SnakeTileAction(destinationTileId, description, board);
                        case "RandomTeleportAction" -> new RandomTeleportTileAction(board, description);
                        case "SwapAction" -> new SwapAction(new BoardGame(GameType.SNAKES_AND_LADDERS), description);
                        case "ExtraThrowAction" -> new ExtraThrowAction(description, board);
                        case "MissingTurnAction" -> new MissingTurnTileAction(description, board);
                        case "GoToJailAction" -> new GoToJailTileAction(description, board);
                        default -> throw new JsonParsingException("Unknown action type: " + actionType);
                    };

                    tile.setLandAction(action);
                }
            }

            return board;
        } catch (IOException | IllegalStateException e) {
            throw new JsonParsingException("Error parsing JSON file", e);
        }
    }
}