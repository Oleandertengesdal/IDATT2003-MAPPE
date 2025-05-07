package idi.edu.idatt.mappe.views.game;

import idi.edu.idatt.mappe.services.AnimationService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * View component for displaying dice.
 * Handles rendering and animating dice.
 */
public class DiceView extends HBox {
    private static final Logger logger = Logger.getLogger(DiceView.class.getName());

    private final AnimationService animationService;
    private int numberOfDice = 0;
    private int maxSides = 20;
    private List<Integer> currentValues = new ArrayList<>();
    private boolean animationInProgress = false;

    /**
     * Creates a new DiceView.
     *
     * @param animationService The animation service to use for animations
     */
    public DiceView(AnimationService animationService) {
        this.animationService = animationService;

        setAlignment(Pos.CENTER);
        setSpacing(10);
        setPadding(new Insets(15));
        setPrefHeight(100);
        setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #cccccc; -fx-border-width: 1px; -fx-border-radius: 5px;");

        createDice(2, 6);
    }

    /**
     * Creates dice with a specific number of dice and sides.
     *
     * @param numberOfDice The number of dice to create
     * @param sides The number of sides on each die
     */
    public void createDice(int numberOfDice, int sides) {
        this.numberOfDice = numberOfDice;
        this.maxSides = Math.max(sides, 6);

        List<Integer> initialValues = new ArrayList<>();
        for (int i = 0; i < numberOfDice; i++) {
            initialValues.add(1);
        }
        updateDiceVisuals(initialValues);
        logger.info("Created " + numberOfDice + " dice with " + sides + " sides each.");
    }

    /**
     * Updates the dice display with new values.
     * This will animate the dice rolling before showing the final values.
     *
     * @param diceValues The final values of the dice
     * @param callback The callback to run when the animation is complete
     */
    public void updateDiceDisplay(List<Integer> diceValues, Runnable callback) {
        if (animationInProgress) {
            logger.warning("Dice animation already in progress.");
            return;
        }

        if (diceValues.size() != numberOfDice) {
            logger.warning("Number of dice values (" + diceValues.size() +
                    ") doesn't match number of dice (" + numberOfDice + ").");
            return;
        }

        this.currentValues = new ArrayList<>(diceValues);
        animationInProgress = true;

        int maxValue = 0;
        for (int value : diceValues) {
            maxValue = Math.max(maxValue, value);
        }
        final int finalMaxSides = Math.max(maxValue, this.maxSides);

        animationService.animateDiceRoll(
                numberOfDice,
                finalMaxSides,
                this::updateDiceVisuals,
                diceValues,
                () -> {
                    animationInProgress = false;
                    if (callback != null) {
                        callback.run();
                    }
                }
        );
    }

    /**
     * Updates the visual representation of the dice.
     *
     * @param values The values to display on the dice
     */
    private void updateDiceVisuals(List<Integer> values) {
        getChildren().clear();

        for (int value : values) {
            StackPane diePane = createDieVisual(value);
            getChildren().add(diePane);
        }
    }

    /**
     * Creates a visual representation of a die.
     *
     * @param value The value to display on the die
     * @return A StackPane containing the die visualization
     */
    private StackPane createDieVisual(int value) {
        StackPane diePane = new StackPane();
        diePane.setMinSize(60, 60);
        diePane.setPrefSize(60, 60);
        diePane.setMaxSize(60, 60);
        diePane.setStyle("-fx-background-color: white; -fx-border-color: black; -fx-border-width: 2px; -fx-border-radius: 5px;");

        Text valueText = new Text(String.valueOf(value));
        valueText.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        diePane.getChildren().add(valueText);
        diePane.setEffect(new DropShadow(10, Color.GRAY));

        return diePane;
    }

    /**
     * Gets the current values of the dice.
     *
     * @return The current values of the dice
     */
    public List<Integer> getCurrentValues() {
        return new ArrayList<>(currentValues);
    }

    /**
     * Checks if a dice animation is in progress.
     *
     * @return True if a dice animation is in progress
     */
    public boolean isAnimationInProgress() {
        return animationInProgress;
    }
}