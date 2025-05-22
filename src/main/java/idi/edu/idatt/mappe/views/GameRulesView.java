
package idi.edu.idatt.mappe.views;

import idi.edu.idatt.mappe.controllers.GameRulesController;
import idi.edu.idatt.mappe.models.GameRules;
import idi.edu.idatt.mappe.models.enums.GameType;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * View for configuring game rules.
 * Displays board variants, dice options, and special rules for the selected game type.
 */
public class GameRulesView extends BorderPane {
    private static final Logger logger = Logger.getLogger(GameRulesView.class.getName());

    private GameRulesController controller;
    private final Stage stage;
    private Scene scene;

    private boolean updatingView = false;

    private boolean isJsonBoardLoaded = false;

    private ComboBox<String> boardVariantCombo;
    private Spinner<Integer> diceCountSpinner;
    private Spinner<Integer> diceSidesSpinner;
    private CheckBox extraThrowOnMaxCheck;
    private CheckBox startOnlyWithSixCheck;
    private CheckBox skipTurnOnSnakeCheck;
    private CheckBox extraTurnOnLadderCheck;
    private Label jsonBoardInfoLabel;
    private Label maxRollLabel;
    private VBox leftPanel;


    private VBox customBoardConfigPanel;
    private Spinner<Integer> snakesCountSpinner;
    private Spinner<Integer> laddersCountSpinner;
    private CheckBox includeRandomTeleportCheck;
    private CheckBox includeSkipTurnCheck;
    private CheckBox includeExtraTurnCheck;
    private Spinner<Integer> randomTeleportCountSpinner;
    private Spinner<Integer> skipTurnCountSpinner;
    private Spinner<Integer> extraTurnCountSpinner;

    private GameType currentDisplayedGameType;
    private VBox rightPanel;

    /**
     * Creates a new GameRulesView.
     *
     * @param gameType The type of game to display rules for
     * @param stage The stage that will display this view
     */
    public GameRulesView(GameType gameType, Stage stage) {
        this.stage = stage;
        this.currentDisplayedGameType = gameType; // Store the initial game type

        getStyleClass().add("game-rules-view");
        setStyle("-fx-background-color: linear-gradient(to bottom, #f5f7fa, #e5e9f0);");

        createHeader(gameType);
        createContent();
        createFooter();

        scene = new Scene(this, 900, 700);

        logger.info("GameRulesView initialized for game type: " + gameType);
    }

    /**
     * Create the header section of the view.
     *
     * @param gameType The game type being configured
     */
    private void createHeader(GameType gameType) {
        VBox header = new VBox(10);
        header.setAlignment(Pos.CENTER);
        header.setPadding(new Insets(25, 0, 25, 0));
        header.setStyle("-fx-background-color: #34495e; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0, 0, 3);");

        Label titleLabel = new Label("Game Rules Configuration");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        titleLabel.setTextFill(Color.WHITE);

        String subtitleText = "Customize your " + gameType.toString().replace("_", " ") + " game";
        Label subtitleLabel = new Label(subtitleText);
        subtitleLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
        subtitleLabel.setTextFill(Color.web("#ecf0f1"));

        header.getChildren().addAll(titleLabel, subtitleLabel);
        setTop(header);
    }

    /**
     * Create the main content of the view.
     */
    private void createContent() {
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setPannable(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");

        HBox content = new HBox(20);
        content.setPadding(new Insets(25));
        content.setAlignment(Pos.CENTER);

        leftPanel = createBoardSelectionPanel();
        rightPanel = createRulesConfigPanel();

        content.getChildren().addAll(leftPanel, rightPanel);
        scrollPane.setContent(content);
        setCenter(content);
    }

    /**
     * Create the board selection panel.
     *
     * @return VBox containing the board selection components
     */
    private VBox createBoardSelectionPanel() {
        VBox panel = new VBox(15);
        panel.getStyleClass().add("panel");
        panel.setPadding(new Insets(20));
        panel.setAlignment(Pos.TOP_CENTER);
        panel.setStyle("-fx-background-color: white; -fx-background-radius: 8px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 3);");
        panel.setPrefWidth(400);

        Label titleLabel = new Label("Board Selection");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        titleLabel.setTextFill(Color.web("#2c3e50"));

        jsonBoardInfoLabel = new Label();
        jsonBoardInfoLabel.setStyle(
                "-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2980b9; " +
                        "-fx-background-color: #e8f4fc; -fx-padding: 10; -fx-background-radius: 5; " +
                        "-fx-border-color: #3498db; -fx-border-width: 1; -fx-border-radius: 5;"
        );
        jsonBoardInfoLabel.setWrapText(true);
        jsonBoardInfoLabel.setTextAlignment(TextAlignment.CENTER);
        jsonBoardInfoLabel.setPrefWidth(350);
        jsonBoardInfoLabel.setVisible(false);

        boardVariantCombo = new ComboBox<>();
        boardVariantCombo.setPrefWidth(350);
        boardVariantCombo.setPromptText("Select a board variant");

        boardVariantCombo.setOnAction(e -> {
            if (updatingView) return;

            String selectedVariant = boardVariantCombo.getValue();
            if (selectedVariant != null && controller != null) {
                controller.selectVariantWithoutViewUpdate(selectedVariant);

                if (selectedVariant.equals("Custom Board")) {
                    customBoardConfigPanel.setVisible(true);
                } else {
                    customBoardConfigPanel.setVisible(false);
                }
            }
        });


        customBoardConfigPanel = createCustomBoardConfigPanel();


        panel.getChildren().addAll(titleLabel, jsonBoardInfoLabel, boardVariantCombo,
                customBoardConfigPanel);

        addLoadFromFileButton(panel);
        return panel;
    }

    /**
     * Create the rules configuration panel.
     *
     * @return VBox containing the rules configuration components
     */
    private VBox createRulesConfigPanel() {
        VBox panel = new VBox(15);
        panel.getStyleClass().add("panel");
        panel.setPadding(new Insets(20));
        panel.setAlignment(Pos.TOP_CENTER);
        panel.setStyle("-fx-background-color: white; -fx-background-radius: 8px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 3);");
        panel.setPrefWidth(400);

        Label titleLabel = new Label("Game Rules Configuration");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        titleLabel.setTextFill(Color.web("#2c3e50"));

        TitledPane specialRulesPane = createSpecialRulesPanel();

        panel.getChildren().addAll(titleLabel, specialRulesPane);

        return panel;
    }

    /**
     * Create the dice configuration panel.
     *
     * @return TitledPane containing the dice configuration components
     */
    private TitledPane createDiceConfigPanel() {
        VBox diceConfig = new VBox(10);
        diceConfig.setPadding(new Insets(15, 10, 15, 10));

        HBox diceCountBox = new HBox(10);
        diceCountBox.setAlignment(Pos.CENTER_LEFT);

        Label diceCountLabel = new Label("Number of Dice:");
        diceCountLabel.setPrefWidth(150);

        SpinnerValueFactory<Integer> diceCountFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 5, 2);
        diceCountSpinner = new Spinner<>(diceCountFactory);
        diceCountSpinner.setEditable(true);
        diceCountSpinner.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (controller != null && !updatingView) {
                GameRules rules = controller.getCurrentRules();
                rules.setNumberOfDice(newVal);
                updateMaxRollLabel();
            }
        });

        diceCountBox.getChildren().addAll(diceCountLabel, diceCountSpinner);

        HBox diceSidesBox = new HBox(10);
        diceSidesBox.setAlignment(Pos.CENTER_LEFT);

        Label diceSidesLabel = new Label("Number of Sides:");
        diceSidesLabel.setPrefWidth(150);

        SpinnerValueFactory<Integer> diceSidesFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(4, 20, 6);
        diceSidesSpinner = new Spinner<>(diceSidesFactory);
        diceSidesSpinner.setEditable(true);
        diceSidesSpinner.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (controller != null && !updatingView) {
                GameRules rules = controller.getCurrentRules();
                rules.setDiceSides(newVal);
                updateMaxRollLabel();
            }
        });

        diceSidesBox.getChildren().addAll(diceSidesLabel, diceSidesSpinner);

        HBox maxRollBox = new HBox(10);
        maxRollBox.setAlignment(Pos.CENTER_LEFT);
        maxRollBox.setPadding(new Insets(5, 0, 0, 0));

        Label maxRollTextLabel = new Label("Maximum Roll:");
        maxRollTextLabel.setPrefWidth(150);

        maxRollLabel = new Label("12");
        maxRollLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        maxRollLabel.setTextFill(Color.web("#e74c3c"));

        maxRollBox.getChildren().addAll(maxRollTextLabel, maxRollLabel);

        diceConfig.getChildren().addAll(diceCountBox, diceSidesBox, maxRollBox);

        TitledPane dicePane = new TitledPane("Dice Configuration", diceConfig);
        dicePane.setExpanded(true);

        return dicePane;
    }

    /**
     * Create the custom board configuration panel.
     *
     * @return VBox containing the custom board configuration components
     */
    private VBox createCustomBoardConfigPanel() {
        VBox panel = new VBox(15);
        panel.setPadding(new Insets(15));
        panel.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #cccccc; -fx-border-radius: 5;");
        panel.setVisible(false);

        Label titleLabel = new Label("Custom Board Configuration");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        titleLabel.setTextFill(Color.web("#34495e"));

        HBox snakesBox = new HBox(10);
        snakesBox.setAlignment(Pos.CENTER_LEFT);

        Label snakesLabel = new Label("Number of Snakes:");
        snakesLabel.setPrefWidth(150);

        SpinnerValueFactory<Integer> snakesFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 20, 8);
        snakesCountSpinner = new Spinner<>(snakesFactory);
        snakesCountSpinner.setEditable(true);
        snakesCountSpinner.setPrefWidth(100);

        snakesBox.getChildren().addAll(snakesLabel, snakesCountSpinner);

        HBox laddersBox = new HBox(10);
        laddersBox.setAlignment(Pos.CENTER_LEFT);

        Label laddersLabel = new Label("Number of Ladders:");
        laddersLabel.setPrefWidth(150);

        SpinnerValueFactory<Integer> laddersFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 20, 8);
        laddersCountSpinner = new Spinner<>(laddersFactory);
        laddersCountSpinner.setEditable(true);
        laddersCountSpinner.setPrefWidth(100);

        laddersBox.getChildren().addAll(laddersLabel, laddersCountSpinner);

        TitledPane otherActionsPane = createOtherTileActionsPane();

        Text descriptionText = new Text("Configure the number of snakes and ladders on the board. \n" +
                "You can also add special tiles like random teleports, skip turns, and extra turns.");

        panel.getChildren().addAll(
                titleLabel,
                new Separator(),
                snakesBox,
                laddersBox,
                otherActionsPane,
                descriptionText
        );

        return panel;
    }

    private void addLoadFromFileButton(VBox panel) {
        Button loadFromFileButton = new Button("Load Rules From File");
        loadFromFileButton.setPrefWidth(350);
        loadFromFileButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; " +
                "-fx-font-weight: bold; -fx-padding: 10px;");

        loadFromFileButton.setOnAction(e -> {
            if (controller != null) {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Load Game Rules");
                fileChooser.getExtensionFilters().add(
                        new FileChooser.ExtensionFilter("JSON Files", "*.json"));

                File rulesDirectory = controller.getDefaultRulesDirectory();
                if (rulesDirectory.exists()) {
                    fileChooser.setInitialDirectory(rulesDirectory);
                }

                File selectedFile = fileChooser.showOpenDialog(getScene().getWindow());
                if (selectedFile != null) {
                    loadRulesFromFile(selectedFile);
                }
            }
        });

        if (currentDisplayedGameType == GameType.THE_LOST_DIAMOND) {
            loadFromFileButton.setDisable(true);
        } else {
            loadFromFileButton.setDisable(false);
        }

        panel.getChildren().add(loadFromFileButton);
    }

    /**
     * Loads game rules from a JSON file and updates the UI
     *
     * @param file The JSON file containing game rules
     */
    private void loadRulesFromFile(File file) {
        try {
            if (controller != null) {
                GameRules loadedRules = controller.loadRulesFromFile(file);
                if (loadedRules != null) {
                    updatingView = true;
                    try {
                        controller.setCurrentRules(loadedRules);
                    } finally {
                        updatingView = false;
                    }
                }
            }
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Error loading rules from file", ex);
            showError("Loading Error", "Failed to load rules: " + ex.getMessage());
        }
    }


    /**
     * Create the other tile actions panel.
     *
     * @return TitledPane containing the other tile actions components
     */
    private TitledPane createOtherTileActionsPane() {
        VBox content = new VBox(10);
        content.setPadding(new Insets(10));

        HBox teleportBox = new HBox(10);
        teleportBox.setAlignment(Pos.CENTER_LEFT);

        includeRandomTeleportCheck = new CheckBox("Include Random Teleports");

        SpinnerValueFactory<Integer> teleportFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 10, 0);
        randomTeleportCountSpinner = new Spinner<>(teleportFactory);
        randomTeleportCountSpinner.setEditable(true);
        randomTeleportCountSpinner.setPrefWidth(70);
        randomTeleportCountSpinner.setDisable(true);

        includeRandomTeleportCheck.selectedProperty().addListener((obs, oldVal, newVal) -> {
            randomTeleportCountSpinner.setDisable(!newVal);
            if (!newVal) randomTeleportCountSpinner.getValueFactory().setValue(0);
        });

        teleportBox.getChildren().addAll(includeRandomTeleportCheck, randomTeleportCountSpinner);

        HBox skipTurnBox = new HBox(10);
        skipTurnBox.setAlignment(Pos.CENTER_LEFT);

        includeSkipTurnCheck = new CheckBox("Include Skip Turn Tiles");

        SpinnerValueFactory<Integer> skipTurnFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 10, 0);
        skipTurnCountSpinner = new Spinner<>(skipTurnFactory);
        skipTurnCountSpinner.setEditable(true);
        skipTurnCountSpinner.setPrefWidth(70);
        skipTurnCountSpinner.setDisable(true);

        includeSkipTurnCheck.selectedProperty().addListener((obs, oldVal, newVal) -> {
            skipTurnCountSpinner.setDisable(!newVal);
            if (!newVal) skipTurnCountSpinner.getValueFactory().setValue(0);
        });

        skipTurnBox.getChildren().addAll(includeSkipTurnCheck, skipTurnCountSpinner);

        HBox extraTurnBox = new HBox(10);
        extraTurnBox.setAlignment(Pos.CENTER_LEFT);

        includeExtraTurnCheck = new CheckBox("Include Extra Turn Tiles");

        SpinnerValueFactory<Integer> extraTurnFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 10, 0);
        extraTurnCountSpinner = new Spinner<>(extraTurnFactory);
        extraTurnCountSpinner.setEditable(true);
        extraTurnCountSpinner.setPrefWidth(70);
        extraTurnCountSpinner.setDisable(true);

        includeExtraTurnCheck.selectedProperty().addListener((obs, oldVal, newVal) -> {
            extraTurnCountSpinner.setDisable(!newVal);
            if (!newVal) extraTurnCountSpinner.getValueFactory().setValue(0);
        });

        extraTurnBox.getChildren().addAll(includeExtraTurnCheck, extraTurnCountSpinner);

        content.getChildren().addAll(teleportBox, skipTurnBox, extraTurnBox);

        TitledPane pane = new TitledPane("Other Tile Actions", content);
        pane.setExpanded(false);

        return pane;
    }



    /**
     * Create the special rules panel.
     *
     * @return TitledPane containing the special rules components
     */
    private TitledPane createSpecialRulesPanel() {
        VBox ruleOptionsPanel = new VBox(10);
        ruleOptionsPanel.setPadding(new Insets(15, 10, 15, 10));

        if (currentDisplayedGameType == GameType.THE_LOST_DIAMOND) {
            Label noCustomRulesLabel = new Label("No custom rules available for The Lost Diamond yet.");
            noCustomRulesLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
            ruleOptionsPanel.getChildren().addAll(
                    noCustomRulesLabel
            );
            logger.info("No custom rules for The Lost Diamond yet.");
        } else {
            createSnakesAndLaddersSpecialRules(ruleOptionsPanel);
        }

        TitledPane specialRulesPane = new TitledPane("Special Rules", ruleOptionsPanel);
        specialRulesPane.setExpanded(true);

        return specialRulesPane;
    }

    /**
     * Creates special rules specific to Snakes and Ladders
     */
    private void createSnakesAndLaddersSpecialRules(VBox container) {
        TitledPane dicePane = createDiceConfigPanel();

        extraThrowOnMaxCheck = new CheckBox("Extra Turn on Maximum Roll");
        extraThrowOnMaxCheck.setSelected(false);
        extraThrowOnMaxCheck.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (controller != null && !updatingView) {
                controller.getCurrentRules().setExtraThrowOnMax(newVal);
            }
        });

        startOnlyWithSixCheck = new CheckBox("Can Only Start with Maximum Roll");
        startOnlyWithSixCheck.setSelected(false);
        startOnlyWithSixCheck.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (controller != null && !updatingView) {
                controller.getCurrentRules().setStartOnlyWithMax(newVal);
            }
        });

        skipTurnOnSnakeCheck = new CheckBox("Skip Turn When Landing on Snake");
        skipTurnOnSnakeCheck.setSelected(false);
        skipTurnOnSnakeCheck.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (controller != null && !updatingView) {
                controller.getCurrentRules().setSkipTurnOnSnake(newVal);
            }
        });

        extraTurnOnLadderCheck = new CheckBox("Extra Turn When Climbing Ladder");
        extraTurnOnLadderCheck.setSelected(false);
        extraTurnOnLadderCheck.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (controller != null && !updatingView) {
                controller.getCurrentRules().setExtraTurnOnLadder(newVal);
            }
        });

        container.getChildren().addAll(
                dicePane,
                extraThrowOnMaxCheck,
                startOnlyWithSixCheck,
                skipTurnOnSnakeCheck,
                extraTurnOnLadderCheck
        );
    }



    /**
     * Create the footer section of the view.
     */
    private void createFooter() {
        HBox footer = new HBox();
        footer.setPadding(new Insets(20));
        footer.setAlignment(Pos.CENTER_RIGHT);
        footer.setSpacing(15);
        footer.setStyle("-fx-background-color: #34495e;");

        Button backButton = new Button("Back");
        backButton.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-font-weight: bold;");
        backButton.setOnAction(e -> {
            logger.info("Back button clicked");
            if (controller != null) {
                controller.cancelRules();
            }
        });
        Button saveRulesButton = new Button("Save Rules");
        saveRulesButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold;");
        saveRulesButton.setOnAction(e -> saveRulesToFile());
        if (currentDisplayedGameType == GameType.THE_LOST_DIAMOND) {
            saveRulesButton.setDisable(true);
        } else {
            saveRulesButton.setDisable(false);

        }

        Button applyRulesButton = new Button("Apply Rules");
        applyRulesButton.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");
        applyRulesButton.setPrefWidth(150);
        applyRulesButton.setOnAction(e -> {
            logger.info("Apply Rules button clicked");
            if (controller != null) {
                controller.applyRules();
            }
        });

        footer.getChildren().addAll(backButton, saveRulesButton, applyRulesButton);
        setBottom(footer);
    }

    /**
     * Sets the board loaded from JSON and updates the UI to display it.
     *
     * @param boardName The name of the loaded board
     * @param boardDescription The description of the loaded board
     */
    public void setJsonLoadedBoard(String boardName, String boardDescription) {
        logger.info("Setting JSON loaded board: " + boardName);

        isJsonBoardLoaded = true;

        jsonBoardInfoLabel.setText("Custom Board Loaded: " + boardName);
        jsonBoardInfoLabel.setVisible(true);

        boardVariantCombo.setDisable(true);
        boardVariantCombo.setStyle("-fx-opacity: 0.7; -fx-background-color: #e0e0e0;");

        leftPanel.requestLayout();
    }


    /**
     * Sets the controller for this view.
     *
     * @param controller The controller to set
     */
    public void setController(GameRulesController controller) {
        this.controller = controller;

        Platform.runLater(this::updateView);
    }

    /**
     * Updates the view based on the current rules in the controller.
     */
    public void updateView() {
        if (controller == null) return;

        try {
            updatingView = true;

            GameRules rules = controller.getCurrentRules();

            GameType newGameType = rules.getGameType();
            if (newGameType != currentDisplayedGameType) {
                currentDisplayedGameType = newGameType;
                Platform.runLater(this::recreateRulesPanel);
                return;
            }

            if (!isJsonBoardLoaded) {
                List<String> variants = controller.getAvailableBoardVariants();

                boardVariantCombo.getItems().clear();
                if (variants != null && !variants.isEmpty()) {
                    boardVariantCombo.getItems().addAll(variants);
                }

                variants.stream()
                        .filter(variant -> variant.equals(rules.getRuleName()) ||
                                variant.contains(rules.getBoardVariant()))
                        .findFirst()
                        .ifPresent(boardVariantCombo::setValue);
            }

            if (diceCountSpinner != null && diceCountSpinner.getValue() != rules.getNumberOfDice()) {
                diceCountSpinner.getValueFactory().setValue(rules.getNumberOfDice());
            }

            if (diceSidesSpinner != null && diceSidesSpinner.getValue() != rules.getDiceSides()) {
                diceSidesSpinner.getValueFactory().setValue(rules.getDiceSides());
            }

            updateMaxRollLabel();

            if (currentDisplayedGameType == GameType.THE_LOST_DIAMOND) {
                logger.info("Updating Lost Diamond rules");
            } else {
                updateSnakesAndLaddersSpecialRules();
            }

            logger.info("View updated with current rules: " + rules.getRuleName());
        } finally {
            updatingView = false;
        }
    }

    private void recreateRulesPanel() {
        if (rightPanel != null) {
            VBox newRightPanel = createRulesConfigPanel();

            HBox content = (HBox) ((ScrollPane) getCenter()).getContent();
            content.getChildren().remove(rightPanel);
            content.getChildren().add(newRightPanel);
            rightPanel = newRightPanel;

            logger.info("Recreated rules panel for game type: " + currentDisplayedGameType);
        }
    }


    /**
     * Updates the Snakes and Ladders special rules in the UI
     */
    private void updateSnakesAndLaddersSpecialRules() {
        if (updatingView || controller == null) return;

        GameRules rules = controller.getCurrentRules();

        if (extraThrowOnMaxCheck != null) {
            extraThrowOnMaxCheck.setSelected(rules.isExtraThrowOnMax());
        }
        if (startOnlyWithSixCheck != null) {
            startOnlyWithSixCheck.setSelected(rules.isStartOnlyWithMax());
        }
        if (skipTurnOnSnakeCheck != null) {
            skipTurnOnSnakeCheck.setSelected(rules.isSkipTurnOnSnake());
        }
        if (extraTurnOnLadderCheck != null) {
            extraTurnOnLadderCheck.setSelected(rules.isExtraTurnOnLadder());
        }
    }


    /**
     * Updates the maximum roll label.
     */
    private void updateMaxRollLabel() {
        if (maxRollLabel == null) return;
        if (controller != null) {
            GameRules rules = controller.getCurrentRules();
            int maxRoll = rules.getMaxRoll();
            maxRollLabel.setText(String.valueOf(maxRoll));
        }
    }
    /**
     * Shows a dialog for entering a filename to save rules.
     *
     * @return The filename entered by the user, or null if cancelled
     */
    public String showSaveFileDialog() {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Save Game Rules");
        dialog.setHeaderText("Enter a name for your game rules");

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField filenameField = new TextField();
        filenameField.setPromptText("ruleset_name");

        grid.add(new Label("Filename:"), 0, 0);
        grid.add(filenameField, 1, 0);

        Label extensionLabel = new Label(".json");
        extensionLabel.setStyle("-fx-text-fill: #666666;");
        grid.add(extensionLabel, 2, 0);
        Node saveButton = dialog.getDialogPane().lookupButton(saveButtonType);
        saveButton.setDisable(true);

        filenameField.textProperty().addListener((observable, oldValue, newValue) -> {
            boolean valid = newValue != null && !newValue.trim().isEmpty() &&
                    newValue.matches("[a-zA-Z0-9_\\-]+");
            saveButton.setDisable(!valid);

            if (!valid && newValue != null && !newValue.isEmpty()) {
                filenameField.setStyle("-fx-text-box-border: red; -fx-focus-color: red;");
            } else {
                filenameField.setStyle("");
            }
        });

        dialog.getDialogPane().setContent(grid);

        Platform.runLater(filenameField::requestFocus);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                return filenameField.getText();
            }
            return null;
        });

        Optional<String> result = dialog.showAndWait();
        return result.orElse(null);
    }

    /**
     * Shows a confirmation dialog for overwriting an existing file.
     *
     * @param filename The name of the file to overwrite
     * @return true if the user confirms overwriting, false otherwise
     */
    public boolean confirmOverwrite(String filename) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Overwrite");
        alert.setHeaderText("File already exists");
        alert.setContentText("The file '" + filename + "' already exists. Do you want to overwrite it?");

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }


    /**
     * Saves the current rules to a file.
     */
    private void saveRulesToFile() {
        logger.info("Saving rules to file");

        if (controller != null) {
            String filename = showSaveFileDialog();
            if (filename != null && !filename.isEmpty()) {
                controller.saveRulesToFile(filename);
            }
        }
    }

    /**
     * Shows an error message.
     *
     * @param title The title of the error dialog
     * @param message The error message
     */
    public void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Shows an information message.
     *
     * @param title The title of the information dialog
     * @param message The information message
     */
    public void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Gets the configured number of snakes for a custom board
     *
     * @return The number of snakes
     */
    public int getSnakeCount() {
        return snakesCountSpinner != null ? snakesCountSpinner.getValue() : 8;
    }

    /**
     * Gets the configured number of ladders for a custom board
     *
     * @return The number of ladders
     */
    public int getLadderCount() {
        return laddersCountSpinner != null ? laddersCountSpinner.getValue() : 8;
    }

    /**
     * Gets the configured number of random teleport tiles for a custom board
     *
     * @return The number of random teleport tiles
     */
    public int getRandomTeleportCount() {
        if (includeRandomTeleportCheck == null || !includeRandomTeleportCheck.isSelected()) return 0;
        return randomTeleportCountSpinner != null ? randomTeleportCountSpinner.getValue() : 0;
    }

    /**
     * Gets the configured number of skip turn tiles for a custom board
     *
     * @return The number of skip turn tiles
     */
    public int getSkipTurnCount() {
        if (includeSkipTurnCheck == null || !includeSkipTurnCheck.isSelected()) return 0;
        return skipTurnCountSpinner != null ? skipTurnCountSpinner.getValue() : 0;
    }

    /**
     * Gets the configured number of extra turn tiles for a custom board
     *
     * @return The number of extra turn tiles
     */
    public int getExtraTurnCount() {
        if (includeExtraTurnCheck == null || !includeExtraTurnCheck.isSelected()) return 0;
        return extraTurnCountSpinner != null ? extraTurnCountSpinner.getValue() : 0;
    }

}