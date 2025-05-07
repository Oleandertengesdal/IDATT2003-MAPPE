package idi.edu.idatt.mappe.services;

import javafx.animation.*;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;
import java.util.logging.Logger;

/**
 * Service for handling animations in the game.
 * Manages dice rolling animations and player movement animations.
 */
public class AnimationService {
    private static final Logger logger = Logger.getLogger(AnimationService.class.getName());

    private static final int STEP_ANIMATION_DURATION_MS = 300;
    private static final int DICE_FRAME_DURATION_MS = 60;
    private static final int DICE_ANIMATION_FRAMES = 10;

    private final Random random = new Random();

    /**
     * Creates a new AnimationService.
     */
    public AnimationService() {
        logger.info("AnimationService initialized");
    }

    /**
     * Animates dice rolling and updates the dice visuals through a callback.
     *
     * @param numberOfDice The number of dice to animate
     * @param maxSides The maximum number of sides on any die
     * @param updateVisuals Callback to update the dice visuals with random values
     * @param finalValues The final values of the dice after animation
     * @param onComplete Callback to run when the animation completes
     */
    public void animateDiceRoll(
            int numberOfDice,
            int maxSides,
            Consumer<List<Integer>> updateVisuals,
            List<Integer> finalValues,
            Runnable onComplete) {

        Timeline timeline = new Timeline();

        // Add frames for random dice values
        for (int frame = 0; frame < DICE_ANIMATION_FRAMES; frame++) {
            int finalMaxSides = maxSides;
            KeyFrame keyFrame = new KeyFrame(
                    Duration.millis((frame + 1) * DICE_FRAME_DURATION_MS),
                    e -> {
                        List<Integer> randomValues = new ArrayList<>();
                        for (int i = 0; i < numberOfDice; i++) {
                            randomValues.add(random.nextInt(finalMaxSides) + 1);
                        }
                        updateVisuals.accept(randomValues);
                    }
            );
            timeline.getKeyFrames().add(keyFrame);
        }

        // Add final frame with the actual dice values
        KeyFrame finalKeyFrame = new KeyFrame(
                Duration.millis(DICE_ANIMATION_FRAMES * DICE_FRAME_DURATION_MS + 100),
                e -> {
                    updateVisuals.accept(finalValues);
                    if (onComplete != null) {
                        onComplete.run();
                    }
                }
        );
        timeline.getKeyFrames().add(finalKeyFrame);

        // Play the animation
        timeline.play();

        logger.info("Dice animation started with " + numberOfDice + " dice");
    }

    /**
     * Animates a token moving to a new position.
     *
     * @param token The token to animate
     * @param x The target x-coordinate
     * @param y The target y-coordinate
     * @param onFinished Callback to run when animation completes
     */
    public void animateTokenMove(Node token, double x, double y, Runnable onFinished) {
        if (token == null) {
            logger.warning("Cannot animate null token");
            if (onFinished != null) {
                onFinished.run();
            }
            return;
        }

        TranslateTransition transition = new TranslateTransition(Duration.millis(STEP_ANIMATION_DURATION_MS), token);


        double currentLayoutX = token.getLayoutX();
        double currentLayoutY = token.getLayoutY();

        token.setLayoutX(x);
        token.setLayoutY(y);

        if (onFinished != null) {
            onFinished.run();
        }

        logger.fine("Token moved to x: " + x + ", y: " + y);
    }

    /**
     * Simple version that immediately sets position without animation.
     *
     * @param token The token to position
     * @param x The target x-coordinate
     * @param y The target y-coordinate
     */
    public void setTokenPosition(Node token, double x, double y) {
        if (token == null) {
            logger.warning("Cannot position null token");
            return;
        }

        token.setLayoutX(x);
        token.setLayoutY(y);

        logger.fine("Token positioned at x: " + x + ", y: " + y);
    }

}