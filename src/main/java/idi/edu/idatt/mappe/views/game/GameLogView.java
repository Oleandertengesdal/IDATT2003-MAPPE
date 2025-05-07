package idi.edu.idatt.mappe.views.game;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

/**
 * View component for displaying the game log.
 * Provides a scrollable text area that logs game events with timestamps.
 */
public class GameLogView extends VBox {
    private static final Logger logger = Logger.getLogger(GameLogView.class.getName());

    private final TextArea logArea;
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
    private final int maxLogEntries = 200;

    /**
     * Creates a new GameLogView.
     */
    public GameLogView() {
        setSpacing(5);

        Label titleLabel = new Label("Game Log");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        logArea = new TextArea();
        logArea.setEditable(false);
        logArea.setWrapText(true);
        logArea.getStyleClass().add("game-log");
        logArea.setPrefHeight(150);

        getChildren().addAll(titleLabel, logArea);

        logger.info("GameLogView initialized");
    }

    /**
     * Adds a message to the game log with a timestamp.
     *
     * @param message The message to add
     */
    public void logGameEvent(String message) {
        String timestamp = "[" + timeFormat.format(new Date()) + "] ";
        String fullMessage = timestamp + message;

        if (Platform.isFxApplicationThread()) {
            appendLogText(fullMessage);
        } else {
            Platform.runLater(() -> appendLogText(fullMessage));
        }
    }

    /**
     * Appends text to the log area and scrolls to the bottom.
     *
     * @param text The text to append
     */
    private void appendLogText(String text) {
        logArea.appendText(text + "\n");

        if (logArea.getText().split("\n").length > maxLogEntries) {
            String[] lines = logArea.getText().split("\n");
            StringBuilder newText = new StringBuilder();
            for (int i = lines.length - maxLogEntries; i < lines.length; i++) {
                newText.append(lines[i]).append("\n");
            }
            logArea.setText(newText.toString());
        }

        logArea.positionCaret(logArea.getLength());
    }

    /**
     * Clears the log.
     */
    public void clear() {
        logArea.clear();
        logger.info("Game log cleared");
    }

    /**
     * Gets the log text.
     *
     * @return The current log text
     */
    public String getLogText() {
        return logArea.getText();
    }
}