package idi.edu.idatt.mappe.models.games;

import idi.edu.idatt.mappe.models.Board;
import idi.edu.idatt.mappe.models.BoardGame;
import idi.edu.idatt.mappe.models.Player;
import idi.edu.idatt.mappe.models.Tile;
import idi.edu.idatt.mappe.models.tileaction.LadderTileAction;
import idi.edu.idatt.mappe.models.tileaction.SnakeTileAction;
import idi.edu.idatt.mappe.models.tileaction.TileAction;

import java.util.Random;

public class SnakesAndLadders extends BoardGame {

    public SnakesAndLadders() {
        super();
        createBoard(90); // Standard board size
        createDice(2); // Two dice
        setupBoard();
    }

    private void setupBoard() {
        Random random = new Random();
        int numberOfSnakes = 10;
        int numberOfLadders = 10;

        for (int i = 0; i < numberOfSnakes; i++) {
            int start = random.nextInt(80) + 10; // Ensure snakes start within the board range
            int end = random.nextInt(start - 1) + 1; // Ensure end is before start
            setSnake(start, end);
        }

        for (int i = 0; i < numberOfLadders; i++) {
            int start = random.nextInt(80) + 1; // Ensure ladders start within the board range
            int end = random.nextInt(90 - start) + start + 1; // Ensure end is after start
            setLadder(start, end);
        }
    }

    private void setSnake(int start, int end) {
        Tile startTile = getBoard().getTile(start - 1);
        Tile endTile = getBoard().getTile(end - 1);
        startTile.setLandAction(new SnakeTileAction(endTile));
    }

    private void setLadder(int start, int end) {
        Tile startTile = getBoard().getTile(start - 1);
        Tile endTile = getBoard().getTile(end - 1);
        startTile.setLandAction(new LadderTileAction(endTile));
    }

    public void startGame() {
        for (Player player : getPlayers()) {
            player.placeOnTile(getBoard().getTile(0));
        }
    }

    public void playRound() {
        for (Player player : getPlayers()) {
            int steps = getDice().roll();
            movePlayer(player, steps);
            player.getCurrentTile().landPlayer(player);
            System.out.println("Player " + player.getName() + " on tile " + player.getCurrentTile().getIndex());
        }
    }
}