package idi.edu.idatt.mappe.utils.factory;

import idi.edu.idatt.mappe.controllers.FileService;
import idi.edu.idatt.mappe.exceptions.JsonParsingException;
import idi.edu.idatt.mappe.models.GameRules;
import idi.edu.idatt.mappe.models.enums.GameType;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Factory class for creating game rules files.
 * This class creates sample game rules files in the default rules directory.
 */
public class GameRulesFilesFactory {
    private static final Logger logger = Logger.getLogger(GameRulesFilesFactory.class.getName());

    private final FileService fileService;

    /**
     * Constructor for GameRulesFilesFactory.
     *
     * @param fileService The file service to use for saving rules
     */
    public GameRulesFilesFactory(FileService fileService) {
        this.fileService = fileService;
    }

    /**
     * Creates sample rules files if they don't already exist.
     *
     * @return True if all files were created successfully, false otherwise
     */
    public boolean createSampleRulesFiles() {
        boolean success = true;
        File rulesDir = fileService.getDefaultRulesDirectory();

        logger.info("Creating sample game rules files in: " + rulesDir.getAbsolutePath());

        success &= createStandardSnakesAndLaddersRules(rulesDir);
        success &= createChaosSnakesAndLaddersRules(rulesDir);
        success &= createQuickClimbRules(rulesDir);
        success &= createSnakePitRules(rulesDir);
        success &= createClassicSnakesAndLaddersRules(rulesDir);

        success &= createCustomSimpleRules(rulesDir);
        success &= createCustomChallengeRules(rulesDir);
        success &= createCustomAdvancedRules(rulesDir);

        return success;
    }

    /**
     * Creates a rules file for the standard Snakes and Ladders game.
     *
     * @param rulesDir The directory to save the file in
     * @return True if the file was created successfully, false otherwise
     */
    private boolean createStandardSnakesAndLaddersRules(File rulesDir) {
        GameRules rules = GameRulesFactory.createSnakesAndLaddersRules();
        return saveRulesFile(rulesDir, rules, "standard_snl_rules.json");
    }

    /**
     * Creates a rules file for the Chaos Snakes and Ladders game.
     *
     * @param rulesDir The directory to save the file in
     * @return True if the file was created successfully, false otherwise
     */
    private boolean createChaosSnakesAndLaddersRules(File rulesDir) {
        GameRules rules = GameRulesFactory.createChaosSnakesAndLaddersRules();
        return saveRulesFile(rulesDir, rules, "chaos_snl_rules.json");
    }

    /**
     * Creates a rules file for the Quick Climb game.
     *
     * @param rulesDir The directory to save the file in
     * @return True if the file was created successfully, false otherwise
     */
    private boolean createQuickClimbRules(File rulesDir) {
        GameRules rules = GameRulesFactory.createQuickSnakesAndLaddersRules();
        return saveRulesFile(rulesDir, rules, "quick_climb_rules.json");
    }

    /**
     * Creates a rules file for the Snake Pit game.
     *
     * @param rulesDir The directory to save the file in
     * @return True if the file was created successfully, false otherwise
     */
    private boolean createSnakePitRules(File rulesDir) {
        GameRules rules = GameRulesFactory.createChallengingSnakesAndLaddersRules();
        return saveRulesFile(rulesDir, rules, "snake_pit_rules.json");
    }

    /**
     * Creates a rules file for the Classic Snakes and Ladders game.
     *
     * @param rulesDir The directory to save the file in
     * @return True if the file was created successfully, false otherwise
     */
    private boolean createClassicSnakesAndLaddersRules(File rulesDir) {
        GameRules rules = GameRulesFactory.createClassicSnakesAndLaddersRules();
        return saveRulesFile(rulesDir, rules, "classic_snl_rules.json");
    }

    /**
     * Creates a rules file for a simple custom board.
     *
     * @param rulesDir The directory to save the file in
     * @return True if the file was created successfully, false otherwise
     */
    private boolean createCustomSimpleRules(File rulesDir) {
        GameRules rules = new GameRules(GameType.SNAKES_AND_LADDERS);
        rules.setBoardVariant("Custom");
        rules.setNumberOfDice(2);
        rules.setDiceSides(6);
        rules.setExtraThrowOnMax(true);

        return saveRulesFile(rulesDir, rules, "custom_simple_rules.json");
    }

    /**
     * Creates a rules file for a challenging custom board.
     *
     * @param rulesDir The directory to save the file in
     * @return True if the file was created successfully, false otherwise
     */
    private boolean createCustomChallengeRules(File rulesDir) {
        GameRules rules = new GameRules(GameType.SNAKES_AND_LADDERS);
        rules.setBoardVariant("Custom");
        rules.setNumberOfDice(1);
        rules.setDiceSides(6);
        rules.setSkipTurnOnSnake(true);

        return saveRulesFile(rulesDir, rules, "custom_challenge_rules.json");
    }

    /**
     * Creates a rules file for an advanced custom board.
     *
     * @param rulesDir The directory to save the file in
     * @return True if the file was created successfully, false otherwise
     */
    private boolean createCustomAdvancedRules(File rulesDir) {
        GameRules rules = new GameRules(GameType.SNAKES_AND_LADDERS);
        rules.setBoardVariant("Custom");
        rules.setNumberOfDice(2);
        rules.setDiceSides(8);
        rules.setExtraThrowOnMax(true);
        rules.setExtraTurnOnLadder(true);

        return saveRulesFile(rulesDir, rules, "custom_advanced_rules.json");
    }

    /**
     * Helper method to save a rules file.
     *
     * @param rulesDir The directory to save the file in
     * @param rules The rules to save
     * @param fileName The name of the file
     * @return True if the file was saved successfully, false otherwise
     */
    private boolean saveRulesFile(File rulesDir, GameRules rules, String fileName) {
        File file = new File(rulesDir, fileName);

        // Skip if file already exists
        if (file.exists()) {
            logger.info("Rules file already exists: " + file.getAbsolutePath());
            return true;
        }

        try {
            fileService.saveGameRulesToFile(rules, file);
            logger.info("Created rules file: " + file.getAbsolutePath());
            return true;
        } catch (IOException | JsonParsingException e) {
            logger.warning("Failed to create rules file: " + file.getAbsolutePath());
            return false;
        }
    }
}