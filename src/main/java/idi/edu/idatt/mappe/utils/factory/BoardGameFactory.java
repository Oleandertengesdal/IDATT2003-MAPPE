package idi.edu.idatt.mappe.utils.factory;

import idi.edu.idatt.mappe.exceptions.InvalidGameTypeException;
import idi.edu.idatt.mappe.models.Board;
import idi.edu.idatt.mappe.models.BoardGame;
import idi.edu.idatt.mappe.models.GameRules;
import idi.edu.idatt.mappe.models.enums.GameType;


import java.util.logging.Logger;

/**
 * Factory class for creating different variants of board games.
 * Modified to work with separate Board and GameRules classes.
 */
public class BoardGameFactory {

    private static final Logger logger = Logger.getLogger(BoardGameFactory.class.getName());

    private BoardGameFactory() {
        // Private constructor to prevent instantiation
    }

    /**
     * Creates a classic board game with 90 tiles
     *
     * @return A classic board game
     */
    public static BoardGame createClassicGame() {
        BoardGame game = new BoardGame(GameType.SNAKES_AND_LADDERS);

        GameRules rules = GameRulesFactory.createSnakesAndLaddersRules();
        game.setGameRules(rules);

        Board board = BoardFactory.createClassicBoard();
        game.setBoard(board);

        game.createDice(rules.getNumberOfDice(), rules.getDiceSides());

        return game;
    }

    /**
     * Creates a game based on the game type
     *
     * @param gameType The type of game to create
     * @return A board game of the specified type
     * @throws InvalidGameTypeException If the game type is invalid
     */
    public static BoardGame createGame(GameType gameType) throws InvalidGameTypeException {
        return switch (gameType) {
            case SNAKES_AND_LADDERS -> createClassicGame();
            case THE_LOST_DIAMOND -> createLostDiamondGame();
            default -> throw new InvalidGameTypeException("Invalid game type: " + gameType);
        };
    }


    /**
     * Creates a custom board game based on the provided rules
     *
     * @param rules The game rules to apply
     * @return A custom board game configured according to the rules
     */
    public static BoardGame createCustomGame(GameRules rules) {
        BoardGame game = new BoardGame(rules.getGameType());
        game.setGameRules(rules);

        if (rules.getGameType() == GameType.THE_LOST_DIAMOND) {
            int startingMoney = rules.getStartingMoney();
            rules.setAdditionalData("startingMoney", startingMoney);

            if (!rules.hasAdditionalData("tokenPrice")) {
                rules.setAdditionalData("tokenPrice", 100);
            }
            if (!rules.hasAdditionalData("travelCostModifier")) {
                rules.setAdditionalData("travelCostModifier", 1.0);
            }
        }


        Board board;
        switch (rules.getGameType()) {
            case SNAKES_AND_LADDERS:
                switch (rules.getBoardVariant()) {
                    case "Chaos":
                        board = BoardFactory.createChaosBoard();
                        break;
                    case "Ladder-Heavy":
                        board = BoardFactory.createLadderHeavyBoard();
                        break;
                    case "Snake-Heavy":
                        board = BoardFactory.createSnakeHeavyBoard();
                        break;
                    case "Custom":
                        int snakeCount = rules.getIntAdditionalData("customSnakeCount", 8);
                        int ladderCount = rules.getIntAdditionalData("customLadderCount", 8);
                        int randomTeleportCount = rules.getIntAdditionalData("customRandomTeleportCount", 0);
                        int skipTurnCount = rules.getIntAdditionalData("customSkipTurnCount", 0);
                        int extraTurnCount = rules.getIntAdditionalData("customExtraTurnCount", 0);

                        board = BoardFactory.createCustomBoard(snakeCount, ladderCount,
                                randomTeleportCount, skipTurnCount,
                                extraTurnCount);
                        break;
                    case "Standard":
                    default:
                        board = BoardFactory.createClassicBoard();
                        break;
                }
                break;
            case THE_LOST_DIAMOND:
                board = switch (rules.getBoardVariant()) {
                    case "Easy" -> BoardFactory.createEasyLostDiamondBoard();
                    case "Challenging" -> BoardFactory.createChallengingLostDiamondBoard();
                    default -> BoardFactory.createLostDiamondBoard();
                };
                break;
            default:
                board = BoardFactory.createGenericBoard(rules.getGameType(), 9, 10);
                break;
        }

        game.setBoard(board);
        game.createDice(rules.getNumberOfDice(), rules.getDiceSides());

        return game;
    }

    /**
     * Creates a standard Lost Diamond game
     *
     * @return A standard Lost Diamond game
     */
    public static BoardGame createLostDiamondGame() {
        BoardGame game = new BoardGame(GameType.THE_LOST_DIAMOND);

        GameRules rules = GameRulesFactory.createLostDiamondRules();
        rules.setStartingMoney(300);
        rules.setAdditionalData("startingMoney", 300);

        game.setGameRules(rules);

        Board board = BoardFactory.createLostDiamondBoard();
        game.setBoard(board);

        game.createDice(rules.getNumberOfDice(), rules.getDiceSides());

        return game;
    }

}