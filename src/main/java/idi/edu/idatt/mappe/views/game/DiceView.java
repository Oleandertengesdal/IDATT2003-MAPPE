package idi.edu.idatt.mappe.views.game;

import idi.edu.idatt.mappe.services.AnimationController;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

/**
 * View component for displaying dice in the game.
 * Supports multiple dice with animation.
 */
public class DiceView extends VBox {
    private static final Logger logger = Logger.getLogger(DiceView.class.getName());
    private final List<DieFace> dice = new ArrayList<>();
    private final AnimationController animationController;

    private Runnable onAnimationComplete;

    /**
     * Creates a new DiceView.
     *
     * @param animationController The animation service to use for dice rolling animations
     */
    public DiceView(AnimationController animationController) {
        this.animationController = animationController;

        setSpacing(10);
        setPadding(new Insets(10));
        setAlignment(Pos.CENTER);

        createDice(2, 6);

        logger.info("DiceView initialized");
    }

    /**
     * Creates the specified number of dice with the given number of sides.
     *
     * @param numberOfDice Number of dice to create
     * @param sides Number of sides on each die
     */
    public void createDice(int numberOfDice, int sides) {
        logger.info("Creating " + numberOfDice + " dice with " + sides + " sides each");

        getChildren().clear();
        dice.clear();

        HBox diceContainer = new HBox(15);
        diceContainer.setAlignment(Pos.CENTER);

        for (int i = 0; i < numberOfDice; i++) {
            DieFace dieFace = new DieFace(sides);
            dice.add(dieFace);
            diceContainer.getChildren().add(dieFace);
        }

        getChildren().add(diceContainer);

        if (numberOfDice > 1) {
            Label totalLabel = new Label("Total: 0");
            totalLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
            totalLabel.setTextFill(Color.web("#2c3e50"));
            getChildren().add(totalLabel);
        }
    }

    /**
     * Updates the dice display with new values and executes the callback after animation.
     *
     * @param values The values to display on the dice
     * @param completeCallback Callback to execute after animation completes
     */
    public void updateDiceDisplay(List<Integer> values, Runnable completeCallback) {
        logger.info("Updating dice display with values: " + values);

        if (values == null || values.isEmpty() || values.size() != dice.size()) {
            logger.warning("Invalid dice values provided: " + values);
            return;
        }

        this.onAnimationComplete = completeCallback;

        animateRoll(values);
    }

    /**
     * Updates the dice display with new values.
     *
     * @param values The values to display on the dice
     */
    public void updateDiceDisplay(List<Integer> values) {
        updateDiceDisplay(values, null);
    }

    /**
     * Animates rolling the dice to the target values.
     *
     * @param targetValues The final values the dice should show
     */
    private void animateRoll(List<Integer> targetValues) {
        final int steps = 10;
        final int delayBetweenSteps = 50;

        Random random = new Random();

        new Thread(() -> {
            try {
                for (int step = 0; step < steps; step++) {
                    final int currentStep = step;

                    final List<Integer> randomValues = new ArrayList<>();
                    for (DieFace die : dice) {
                        int randomValue = random.nextInt(die.getNumberOfSides()) + 1;
                        randomValues.add(randomValue);
                    }

                    Platform.runLater(() -> {
                        for (int i = 0; i < dice.size(); i++) {
                            dice.get(i).setValue(randomValues.get(i));
                        }

                        if (dice.size() > 1 && getChildren().size() > 1) {
                            int total = randomValues.stream().mapToInt(Integer::intValue).sum();
                            ((Label) getChildren().get(1)).setText("Total: " + total);
                        }
                    });

                    Thread.sleep(delayBetweenSteps);
                }

                Platform.runLater(() -> {
                    for (int i = 0; i < dice.size(); i++) {
                        dice.get(i).setValue(targetValues.get(i));
                    }

                    if (dice.size() > 1 && getChildren().size() > 1) {
                        int total = targetValues.stream().mapToInt(Integer::intValue).sum();
                        ((Label) getChildren().get(1)).setText("Total: " + total);
                    }

                    if (onAnimationComplete != null) {
                        onAnimationComplete.run();
                    }
                });

            } catch (InterruptedException e) {
                logger.warning("Dice animation interrupted: " + e.getMessage());
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    /**
     * Inner class representing a visual die face.
     */
    private static class DieFace extends Pane {
        private final int numberOfSides;
        private final Text valueText;

        /**
         * Creates a new die face with the specified number of sides.
         *
         * @param sides Number of sides on the die
         */
        public DieFace(int sides) {
            this.numberOfSides = sides;

            Rectangle rectangle = new Rectangle(40, 40);
            rectangle.setFill(Color.WHITE);
            rectangle.setStroke(Color.BLACK);
            rectangle.setArcWidth(10);
            rectangle.setArcHeight(10);

            valueText = new Text("1");
            valueText.setFont(Font.font("Arial", FontWeight.BOLD, 18));
            valueText.setFill(Color.ORANGERED);

            valueText.setLayoutX(15);
            valueText.setLayoutY(25);

            getChildren().addAll(rectangle, valueText);

            setPrefSize(40, 40);
            setMinSize(40, 40);
            setMaxSize(40, 40);
        }

        /**
         * Gets the number of sides on this die.
         *
         * @return Number of sides
         */
        public int getNumberOfSides() {
            return numberOfSides;
        }

        /**
         * Sets the value displayed on the die face.
         *
         * @param value Value to display
         */
        public void setValue(int value) {
            if (value < 1 || value > numberOfSides) {
                throw new IllegalArgumentException("Die value must be between 1 and " + numberOfSides);
            }
            valueText.setText(String.valueOf(value));

            if (value < 10) {
                valueText.setLayoutX(15);
            } else {
                valueText.setLayoutX(10);
            }
        }
    }
}