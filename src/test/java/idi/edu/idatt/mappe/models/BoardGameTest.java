package idi.edu.idatt.mappe.models;

import idi.edu.idatt.mappe.models.enums.GameType;
import idi.edu.idatt.mappe.models.enums.GameState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BoardGameTest {

    private BoardGame boardGame;
    private Player player1;
    private Player player2;

    @BeforeEach
    void setUp() {
        Board board = new Board(10, 10, GameType.SNAKES_AND_LADDERS);
        boardGame = new BoardGame(GameType.SNAKES_AND_LADDERS);
        boardGame.setBoard(board);
        boardGame.createDice(2);
        player1 = new Player("Player1", "Token1");
        player2 = new Player("Player2", "Token2");
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
    void testStartGame() {
        boardGame.startGame();
        assertEquals(boardGame.getBoard().getTileByIndex(1), player1.getCurrentTile());
        assertEquals(boardGame.getBoard().getTileByIndex(1), player2.getCurrentTile());
        assertEquals(GameState.STARTED, boardGame.getGameState());
    }

    @Test
    void testMovePlayer() {
        boardGame.setCurrentPlayer(player1);
        boardGame.createDice(1);
        boardGame.play();
        assertNotEquals(boardGame.getBoard().getTileByIndex(1), player1.getCurrentTile());
    }

    @Test
    void testGetWinner() {
        player1.placeOnTile(boardGame.getBoard().getTileByIndex(100));
        assertEquals(player1, boardGame.getWinner());
    }
}