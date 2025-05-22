package idi.edu.idatt.mappe.controllers;

import idi.edu.idatt.mappe.models.Board;
import idi.edu.idatt.mappe.models.GameRules;
import idi.edu.idatt.mappe.models.enums.GameType;
import idi.edu.idatt.mappe.utils.factory.GameRulesFactory;
import idi.edu.idatt.mappe.views.GameRulesView;
import javafx.application.Platform;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller for game rules configuration.
 * Handles creating, loading, saving, and applying game rules.
 */
public class GameRulesController {
    private static final Logger LOGGER = Logger.getLogger(GameRulesController.class.getName());

    private GameRules currentRules;
    private final GameRulesView rulesView;
    private final FileService fileService;
    private final Consumer<GameRules> onRulesConfirmedCallback;

    private boolean isJsonBoardLoaded = false;
    private File loadedBoardFile = null;
    private Board loadedBoard = null;

    private final Map<String, String> boardVariants = new HashMap<>();
    private final Map<String, GameRules> predefinedRules = new HashMap<>();

    /**
     * Creates a new GameRulesController.
     *
     * @param gameType The type of game to configure rules for
     * @param rulesView The view to display the rules
     * @param fileService The file service for loading/saving rules
     * @param onRulesConfirmedCallback Callback to execute when rules are confirmed
     */
    public GameRulesController(GameType gameType, GameRulesView rulesView, FileService fileService,
                               Consumer<GameRules> onRulesConfirmedCallback) {
        this.rulesView = rulesView;
        this.fileService = fileService;
        this.onRulesConfirmedCallback = onRulesConfirmedCallback;

        initializePredefinedRules();
        initializeBoardVariants(gameType);

        setDefaultRules(gameType);

        rulesView.setController(this);

        LOGGER.info("GameRulesController initialized for game type: " + gameType);
    }

    /**
     * Initialize predefined rule sets for different game types
     */
    private void initializePredefinedRules() {
        // Snakes and Ladders variants
        predefinedRules.put("Classic Rules", GameRulesFactory.createSnakesAndLaddersRules());
        predefinedRules.put("Chaos Rules", GameRulesFactory.createChaosSnakesAndLaddersRules());
        predefinedRules.put("Quick Game Rules", GameRulesFactory.createQuickSnakesAndLaddersRules());
        predefinedRules.put("Challenge Rules", GameRulesFactory.createChallengingSnakesAndLaddersRules());
        predefinedRules.put("Single Die Rules", GameRulesFactory.createClassicSnakesAndLaddersRules());

        predefinedRules.put("Standard Lost Diamond", GameRulesFactory.createLostDiamondRules());
        predefinedRules.put("Easy Lost Diamond", GameRulesFactory.createEasyLostDiamondRules());
        predefinedRules.put("Challenging Lost Diamond", GameRulesFactory.createChallengingLostDiamondRules());


        LOGGER.info("Initialized " + predefinedRules.size() + " predefined rule sets");
    }

    /**
     * Initialize board variants for the given game type
     *
     * @param gameType The game type to initialize variants for
     */
    private void initializeBoardVariants(GameType gameType) {
        boardVariants.clear();

        switch (gameType) {
            case SNAKES_AND_LADDERS:
                boardVariants.put("Standard Snakes and Ladders", "Standard");
                boardVariants.put("Chaos Snakes and Ladders", "Chaos");
                boardVariants.put("Quick Climb", "Ladder-Heavy");
                boardVariants.put("Snake Pit", "Snake-Heavy");
                boardVariants.put("Custom Board", "Custom");
                break;
            case THE_LOST_DIAMOND:
                boardVariants.put("Standard Lost Diamond", "Standard");
                boardVariants.put("Easy Lost Diamond", "Easy");
                boardVariants.put("Challenging Lost Diamond", "Challenging");
                break;
            default:
                break;
        }

        LOGGER.info("Initialized " + boardVariants.size() + " board variants for " + gameType);
    }

    /**
     * Set default rules based on game type
     */
    private void setDefaultRules(GameType gameType) {
        switch (gameType) {
            case SNAKES_AND_LADDERS:
                currentRules = predefinedRules.get("Classic Rules");
                break;
            case THE_LOST_DIAMOND:
                currentRules = predefinedRules.get("Standard Lost Diamond");
                break;
            default:
                currentRules = new GameRules(gameType);
                break;
        }

        LOGGER.info("Set default rules for game type: " + gameType);
    }
    /**
     * Get a list of available board variants for the current game type
     *
     * @return List of variant display names
     */
    public List<String> getAvailableBoardVariants() {
        return new ArrayList<>(boardVariants.keySet());
    }

    /**
     * Get the current game rules
     *
     * @return The current game rules
     */
    public GameRules getCurrentRules() {
        return currentRules;
    }

    /**
     * Set the current game rules and update the view
     *
     * @param rules The rules to set
     */
    public void setCurrentRules(GameRules rules) {
        this.currentRules = rules;
        rulesView.updateView();
        LOGGER.info("Set current rules to: " + rules.toString());
    }

    /**
     * Select a board variant without triggering a view update.
     * This prevents potential infinite recursion issues.
     *
     * @param variantDisplayName The display name of the variant to select
     */
    public void selectVariantWithoutViewUpdate(String variantDisplayName) {
        if (boardVariants.containsKey(variantDisplayName)) {
            String variantKey = boardVariants.get(variantDisplayName);

            if (predefinedRules.containsKey(variantDisplayName)) {
                GameRules newRules = predefinedRules.get(variantDisplayName);
                this.currentRules = newRules;
                LOGGER.info("Switched to predefined rules: " + variantDisplayName + " with starting money: " + newRules.getStartingMoney());
            } else {
                this.currentRules.setBoardVariant(variantKey);
                LOGGER.info("Selected board variant without view update: " + variantDisplayName);
            }
        } else {
            LOGGER.warning("Board variant not found: " + variantDisplayName);
        }
    }

    /**
     * Loads a board from a JSON file and updates the view accordingly.
     *
     * @param file The JSON file containing board data
     */
    public void loadBoardFromFile(File file) {
        LOGGER.info("Loading board from file: " + file.getAbsolutePath());
        try {
            Board loadedBoardFromFile = fileService.loadBoardFromFile(file);

            if (loadedBoardFromFile != null) {
                isJsonBoardLoaded = true;
                loadedBoardFile = file;
                this.loadedBoard = loadedBoardFromFile;

                String boardName = file.getName().replace(".json", "");
                String boardDescription = "Custom board loaded from " + file.getName();

                currentRules.setGameType(loadedBoardFromFile.getGameType());
                rulesView.setJsonLoadedBoard(boardName, boardDescription);

                LOGGER.info("Successfully loaded board from file: " + boardName);
            } else {
                rulesView.showError("Loading Error", "Failed to load board data from the file.");
                LOGGER.warning("Board loaded from file was null");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error loading board from file", e);
            rulesView.showError("Loading Error", "Failed to load board: " + e.getMessage());
        }
    }

    /**
     * Save the current rules to a file with the given filename
     *
     * @param filename The name to use for the file (without extension)
     */
    public void saveRulesToFile(String filename) {
        LOGGER.info("Saving rules to file: " + filename);

        try {
            if (!filename.endsWith(".json")) {
                filename += ".json";
            }

            File directory = fileService.getDefaultRulesDirectory();
            File file = new File(directory, filename);

            if (file.exists()) {
                boolean overwrite = rulesView.confirmOverwrite(filename);
                if (!overwrite) {
                    LOGGER.info("Save operation cancelled - user chose not to overwrite");
                    return;
                }
            }

            String ruleName = filename.replace(".json", "");
            currentRules.setRuleName(ruleName);

            fileService.saveGameRulesToFile(currentRules, file);
            rulesView.showInfo("Rules Saved", "Game rules were successfully saved as " + filename);
        } catch (Exception e) {
            LOGGER.warning("Error saving rules to file: " + e.getMessage());
            rulesView.showError("Saving Error", "Failed to save rules: " + e.getMessage());
        }
    }


    /**
     * Apply the current rules and return them via callback
     */
    public void applyRules() {
        try {
            LOGGER.info("Applying rules: " + currentRules.getRuleName());

            if (isJsonBoardLoaded && loadedBoard != null) {
                currentRules.setAdditionalData("loadedFromJson", true);
                currentRules.setAdditionalData("loadedBoardFile", loadedBoardFile);
                currentRules.setAdditionalData("loadedBoard", loadedBoard);
            }

            if ("Custom".equals(currentRules.getBoardVariant())) {
                int snakeCount = rulesView.getSnakeCount();
                int ladderCount = rulesView.getLadderCount();
                int randomTeleportCount = rulesView.getRandomTeleportCount();
                int skipTurnCount = rulesView.getSkipTurnCount();
                int extraTurnCount = rulesView.getExtraTurnCount();

                currentRules.setAdditionalData("customSnakeCount", snakeCount);
                currentRules.setAdditionalData("customLadderCount", ladderCount);
                currentRules.setAdditionalData("customRandomTeleportCount", randomTeleportCount);
                currentRules.setAdditionalData("customSkipTurnCount", skipTurnCount);
                currentRules.setAdditionalData("customExtraTurnCount", extraTurnCount);

                String description = String.format(
                        "Custom board with %d snakes, %d ladders, %d random teleports, %d skip turn tiles, and %d extra turn tiles",
                        snakeCount, ladderCount, randomTeleportCount, skipTurnCount, extraTurnCount
                );
                currentRules.setRuleDescription(description);
            }

            if (onRulesConfirmedCallback != null) {
                onRulesConfirmedCallback.accept(currentRules);
            }

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to apply rules", e);
            rulesView.showError("Failed to apply rules", e.getMessage());
        }
    }

    /**
     * Loads game rules from a file
     *
     * @param file The file to load rules from
     * @return The loaded game rules
     */
    public GameRules loadRulesFromFile(File file) {
        LOGGER.info("Loading rules from file: " + file.getAbsolutePath());
        try {
            GameRules rules = fileService.loadGameRulesFromFile(file);
            setCurrentRules(rules);
            return rules;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error loading rules from file", e);
            rulesView.showError("Loading Error", "Failed to load rules: " + e.getMessage());
            return null;
        }
    }

    /**
     * Gets the default directory for game rules files.
     *
     * @return The default directory for game rules files
     */
    public File getDefaultRulesDirectory() {
        return fileService.getDefaultRulesDirectory();
    }


    /**
     * Cancel rules configuration and return to previous view
     */
    public void cancelRules() {
        if (onRulesConfirmedCallback != null) {
            onRulesConfirmedCallback.accept(null);
        }
    }
}