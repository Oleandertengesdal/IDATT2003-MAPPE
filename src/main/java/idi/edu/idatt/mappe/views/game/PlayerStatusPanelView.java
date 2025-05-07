package idi.edu.idatt.mappe.views.game;

import idi.edu.idatt.mappe.models.Player;
import idi.edu.idatt.mappe.services.TokenService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * View component for displaying player statuses.
 * Shows a list of players with their tokens, names, and current positions.
 */
public class PlayerStatusPanelView extends VBox {
    private static final Logger logger = Logger.getLogger(PlayerStatusPanelView.class.getName());

    private final Map<Player, HBox> playerEntries = new HashMap<>();
    private final TokenService tokenService;
    private final VBox playersContainer;

    private final Map<Player, Label> positionLabels = new HashMap<>();


    /**
     * Creates a new PlayerStatusPanelView.
     *
     * @param tokenService The token service to use for creating token visualizations
     */
    public PlayerStatusPanelView(TokenService tokenService) {
        this.tokenService = tokenService;

        setSpacing(10);
        setPadding(new Insets(10));
        setStyle("-fx-background-color: white; -fx-border-color: #cccccc; -fx-border-width: 1px; -fx-border-radius: 5px;");

        Label titleLabel = new Label("Players");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        playersContainer = new VBox(5);

        ScrollPane scrollPane = new ScrollPane(playersContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        getChildren().addAll(titleLabel, scrollPane);

        logger.info("PlayerStatusPanelView initialized");
    }

    /**
     * Adds a player to the status panel.
     *
     * @param player The player to add
     * @param color The color for the player's token
     */
    public void addPlayer(Player player, Color color) {
        if (playerEntries.containsKey(player)) {
            logger.warning("Player " + player.getName() + " already exists in status panel");
            return;
        }

        Node miniToken = tokenService.createMiniToken(player, color, 24);

        HBox entryBox = createPlayerStatusBox(player, miniToken);
        playerEntries.put(player, entryBox);

        playersContainer.getChildren().add(entryBox);

        logger.info("Added player " + player.getName() + " to status panel");
    }

    /**
     * Creates a HBox with player status information
     *
     * @param player The player who's name and position will be displayed
     * @param tokenNode The token node to display
     */
    private HBox createPlayerStatusBox(Player player, Node tokenNode) {
        HBox entryBox = new HBox();
        entryBox.setSpacing(10);
        entryBox.setPadding(new Insets(8));
        entryBox.setAlignment(Pos.CENTER_LEFT);
        entryBox.setStyle("-fx-border-color: #cccccc; -fx-border-width: 0 0 1px 0;");

        HBox tokenBox = new HBox(tokenNode);
        tokenBox.setAlignment(Pos.CENTER);
        tokenBox.setMinWidth(30);

        Label nameLabel = new Label(player.getName());
        nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        nameLabel.setId("name-" + player.getName());
        nameLabel.setStyle("-fx-text-fill: #222222;");

        Label positionLabel = new Label(getPositionText(player));
        positionLabel.setFont(Font.font("Arial", 13));
        positionLabel.setId("position-" + player.getName());
        positionLabel.setStyle("-fx-text-fill: #666666;");

        positionLabels.put(player, positionLabel);

        VBox infoBox = new VBox(3, nameLabel, positionLabel);
        infoBox.setAlignment(Pos.CENTER_LEFT);

        entryBox.getChildren().addAll(tokenBox, infoBox);
        return entryBox;
    }

    /**
     * Gets the position text for a player.
     */
    private String getPositionText(Player player) {
        return "Position: " + (player.getCurrentTile() != null ?
                player.getCurrentTile().getIndex() : "Start");
    }

    /**
     * Updates the status for a specific player.
     *
     * @param player The player to update
     */
    public void updatePlayerStatus(Player player) {
        Label positionLabel = positionLabels.get(player);
        if (positionLabel != null) {
            positionLabel.setText(getPositionText(player));
            logger.info("Updated position for " + player.getName() +
                    " to: " + getPositionText(player));
        } else {
            logger.warning("Position label for " + player.getName() + " not found");
        }
    }

    /**
     * Updates the status for all players.
     */
    public void updateAllPlayerStatus() {
        for (Player player : playerEntries.keySet()) {
            updatePlayerStatus(player);
        }
    }

    /**
     * Removes a player from the status panel.
     *
     * @param player The player to remove
     */
    public void removePlayer(Player player) {
        HBox entry = playerEntries.remove(player);
        positionLabels.remove(player);
        if (entry != null) {
            playersContainer.getChildren().remove(entry);
            logger.info("Removed player " + player.getName() + " from status panel");
        } else {
            logger.warning("Player " + player.getName() + " not found in status panel");
        }
    }

    /**
     * Clears all players from the status panel.
     */
    public void clearPlayers() {
        playerEntries.clear();
        playersContainer.getChildren().clear();
        positionLabels.clear();
        logger.info("Cleared all players from status panel");
    }
}