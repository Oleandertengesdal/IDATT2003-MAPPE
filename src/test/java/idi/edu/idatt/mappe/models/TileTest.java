package idi.edu.idatt.mappe.models;

import idi.edu.idatt.mappe.models.enums.Direction;
import idi.edu.idatt.mappe.models.enums.TileType;
import idi.edu.idatt.mappe.models.tileaction.LadderTileAction;
import idi.edu.idatt.mappe.models.tileaction.TileAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TileTest {

    private Tile tile;
    private Tile connectedTile;
    private Player player;
    private TileAction action;

    @BeforeEach
    void setUp() {
        tile = new Tile(1, "Test City", TileType.CITY, 0, 0);
        connectedTile = new Tile(2, "Connected City", TileType.CITY, 1, 1);
        player = new Player("TestPlayer");
        action = new LadderTileAction(2, "Climb the ladder", null);
    }

    @Test
    void testSetAndGetNextTile() {
        tile.setNextTile(connectedTile);
        assertEquals(connectedTile, tile.getNextTile());
    }

    @Test
    void testSetAndGetLandAction() {
        tile.setLandAction(action);
        assertEquals(action, tile.getLandAction());
    }

    @Test
    void testAddAndGetConnections() {
        tile.addConnection(Direction.NORTH, connectedTile, 10);
        assertEquals(connectedTile, tile.getConnectionInDirection(Direction.NORTH));
        assertEquals(10, tile.getTravelCost(Direction.NORTH));
    }

    @Test
    void testHasToken() {
        assertFalse(tile.hasToken());
        tile.setHiddenToken(null);
        assertFalse(tile.hasToken());
    }

    @Test
    void testRevealToken() {
        tile.setHiddenToken(null);
        assertNull(tile.revealToken(player));
    }

    @Test
    void testGetAffordableConnections() {
        tile.addConnection(Direction.NORTH, connectedTile, 10);
        player.setMoney(20);
        assertTrue(tile.getAffordableConnections(player).contains(connectedTile));
    }

    @Test
    void testSetAndGetCoordinates() {
        tile.setCoordinates(5, 10);
        assertEquals(5, tile.getX());
        assertEquals(10, tile.getY());
    }

    @Test
    void testIsGoal() {
        tile.setGoal(true);
        assertTrue(tile.isGoal());
    }

    @Test
    void testIsStart() {
        tile.setStart(true);
        assertTrue(tile.isStart());
    }
}