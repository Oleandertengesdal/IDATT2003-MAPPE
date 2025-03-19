package idi.edu.idatt.mappe;

import idi.edu.idatt.mappe.models.Player;
import idi.edu.idatt.mappe.models.BoardGame;

public class BoardgameApp {
    private final BoardGame boardGame = new BoardGame();

    public static void main(String[] args) {
        BoardgameApp app = new BoardgameApp();
        app.start();
    }

    public void start() {
        this.boardGame.createBoard(90); // Creates a standard board of 90 linked tiles
        this.boardGame.createDice(2); // Creates 2 Dice
        this.boardGame.addPlayer(new Player("Arne", "Skateboard"));
        this.boardGame.addPlayer(new Player("Ivar", "Hat"));
        this.boardGame.addPlayer(new Player("Majid", "Shoe"));
        this.boardGame.addPlayer(new Player("Atle", "Car"));

        // Play the game
        listPlayers();
        int roundNumber = 1;
        while (!this.boardGame.isFinished()) {
            System.out.println("Round number " + roundNumber++);
            this.boardGame.playOneRound();
            if (!this.boardGame.isFinished()) {
                showPlayerStatus(); // Displays the names of the players and which tile they are on
            }
            System.out.println();
        }
        System.out.println("And the winner is: " + this.boardGame.getWinner().getName());
    }

    private void listPlayers() {
        System.out.println("The following players are playing the game:");
        for (Player player : boardGame.getPlayers()) {
            System.out.println("Name: " + player.getName());
        }
    }

    private void showPlayerStatus() {
        for (Player player : boardGame.getPlayers()) {
            System.out.println("Player " + player.getName() + " is at tile " + player.getCurrentTile().getIndex());
        }
    }
}