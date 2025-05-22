package idi.edu.idatt.mappe.models;

import idi.edu.idatt.mappe.models.enums.TokenType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

    private Player player;
    private Tile startTile;
    private Tile nextTile;

    @BeforeEach
    void setUp() {
        startTile = new Tile(1, "Start City", null, 0, 0);
        nextTile = new Tile(2, "Next City", null, 1, 1);
        startTile.setNextTile(nextTile);

        player = new Player("TestPlayer", "Token1", 500);
        player.placeOnTile(startTile);
    }

    @Test
    void testGetName() {
        assertEquals("TestPlayer", player.getName());
    }

    @Test
    void testSetName() {
        player.setName("NewName");
        assertEquals("NewName", player.getName());
    }

    @Test
    void testGetToken() {
        assertEquals("Token1", player.getToken());
    }

    @Test
    void testSetToken() {
        player.setToken("NewToken");
        assertEquals("NewToken", player.getToken());
    }

    @Test
    void testPlaceOnTile() {
        assertEquals(startTile, player.getCurrentTile());
    }

    @Test
    void testMove() {
        player.move(1);
        assertEquals(nextTile, player.getCurrentTile());
    }

    @Test
    void testMoveWithMissingTurn() {
        player.setMissingTurn(true);
        player.move(1);
        assertEquals(startTile, player.getCurrentTile());
        assertFalse(player.isMissingTurn());
    }

    @Test
    void testHasExtraThrow() {
        assertFalse(player.hasExtraThrow());
        player.setExtraThrow(true);
        assertTrue(player.hasExtraThrow());
    }

    @Test
    void testMoneyManagement() {
        assertEquals(500, player.getMoney());
        player.addMoney(100);
        assertEquals(600, player.getMoney());
        assertTrue(player.spendMoney(200));
        assertEquals(400, player.getMoney());
        assertFalse(player.spendMoney(500)); // Not enough money
    }

    @Test
    void testHasDiamond() {
        assertFalse(player.hasDiamond());
        player.setHasDiamond(true);
        assertTrue(player.hasDiamond());
    }

    @Test
    void testReset() {
        player.setHasDiamond(true);
        player.setExtraThrow(true);
        player.setMissingTurn(true);
        player.addMoney(100);
        player.reset();

        assertNull(player.getCurrentTile());
        assertFalse(player.hasDiamond());
        assertFalse(player.hasExtraThrow());
        assertFalse(player.isMissingTurn());
        assertEquals(500, player.getMoney());
    }
}