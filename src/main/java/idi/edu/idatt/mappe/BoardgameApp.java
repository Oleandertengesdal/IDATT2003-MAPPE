package idi.edu.idatt.mappe;

import idi.edu.idatt.mappe.models.Player;
import idi.edu.idatt.mappe.models.Tile;
import idi.edu.idatt.mappe.models.games.SnakesAndLadders;
import idi.edu.idatt.mappe.models.tileaction.TileAction;
import idi.edu.idatt.mappe.utility.ConfigFileReader;

import java.util.logging.Logger;

public class BoardgameApp {
    public static void main(String[] args) {
        final Logger logger = ConfigFileReader.getLogger(BoardgameApp.class);
        // Set up the game
        SnakesAndLadders game = new SnakesAndLadders();
        game.addPlayer(new Player("Fredrik"));
        game.addPlayer(new Player("Morten"));
        game.addPlayer(new Player("Oleander"));
        game.addPlayer(new Player("Anders"));

        game.addPlayerBoard();


        // Print the players
        System.out.println("The following players are playing the game:");
        for (Player player : game.getPlayers()) {
            System.out.println("Name: " + player.getName());
        }

        for (Player player : game.getPlayers()) {
            System.out.println("Player " + player.getName() + " is at tile " + player.getCurrentTile().getIndex());
        }

        // Play the game
        int roundCounter = 1;
        while (!game.getWinner()) {
            System.out.println(" ");
            System.out.println("Round " + roundCounter);
            game.playRound();
            roundCounter++;
        }
    }
}