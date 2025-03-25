package idi.edu.idatt.mappe.files.writer;

import com.google.gson.*;
import idi.edu.idatt.mappe.exceptions.TileActionNotFoundException;
import idi.edu.idatt.mappe.models.Board;
import idi.edu.idatt.mappe.models.Tile;
import idi.edu.idatt.mappe.models.tileaction.*;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

/**
 * Implementation of BoardFileWriter using GSON
 * <p>
 *     This class writes a board to a JSON file using GSON
 *     The board is serialized to a JSON object and then written to a file
 *     The board is serialized as follows:
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
 * </p>
 *
 * @see BoardFileWriter
 * @version 1.0
 * @author ---
 */
public class BoardFileWriterGson implements BoardFileWriter {

    /**
     * Writes a board to a JSON file
     *
     * @param board The board to write to the file
     * @param fileName The name of the file to write to
     * @throws IOException If an I/O error occurs
     */
    @Override
    public void writeBoard(Board board, String fileName) throws JsonParseException, IOException, TileActionNotFoundException {
        JsonObject boardJson = serializeBoard(board);

        // Write the JSON to a file
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String jsonString = gson.toJson(boardJson);

        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write(jsonString);
        }
    }

    /**
     * Serializes a board to a JsonObject
     *
     * @param board The board to serialize
     * @return A JsonObject representing the board
     */
    private JsonObject serializeBoard(Board board) throws TileActionNotFoundException {
        JsonObject boardJson = new JsonObject();
        boardJson.addProperty("name", "Game Board");
        boardJson.addProperty("description", "A game board with tiles and actions");

        // Add board dimensions
        boardJson.addProperty("rows", board.getRows());
        boardJson.addProperty("columns", board.getColumns());

        JsonArray tilesJsonArray = new JsonArray();
        Map<Integer, Tile> tiles = board.getTiles();

        for (Map.Entry<Integer, Tile> entry : tiles.entrySet()) {
            int tileId = entry.getKey();
            Tile tile = entry.getValue();

            JsonObject tileJson = new JsonObject();
            tileJson.addProperty("id", tileId);

            // Add coordinates
            tileJson.addProperty("x", tile.getX());
            tileJson.addProperty("y", tile.getY());

            // Add next tile if it exists
            if (tile.getNextTile() != null) {
                tileJson.addProperty("nextTile", tile.getNextTile().getIndex());
            }

            TileAction action = tile.getLandAction();
            if (action != null) {
                JsonObject actionJson = getJsonObject(action);

                tileJson.add("action", actionJson);
            }

            tilesJsonArray.add(tileJson);
        }

        boardJson.add("tiles", tilesJsonArray);
        return boardJson;
    }

    /**
     * Returns a JsonObject representing a tile action
     *
     * @param action The tile action to serialize
     * @return A JsonObject representing the tile action
     * @throws TileActionNotFoundException If the action type is unknown
     */
    private static JsonObject getJsonObject(TileAction action) throws TileActionNotFoundException {
        JsonObject actionJson = new JsonObject();

        if (action instanceof LadderTileAction ladderTileAction) {
            actionJson.addProperty("type", "LadderAction");
            actionJson.addProperty("destinationTileId", ladderTileAction.getDestinationTileId());
            actionJson.addProperty("description", ladderTileAction.getDescription());
        } else if (action instanceof SnakeTileAction snakeTileAction) {
            actionJson.addProperty("type", "SnakeAction");
            actionJson.addProperty("destinationTileId", snakeTileAction.getDestinationTileId());
            actionJson.addProperty("description", snakeTileAction.getDescription());
        } else if (action instanceof RandomTeleportTileAction randomTeleportTileAction) {
            actionJson.addProperty("type", "RandomTeleportAction");
            actionJson.addProperty("description", randomTeleportTileAction.getDescription());
            actionJson.addProperty("destinationTileId", -1); // Placeholder for random teleport
        } else {
            throw new TileActionNotFoundException("Unknown action type: " + action.getClass().getName());
        }
        return actionJson;
    }
}