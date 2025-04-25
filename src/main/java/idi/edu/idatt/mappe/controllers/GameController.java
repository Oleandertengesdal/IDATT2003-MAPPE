package idi.edu.idatt.mappe.controllers;

import idi.edu.idatt.mappe.exceptions.JsonParsingException;
import idi.edu.idatt.mappe.exceptions.TileActionNotFoundException;
import idi.edu.idatt.mappe.files.reader.BoardFileReaderGson;
import idi.edu.idatt.mappe.files.writer.BoardFileWriterGson;
import idi.edu.idatt.mappe.models.Board;
import idi.edu.idatt.mappe.models.BoardGame;
import idi.edu.idatt.mappe.models.GameState;
import idi.edu.idatt.mappe.models.Player;
import idi.edu.idatt.mappe.views.GameView;

import idi.edu.idatt.mappe.files.reader.PlayerFileReaderCVS;
import idi.edu.idatt.mappe.files.writer.PlayerFileWriterCVS;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class GameController {

    private final BoardGame boardGame;
    private GameView gameView;

    public GameController(BoardGame boardGame, GameView gameView) {
        this.boardGame = boardGame;
        this.gameView = gameView;
        boardGame.addObserver(gameView);
    }

    public void addPlayer(String name) {
        Player player = new Player(name);
        boardGame.addPlayer(player);
        gameView.addPlayer(player);
    }

    public void startGame() {
        boardGame.startGame();
    }

    public void playTurn() {
        boardGame.playOneRound();
    }

    public GameState getGameState() {
        return boardGame.getGameState();
    }

    public void loadPlayersFromCsv(File file) throws IOException {
        PlayerFileReaderCVS reader = new PlayerFileReaderCVS();
        List<Player> players = reader.loadPlayers(file.getAbsolutePath());
        players.stream()
                .filter(player -> boardGame.getPlayers().stream().noneMatch(p -> p.getName().equals(player.getName())))
                .forEach(player -> {
                    boardGame.addPlayer(player);
                    gameView.addPlayer(player);
                });
    }

    public void savePlayersToCsv(File file) throws IOException {
        PlayerFileWriterCVS writer = new PlayerFileWriterCVS();
        writer.savePlayers(boardGame.getPlayers(), file.getAbsolutePath());
    }

    public void loadBoardFromFile(File file) throws JsonParsingException {
        Board board = new BoardFileReaderGson().readBoard(file.getAbsolutePath());
        boardGame.setBoard(board);
        boardGame.createDice(2, 6);
        gameView = new GameView(board);
    }

    public void saveBoardToFile(File file) throws IOException, JsonParsingException, TileActionNotFoundException {
        new BoardFileWriterGson().writeBoard(boardGame.getBoard(), file.getAbsolutePath());
    }

    public GameView getView() {
        return gameView;
    }
}