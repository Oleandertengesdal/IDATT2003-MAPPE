package idi.edu.idatt.mappe.controllers;

import idi.edu.idatt.mappe.BoardgameMain;
import idi.edu.idatt.mappe.exceptions.InvalidGameTypeException;
import idi.edu.idatt.mappe.exceptions.JsonParsingException;
import idi.edu.idatt.mappe.files.reader.BoardFileReader;
import idi.edu.idatt.mappe.files.reader.BoardFileReaderGson;
import idi.edu.idatt.mappe.models.Board;
import idi.edu.idatt.mappe.models.BoardGame;
import idi.edu.idatt.mappe.factory.BoardGameFactory;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.logging.Logger;

import static java.util.logging.Logger.getLogger;

public class GameSelectionController {
    private final BoardgameMain app;
    private final VBox view;

    private static final Logger logger = getLogger(GameSelectionController.class.getName());

    public GameSelectionController(BoardgameMain app) {
        this.app = app;
        this.view = new VBox(20);
        view.setPadding(new Insets(20));
        view.setAlignment(Pos.CENTER);

        initializeUI();
    }

    private void initializeUI() {
        Label title = new Label("Select a Game");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        // Create buttons for each game type
        Button snakesAndLaddersBtn = new Button("Snakes & Ladders");
        snakesAndLaddersBtn.setOnAction(e -> loadGame("SNAKES_LADDERS"));

        Button monopolyBtn = new Button("Monopoly");
        monopolyBtn.setOnAction(e -> loadGame("MONOPOLY"));

        Button lostDiamondBtn = new Button("The Lost Diamond");
        lostDiamondBtn.setOnAction(e -> loadGame("LOST_DIAMOND"));

        Button loadFromJsonBtn = new Button("Load Custom Game from JSON");
        loadFromJsonBtn.setOnAction(e -> loadFromJson());

        view.getChildren().addAll(title, snakesAndLaddersBtn, monopolyBtn,
                lostDiamondBtn, loadFromJsonBtn);
    }

    private void loadGame(String gameType) {
        //BoardGame game = BoardGameFactory.createGame(gameType);
        //app.showPlayerSetupScreen(game);
    }

    private void loadFromJson() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Board Game JSON File");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("JSON Files", "*.json"));

        File file = fileChooser.showOpenDialog(app.getPrimaryStage());
        if (file != null) {
            try {
                BoardFileReaderGson reader = new BoardFileReaderGson();
                Board board = reader.readBoard(file.getAbsolutePath());
                //app.showPlayerSetupScreen(board);
            } catch (JsonParsingException e) {
                logger.warning(() -> "Invalid game type: " + e.getMessage());
                showError("Error loading game", e.getMessage());
            }
        }
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public Parent getView() {
        return view;
    }
}