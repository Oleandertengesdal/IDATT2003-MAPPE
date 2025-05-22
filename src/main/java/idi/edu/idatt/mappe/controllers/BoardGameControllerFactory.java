package idi.edu.idatt.mappe.controllers;

import idi.edu.idatt.mappe.exceptions.InvalidGameTypeException;
import idi.edu.idatt.mappe.models.BoardGame;
import idi.edu.idatt.mappe.models.GameRules;
import idi.edu.idatt.mappe.models.enums.GameType;
import idi.edu.idatt.mappe.utils.factory.GameRulesFactory;
import idi.edu.idatt.mappe.views.GameView;

import java.util.logging.Logger;

import static java.util.logging.Logger.getLogger;

/**
 * Factory class for creating appropriate game controllers based on game type.
 * Implements the Factory design pattern.
 */
public class BoardGameControllerFactory {

    private static final Logger logger = getLogger(BoardGameControllerFactory.class.getName());


    /**
     * Private constructor to prevent instantiation.
     */
    private BoardGameControllerFactory() {}

    /**
     * Creates a controller for the given board game.
     *
     * @param boardGame The board game to create a controller for
     * @param gameView The game view
     * @param fileService The file service for file operations
     * @return A controller appropriate for the game type
     * @throws InvalidGameTypeException If the game type is not supported
     */
    public static BoardGameController createController(BoardGame boardGame, GameView gameView, FileService fileService)
            throws InvalidGameTypeException {
        GameType gameType = boardGame.getGameType();
        logger.info("Creating controller for game type: " + gameType);

        if (boardGame.getGameRules() == null) {
            logger.warning("No game rules found, creating emergency defaults for: " + gameType);
            switch (gameType) {
                case SNAKES_AND_LADDERS:
                    boardGame.setGameRules(GameRulesFactory.createSnakesAndLaddersRules());
                    break;
                case THE_LOST_DIAMOND:
                    boardGame.setGameRules(GameRulesFactory.createLostDiamondRules());
                    break;
                default:
                    boardGame.setGameRules(new GameRules(gameType));
                    break;
            }
            logger.info("Created emergency default rules for game type: " + gameType);
        } else {
            logger.info("Using existing game rules: " + boardGame.getGameRules().getRuleName());
        }

        return switch (gameType) {
            case SNAKES_AND_LADDERS -> new SnakesAndLaddersController(boardGame, gameView, fileService);
            case THE_LOST_DIAMOND -> new LostDiamondController(boardGame, gameView, fileService);
            default -> throw new InvalidGameTypeException("No controller available for game type: " + gameType);
        };
    }
}
