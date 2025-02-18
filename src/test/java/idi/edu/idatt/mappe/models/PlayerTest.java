package idi.edu.idatt.mappe.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

    private Player player;
    private BoardGame game;
    private Tile startTile;
    private Tile nextTile;

    @BeforeEach
    void setUp() {
        game = new BoardGame();
        startTile = new Tile(1);
        nextTile = new Tile(2);
        startTile.setNextTile(nextTile);
        game.createBoard(100);
        player = new Player("Test");
        game.addPlayer(player);
        player.placeOnTile(startTile);
    }

    @Test
    void getName() {
        assertEquals("Test", player.getName());
    }

    @Test
    void getCurrentTile() {
        assertEquals(1, player.getCurrentTile().getIndex());
    }

    @Test
    void setName() {
        player.setName("Test2");
        assertEquals("Test2", player.getName());
    }

    @Test
    void getBoard() {
        assertNotNull(player.getGame());
    }

    @Test
    void setBoard() {
        BoardGame newGame = new BoardGame();
        newGame.createBoard(100);
        player.setGame(newGame);
        assertEquals(newGame.getBoard(), player.getGame().getBoard());
    }

    @Test
    void placeOnTile() {
        Tile newTile = new Tile(3);
        player.placeOnTile(newTile);
        assertEquals(newTile, player.getCurrentTile());
    }

    @Test
    void hasExtraThrow() {
        assertFalse(player.hasExtraThrow());
        player.setExtraThrow(true);
        assertTrue(player.hasExtraThrow());
    }

    @Test
    void setExtraThrow() {
        player.setExtraThrow(true);
        assertTrue(player.hasExtraThrow());
    }

    @Test
    void isMissingTurn() {
        assertFalse(player.isMissingTurn());
        player.setMissingTurn(true);
        assertTrue(player.isMissingTurn());
    }

    @Test
    void setMissingTurn() {
        player.setMissingTurn(true);
        assertTrue(player.isMissingTurn());
    }

    @Test
    void move() {
        player.move(1);
        assertEquals(nextTile, player.getCurrentTile());
    }

    @Test
    void moveBeyondLastTile() {
        Tile lastTile = new Tile(100);
        nextTile.setNextTile(lastTile);
        player.move(2);
        assertEquals(lastTile, player.getCurrentTile());
    }

    @Test
    void moveWithMissingTurn() {
        player.setMissingTurn(true);
        player.move(1);
        assertEquals(startTile, player.getCurrentTile());
        assertFalse(player.isMissingTurn());
    }

    @Test
    void moveWithoutInitialTile() {
        Player newPlayer = new Player("NewTest");
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            newPlayer.move(1);
        });
        assertEquals("The player must be placed on a tile before moving", exception.getMessage());
    }
}