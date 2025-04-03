package idi.edu.idatt.mappe.controllers;

import idi.edu.idatt.mappe.BoardgameMain;
import idi.edu.idatt.mappe.files.reader.PlayerFileReader;
import idi.edu.idatt.mappe.files.reader.PlayerFileReaderCVS;
import idi.edu.idatt.mappe.files.writer.PlayerFileWriterCVS;
import idi.edu.idatt.mappe.models.BoardGame;
import idi.edu.idatt.mappe.models.Player;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PlayerSetupController {
    private final BoardgameMain app;
    private final BoardGame game;
    private final VBox view;
    private final List<PlayerConfig> playerConfigs = new ArrayList<>();
    private final List<Player> availablePlayers = new ArrayList<>();

    public PlayerSetupController(BoardgameMain app, BoardGame game) {
        this.app = app;
        this.game = game;
        this.view = new VBox(20);
        view.setPadding(new Insets(20));
        view.setAlignment(Pos.CENTER);

        initializeUI();
        loadAvailablePlayers();
    }

    private void initializeUI() {
        Label title = new Label("Player Setup");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        // Player count selection
        Spinner<Integer> playerCountSpinner = new Spinner<>(2, 10, 2);
        playerCountSpinner.valueProperty().addListener((obs, oldVal, newVal) -> {
            updatePlayerConfigControls(newVal);
        });

        HBox playerCountBox = new HBox(10, new Label("Number of Players:"), playerCountSpinner);
        playerCountBox.setAlignment(Pos.CENTER);

        // Player configuration area
        VBox playerConfigBox = new VBox(10);
        playerConfigBox.setAlignment(Pos.CENTER);

        // Start game button
        Button startGameBtn = new Button("Start Game");
        startGameBtn.setOnAction(e -> startGame());

        // Add all components to main view
        view.getChildren().addAll(
                title,
                playerCountBox,
                playerConfigBox,
                startGameBtn
        );

        // Initialize with default player count
        updatePlayerConfigControls(playerCountSpinner.getValue());
    }

    private void loadAvailablePlayers() {
        try {
            // Load players from CSV file
            PlayerFileReaderCVS playerReader = new PlayerFileReaderCVS();
            availablePlayers.addAll(playerReader.loadPlayers("players.csv"));

            // Add some default players if CSV is empty or doesn't exist
            if (availablePlayers.isEmpty()) {
                availablePlayers.add(new Player("Player 1", "Top Hat"));
                availablePlayers.add(new Player("Player 2", "Race Car"));
                availablePlayers.add(new Player("Player 3", "Cat"));
                availablePlayers.add(new Player("Player 4", "Thimble"));
            }
        } catch (IOException e) {
            // If file reading fails, use default players
            availablePlayers.add(new Player("Player 1", "Top Hat"));
            availablePlayers.add(new Player("Player 2", "Race Car"));
            availablePlayers.add(new Player("Player 3", "Cat"));
            availablePlayers.add(new Player("Player 4", "Thimble"));
        }
    }

    private void updatePlayerConfigControls(int playerCount) {
        VBox playerConfigBox = (VBox) view.getChildren().get(2);
        playerConfigBox.getChildren().clear();
        playerConfigs.clear();

        for (int i = 0; i < playerCount; i++) {
            HBox playerBox = new HBox(10);
            playerBox.setAlignment(Pos.CENTER);

            // Player name selection (ComboBox instead of TextField)
            ComboBox<String> nameCombo = new ComboBox<>();
            nameCombo.setPromptText("Select player");

            // Populate with available players
            for (Player player : availablePlayers) {
                nameCombo.getItems().add(player.getName());
            }

            // Add "New Player" option
            nameCombo.getItems().add("New Player...");

            // Piece selection
            ComboBox<String> pieceCombo = new ComboBox<>();
            pieceCombo.setPromptText("Select piece");

            // When name is selected, update available pieces
            nameCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
                if ("New Player...".equals(newVal)) {
                    // Allow custom name input
                    TextInputDialog dialog = new TextInputDialog();
                    dialog.setTitle("New Player");
                    dialog.setHeaderText("Enter player name");
                    dialog.setContentText("Name:");

                    Optional<String> result = dialog.showAndWait();
                    result.ifPresent(name -> {
                        nameCombo.getSelectionModel().select(name);
                        if (!nameCombo.getItems().contains(name)) {
                            nameCombo.getItems().add(name);
                        }
                    });
                } else if (newVal != null) {
                    // Find the selected player and set their piece
                    Player selectedPlayer = findPlayerByName(newVal);
                    if (selectedPlayer != null) {
                        pieceCombo.getSelectionModel().select(selectedPlayer.getToken());
                    }
                }
            });

            // Populate pieces
            pieceCombo.getItems().addAll(
                    "Top Hat", "Race Car", "Cat", "Thimble",
                    "Battleship", "Dog", "Wheelbarrow", "Shoe"
            );

            // Set default selections
            if (i < availablePlayers.size()) {
                Player defaultPlayer = availablePlayers.get(i);
                nameCombo.getSelectionModel().select(defaultPlayer.getName());
                pieceCombo.getSelectionModel().select(defaultPlayer.getToken());
            } else {
                nameCombo.getSelectionModel().selectFirst();
                pieceCombo.getSelectionModel().selectFirst();
            }

            playerBox.getChildren().addAll(
                    new Label("Player " + (i + 1) + ":"),
                    nameCombo,
                    new Label("Piece:"),
                    pieceCombo
            );

            playerConfigBox.getChildren().add(playerBox);
            playerConfigs.add(new PlayerConfig(nameCombo, pieceCombo));
        }
    }

    private Player findPlayerByName(String name) {
        for (Player player : availablePlayers) {
            if (player.getName().equals(name)) {
                return player;
            }
        }
        return null;
    }

    private void startGame() {
        try {
            // Clear existing players
            //game.clearPlayers();

            // Add configured players
            for (PlayerConfig config : playerConfigs) {
                String name = config.nameCombo.getValue();
                String piece = config.pieceCombo.getValue();

                if (name == null || name.isEmpty() || piece == null) {
                    throw new IllegalArgumentException("All players must have a name and selected piece");
                }

                game.addPlayer(new Player(name, piece));
            }

            // Proceed to game screen
            app.showGameScreen(game);
        } catch (Exception e) {
            showError("Error starting game", e.getMessage());
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

    private static class PlayerConfig {
        ComboBox<String> nameCombo;
        ComboBox<String> pieceCombo;

        PlayerConfig(ComboBox<String> nameCombo, ComboBox<String> pieceCombo) {
            this.nameCombo = nameCombo;
            this.pieceCombo = pieceCombo;
        }
    }
}