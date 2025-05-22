package idi.edu.idatt.mappe.models;

import idi.edu.idatt.mappe.models.enums.GameType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameRulesTest {

    private GameRules gameRules;

    @BeforeEach
    void setUp() {
        gameRules = new GameRules();
    }

    @Test
    void testDefaultConstructor() {
        assertEquals(GameType.SNAKES_AND_LADDERS, gameRules.getGameType());
        assertEquals("Default Rules", gameRules.getRuleName());
        assertEquals("Standard rules configuration", gameRules.getRuleDescription());
        assertEquals("Standard", gameRules.getBoardVariant());
        assertEquals(2, gameRules.getNumberOfDice());
        assertEquals(6, gameRules.getDiceSides());
        assertEquals(300, gameRules.getStartingMoney());
        assertFalse(gameRules.isExtraThrowOnMax());
        assertFalse(gameRules.isStartOnlyWithMax());
        assertFalse(gameRules.isSkipTurnOnSnake());
        assertFalse(gameRules.isExtraTurnOnLadder());
        assertEquals(3, gameRules.getConsecutiveSixesLimit());
    }

    @Test
    void testCustomConstructor() {
        GameRules customRules = new GameRules(GameType.THE_LOST_DIAMOND, "CustomBoard", 1, 8);
        assertEquals(GameType.THE_LOST_DIAMOND, customRules.getGameType());
        assertEquals("THE_LOST_DIAMOND Rules", customRules.getRuleName());
        assertEquals("Custom rules for THE_LOST_DIAMOND", customRules.getRuleDescription());
        assertEquals("CustomBoard", customRules.getBoardVariant());
        assertEquals(1, customRules.getNumberOfDice());
        assertEquals(8, customRules.getDiceSides());
    }

    @Test
    void testSettersAndGetters() {
        gameRules.setGameType(GameType.THE_LOST_DIAMOND);
        assertEquals(GameType.THE_LOST_DIAMOND, gameRules.getGameType());

        gameRules.setRuleName("Custom Rules");
        assertEquals("Custom Rules", gameRules.getRuleName());

        gameRules.setRuleDescription("Custom Description");
        assertEquals("Custom Description", gameRules.getRuleDescription());

        gameRules.setBoardVariant("CustomBoard");
        assertEquals("CustomBoard", gameRules.getBoardVariant());

        gameRules.setNumberOfDice(3);
        assertEquals(3, gameRules.getNumberOfDice());

        gameRules.setDiceSides(10);
        assertEquals(10, gameRules.getDiceSides());

        gameRules.setStartingMoney(500);
        assertEquals(500, gameRules.getStartingMoney());

        gameRules.setExtraThrowOnMax(true);
        assertTrue(gameRules.isExtraThrowOnMax());

        gameRules.setStartOnlyWithMax(true);
        assertTrue(gameRules.isStartOnlyWithMax());

        gameRules.setSkipTurnOnSnake(true);
        assertTrue(gameRules.isSkipTurnOnSnake());

        gameRules.setExtraTurnOnLadder(true);
        assertTrue(gameRules.isExtraTurnOnLadder());

        gameRules.setConsecutiveSixesLimit(5);
        assertEquals(5, gameRules.getConsecutiveSixesLimit());
    }

    @Test
    void testAdditionalData() {
        gameRules.setAdditionalData("key1", "value1");
        assertTrue(gameRules.hasAdditionalData("key1"));
        assertEquals("value1", gameRules.getAdditionalData("key1"));

        gameRules.setAdditionalData("key2", 42);
        assertEquals(42, gameRules.getIntAdditionalData("key2", 0));
        assertEquals(0, gameRules.getIntAdditionalData("key3", 0)); // Default value
    }

    @Test
    void testMaxRoll() {
        gameRules.setNumberOfDice(2);
        gameRules.setDiceSides(6);
        assertEquals(12, gameRules.getMaxRoll());

        gameRules.setNumberOfDice(3);
        gameRules.setDiceSides(8);
        assertEquals(24, gameRules.getMaxRoll());
    }
}