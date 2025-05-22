package idi.edu.idatt.mappe.controllers;

import idi.edu.idatt.mappe.models.*;
import idi.edu.idatt.mappe.models.enums.Direction;
import idi.edu.idatt.mappe.models.enums.GameState;
import idi.edu.idatt.mappe.models.enums.TokenType;
import idi.edu.idatt.mappe.utils.factory.GameRulesFactory;
import idi.edu.idatt.mappe.views.GameView;
import idi.edu.idatt.mappe.views.game.LostDiamondDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

/**
 * Controller for The Lost Diamond game.
 * Handles game-specific logic including token revealing, travel between cities, and win conditions.
 */
public class LostDiamondController extends BoardGameController implements LostDiamondObserver {
    private static final Logger logger = Logger.getLogger(LostDiamondController.class.getName());

    /**
     * Creates a new LostDiamondController.
     *
     * @param boardGame The board game model
     * @param gameView The game view
     * @param fileService The file service for file operations
     */
    public LostDiamondController(BoardGame boardGame, GameView gameView, FileService fileService) {
        super(boardGame, gameView, fileService);
        boardGame.addObserver(this);

        gameView.initializeForGameType();

        if (boardGame.getGameRules() == null) {
            logger.warning("No game rules found, creating emergency defaults");
            GameRules emergencyRules = GameRulesFactory.createLostDiamondRules();
            boardGame.setGameRules(emergencyRules);
        }

        GameRules rules = boardGame.getGameRules();
        logger.info("LostDiamondController using rules: " + rules.getRuleName() +
                " with starting money: " + getStartingMoney());

        logger.info("LostDiamondController initialized");
    }

    /**
     * Gets the starting money for players based on game rules.
     *
     * @return The starting money
     */
    private int getStartingMoney() {
        GameRules rules = boardGame.getGameRules();
        if (rules == null) {
            logger.warning("No rules available, using emergency default of 300");
            return 300;
        }

        Object startingMoneyObj = rules.getAdditionalData("startingMoney");
        if (startingMoneyObj instanceof Integer) {
            int money = (Integer) startingMoneyObj;
            logger.info("Using starting money from rules additional data: " + money);
            return money;
        }

        int defaultMoney = rules.getStartingMoney();
        logger.info("Using starting money from rules default: " + defaultMoney);
        return defaultMoney;
    }

    @Override
    public void playTurn() {
        if (boardGame.getGameState() != GameState.STARTED) {
            gameView.logGameEvent("Game has not started yet.");
            return;
        }

        if (animationInProgress) {
            gameView.logGameEvent("Please wait for current animation to complete.");
            return;
        }

        Player currentPlayer = boardGame.getCurrentPlayer();
        if (currentPlayer == null) {
            logger.warning("Current player is null");
            return;
        }

        if (currentPlayer.isMissingTurn()) {
            gameView.logGameEvent(currentPlayer.getName() + " misses this turn.");
            currentPlayer.setMissingTurn(false);
            advanceToNextPlayer();
            return;
        }

        showPlayerActions(currentPlayer);
    }

    @Override
    public void addPlayer(Player player) {
        int startingMoney = getStartingMoney();
        player.setMoney(startingMoney);

        super.addPlayer(player);

        placePlayerOnStartingTile(player);

        gameView.updatePlayerMoney(player);
        gameView.logGameEvent(player.getName() + " starts with " + startingMoney + " coins");

        logger.info("Initialized " + player.getName() + " with " + startingMoney + " coins");
    }

    /**
     * Places a player on one of the starting tiles (Cairo or Tangier)
     * Alternates between the two starting cities for different players
     *
     * @param player The player to place
     */
    private void placePlayerOnStartingTile(Player player) {
        Board board = boardGame.getBoard();
        if (board == null) {
            logger.warning("Board is null, cannot place player on starting tile");
            return;
        }

        List<Player> currentPlayers = boardGame.getPlayers();
        int playerIndex = currentPlayers.indexOf(player);

        int startingTileId = (playerIndex % 2 == 0) ? 1 : 2;

        Tile startingTile = board.getTileByIndex(startingTileId);

        if (startingTile != null) {
            player.placeOnTile(startingTile);
            logger.info("Placed " + player.getName() + " on starting tile " + startingTileId +
                    " (" + startingTile.getName() + ")");
        } else {
            logger.warning("Starting tile " + startingTileId + " not found, trying to find any starting city");

            for (Tile tile : board.getTiles().values()) {
                if (tile != null && tile.isStartingCity()) {
                    player.placeOnTile(tile);
                    logger.info("Placed " + player.getName() + " on starting city " + tile.getName());
                    return;
                }
            }

            logger.warning("No starting cities found for player " + player.getName());
        }
    }


    /**
     * Shows available actions for the player's turn (without action points)
     *
     * @param player The current player
     */
    private void showPlayerActions(Player player) {
        Tile currentTile = player.getCurrentTile();
        if (currentTile == null) {
            logger.warning("Current tile is null");
            return;
        }

        List<Tile> affordableTiles = getAffordableTiles(player, currentTile);

        if (affordableTiles.isEmpty() && !currentTile.hasToken()) {
            handleNoAvailableActions(player);
            return;
        }

        showActionDialog(player, currentTile, affordableTiles);
    }

    /**
     * Shows a dialog with available actions for the player (updated without action points)
     *
     * @param player The current player
     * @param currentTile The player's current location
     * @param affordableTiles Destinations the player can afford
     */
    private void showActionDialog(Player player, Tile currentTile, List<Tile> affordableTiles) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Your Turn - " + player.getName());
            alert.setHeaderText("You are in " + currentTile.getName());

            StringBuilder content = new StringBuilder();
            content.append("Money: ").append(player.getMoney()).append(" coins\n\n");

            if (player.hasDiamond()) {
                content.append("🌟 You have the LOST DIAMOND! 🌟\n");
                content.append("Return to Cairo or Tangier to win!\n\n");
            }

            content.append("Available actions:\n");

            boolean canTravel = !affordableTiles.isEmpty();
            boolean canBuyToken = currentTile.hasToken() && player.getMoney() >= currentTile.getTokenPrice();
            boolean canRollForToken = currentTile.hasToken() && shouldAllowDiceRoll();

            if (canTravel) {
                content.append("• Travel to another city (").append(affordableTiles.size()).append(" destinations available)\n");

                content.append("  Available destinations: ");
                for (int i = 0; i < Math.min(3, affordableTiles.size()); i++) {
                    content.append(affordableTiles.get(i).getName());
                    if (i < Math.min(2, affordableTiles.size() - 1)) content.append(", ");
                }
                if (affordableTiles.size() > 3) content.append("...");
                content.append("\n");
            }

            if (canBuyToken) {
                content.append("• Buy hidden token (").append(currentTile.getTokenPrice()).append(" coins)\n");
            }

            if (canRollForToken) {
                int diceRollThreshold = calculateDiceRollThreshold();
                content.append("• Roll dice to try for free token (need ").append(diceRollThreshold).append("+ to succeed)\n");
            }

            if (!canTravel && !canBuyToken && !canRollForToken) {
                content.append("• No actions available - you need more money!\n");
                content.append("  Cheapest travel option requires more coins.\n");
            }

            alert.setContentText(content.toString());

            ButtonType travelButton = new ButtonType("Travel");
            ButtonType buyTokenButton = new ButtonType("Buy Token");
            ButtonType rollTokenButton = new ButtonType("Roll for Token");
            ButtonType passButton = new ButtonType("Pass Turn");

            List<ButtonType> buttons = new ArrayList<>();
            if (canTravel) buttons.add(travelButton);
            if (canBuyToken) buttons.add(buyTokenButton);
            if (canRollForToken) buttons.add(rollTokenButton);
            buttons.add(passButton);

            alert.getButtonTypes().setAll(buttons);

            alert.showAndWait().ifPresent(response -> {
                if (response == travelButton) {
                    showDestinationDialog(player, currentTile, affordableTiles);
                } else if (response == buyTokenButton) {
                    buyToken(player);
                } else if (response == rollTokenButton) {
                    rollForFreeToken(player);
                } else {
                    handlePlayerPass(player);
                }
            });
        });
    }

    /**
     * Determines if dice rolling for free tokens should be allowed based on game rules
     *
     * @return true if dice rolling is allowed, false otherwise
     */
    private boolean shouldAllowDiceRoll() {
        GameRules rules = boardGame.getGameRules();
        if (rules == null) {
            logger.warning("No rules available, defaulting to allow dice rolls");
            return true;
        }

        Object allowDiceRolls = rules.getAdditionalData("allowDiceRollForTokens");
        boolean allowed = allowDiceRolls == null || (Boolean) allowDiceRolls;

        logger.fine("Dice roll for tokens allowed: " + allowed + " (from rules: " + rules.getRuleName() + ")");
        return allowed;
    }

    /**
     * Calculates the minimum dice roll needed to get a free token
     *
     * @return the threshold value
     */
    private int calculateDiceRollThreshold() {
        GameRules rules = boardGame.getGameRules();
        if (rules == null) {
            logger.warning("No rules for dice threshold, using default of 4");
            return 4;
        }

        Object customThreshold = rules.getAdditionalData("diceRollThreshold");
        if (customThreshold instanceof Integer integer) {
            int threshold = integer;
            logger.fine("Using custom dice threshold from rules: " + threshold);
            return threshold;
        }

        int diceSides = rules.getDiceSides();
        int numberOfDice = rules.getNumberOfDice();
        int maxRoll = numberOfDice * diceSides;
        int threshold = (maxRoll / 2) + 1;

        logger.fine("Calculated dice threshold: " + threshold +
                " (from " + numberOfDice + "d" + diceSides + ")");
        return threshold;
    }

    /**
     * Handles rolling dice to try to get a token for free
     *
     * @param player The player attempting to get a free token
     */
    private void rollForFreeToken(Player player) {
        Tile currentTile = player.getCurrentTile();

        if (!currentTile.hasToken()) {
            gameView.logGameEvent("No token available to claim.");
            advanceToNextPlayer();
            return;
        }

        List<Integer> diceValues = rollDice();
        if (diceValues.isEmpty()) {
            gameView.logGameEvent("Failed to roll dice.");
            advanceToNextPlayer();
            return;
        }

        int totalRoll = diceValues.stream().mapToInt(Integer::intValue).sum();
        int threshold = calculateDiceRollThreshold();

        gameView.logGameEvent(player.getName() + " rolled " + totalRoll + " (needed " + threshold + "+ for free token)");
        logRuleUsage("Dice Roll for Token", "Roll: " + totalRoll + ", Threshold: " + threshold);

        if (totalRoll >= threshold) {
            gameView.logGameEvent("Success! " + player.getName() + " gets the token for free!");

            int moneyBefore = player.getMoney();

            TokenType revealedToken = player.getCurrentTile().revealToken(player);

            if (revealedToken != null) {
                processTokenEffect(player, revealedToken);

                gameView.updatePlayerMoney(player);

                gameView.refreshBoardView();


                int finalMoney = player.getMoney();
                int netChange = finalMoney - moneyBefore;
                if (netChange != 0) {
                    gameView.logGameEvent(player.getName() + " now has " + finalMoney + " coins " +
                            "(change: " + (netChange > 0 ? "+" : "") + netChange + ")");
                }

                Platform.runLater(() -> {
                    LostDiamondDialog.showTokenRevealDialog(revealedToken);
                    advanceToNextPlayer();
                });
            } else {
                logger.warning("Token reveal returned null for player: " + player.getName());
                advanceToNextPlayer();
            }
        } else {
            gameView.logGameEvent("Failed! " + player.getName() + " can still buy the token or pass.");

            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Dice Roll Failed");
                alert.setHeaderText("You didn't roll high enough!");

                boolean canBuy = player.getMoney() >= currentTile.getTokenPrice();

                if (canBuy) {
                    alert.setContentText("Would you like to buy the token for " + currentTile.getTokenPrice() + " coins instead?");
                    alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
                } else {
                    alert.setContentText("You don't have enough money to buy the token (" + currentTile.getTokenPrice() + " coins needed).\nYour turn will pass.");
                    alert.getButtonTypes().setAll(ButtonType.OK);
                }

                alert.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.YES && canBuy) {
                        buyToken(player);
                    } else {
                        advanceToNextPlayer();
                    }
                });
            });
        }
    }

    /**
     * Handles buying a token with coins
     *
     * @param player The player buying the token
     */
    private void buyToken(Player player) {
        Tile currentTile = player.getCurrentTile();

        if (!currentTile.isCity() || !currentTile.hasToken()) {
            logger.warning("Attempted to buy token on non-city or city without token");
            advanceToNextPlayer();
            return;
        }

        int tokenPrice = currentTile.getTokenPrice();
        if (player.getMoney() < tokenPrice) {
            gameView.logGameEvent(player.getName() + " doesn't have enough money to buy the token.");
            advanceToNextPlayer();
            return;
        }

        gameView.logGameEvent(player.getName() + " paid " + tokenPrice + " coins to reveal the token.");
        logger.info(player.getName() + " paid " + tokenPrice + " coins to reveal the token, current money: " + player.getMoney());

        if (!player.spendMoney(tokenPrice)) {
            gameView.logGameEvent("Failed to process payment.");
            advanceToNextPlayer();
            return;
        }
        TokenType revealedToken = player.getCurrentTile().revealToken(player);


        if (revealedToken == null) {
            gameView.logGameEvent("Failed to reveal token.");
            player.addMoney(tokenPrice);
            advanceToNextPlayer();
            return;
        }
        processTokenEffect(player, revealedToken);

        gameView.updatePlayerMoney(player);

        gameView.refreshBoardView();


        Platform.runLater(() -> {
            LostDiamondDialog.showTokenRevealDialog(revealedToken);
            advanceToNextPlayer();
        });
    }
    /**
     * Processes the effect of a token on the player
     *
     * @param player The player who found the token
     * @param tokenType The type of token found
     */
    private void processTokenEffect(Player player, TokenType tokenType) {
        switch (tokenType) {
            case RUBY:
                gameView.logGameEvent(player.getName() + " found a ruby and gained " +
                        TokenType.RUBY.getValue() + " coins!");
                break;
            case EMERALD:
                gameView.logGameEvent(player.getName() + " found an emerald and gained " +
                        TokenType.EMERALD.getValue() + " coins!");
                break;
            case TOPAZ:
                gameView.logGameEvent(player.getName() + " found a topaz and gained " +
                        TokenType.TOPAZ.getValue() + " coins!");
                break;
            case DIAMOND:
                gameView.logGameEvent("🌟 " + player.getName() + " found THE LOST DIAMOND! 🌟");
                break;
            case THIEF:
                int stolenAmount = player.getMoney();
                gameView.logGameEvent(player.getName() + " encountered a THIEF! Lost " +
                        stolenAmount + " coins!");
                break;
            case EMPTY:
                gameView.logGameEvent(player.getName() + " found nothing of value.");
                break;
        }

        gameView.logGameEvent(player.getName() + " now has " + player.getMoney() + " coins.");
    }


    /**
     * Gets the list of affordable tiles for the player based on their current position.
     *
     * @param player The player
     * @param currentTile The player's current tile
     * @return List of affordable tiles
     */
    private List<Tile> getAffordableTiles(Player player, Tile currentTile) {
        List<Tile> affordableTiles = new ArrayList<>();

        if (currentTile == null) {
            logger.warning("Current tile is null for player: " + player.getName());
            return affordableTiles;
        }

        logger.info("Checking affordable tiles for " + player.getName() +
                " at " + currentTile.getName() +
                " with " + player.getMoney() + " coins");

        Map<Direction, Tile> connections = currentTile.getConnections();

        for (Map.Entry<Direction, Tile> entry : connections.entrySet()) {
            Direction direction = entry.getKey();
            Tile destination = entry.getValue();
            int travelCost = currentTile.getTravelCost(direction);

            logger.fine("Checking route " + direction + " to " + destination.getName() +
                    " costs " + travelCost + " coins");

            if (player.getMoney() >= travelCost) {
                affordableTiles.add(destination);
            }
        }

        logger.info("Found " + affordableTiles.size() + " affordable destinations for " + player.getName());
        return affordableTiles;
    }

    /**
     * Handles the case where the player has no available actions.
     *
     * @param player The player with no available actions
     */
    private void handleNoAvailableActions(Player player) {
        Tile currentTile = player.getCurrentTile();

        if (currentTile != null && !currentTile.getConnections().isEmpty()) {
            int cheapestCost = currentTile.getConnections().keySet().stream()
                    .mapToInt(currentTile::getTravelCost)
                    .min()
                    .orElse(0);

            gameView.logGameEvent(player.getName() + " has no available actions.");
            gameView.logGameEvent("Need at least " + cheapestCost + " coins to travel from " +
                    currentTile.getName() + ". Currently has " + player.getMoney() + " coins.");

            if (player.getMoney() < cheapestCost && cheapestCost > 0) {
                GameRules rules = boardGame.getGameRules();
                boolean emergencyEnabled = true;
                int emergencyAmount = 100;

                if (rules != null) {
                    Object emergencyEnabledObj = rules.getAdditionalData("emergencyMoneyEnabled");
                    if (emergencyEnabledObj instanceof Boolean) {
                        emergencyEnabled = (Boolean) emergencyEnabledObj;
                    }

                    Object emergencyAmountObj = rules.getAdditionalData("emergencyMoneyAmount");
                    if (emergencyAmountObj instanceof Integer) {
                        emergencyAmount = (Integer) emergencyAmountObj;
                    }
                }

                if (emergencyEnabled) {
                    int moneyNeeded = cheapestCost - player.getMoney();
                    int moneyToGive = Math.max(moneyNeeded, emergencyAmount);

                    player.setMoney(player.getMoney() + moneyToGive);
                    gameView.updatePlayerMoney(player);
                    gameView.logGameEvent("Emergency: " + player.getName() + " received " +
                            moneyToGive + " emergency coins to continue playing!");

                    logRuleUsage("Emergency Money Given", moneyToGive);
                    showPlayerActions(player);
                    return;
                } else {
                    gameView.logGameEvent("Emergency money is disabled. " + player.getName() +
                            " cannot continue without help!");
                }
            }
        } else {
            gameView.logGameEvent(player.getName() + " has no available actions and no connections from current tile.");
        }

        advanceToNextPlayer();
    }


    /**
     * Shows a dialog for the player to select their destination.
     *
     * @param player The player who is moving
     * @param currentTile The player's current tile
     * @param affordableTiles The tiles the player can afford to move to
     */
    private void showDestinationDialog(Player player, Tile currentTile, List<Tile> affordableTiles) {
        LostDiamondDialog dialog = new LostDiamondDialog(gameView, player, currentTile, affordableTiles);
        dialog.showDialogAndWait().ifPresent(result -> {
            if (result.getDestinationTile() != null) {
                executeMove(player, result.getDestinationTile());
            } else {
                handlePlayerPass(player);
            }
        });
    }

    /**
     * Handles the case where the player chooses to pass their turn.
     *
     * @param player The player who passed
     */
    private void handlePlayerPass(Player player) {
        gameView.logGameEvent(player.getName() + " passed their turn.");
        advanceToNextPlayer();
    }

    /**
     * Executes a move to the selected destination.
     *
     * @param player The player to move
     * @param destinationTile The destination tile
     */
    private void executeMove(Player player, Tile destinationTile) {
        Tile currentTile = player.getCurrentTile();
        Direction direction = null;

        for (Direction dir : currentTile.getAvailableDirections()) {
            if (currentTile.getConnectionInDirection(dir) == destinationTile) {
                direction = dir;
                break;
            }
        }

        if (direction == null) {
            logger.warning("Could not find direction to destination tile");
            return;
        }

        int travelCost = currentTile.getTravelCost(direction);

        if (player.getMoney() < travelCost) {
            gameView.logGameEvent(player.getName() + " doesn't have enough money to travel there.");
            return;
        }

        if (travelCost > 0) {
            player.spendMoney(travelCost);
            gameView.logGameEvent(player.getName() + " spent " + travelCost +
                    " coins to travel to " + destinationTile.getName() + ".");
            gameView.updatePlayerMoney(player);
        }

        player.placeOnTile(destinationTile);
        gameView.updatePlayerPosition(player);
        gameView.logGameEvent(player.getName() + " traveled to " + destinationTile.getName() + ".");

        handleCityArrival(player, destinationTile);
    }

    /**
     * Logs rule usage for debugging and analysis purposes
     *
     * @param action The action being performed
     * @param value The value associated with the action
     */
    private void logRuleUsage(String action, Object value) {
        GameRules rules = boardGame.getGameRules();
        String ruleName = rules != null ? rules.getRuleName() : "No Rules";
        logger.info("Rule Usage - Action: " + action + ", Value: " + value + ", Rules: " + ruleName);
    }

    /**
     * Handles logic when a player arrives at a city.
     *
     * @param player The player
     * @param city The city tile
     */
    private void handleCityArrival(Player player, Tile city) {
        if (city.isCity() && city.hasToken()) {
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("City with Hidden Token");
                alert.setHeaderText("You arrived at " + city.getName());

                StringBuilder content = new StringBuilder();
                content.append("This city has a hidden token costing ").append(city.getTokenPrice()).append(" coins.\n\n");
                content.append("Choose your action:");

                alert.setContentText(content.toString());

                List<ButtonType> buttons = new ArrayList<>();

                boolean canBuy = player.getMoney() >= city.getTokenPrice();
                boolean canRoll = shouldAllowDiceRoll();

                if (canBuy) {
                    buttons.add(new ButtonType("Buy Token (" + city.getTokenPrice() + " coins)"));
                }

                if (canRoll) {
                    int threshold = calculateDiceRollThreshold();
                    buttons.add(new ButtonType("Roll for Free (need " + threshold + "+)"));
                }

                buttons.add(new ButtonType("Pass Turn"));
                alert.getButtonTypes().setAll(buttons);

                alert.showAndWait().ifPresent(response -> {
                    String responseText = response.getText();
                    if (responseText.startsWith("Buy Token")) {
                        buyToken(player);
                    } else if (responseText.startsWith("Roll for Free")) {
                        rollForFreeToken(player);
                    } else {
                        advanceToNextPlayer();
                    }
                });
            });
        } else if (city.isStartingCity() && player.hasDiamond()) {
            boardGame.notifyGameWinner(player);
            boardGame.setGameState(GameState.FINISHED);
        } else {
            advanceToNextPlayer();
        }
    }

    @Override
    protected void movePlayer(Player player, int steps, Runnable callback) {
        if (callback != null) {
            callback.run();
        }
    }

    @Override
    public void onTokenRevealed(Player player, TokenType tokenType) {
        String message = player.getName() + " found ";
        switch (tokenType) {
            case DIAMOND:
                message += "THE LOST DIAMOND!";
                break;
            case RUBY:
                message += "a valuable ruby worth " + TokenType.RUBY.getValue() + " coins!";
                break;
            case EMERALD:
                message += "an emerald worth " + TokenType.EMERALD.getValue() + " coins!";
                break;
            case TOPAZ:
                message += "a topaz worth " + TokenType.TOPAZ.getValue() + " coins!";
                break;
            case THIEF:
                message += "a THIEF! All money and treasures were stolen!";
                break;
            case EMPTY:
                message += "nothing of value.";
                break;
        }

        gameView.logGameEvent(message);
    }

    @Override
    public void onDiamondFound(Player player) {
        gameView.logGameEvent("🌟 " + player.getName() + " found THE LOST DIAMOND! 🌟");
        gameView.logGameEvent("They must now return to either Cairo or Tangier to win the game!");
    }

    @Override
    public void onPlayerMoved(Player player, int steps) {
        gameView.logGameEvent(player.getName() + " moved to " +
                (player.getCurrentTile() != null ? player.getCurrentTile().getName() : "unknown location"));
        gameView.updatePlayerPosition(player);
    }

    @Override
    public void onGameStateChanged(GameState gameState) {
        gameView.logGameEvent("Game state changed to: " + gameState);
        if (gameState == GameState.FINISHED) {
            gameView.setRollDiceButtonEnabled(false);
        }
    }

    @Override
    public void onGameWinner(Player winner) {
        gameView.logGameEvent("🏆 " + winner.getName() + " has returned with the Lost Diamond and won the game! 🏆");
    }

    @Override
    public void onPlayerCaptured(Player captor, Player victim) {
    }

    @Override
    public void onPlayerExtraTurn(Player player) {
        gameView.logGameEvent(player.getName() + " gets an extra turn!");
        gameView.setRollDiceButtonEnabled(true);
    }

    @Override
    public void onPlayerSkipTurn(Player player) {
        gameView.logGameEvent(player.getName() + " loses a turn!");
    }

}