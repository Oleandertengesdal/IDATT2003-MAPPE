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
    void testGetTile() {
        assertEquals(1, board.getTile(1).getIndex());
        assertEquals(100, board.getTile(100).getIndex());
    }

    @Test
    void testGetTileOutOfBounds() {
        assertThrows(IndexOutOfBoundsException.class, () -> board.getTile(101));
    }

    @Test
    void testGetTileNegative() {
        assertThrows(IndexOutOfBoundsException.class, () -> board.getTile(-1));
    }

    @Test
    void testGetTileAction() {
        TileAction action = new LadderTileAction(10, "Test", board);
        board.getTile(10).setLandAction(action);
        assertEquals(action, board.getTile(10).getLandAction());
    }

    @Test
    void testGetTileActionNull() {
        assertNull(board.getTile(10).getLandAction());
    }

    @Test
    void testGetTileActionOutOfBounds() {
        assertThrows(IndexOutOfBoundsException.class, () -> board.getTile(101).getLandAction());
    }

    @Test
    void testGetTileActionNegative() {
        assertThrows(IndexOutOfBoundsException.class, () -> board.getTile(-1).getLandAction());
    }

    @Test
    void testGetTileActionOutOfBoundsSet() {
        assertThrows(IndexOutOfBoundsException.class, () -> board.getTile(101).setLandAction(new LadderTileAction(10, "Test", board)));
    }

    @Test
    void testGetTileActionNegativeSet() {
        assertThrows(IndexOutOfBoundsException.class, () -> board.getTile(-1).setLandAction(new LadderTileAction(10, "Test", board)));
    }

    @Test
    void testGetTileActionSet() {
        TileAction action = new LadderTileAction(10, "Test", board);
        board.getTile(10).setLandAction(action);
        assertEquals(action, board.getTile(10).getLandAction());
    }

    @Test
    void testGetTileActionSetNull() {
        board.getTile(10).setLandAction(null);
        assertNull(board.getTile(10).getLandAction());
    }

}