package idi.edu.idatt.mappe.utils.factory;

import idi.edu.idatt.mappe.models.GameRules;
import idi.edu.idatt.mappe.models.enums.GameType;

/**
 * Factory class for creating different preset game rule configurations
 */
public class GameRulesFactory {

    /**
     * private constructor to prevent instantiation
     */
    private GameRulesFactory() {
    }

    /**
     * Creates a standard rules configuration for Snakes and Ladders
     *
     * @return A standard configuration for Snakes and Ladders
     */
    public static GameRules createSnakesAndLaddersRules() {
        GameRules rules = new GameRules(GameType.SNAKES_AND_LADDERS);
        rules.setRuleName("Classic Rules");
        rules.setRuleDescription("The classic game rules with standard dice configuration");
        rules.setNumberOfDice(2);
        rules.setDiceSides(6);
        return rules;
    }

    /**
     * Creates a chaos variant of Snakes and Ladders with many random events
     *
     * @return A chaos configuration for Snakes and Ladders
     */
    public static GameRules createChaosSnakesAndLaddersRules() {
        GameRules rules = new GameRules(GameType.SNAKES_AND_LADDERS);
        rules.setRuleName("Chaos Rules");
        rules.setRuleDescription("Chaotic rules with random teleports and extra turns");
        rules.setNumberOfDice(2);
        rules.setDiceSides(6);
        rules.setExtraThrowOnMax(true);
        return rules;
    }

    /**
     * Creates a quick game variant of Snakes and Ladders
     *
     * @return A quick game configuration for Snakes and Ladders
     */
    public static GameRules createQuickSnakesAndLaddersRules() {
        GameRules rules = new GameRules(GameType.SNAKES_AND_LADDERS);
        rules.setRuleName("Quick Game Rules");
        rules.setRuleDescription("Rules designed for a faster game with extra turns on ladders");
        rules.setNumberOfDice(2);
        rules.setDiceSides(6);
        rules.setExtraTurnOnLadder(true);
        return rules;
    }

    /**
     * Creates a challenging variant of Snakes and Ladders
     *
     * @return A challenging configuration for Snakes and Ladders
     */
    public static GameRules createChallengingSnakesAndLaddersRules() {
        GameRules rules = new GameRules(GameType.SNAKES_AND_LADDERS);
        rules.setRuleName("Challenge Rules");
        rules.setRuleDescription("Challenging rules with skipped turns on snakes");
        rules.setNumberOfDice(2);
        rules.setDiceSides(6);
        rules.setSkipTurnOnSnake(true);
        return rules;
    }

    /**
     * Create a Classic Snakes and Ladders game with 1 die and 6 sides, and roll again on max
     *
     * @return A classic configuration for Snakes and Ladders
     */
    public static GameRules createClassicSnakesAndLaddersRules() {
        GameRules rules = new GameRules(GameType.SNAKES_AND_LADDERS);
        rules.setRuleName("Single Die Rules");
        rules.setRuleDescription("Classic rules with one die and extra throw on maximum roll");
        rules.setNumberOfDice(1);
        rules.setDiceSides(6);
        rules.setExtraThrowOnMax(true);
        return rules;
    }

    /**
     * Creates a simple custom rule configuration
     *
     * @param gameType The game type
     * @param ruleName The name of this rule set
     * @param ruleDescription Description of the rule set
     * @param numberOfDice Number of dice
     * @param diceSides Number of sides on each die
     * @return A custom rules configuration
     */
    public static GameRules createCustomRules(GameType gameType, String ruleName,
                                              String ruleDescription, int numberOfDice, int diceSides) {
        GameRules rules = new GameRules(gameType);
        rules.setRuleName(ruleName);
        rules.setRuleDescription(ruleDescription);
        rules.setNumberOfDice(numberOfDice);
        rules.setDiceSides(diceSides);
        return rules;
    }

    /**
     * Creates default rules for Lost Diamond game
     */
    public static GameRules createLostDiamondRules() {
        GameRules rules = new GameRules(GameType.THE_LOST_DIAMOND);
        rules.setRuleName("Standard Lost Diamond Rules");
        rules.setRuleDescription("Standard rules for The Lost Diamond game");
        rules.setBoardVariant("Standard");

        rules.setNumberOfDice(1);
        rules.setDiceSides(6);
        rules.setStartingMoney(300);

        rules.setAdditionalData("startingMoney", 300);
        rules.setAdditionalData("tokenPrice", 100);
        rules.setAdditionalData("travelCostModifier", 1.0);

        rules.setAdditionalData("allowDiceRollForTokens", true);
        rules.setAdditionalData("diceRollThreshold", 4);
        rules.setAdditionalData("emergencyMoneyEnabled", true);
        rules.setAdditionalData("emergencyMoneyAmount", 100);

        return rules;
    }

    /**
     * Creates easy rules for Lost Diamond game
     */
    public static GameRules createEasyLostDiamondRules() {
        GameRules rules = new GameRules(GameType.THE_LOST_DIAMOND);
        rules.setRuleName("Easy Lost Diamond Rules");
        rules.setRuleDescription("Easier version with more money and cheaper travel");
        rules.setBoardVariant("Easy");

        rules.setNumberOfDice(1);
        rules.setDiceSides(6);
        rules.setStartingMoney(500);

        rules.setAdditionalData("startingMoney", 500);
        rules.setAdditionalData("tokenPrice", 80);
        rules.setAdditionalData("travelCostModifier", 0.7);

        rules.setAdditionalData("allowDiceRollForTokens", true);
        rules.setAdditionalData("diceRollThreshold", 3);
        rules.setAdditionalData("emergencyMoneyEnabled", true);
        rules.setAdditionalData("emergencyMoneyAmount", 150);

        return rules;
    }

    /**
     * Creates challenging rules for Lost Diamond game
     */
    public static GameRules createChallengingLostDiamondRules() {
        GameRules rules = new GameRules(GameType.THE_LOST_DIAMOND);
        rules.setRuleName("Challenging Lost Diamond Rules");
        rules.setRuleDescription("Challenging version with less money and expensive travel");
        rules.setBoardVariant("Challenging");

        rules.setNumberOfDice(1);
        rules.setDiceSides(6);
        rules.setStartingMoney(150);

        rules.setAdditionalData("startingMoney", 500);
        rules.setAdditionalData("tokenPrice", 80);
        rules.setAdditionalData("travelCostModifier", 0.7);

        // Easier rules
        rules.setAdditionalData("allowDiceRollForTokens", true);
        rules.setAdditionalData("diceRollThreshold", 3);
        rules.setAdditionalData("emergencyMoneyEnabled", true);
        rules.setAdditionalData("emergencyMoneyAmount", 150);

        return rules;
    }
}
