package idi.edu.idatt.mappe.models;

import idi.edu.idatt.mappe.models.tileaction.LadderTileAction;
import idi.edu.idatt.mappe.models.tileaction.TileAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TileTest {

    private Tile tile;
    private Tile nextTile;
    private TileAction action;
    private Player player;
    private BoardGame board;

    @BeforeEach
    void setUp() {
        tile = new Tile(1);
        nextTile = new Tile(2);
        player = new Player("TestPlayer");
        board = new BoardGame();
        board.createBoard(100);
    }

    @Test
    void testSetAndGetNextTile() {
        tile.setNextTile(nextTile);
        assertEquals(nextTile, tile.getNextTile());
    }

    @Test
    void testSetAndPerformLandAction() {
        action = new TileAction() {
            @Override
            public void perform(Player player) {
                player.setExtraThrow(true);
            }
        };
        tile.setLandAction(action);
        tile.performLandAction(player);
        assertTrue(player.hasExtraThrow());
    }

    @Test
    void testGetIndex() {
        assertEquals(1, tile.getIndex());
    }

    @Test
    void testLandPlayer() {
        action = new TileAction() {
            @Override
            public void perform(Player player) {
                player.setExtraThrow(true);
            }
        };
        tile.setLandAction(action);
        tile.landPlayer(player);
        assertTrue(player.hasExtraThrow());
    }

    @Test
    void testLadderTileAction() {
        Tile destinationTile = new Tile(3);
        board.getBoard().getTiles().put(3, destinationTile);
        LadderTileAction ladderAction = new LadderTileAction(3, "Climb the ladder", board.getBoard());
        tile.setLandAction(ladderAction);
        tile.performLandAction(player);
        assertEquals(destinationTile, player.getCurrentTile());
    }
}