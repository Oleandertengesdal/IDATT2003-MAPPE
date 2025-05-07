package idi.edu.idatt.mappe.views;

import idi.edu.idatt.mappe.controllers.BoardGameController;
import idi.edu.idatt.mappe.controllers.PlayerController;
import idi.edu.idatt.mappe.models.Player;
import idi.edu.idatt.mappe.models.PlayerSelectionEntry;
import idi.edu.idatt.mappe.controllers.FileService;
import idi.edu.idatt.mappe.views.game.PlayerCardView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.function.Consumer;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * A redesigned player selection view that displays players as card components
 * instead of a table for a more user-friendly and visually appealing interface.
 * Uses FileService for file operations.
 */
public class PlayerSelectionView extends BorderPane {

    private static final List<String> AVAILABLE_TOKENS = List.of(
            "Star", "Car", "Hat", "Dog", "Cat", "Bishop", "Burger",
            "Chef", "Controller", "Graduation", "Knight", "Shoe", "Skate",
            "Skateboard", "Wizard", "None"
    );

    private static final List<String> AVAILABLE_COLORS = List.of(
            "#e74c3c", "#3498db", "#2ecc71", "#f1c40f", "#9b59b6",
            "#e67e22", "#1abc9c", "#34495e", "#d35400", "#16a085"
    );

    private final Map<String, Image> tokenImages = new HashMap<>();

    private final BoardGameController boardGameController;
    private final PlayerController playerController;
    private final FileService fileService;
    private final Stage stage;
    private final Consumer<Boolean> onStartGameCallback;

    private final ObservableList<PlayerSelectionEntry> playerEntries = FXCollections.observableArrayList();
    private final List<PlayerCardView> playerCards = new ArrayList<>();

    private FlowPane playersContainer;
    private ScrollPane scrollPane;
    private TextField nameField;
    private ComboBox<String> tokenCombo;
    private ComboBox<String> colorCombo;
    private Random random = new Random();

    private static final Logger logger = Logger.getLogger(PlayerSelectionView.class.getName());

    /**
     * Constructor for PlayerSelectionView.
     *
     * @param boardGameController      The game controller
     * @param playerController    The player controller
     * @param fileService         The file service for loading/saving players
     * @param stage               The stage for the dialog
     * @param onStartGameCallback The callback to execute when the game starts
     */
    public PlayerSelectionView(BoardGameController boardGameController, PlayerController playerController,
                               FileService fileService, Stage stage, Consumer<Boolean> onStartGameCallback) {
        this.boardGameController = boardGameController;
        this.playerController = playerController;
        this.fileService = fileService;
        this.stage = stage;
        this.onStartGameCallback = onStartGameCallback;

        logger.info("Initializing PlayerSelectionView");

        // Apply global styles
        this.setStyle("-fx-background-color: linear-gradient(to bottom, #f5f7fa, #e5e9f0);");

        loadTokenImages();
        loadExistingPlayers();
        setupLayout();
    }

    /**
     * Sets up the layout for the player selection panel.
     */
    private void setupLayout() {
        // Create header
        VBox header = createHeader();
        this.setTop(header);

        VBox contentArea = new VBox(20);
        contentArea.setAlignment(Pos.CENTER);
        contentArea.setPadding(new Insets(20));

        // Players container with cards
        createPlayersContainer();

        // Add player form
        VBox addPlayerForm = createAddPlayerForm();
        // Add a separator between the players and the form
        Separator separator = new Separator();
        separator.setPadding(new Insets(10, 0, 10, 0));

        contentArea.getChildren().addAll(
                new Label("Select Players for the Game:"),
                scrollPane,
                separator,
                addPlayerForm
        );

        this.setCenter(contentArea);

        // Create button bar
        HBox buttons = createButtonBar();
        this.setBottom(buttons);
    }

    /**
     * Creates the header section of the view.
     *
     * @return VBox containing the header
     */
    private VBox createHeader() {
        VBox header = new VBox(10);
        header.setAlignment(Pos.CENTER);
        header.setPadding(new Insets(25, 0, 25, 0));
        header.setStyle("-fx-background-color: #34495e; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0, 0, 3);");

        Label title = new Label("Player Selection");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        title.setTextFill(Color.WHITE);
        title.setTextAlignment(TextAlignment.CENTER);

        Label subtitle = new Label("Choose your players and customize their tokens and colors");
        subtitle.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
        subtitle.setTextFill(Color.web("#ecf0f1"));
        subtitle.setTextAlignment(TextAlignment.CENTER);

        header.getChildren().addAll(title, subtitle);
        return header;
    }

    /**
     * Creates the players container with card layout.
     */
    private void createPlayersContainer() {
        playersContainer = new FlowPane();
        playersContainer.setHgap(15);
        playersContainer.setVgap(15);
        playersContainer.setPadding(new Insets(15));
        playersContainer.setAlignment(Pos.CENTER);

        scrollPane = new ScrollPane(playersContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(400);
        scrollPane.setStyle("-fx-background-color: white; -fx-background: white;");

        refreshPlayerCards();
    }

    /**
     * Refreshes the player cards based on current entries.
     */
    private void refreshPlayerCards() {
        playerCards.clear();
        playersContainer.getChildren().clear();

        if (playerEntries.isEmpty()) {
            Label placeholder = new Label("No players available. Add players below.");
            placeholder.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
            placeholder.setTextFill(Color.web("#95a5a6"));
            placeholder.setPadding(new Insets(40));
            playersContainer.getChildren().add(placeholder);
            return;
        }

        for (PlayerSelectionEntry entry : playerEntries) {
            PlayerCardView card = new PlayerCardView(entry, tokenImages, this::removePlayerCard);
            playerCards.add(card);
            playersContainer.getChildren().add(card);
        }
    }

    /**
     * Removes a player card from the view.
     *
     * @param card The card to remove
     */
    private void removePlayerCard(PlayerCardView card) {
        PlayerSelectionEntry entry = card.getEntry();
        playerEntries.remove(entry);
        playerCards.remove(card);
        playersContainer.getChildren().remove(card);

        if (playerCards.isEmpty()) {
            refreshPlayerCards();
        }
    }

    /**
     * Creates the form for adding new players.
     *
     * @return VBox containing the add player form
     */
    private VBox createAddPlayerForm() {
        VBox form = new VBox(15);
        form.setPadding(new Insets(20, 10, 10, 10));
        form.getStyleClass().add("add-player-form");
        form.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 8px; -fx-border-color: #e1e1e1; -fx-border-radius: 8px;");

        Label formTitle = new Label("Add New Player");
        formTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        formTitle.setTextFill(Color.web("#2c3e50"));

        nameField = new TextField();
        nameField.setPromptText("Enter player name");
        nameField.setPrefWidth(250);
        nameField.setStyle("-fx-background-radius: 4px; -fx-border-radius: 4px;");

        tokenCombo = new ComboBox<>();
        tokenCombo.getItems().addAll(AVAILABLE_TOKENS);
        tokenCombo.setPromptText("Select token");
        tokenCombo.setPrefWidth(200);
        tokenCombo.setStyle("-fx-background-radius: 4px;");

        if (!tokenImages.isEmpty()) {
            tokenCombo.setCellFactory(listView -> new ListCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);

                    if (empty || item == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        setText(item);
                    }
                }
            });
        }

        colorCombo = new ComboBox<>();
        for (String color : AVAILABLE_COLORS) {
            colorCombo.getItems().add(color);
        }
        colorCombo.setValue(AVAILABLE_COLORS.getFirst());
        colorCombo.setPromptText("Select color");
        colorCombo.setPrefWidth(200);
        colorCombo.setStyle("-fx-background-radius: 4px;");

        colorCombo.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(String color, boolean empty) {
                super.updateItem(color, empty);
                if (empty || color == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    javafx.scene.shape.Rectangle colorRect = new javafx.scene.shape.Rectangle(16, 16);
                    colorRect.setFill(Color.web(color));
                    colorRect.setStroke(Color.BLACK);
                    colorRect.setStrokeWidth(0.5);
                    setGraphic(new HBox(10, colorRect));
                }
            }
        });

        Button randomizeBtn = new Button("Randomize");
        randomizeBtn.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-background-radius: 4px;");

        randomizeBtn.setOnMouseEntered(e -> randomizeBtn.setStyle("-fx-background-color: #7f8c8d; -fx-text-fill: white; -fx-background-radius: 4px;"));
        randomizeBtn.setOnMouseExited(e -> randomizeBtn.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-background-radius: 4px;"));

        randomizeBtn.setOnAction(e -> {
            int tokenIndex = random.nextInt(AVAILABLE_TOKENS.size());
            int colorIndex = random.nextInt(AVAILABLE_COLORS.size());
            tokenCombo.setValue(AVAILABLE_TOKENS.get(tokenIndex));
            colorCombo.setValue(AVAILABLE_COLORS.get(colorIndex));
        });

        Button addPlayerBtn = new Button("Add Player");
        addPlayerBtn.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 4px;");

        addPlayerBtn.setOnMouseEntered(e -> addPlayerBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 4px;"));
        addPlayerBtn.setOnMouseExited(e -> addPlayerBtn.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 4px;"));

        addPlayerBtn.setOnAction(e -> {
            String name = nameField.getText().trim();
            String token = tokenCombo.getValue();
            String color = colorCombo.getValue();

            if (name.isEmpty()) {
                showAlert("Missing Information", "Please enter a player name.", Alert.AlertType.WARNING);
                return;
            }

            if (token == null) {
                token = AVAILABLE_TOKENS.get(random.nextInt(AVAILABLE_TOKENS.size()));
            }

            if (color == null) {
                color = AVAILABLE_COLORS.get(random.nextInt(AVAILABLE_COLORS.size()));
            }

            // Check if player with this name already exists
            if (playerEntries.stream().anyMatch(entry -> entry.getName().equalsIgnoreCase(name))) {
                showAlert("Player Exists", "A player with this name already exists.", Alert.AlertType.WARNING);
                return;
            }

            Player player = new Player(name, token);
            playerController.addPlayer(player);

            PlayerSelectionEntry entry = new PlayerSelectionEntry(player);
            entry.setColor(color);
            entry.setSelected(true);
            playerEntries.add(entry);

            // Refresh the player cards
            refreshPlayerCards();

            nameField.clear();
            tokenCombo.setValue(null);
            colorCombo.setValue(AVAILABLE_COLORS.get(0));

            logger.info("Added new player: " + name);
        });

        Label nameLabel = new Label("Name:");
        nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        nameLabel.setTextFill(Color.web("#34495e"));
        nameLabel.setPrefWidth(80);

        Label tokenLabel = new Label("Token:");
        tokenLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        tokenLabel.setTextFill(Color.web("#34495e"));
        tokenLabel.setPrefWidth(80);

        Label colorLabel = new Label("Color:");
        colorLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        colorLabel.setTextFill(Color.web("#34495e"));
        colorLabel.setPrefWidth(80);

        HBox nameBox = new HBox(10, nameLabel, nameField);
        nameBox.setAlignment(Pos.CENTER_LEFT);

        HBox tokenBox = new HBox(10, tokenLabel, tokenCombo);
        tokenBox.setAlignment(Pos.CENTER_LEFT);

        HBox colorBox = new HBox(10, colorLabel, colorCombo);
        colorBox.setAlignment(Pos.CENTER_LEFT);

        HBox buttonBox = new HBox(10, randomizeBtn, addPlayerBtn);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        buttonBox.setPadding(new Insets(10, 0, 0, 0));

        form.getChildren().addAll(formTitle, nameBox, tokenBox, colorBox, buttonBox);

        return form;
    }

    /**
     * Creates the button bar at the bottom of the panel.
     *
     * @return HBox containing the buttons
     */
    private HBox createButtonBar() {
        HBox buttonBar = new HBox(15);
        buttonBar.setAlignment(Pos.CENTER_RIGHT);
        buttonBar.setPadding(new Insets(20));
        buttonBar.setStyle("-fx-background-color: #34495e;");

        Button loadPlayersBtn = new Button("Load Players");
        loadPlayersBtn.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 4px;");

        loadPlayersBtn.setOnMouseEntered(e -> loadPlayersBtn.setStyle("-fx-background-color: #e67e22; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 4px;"));
        loadPlayersBtn.setOnMouseExited(e -> loadPlayersBtn.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 4px;"));

        loadPlayersBtn.setOnAction(e -> loadPlayersFromCsv());

        Button savePlayersBtn = new Button("Save Players");
        savePlayersBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 4px;");

        savePlayersBtn.setOnMouseEntered(e -> savePlayersBtn.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 4px;"));
        savePlayersBtn.setOnMouseExited(e -> savePlayersBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 4px;"));

        savePlayersBtn.setOnAction(e -> savePlayersToCsv());

        Button cancelBtn = new Button("Back to Menu");
        cancelBtn.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 4px;");

        cancelBtn.setOnMouseEntered(e -> cancelBtn.setStyle("-fx-background-color: #7f8c8d; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 4px;"));
        cancelBtn.setOnMouseExited(e -> cancelBtn.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 4px;"));

        cancelBtn.setOnAction(e -> {
            if (onStartGameCallback != null) {
                onStartGameCallback.accept(false);
            }
        });

        Button startGameBtn = new Button("Start Game");
        startGameBtn.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-background-radius: 4px;");
        startGameBtn.setPrefWidth(150);

        startGameBtn.setOnMouseEntered(e -> startGameBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-background-radius: 4px;"));
        startGameBtn.setOnMouseExited(e -> startGameBtn.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-background-radius: 4px;"));

        startGameBtn.setOnAction(e -> {
            boolean result = prepareAndStartGame();
            if (result && onStartGameCallback != null) {
                onStartGameCallback.accept(true);
            }
        });

        buttonBar.getChildren().addAll(loadPlayersBtn, savePlayersBtn, cancelBtn, startGameBtn);

        return buttonBar;
    }
    /**
     * Method to load the token images from resources.
     */
    private void loadTokenImages() {
        logger.info("Loading token images");

        for (String token : AVAILABLE_TOKENS) {
            try {
                String imagePath = "/images/tokens/" + token.toLowerCase() + ".png";
                URL resource = getClass().getResource(imagePath);

                if (resource != null) {
                    Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream(imagePath)));
                    tokenImages.put(token, image);
                    logger.info("Loaded image for token: " + token);
                } else {
                    logger.warning("Image not found for token: " + token);
                }
            } catch (Exception e) {
                logger.warning("Failed to load image for token: " + token + " - " + e.getMessage());
            }
        }
    }

    /**
     * Loads existing players from the player controller.
     */
    private void loadExistingPlayers() {
        playerEntries.clear();

        for (Player player : playerController.getPlayers()) {
            playerEntries.add(new PlayerSelectionEntry(player));
        }

        logger.info("Loaded " + playerEntries.size() + " existing players");
    }

    /**
     * Prepares the game with selected players and starts it.
     *
     * @return True if the game was successfully prepared, false otherwise
     */
    private boolean prepareAndStartGame() {
        List<PlayerSelectionEntry> selectedEntries = playerEntries.stream()
                .filter(PlayerSelectionEntry::isSelected)
                .collect(Collectors.toList());

        if (selectedEntries.isEmpty()) {
            showAlert("No Players Selected",
                    "Please select at least one player to start the game.",
                    Alert.AlertType.WARNING);
            return false;
        }

        if (selectedEntries.size() > 5) {
            showAlert("Too Many Players",
                    "Please select a maximum of 5 players.",
                    Alert.AlertType.WARNING);
            return false;
        }

        selectedEntries.forEach(boardGameController::addPlayer);

        logger.info("Game prepared with " + selectedEntries.size() + " players");
        return true;
    }

    /**
     * Loads players from a CSV file using the FileService.
     */
    private void loadPlayersFromCsv() {
        logger.info("Loading players from CSV");

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Load Players from CSV");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV Files", "*.csv"));

        // Set initial directory to the default player directory
        try {
            File resourcesDir = fileService.getDefaultPlayerDirectory();
            if (resourcesDir.exists()) {
                fileChooser.setInitialDirectory(resourcesDir);
            }
        } catch (Exception e) {
            logger.warning("Could not set initial directory: " + e.getMessage());
        }

        File selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null) {
            try {
                // Use FileService to load players
                List<Player> loadedPlayers = fileService.loadPlayersFromFile(selectedFile);

                // Update the player controller with loaded players
                for (Player player : loadedPlayers) {
                    playerController.addPlayer(player);
                }

                // Clear existing entries and add entries for loaded players
                playerEntries.clear();
                for (Player player : loadedPlayers) {
                    playerEntries.add(new PlayerSelectionEntry(player));
                }

                // Refresh the player cards
                refreshPlayerCards();

                showAlert("Players Loaded",
                        "Successfully loaded " + loadedPlayers.size() + " players.",
                        Alert.AlertType.INFORMATION);

                logger.info("Loaded " + loadedPlayers.size() + " players from file: " + selectedFile.getAbsolutePath());
            } catch (IOException ex) {
                showAlert("Error Loading Players",
                        "Could not load players from file: " + ex.getMessage(),
                        Alert.AlertType.ERROR);

                logger.warning("Error loading players from file: " + ex.getMessage());
            }
        }
    }

    /**
     * Saves players to a CSV file using the FileService.
     */
    private void savePlayersToCsv() {
        logger.info("Saving players to CSV");

        // Collect all players from the current entries
        List<Player> currentPlayers = playerEntries.stream()
                .map(PlayerSelectionEntry::getPlayer)
                .collect(Collectors.toList());

        if (currentPlayers.isEmpty()) {
            showAlert("No Players",
                    "There are no players to save to the CSV file.",
                    Alert.AlertType.INFORMATION);
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Players to CSV");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        fileChooser.setInitialFileName("players.csv");

        // Set initial directory to default player directory
        try {
            File resourcesDir = fileService.getDefaultPlayerDirectory();
            if (resourcesDir.exists()) {
                fileChooser.setInitialDirectory(resourcesDir);
            }
        } catch (Exception e) {
            logger.warning("Could not set initial directory: " + e.getMessage());
        }

        File selectedFile = fileChooser.showSaveDialog(stage);
        if (selectedFile != null) {
            try {
                // Use FileService to save players
                fileService.savePlayersToFile(selectedFile, currentPlayers);

                showAlert("Players Saved",
                        "Successfully saved " + currentPlayers.size() + " players to " + selectedFile.getName(),
                        Alert.AlertType.INFORMATION);

                logger.info("Saved " + currentPlayers.size() + " players to file: " + selectedFile.getAbsolutePath());
            } catch (IOException e) {
                showAlert("Error Saving Players",
                        "Could not save players to file: " + e.getMessage(),
                        Alert.AlertType.ERROR);

                logger.warning("Error saving players to file: " + e.getMessage());
            }
        }
    }

    /**
     * Shows an alert dialog.
     *
     * @param title     The title of the alert
     * @param message   The message to display
     * @param alertType The type of alert
     */
    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle("-fx-background-color: white;");

        // Add different styles based on alert type
        if (alertType == Alert.AlertType.ERROR) {
            dialogPane.setStyle(dialogPane.getStyle() + "; -fx-header-color: #e74c3c;");
        } else if (alertType == Alert.AlertType.WARNING) {
            dialogPane.setStyle(dialogPane.getStyle() + "; -fx-header-color: #f39c12;");
        } else if (alertType == Alert.AlertType.INFORMATION) {
            dialogPane.setStyle(dialogPane.getStyle() + "; -fx-header-color: #3498db;");
        }

        alert.showAndWait();
    }
}