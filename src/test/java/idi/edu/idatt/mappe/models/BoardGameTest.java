package idi.edu.idatt.mappe.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BoardGameTest {

    private BoardGame boardGame;
    private Player player1;
    private Player player2;

    @BeforeEach
    void setUp() {
        boardGame = new BoardGame();
        boardGame.createBoard(100);
        player1 = new Player("Player1");
        player2 = new Player("Player2");
        boardGame.addPlayer(player1);
        boardGame.addPlayer(player2);
    }

    @Test
    void testCreateBoard() {
        assertNotNull(boardGame.getBoard());
        assertEquals(100, boardGame.getBoard().getTiles().size());
    }

    @Test
    void testAddPlayer() {
        assertEquals(2, boardGame.getPlayers().size());
        assertTrue(boardGame.getPlayers().contains(player1));
        assertTrue(boardGame.getPlayers().contains(player2));
    }

    @Test
    void testSetCurrentPlayer() {
        boardGame.setCurrentPlayer(player1);
        assertEquals(player1, boardGame.getCurrentPlayer());
    }

    @Test
    void testStartGame() {
        boardGame.startGame();
        assertEquals(boardGame.getBoard().getTile(1), player1.getCurrentTile());
        assertEquals(boardGame.getBoard().getTile(1), player2.getCurrentTile());
    }

    @Test
    void testMovePlayer() {
        boardGame.setCurrentPlayer(player1);
        boardGame.createDice(1);
        boardGame.play();
        assertNotEquals(boardGame.getBoard().getTile(1), player1.getCurrentTile());
    }

    @Test
    void testGetWinner() {
        Tile lastTile = boardGame.getBoard().getTile(100);
        boardGame.getBoard().addTile(100 ,lastTile);
        player1.placeOnTile(lastTile);
        boardGame.getWinner();
        // Check console output for winner announcement
    }
}