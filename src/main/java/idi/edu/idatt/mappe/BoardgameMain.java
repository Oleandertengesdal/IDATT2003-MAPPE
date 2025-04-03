package idi.edu.idatt.mappe;

import idi.edu.idatt.mappe.controllers.GameController;
import idi.edu.idatt.mappe.controllers.GameSelectionController;
import idi.edu.idatt.mappe.controllers.PlayerSetupController;
import idi.edu.idatt.mappe.models.BoardGame;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Window;

/**
 * Main class for the Board Game application.
 * This class initializes the JavaFX application and sets up the main stage.
 */
public class BoardgameMain extends Application  {

    private Stage primaryStage;
    private Scene currentScene;

    private final BoardGame boardGame = new BoardGame();


    public static void main(String[] args) {
        launch(args);
    }

    public void showGameSelectionScreen() {
        GameSelectionController controller = new GameSelectionController(this);
        currentScene = new Scene(controller.getView());
        primaryStage.setScene(currentScene);
    }

    public void showPlayerSetupScreen(BoardGame game) {
        PlayerSetupController controller = new PlayerSetupController(this, game);
        currentScene = new Scene(controller.getView());
        primaryStage.setScene(currentScene);
    }

    public void showGameScreen(BoardGame game) {
        GameController controller = new GameController(this, game);
        currentScene = new Scene(controller.getView());
        primaryStage.setScene(currentScene);
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Board Game");

        // Load initial screen
        showGameSelectionScreen();

        primaryStage.show();
    }

    public Window getPrimaryStage() {
        return primaryStage;
    }

    /*
    public static void main(String[] args) {
        BoardgameMain app = new BoardgameMain();
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
    */
}