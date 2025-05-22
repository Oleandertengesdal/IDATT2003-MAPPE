package idi.edu.idatt.mappe.services;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.effect.GaussianBlur;
import javafx.util.Duration;

import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

/**
 * Service for handling animations in the game.
 * Manages dice rolling animations and player movement animations.
 */
public class AnimationController {
    private static final Logger logger = Logger.getLogger(AnimationController.class.getName());

    private static final int STEP_ANIMATION_DURATION_MS = 300;
    private static final int DICE_FRAME_DURATION_MS = 60;
    private static final int DICE_ANIMATION_FRAMES = 10;

    private final Random random = new Random();

    /**
     * Creates a new AnimationService.
     */
    public AnimationController() {
        logger.info("AnimationService initialized");
    }

    /**
     * Sets the position of a token immediately without animation.
     *
     * @param token The token to position
     * @param x The x coordinate
     * @param y The y coordinate
     */
    public void setTokenPosition(Node token, double x, double y) {
        if (token == null) return;

        token.setLayoutX(x);
        token.setLayoutY(y);
    }

    /**
     * Animates a token moving to a new position.
     *
     * @param token The token to animate
     * @param x The target x coordinate
     * @param y The target y coordinate
     * @param onFinished Callback to run when the animation finishes
     */
    public void animateTokenMove(Node token, double x, double y, Runnable onFinished) {
        if (token == null) {
            if (onFinished != null) {
                onFinished.run();
            }
            return;
        }

        Timeline timeline = new Timeline();

        // Add blur effect during animation
        GaussianBlur blur = new GaussianBlur(0);
        token.setEffect(blur);

        // Create keyframes for the animation
        KeyFrame start = new KeyFrame(Duration.ZERO,
                new KeyValue(token.layoutXProperty(), token.getLayoutX()),
                new KeyValue(token.layoutYProperty(), token.getLayoutY()),
                new KeyValue(blur.radiusProperty(), 0));

        KeyFrame blurStart = new KeyFrame(Duration.millis(100),
                new KeyValue(blur.radiusProperty(), 2));

        KeyFrame end = new KeyFrame(Duration.millis(500),
                new KeyValue(token.layoutXProperty(), x),
                new KeyValue(token.layoutYProperty(), y),
                new KeyValue(blur.radiusProperty(), 0));

        timeline.getKeyFrames().addAll(start, blurStart, end);

        timeline.setOnFinished(e -> {
            token.setEffect(null);
            if (onFinished != null) {
                onFinished.run();
            }
        });

        timeline.play();
    }

    /**
     * Animates rolling the dice to the target values.
     *
     * @param targetValues The final values the dice should show
     * @param onComplete Callback to execute after animation completes
     */
    public void animateRoll(List<Integer> targetValues, Runnable onComplete) {
    }

    /**
     * Animates text appearing with a fade-in effect.
     *
     * @param label The label to animate
     * @param text The text to display
     */
    public void animateTextAppear(Label label, String text) {
        if (label == null) return;

        label.setOpacity(0);
        label.setText(text);

        Timeline timeline = new Timeline();
        KeyFrame start = new KeyFrame(Duration.ZERO, new KeyValue(label.opacityProperty(), 0));
        KeyFrame end = new KeyFrame(Duration.millis(500), new KeyValue(label.opacityProperty(), 1));

        timeline.getKeyFrames().addAll(start, end);
        timeline.play();
    }
}