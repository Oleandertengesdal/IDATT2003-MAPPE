package idi.edu.idatt.mappe.views.game;

import idi.edu.idatt.mappe.models.PlayerSelectionEntry;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.Logger;

import static java.util.logging.Logger.getLogger;

/**
 * The class represents the players in the PlayerSelctionView.
 * The players are stacked, with information about their name, token, color.
 * It also has a delete button to remove the player from the list.
 * It also has a checkbox to select the player.
 */
public class PlayerCardView extends HBox {
    private static final List<String> AVAILABLE_TOKENS = List.of(
            "Star", "Car", "Hat", "Dog", "Cat", "Bishop", "Burger",
            "Chef", "Controller", "Graduation", "Knight", "Shoe", "Skate",
            "Skateboard", "Wizard", "none"
    );

    private static final List<String> AVAILABLE_COLORS = List.of(
            "#e74c3c", "#3498db", "#2ecc71", "#f1c40f", "#9b59b6",
            "#e67e22", "#1abc9c", "#34495e", "#d35400", "#16a085"
    );

    private final PlayerSelectionEntry entry;
    private final Map<String, Image> tokenImages;
    private final BooleanProperty selected = new SimpleBooleanProperty(false);
    private final Consumer<PlayerCardView> onDeleteCallback;


    private ImageView tokenImageView;
    private Circle colorCircle;

    private static final Logger logger = getLogger(PlayerCardView.class.getName());

    /**
     * Creates a new player row.
     *
     * @param entry The player entry to represent
     * @param tokenImages Map of token images
     * @param onDeleteCallback Callback when delete is requested
     */
    public PlayerCardView(PlayerSelectionEntry entry, Map<String, Image> tokenImages, Consumer<PlayerCardView> onDeleteCallback) {
        this.entry = entry;
        this.tokenImages = tokenImages;
        this.onDeleteCallback = onDeleteCallback;

        this.selected.set(entry.isSelected());
        this.selected.addListener((obs, oldVal, newVal) -> entry.setSelected(newVal));

        entry.selectedProperty().bindBidirectional(selected);

        setupLayout();
    }

    /**
     * Sets up the row layout with horizontal arrangement of elements.
     */
    private void setupLayout() {
        this.setSpacing(15);
        this.setPadding(new Insets(12));
        this.setStyle("-fx-background-color: white; -fx-border-color: #e1e1e1; -fx-border-width: 0 0 1 0;");
        this.setPrefHeight(60);
        this.setMinHeight(60);
        this.setAlignment(Pos.CENTER_LEFT);

        this.setOnMouseEntered(e -> this.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #e1e1e1; -fx-border-width: 0 0 1 0;"));

        this.setOnMouseExited(e -> this.setStyle("-fx-background-color: white; -fx-border-color: #e1e1e1; -fx-border-width: 0 0 1 0;"));

        createSelectionCheckbox();
        createTokenAvatar();
        createNameLabel();
        createTokenSelector();
        createColorSelector();
        createDeleteButton();

        this.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(this, Priority.ALWAYS);
    }

    /**
     * Creates the selection checkbox.
     */
    private void createSelectionCheckbox() {
        CheckBox selectCheckBox;
        selectCheckBox = new CheckBox();
        selectCheckBox.selectedProperty().bindBidirectional(selected);
        selectCheckBox.setPadding(new Insets(0, 5, 0, 5));

        selected.addListener((obs, oldVal, newVal) -> {
            if (Boolean.TRUE.equals(newVal)) {
                this.setStyle("-fx-background-color: #8fddaa; -fx-border-color: #a2ddb9; -fx-border-width: 0 0 1 0;");
            } else {
                this.setStyle("-fx-background-color: #f1aeae; -fx-border-color: #c6a0a0; -fx-border-width: 0 0 1 0;");
            }
        });

        if (selected.get()) {
            this.setStyle("-fx-background-color: #eaf2f8; -fx-border-color: #e1e1e1; -fx-border-width: 0 0 1 0;");
        }

        this.getChildren().add(selectCheckBox);
    }

    /**
     * Creates the token avatar display.
     */
    private void createTokenAvatar() {
        StackPane avatarPane = new StackPane();
        avatarPane.setPrefSize(40, 40);
        avatarPane.setMinSize(40, 40);

        colorCircle = new Circle(20);
        colorCircle.setFill(Color.web(entry.getColor()));
        colorCircle.setStroke(Color.web("#bdc3c7"));
        colorCircle.setStrokeWidth(1);

        tokenImageView = new ImageView();
        tokenImageView.setFitHeight(25);
        tokenImageView.setFitWidth(25);
        tokenImageView.setPreserveRatio(true);

        updateTokenImage();

        avatarPane.getChildren().addAll(colorCircle, tokenImageView);
        this.getChildren().add(avatarPane);
    }

    /**
     * Creates the player name label.
     */
    private void createNameLabel() {
        Label nameLabel = new Label(entry.getName());
        nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        nameLabel.setTextFill(Color.web("#2c3e50"));
        nameLabel.setMaxWidth(Double.MAX_VALUE);
        nameLabel.setPrefWidth(400);
        nameLabel.setMinWidth(120);


        this.getChildren().add(nameLabel);
    }

    /**
     * Creates the token selection dropdown.
     */
    private void createTokenSelector() {
        ComboBox<String> tokenCombo;
        tokenCombo = new ComboBox<>();
        tokenCombo.getItems().addAll(AVAILABLE_TOKENS);
        tokenCombo.setValue(entry.getToken());
        tokenCombo.setPrefWidth(120);
        tokenCombo.setPromptText("Select Token");

        tokenCombo.setCellFactory(listView -> new ListCell<>() {
            private final ImageView imageView = new ImageView();

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Image tokenImage = tokenImages.get(item);
                    if (tokenImage != null) {
                        imageView.setImage(tokenImage);
                        imageView.setFitHeight(16);
                        imageView.setFitWidth(16);
                        setGraphic(new HBox(5, imageView, new Label(item)));
                    } else {
                        setText(item);
                    }
                }
            }
        });

        tokenCombo.setOnAction(e -> {
            String selectedToken = tokenCombo.getValue();
            entry.setToken(selectedToken);
            updateTokenImage();
        });

        this.getChildren().add(tokenCombo);
    }

    /**
     * Creates the color selection dropdown.
     */
    private void createColorSelector() {
        ComboBox<String> colorCombo;
        colorCombo = new ComboBox<>();
        colorCombo.getItems().addAll(AVAILABLE_COLORS);
        colorCombo.setValue(entry.getColor());
        colorCombo.setPrefWidth(120);
        colorCombo.setPromptText("Select Color");

        colorCombo.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(String color, boolean empty) {
                super.updateItem(color, empty);
                if (empty || color == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Circle circle = new Circle(8);
                    circle.setFill(Color.web(color));
                    circle.setStroke(Color.BLACK);
                    circle.setStrokeWidth(0.5);
                    setGraphic(circle);
                }
            }
        });

        colorCombo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(String color, boolean empty) {
                super.updateItem(color, empty);
                if (empty || color == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Circle circle = new Circle(8);
                    circle.setFill(Color.web(color));
                    circle.setStroke(Color.BLACK);
                    circle.setStrokeWidth(0.5);
                    setGraphic(circle);
                }
            }
        });

        colorCombo.setOnAction(e -> {
            String selectedColor = colorCombo.getValue();
            entry.setColor(selectedColor);
            colorCircle.setFill(Color.web(selectedColor));
        });

        this.getChildren().add(colorCombo);
    }

    /**
     * Creates the delete button.
     */
    private void createDeleteButton() {
        Button deleteButton = new Button("×");
        deleteButton.setStyle(
                "-fx-background-color: transparent; " +
                        "-fx-text-fill: #e74c3c; " +
                        "-fx-font-weight: bold; " +
                        "-fx-font-size: 16px;"
        );

        deleteButton.setOnMouseEntered(e ->
                deleteButton.setStyle(
                        "-fx-background-color: #e74c3c; " +
                                "-fx-text-fill: white; " +
                                "-fx-font-weight: bold; " +
                                "-fx-font-size: 16px;" +
                                "-fx-background-radius: 2px;"
                )
        );

        deleteButton.setOnMouseExited(e ->
                deleteButton.setStyle(
                        "-fx-background-color: transparent; " +
                                "-fx-text-fill: #e74c3c; " +
                                "-fx-font-weight: bold; " +
                                "-fx-font-size: 16px;"
                )
        );

        deleteButton.setOnAction(e -> {
            if (onDeleteCallback != null) {
                onDeleteCallback.accept(this);
            }
        });

        this.getChildren().add(deleteButton);
    }

    /**
     * Updates the token image based on current token.
     */
    private void updateTokenImage() {
        String token = entry.getToken().substring(0, 1).toUpperCase() + entry.getToken().substring(1).toLowerCase();
        Image tokenImage = tokenImages.get(token);

        if (tokenImage != null) {
            tokenImageView.setImage(tokenImage);
        } else {
            logger.warning("Token image not found for token: " + token + ". Using default image.");
            tokenImageView.setImage(tokenImages.get("cat"));
        }
    }

    /**
     * Gets the player entry represented by this row.
     *
     * @return The player entry
     */
    public PlayerSelectionEntry getEntry() {
        return entry;
    }

}