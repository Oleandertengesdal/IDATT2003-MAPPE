package idi.edu.idatt.mappe.models;

import idi.edu.idatt.mappe.models.enums.GameType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class BoardTest {

    private Board board;

    @BeforeEach
    void setUp() {
        board = new Board(3, 3, GameType.SNAKES_AND_LADDERS); // 3x3 board for testing
    }

    @Test
    void testBoardInitialization() {
        assertEquals(3, board.getRows());
        assertEquals(3, board.getColumns());
        assertEquals(GameType.SNAKES_AND_LADDERS, board.getGameType());
        assertEquals(9, board.getTiles().size()); // 3x3 = 9 tiles
    }

    @Test
    void testGetTileByIndex() {
        Tile tile = board.getTileByIndex(1);
        assertNotNull(tile);
        assertEquals(1, tile.getIndex());
    }

    @Test
    void testGetTileByIndexOutOfBounds() {
        assertThrows(IndexOutOfBoundsException.class, () -> board.getTileByIndex(10));
    }

    @Test
    void testGetTileByCoordinates() {
        Tile tile = board.getTileByCoordinates(0, 0);
        assertNotNull(tile);
        assertEquals(1, tile.getIndex()); // Top-left tile in a 3x3 board
    }

    @Test
    void testGetTileByCoordinatesInvalid() {
        Tile tile = board.getTileByCoordinates(5, 5); // Out of bounds
        assertNull(tile);
    }

    @Test
    void testAddTile() {
        Tile newTile = new Tile(10, 2, 2);
        board.addTile(10, newTile);

        Map<Integer, Tile> tiles = board.getTiles();
        assertTrue(tiles.containsKey(10));
        assertEquals(newTile, tiles.get(10));
    }

    @Test
    void testSetAndGetGameType() {
        board.setGameType(GameType.THE_LOST_DIAMOND);
        assertEquals(GameType.THE_LOST_DIAMOND, board.getGameType());
    }
}