package idi.edu.idatt.mappe.views.game;

import idi.edu.idatt.mappe.models.*;
import idi.edu.idatt.mappe.models.enums.Direction;
import idi.edu.idatt.mappe.models.enums.TokenType;
import idi.edu.idatt.mappe.views.GameView;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.List;
import java.util.Optional;

/**
 * Dialog for The Lost Diamond game interactions.
 * Handles destination selection and token reveal animations.
 */
public class LostDiamondDialog extends Alert {

    private Tile selectedDestination;

    /**
     * Result class to hold the selected destination
     */
    public static class Result {
        private final Tile destinationTile;

        public Result(Tile destinationTile) {
            this.destinationTile = destinationTile;
        }

        public Tile getDestinationTile() {
            return destinationTile;
        }
    }

    /**
     * Creates a dialog for selecting a destination.
     *
     * @param gameView The game view
     * @param player The player who is moving
     * @param currentTile The current tile
     * @param affordableDestinations The destinations the player can afford to travel to
     */
    public LostDiamondDialog(GameView gameView, Player player, Tile currentTile,
                             List<Tile> affordableDestinations) {
        super(AlertType.CONFIRMATION);

        setTitle("Select Destination");
        setHeaderText(player.getName() + ", select where to move from " + currentTile.getName());

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        Label moneyLabel = new Label("Your money: " + player.getMoney() + " coins");
        moneyLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        grid.add(moneyLabel, 0, 0, 2, 1);

        int row = 1;
        for (Tile destination : affordableDestinations) {
            Label nameLabel = new Label(destination.getName());

            Direction direction = null;
            for (Direction dir : currentTile.getAvailableDirections()) {
                if (currentTile.getConnectionInDirection(dir) == destination) {
                    direction = dir;
                    break;
                }
            }

            int cost = (direction != null) ? currentTile.getTravelCost(direction) : 0;
            Label costLabel = new Label(cost + " coins");

            if (cost > 0) {
                costLabel.setTextFill(Color.RED);
            } else {
                costLabel.setTextFill(Color.GREEN);
                costLabel.setText("Free (land route)");
            }

            Button selectButton = new Button("Select");
            selectButton.setOnAction(e -> {
                selectedDestination = destination;
                setResult(ButtonType.OK);
                close();
            });

            grid.add(nameLabel, 0, row);
            grid.add(costLabel, 1, row);
            grid.add(selectButton, 2, row);
            row++;
        }

        getDialogPane().setContent(grid);
        getButtonTypes().setAll(ButtonType.CANCEL);
    }

    /**
     * Gets the result with the selected destination.
     *
     * @return Result containing the selected destination
     */
    public Optional<Result> showDialogAndWait() {
        Optional<ButtonType> buttonResult = super.showAndWait();

        if (buttonResult.isPresent() && buttonResult.get() == ButtonType.OK) {
            return Optional.of(new Result(selectedDestination));
        } else {
            return Optional.of(new Result(null));
        }
    }

    /**
     * Shows a dialog with the result of revealing a token.
     *
     * @param tokenType The revealed token type
     */
    public static void showTokenRevealDialog(TokenType tokenType) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Token Revealed");

        VBox content = new VBox(10);
        content.setAlignment(Pos.CENTER);

        Label titleLabel = new Label("You found:");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));

        Label tokenLabel = new Label(tokenType.name());
        tokenLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        switch (tokenType) {
            case DIAMOND:
                tokenLabel.setTextFill(Color.BLUE);
                tokenLabel.setText("THE LOST DIAMOND!");
                break;
            case RUBY:
                tokenLabel.setTextFill(Color.RED);
                break;
            case EMERALD:
                tokenLabel.setTextFill(Color.GREEN);
                break;
            case TOPAZ:
                tokenLabel.setTextFill(Color.ORANGE);
                break;
            case THIEF:
                tokenLabel.setTextFill(Color.BLACK);
                tokenLabel.setText("A THIEF!");
                break;
            case EMPTY:
                tokenLabel.setText("Nothing of value");
                tokenLabel.setTextFill(Color.GRAY);
                break;
        }

        Label valueLabel = new Label();
        if (tokenType != TokenType.THIEF && tokenType != TokenType.EMPTY) {
            valueLabel.setText("Value: " + tokenType.getValue() + " coins");
        } else if (tokenType == TokenType.THIEF) {
            valueLabel.setText("You lost all your money!");
        }

        content.getChildren().addAll(titleLabel, tokenLabel, valueLabel);

        alert.getDialogPane().setContent(content);
        alert.showAndWait();
    }
}