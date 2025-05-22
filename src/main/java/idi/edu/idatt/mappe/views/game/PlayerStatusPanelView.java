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
import java.util.concurrent.atomic.AtomicReference;
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

    private final Map<Player, Label> moneyLabels = new HashMap<>();
    private boolean showMoney = false;



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
     * Sets whether to show money instead of position.
     *
     * @param showMoney True to show money, false to show position
     */
    public void showMoneyInsteadOfPosition(boolean showMoney) {
        this.showMoney = showMoney;

        for (Player player : playerEntries.keySet()) {
            HBox entryBox = playerEntries.get(player);
            updatePlayerDisplay(player, entryBox);
        }
    }

    /**
     * Creates a HBox with player status information
     *
     * @param player The player who's status will be displayed
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

        Label statusLabel = new Label(showMoney ? getMoneyText(player) : getPositionText(player));
        statusLabel.setFont(Font.font("Arial", 13));
        statusLabel.setId(showMoney ? "money-" + player.getName() : "position-" + player.getName());
        statusLabel.setStyle("-fx-text-fill: #666666;");

        if (showMoney) {
            moneyLabels.put(player, statusLabel);
        } else {
            positionLabels.put(player, statusLabel);
        }

        VBox infoBox = new VBox(3, nameLabel, statusLabel);
        infoBox.setAlignment(Pos.CENTER_LEFT);

        entryBox.getChildren().addAll(tokenBox, infoBox);
        return entryBox;
    }

    /**
     * Updates the player display based on the current mode (position or money)
     */
    private void updatePlayerDisplay(Player player, HBox entryBox) {
        if (entryBox == null) return;

        AtomicReference<VBox> infoBox = new AtomicReference<>();
        entryBox.getChildren().stream()
                .filter(VBox.class::isInstance)
                .findFirst()
                .ifPresent(node -> infoBox.set((VBox) node));

        if (infoBox.get() == null || infoBox.get().getChildren().size() < 2) return;

        Node statusNode = infoBox.get().getChildren().get(1);
        if (statusNode instanceof Label) {
            Label statusLabel = (Label) statusNode;

            if (showMoney) {
                statusLabel.setText(getMoneyText(player));
                moneyLabels.put(player, statusLabel);
            } else {
                statusLabel.setText(getPositionText(player));
                positionLabels.put(player, statusLabel);
            }
        }
    }

    /**
     * Gets the money text for a player.
     */
    private String getMoneyText(Player player) {
        return "Money: " + player.getMoney() + " coins";
    }

    /**
     * Updates the money display for a player.
     *
     * @param player The player to update
     */
    public void updatePlayerMoney(Player player) {
        Label moneyLabel = moneyLabels.get(player);
        if (moneyLabel != null) {
            moneyLabel.setText(getMoneyText(player));
            logger.info("Updated money for " + player.getName() +
                    " to: " + getMoneyText(player));
        } else {
            logger.warning("Money label for " + player.getName() + " not found");
        }
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
}