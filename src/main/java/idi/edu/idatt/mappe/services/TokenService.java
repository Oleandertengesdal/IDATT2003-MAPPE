package idi.edu.idatt.mappe.services;

import idi.edu.idatt.mappe.models.Player;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Service for managing token images and creating visual token nodes.
 * This service handles loading, caching, and creating player tokens.
 */
public class TokenService {
    private static final Logger logger = Logger.getLogger(TokenService.class.getName());

    private final Map<String, Image> tokenImages = new HashMap<>();
    private final int defaultTokenSize = 32;

    /**
     * Creates a new TokenService and loads available token images.
     */
    public TokenService() {
        loadTokenImages();
    }

    /**
     * Loads token images from resources.
     */
    private void loadTokenImages() {
        String[] tokenNames = {
                "star", "car", "hat", "dog", "cat", "bishop", "burger",
                "chef", "controller", "graduation", "knight", "shoe", "skate",
                "skateboard", "wizard", "default", "none"
        };

        String imagePath = "/images/tokens/";
        for (String tokenName : tokenNames) {
            try {
                URL resource = getClass().getResource(imagePath + tokenName + ".png");
                if (resource != null) {
                    Image image = new Image(resource.openStream());
                    tokenImages.put(tokenName.toLowerCase(), image);
                    logger.info("Loaded image for token: " + tokenName);
                } else {
                    logger.warning("Image not found for token: " + tokenName);
                }
            } catch (Exception e) {
                logger.severe("Failed to load image for token: " + tokenName);
            }
        }
    }

    /**
     * Gets the image for a token name.
     *
     * @param tokenName The name of the token
     * @return The token image, or null if not found
     */
    public Image getTokenImage(String tokenName) {
        return tokenImages.get(tokenName.toLowerCase());
    }

    /**
     * Checks if a token image exists for the given name.
     *
     * @param tokenName The name of the token
     * @return True if the token image exists
     */
    public boolean hasTokenImage(String tokenName) {
        return tokenImages.containsKey(tokenName.toLowerCase());
    }

    /**
     * Creates a visual token node for a player with the given color.
     *
     * @param player The player to create a token for
     * @param color The color for the player's token
     * @return A Node representing the player's token
     */
    public Node createTokenNode(Player player, Color color) {
        return createTokenNode(player, color, defaultTokenSize);
    }

    /**
     * Creates a visual token node for a player with the given color and size.
     *
     * @param player The player to create a token for
     * @param color The color for the player's token
     * @param size The size of the token (width and height)
     * @return A Node representing the player's token
     */
    public Node createTokenNode(Player player, Color color, int size) {
        String tokenName = player.getToken().toLowerCase();
        double tokenRadius = size / 2.0;

        StackPane tokenStack = new StackPane();
        tokenStack.setPrefSize(size, size);

        Circle backgroundCircle = new Circle(tokenRadius);
        backgroundCircle.setFill(color);
        backgroundCircle.setStroke(Color.DARKGRAY);
        backgroundCircle.setStrokeWidth(1.0);

        tokenStack.getChildren().add(backgroundCircle);

        if (hasTokenImage(tokenName)) {
            ImageView tokenImage = new ImageView(getTokenImage(tokenName));
            tokenImage.setFitWidth(size * 0.8);
            tokenImage.setFitHeight(size * 0.8);
            tokenImage.setPreserveRatio(true);

            tokenStack.getChildren().add(tokenImage);
            logger.info("Created token node with image: " + tokenName + " for player: " + player.getName());
        } else {
            String initial = player.getName().substring(0, 1).toUpperCase();
            Text initialText = new Text(initial);
            initialText.setFill(Color.WHITE);
            initialText.setStyle("-fx-font-size: " + (size / 2) + "px; -fx-font-weight: bold;");
            tokenStack.getChildren().add(initialText);

            logger.info("Created token node with initial for player: " + player.getName());
        }

        return tokenStack;
    }

    /**
     * Creates a mini version of a token for a player.
     *
     * @param player The player
     * @param color The color to use
     * @param size The size of the mini token
     * @return A Node representing the mini token
     */
    public Node createMiniToken(Player player, Color color, int size) {
        StackPane miniStack = new StackPane();
        miniStack.setPrefSize(size, size);

        Circle miniCircle = new Circle(size / 2.0);
        miniCircle.setFill(color);
        miniCircle.setStroke(Color.BLACK);
        miniCircle.setStrokeWidth(1);

        miniStack.getChildren().add(miniCircle);

        String tokenName = player.getToken().toLowerCase();
        if (hasTokenImage(tokenName)) {
            ImageView miniView = new ImageView(getTokenImage(tokenName));
            miniView.setFitWidth(size * 0.8);
            miniView.setFitHeight(size * 0.8);
            miniView.setPreserveRatio(true);

            miniStack.getChildren().add(miniView);
        } else {
            //Create a mini token with the player's initial if no image is available.
            String initial = player.getName().substring(0, 1).toUpperCase();
            Text miniText = new Text(initial);
            miniText.setFill(Color.WHITE);
            miniText.setStyle("-fx-font-size: " + (size / 3) + "px; -fx-font-weight: bold;");

            miniStack.getChildren().add(miniText);
        }

        return miniStack;
    }

    /**
     * Gets the default token size.
     *
     * @return The default token size
     */
    public int getDefaultTokenSize() {
        return defaultTokenSize;
    }

    /**
     * Gets all available token names.
     *
     * @return An array of available token names
     */
    public String[] getAvailableTokenNames() {
        return tokenImages.keySet().toArray(new String[0]);
    }
}