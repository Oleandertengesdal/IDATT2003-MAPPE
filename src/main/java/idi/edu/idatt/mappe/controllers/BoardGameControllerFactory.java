package idi.edu.idatt.mappe.controllers;

import idi.edu.idatt.mappe.exceptions.InvalidGameTypeException;
import idi.edu.idatt.mappe.models.BoardGame;
import idi.edu.idatt.mappe.models.enums.GameType;
import idi.edu.idatt.mappe.views.GameView;

/**
 * Factory class for creating appropriate game controllers based on game type.
 * Implements the Factory design pattern.
 */
public class BoardGameControllerFactory {


    /**
     * Private constructor to prevent instantiation.
     */
    private BoardGameControllerFactory() {}

    /**
     * Creates and returns a controller appropriate for the given board game.
     *
     * @param boardGame The board game model
     * @param gameView The game view
     * @param fileService The file service for file operations
     * @return A controller appropriate for the board game type
     */
    public static BoardGameController createController(BoardGame boardGame,
                                                       GameView gameView,
                                                       FileService fileService) throws InvalidGameTypeException {

        GameType gameType = boardGame.getGameType();
        return switch (gameType) {
            case LUDO -> new LudoController(boardGame, gameView, fileService);
            case SNAKES_AND_LADDERS -> new SnakesAndLaddersController(boardGame, gameView, fileService);
            default -> throw new InvalidGameTypeException("Unsupported game type: " + gameType);
        };
    }
}