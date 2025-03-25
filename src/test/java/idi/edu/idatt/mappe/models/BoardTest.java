package idi.edu.idatt.mappe.models;

import idi.edu.idatt.mappe.models.tileaction.LadderTileAction;
import idi.edu.idatt.mappe.models.tileaction.TileAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BoardTest {

    private BoardGame game;
    private Board board;
    private Tile startTile;
    private Tile endTile;

    @BeforeEach
    void setUp() {
        game = new BoardGame();
        game.createBoard(100);
        board = game.getBoard();
        startTile = new Tile(1);
        endTile = new Tile(100);
    }

    @Test
    void testCreateBoard() {
        assertEquals(100, board.getTiles().size());
        assertEquals(1, board.getTiles().get(1).getIndex());
        assertEquals(100, board.getTiles().get(100).getIndex());
    }

    @Test
    void testGetTileByIndex() {
        assertEquals(1, board.getTileByIndex(1).getIndex());
        assertEquals(100, board.getTileByIndex(100).getIndex());
    }

    @Test
    void testGetTileByIndexOutOfBounds() {
        assertThrows(IndexOutOfBoundsException.class, () -> board.getTileByIndex(101));
    }

    @Test
    void testGetTileByIndexNegative() {
        assertThrows(IndexOutOfBoundsException.class, () -> board.getTileByIndex(-1));
    }

    @Test
    void testGetTileByIndexAction() {
        TileAction action = new LadderTileAction(10, "Test", board);
        board.getTileByIndex(10).setLandAction(action);
        assertEquals(action, board.getTileByIndex(10).getLandAction());
    }

    @Test
    void testGetTileByIndexActionNull() {
        assertNull(board.getTileByIndex(10).getLandAction());
    }

    @Test
    void testGetTileByIndexActionOutOfBounds() {
        assertThrows(IndexOutOfBoundsException.class, () -> board.getTileByIndex(101).getLandAction());
    }

    @Test
    void testGetTileByIndexActionNegative() {
        assertThrows(IndexOutOfBoundsException.class, () -> board.getTileByIndex(-1).getLandAction());
    }

    @Test
    void testGetTileByIndexActionOutOfBoundsSet() {
        assertThrows(IndexOutOfBoundsException.class, () -> board.getTileByIndex(101).setLandAction(new LadderTileAction(10, "Test", board)));
    }

    @Test
    void testGetTileByIndexActionNegativeSet() {
        assertThrows(IndexOutOfBoundsException.class, () -> board.getTileByIndex(-1).setLandAction(new LadderTileAction(10, "Test", board)));
    }

    @Test
    void testGetTileByIndexActionSet() {
        TileAction action = new LadderTileAction(10, "Test", board);
        board.getTileByIndex(10).setLandAction(action);
        assertEquals(action, board.getTileByIndex(10).getLandAction());
    }

    @Test
    void testGetTileByIndexActionSetNull() {
        board.getTileByIndex(10).setLandAction(null);
        assertNull(board.getTileByIndex(10).getLandAction());
    }

}