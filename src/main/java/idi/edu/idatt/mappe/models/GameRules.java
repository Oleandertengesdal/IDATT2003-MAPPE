package idi.edu.idatt.mappe.models;

import idi.edu.idatt.mappe.models.enums.GameType;

import java.util.HashMap;
import java.util.Map;

/**
 * Model class representing game rules configuration for board games.
 * Focuses only on rule settings like dice configuration and special movement rules.
 * Follows single responsibility principle by not handling board-specific information.
 */
public class GameRules {

    private GameType gameType;
    private String ruleName;
    private String ruleDescription;
    private String boardVariant = "Standard";
    private int startingMoney = 300;

    private int numberOfDice = 2;
    private int diceSides = 6;

    private boolean extraThrowOnMax = false;
    private boolean startOnlyWithMax = false;
    private boolean skipTurnOnSnake = false;
    private boolean extraTurnOnLadder = false;
    private int consecutiveSixesLimit = 3;

    private final Map<String, Object> additionalData = new HashMap<>();


    /**
     * Creates a new GameRules object with default settings
     */
    public GameRules() {
        this.gameType = GameType.SNAKES_AND_LADDERS;
        this.ruleName = "Default Rules";
        this.ruleDescription = "Standard rules configuration";
    }

    /**
     * Creates a new GameRules object for a specific game type
     *
     * @param gameType The type of game
     */
    public GameRules(GameType gameType) {
        this.gameType = gameType;
        this.ruleName = "Default " + gameType.name() + " Rules";
        this.ruleDescription = "Standard rules for " + gameType.name();
    }

    /**
     * Creates a new GameRules object with specified parameters
     *
     * @param gameType The type of game
     * @param boardVariant The board variant to use with these rules
     * @param numberOfDice Number of dice to use
     * @param diceSides Number of sides on each die
     */
    public GameRules(GameType gameType, String boardVariant, int numberOfDice, int diceSides) {
        this.gameType = gameType;
        this.ruleName = gameType.name() + " Rules";
        this.ruleDescription = "Custom rules for " + gameType.name();
        this.boardVariant = boardVariant;
        this.numberOfDice = numberOfDice;
        this.diceSides = diceSides;
    }

    public String getBoardVariant() {
        return boardVariant;
    }

    public void setBoardVariant(String boardVariant) {
        this.boardVariant = boardVariant;
    }

    public GameType getGameType() {
        return gameType;
    }

    public void setGameType(GameType gameType) {
        this.gameType = gameType;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public String getRuleDescription() {
        return ruleDescription;
    }

    public void setRuleDescription(String ruleDescription) {
        this.ruleDescription = ruleDescription;
    }

    public int getNumberOfDice() {
        return numberOfDice;
    }

    public void setNumberOfDice(int numberOfDice) {
        this.numberOfDice = numberOfDice;
    }

    public int getDiceSides() {
        return diceSides;
    }

    public void setDiceSides(int diceSides) {
        this.diceSides = diceSides;
    }

    public boolean isExtraThrowOnMax() {
        return extraThrowOnMax;
    }

    public void setExtraThrowOnMax(boolean extraThrowOnMax) {
        this.extraThrowOnMax = extraThrowOnMax;
    }

    public boolean isStartOnlyWithMax() {
        return startOnlyWithMax;
    }

    public void setStartOnlyWithMax(boolean startOnlyWithMax) {
        this.startOnlyWithMax = startOnlyWithMax;
    }

    public boolean isSkipTurnOnSnake() {
        return skipTurnOnSnake;
    }

    public void setSkipTurnOnSnake(boolean skipTurnOnSnake) {
        this.skipTurnOnSnake = skipTurnOnSnake;
    }

    public boolean isExtraTurnOnLadder() {
        return extraTurnOnLadder;
    }

    public void setExtraTurnOnLadder(boolean extraTurnOnLadder) {
        this.extraTurnOnLadder = extraTurnOnLadder;
    }

    public int getConsecutiveSixesLimit() {
        return consecutiveSixesLimit;
    }

    public void setConsecutiveSixesLimit(int consecutiveSixesLimit) {
        this.consecutiveSixesLimit = consecutiveSixesLimit;
    }

    /**
     * Sets additional data associated with these rules
     *
     * @param key The key for the data
     * @param value The data value
     */
    public void setAdditionalData(String key, Object value) {
        additionalData.put(key, value);
    }

    /**
     * Gets additional data associated with these rules
     *
     * @param key The key for the data
     * @return The data value, or null if no data is associated with the key
     */
    public Object getAdditionalData(String key) {
        return additionalData.get(key);
    }

    /**
     * Gets an integer value from the additional data, or a default value if it doesn't exist
     *
     * @param key The key to look up
     * @param defaultValue The default value to return if the key doesn't exist
     * @return The value from additional data, or the default value
     */
    public int getIntAdditionalData(String key, int defaultValue) {
        Object value = additionalData.get(key);
        if (value instanceof Integer) {
            return (Integer) value;
        }
        return defaultValue;
    }


    /**
     * Checks if additional data is associated with these rules
     *
     * @param key The key to check
     * @return True if data exists for the key, false otherwise
     */
    public boolean hasAdditionalData(String key) {
        return additionalData.containsKey(key);
    }



    /**
     * Calculates the maximum possible roll with the current dice configuration
     *
     * @return The maximum possible dice roll
     */
    public int getMaxRoll() {
        return numberOfDice * diceSides;
    }

    /**
     * Gets the starting money for players
     *
     * @return The starting money
     */
    public int getStartingMoney() {
        return startingMoney;
    }

    /**
     * Sets the starting money for players
     *
     * @param startingMoney The starting money
     */
    public void setStartingMoney(int startingMoney) {
        this.startingMoney = startingMoney;
    }

    @Override
    public String toString() {
        return "GameRules{" +
                "ruleName='" + ruleName + '\'' +
                ", gameType=" + gameType +
                ", dice=" + numberOfDice + "d" + diceSides +
                '}';
    }
}