package idi.edu.idatt.mappe.controllers;

import idi.edu.idatt.mappe.BoardgameMain;
import idi.edu.idatt.mappe.models.*;
import idi.edu.idatt.mappe.models.tileaction.LadderTileAction;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.util.HashMap;
import java.util.Map;

public class GameController extends BoardGameObserver {
    private final BoardgameMain app;
    private final BoardGame game;
    private final BorderPane view;

    private Canvas boardCanvas;
    private GraphicsContext gc;
    private Map<Player, Image> playerImages = new HashMap<>();
    private Map<Tile, Point2D> tilePositions = new HashMap<>();

    public GameController(BoardgameMain app, BoardGame game) {
        this.app = app;
        this.game = game;
        this.view = new BorderPane();

        game.addObserver(this);
        initializeUI();
        initializeBoard();
    }

    private void initializeUI() {
        // Top section - Game info
        HBox topBox = new HBox(10);
        topBox.setPadding(new Insets(10));
        topBox.setAlignment(Pos.CENTER);

        Label gameNameLabel = new Label("game");
        gameNameLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Button backButton = new Button("Back to Menu");
        backButton.setOnAction(e -> app.showGameSelectionScreen());

        topBox.getChildren().addAll(gameNameLabel, backButton);
        view.setTop(topBox);

        // Center - Game board
        boardCanvas = new Canvas(800, 600);
        gc = boardCanvas.getGraphicsContext2D();
        view.setCenter(boardCanvas);

        // Bottom - Controls and player info
        VBox bottomBox = new VBox(10);
        bottomBox.setPadding(new Insets(10));

        HBox playerInfoBox = new HBox(20);
        playerInfoBox.setAlignment(Pos.CENTER);

        for (Player player : game.getPlayers()) {
            VBox playerBox = new VBox(5);
            playerBox.setAlignment(Pos.CENTER);

            //ImageView pieceView = new ImageView(getPlayerImage(player));
            //pieceView.setFitWidth(30);
            //pieceView.setFitHeight(30);

            Label nameLabel = new Label(player.getName());
            Label positionLabel = new Label("Position: " + player.getCurrentTile().getIndex());

           // playerBox.getChildren().addAll(pieceView, nameLabel, positionLabel);
            playerBox.setUserData(player); // Store player reference

            playerInfoBox.getChildren().add(playerBox);
        }

        Button rollButton = new Button("Roll Dice");
        //rollButton.setOnAction(e -> rollDice());
        rollButton.setStyle("-fx-font-size: 16px; -fx-padding: 10 20;");

        bottomBox.getChildren().addAll(playerInfoBox, rollButton);
        view.setBottom(bottomBox);
    }

    private void initializeBoard() {
        // Calculate positions for all tiles
        Board board = game.getBoard();
        int rows = board.getRows();
        int cols = board.getColumns();

        double tileWidth = boardCanvas.getWidth() / (cols + 2);
        double tileHeight = boardCanvas.getHeight() / (rows + 2);

        // Snake and ladder boards typically have a back-and-forth pattern
        boolean leftToRight = true;
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                int displayCol = leftToRight ? col : (cols - 1 - col);
                Tile tile = board.getTileByCoordinates(row, col);
                if (tile != null) {
                    double x = (displayCol + 1) * tileWidth;
                    double y = boardCanvas.getHeight() - (row + 1) * tileHeight;
                    tilePositions.put(tile, new Point2D(x, y));
                }
            }
            leftToRight = !leftToRight;
        }

        drawBoard();
    }

    private void drawBoard() {
        gc.clearRect(0, 0, boardCanvas.getWidth(), boardCanvas.getHeight());

        // Draw tiles
        for (Map.Entry<Tile, Point2D> entry : tilePositions.entrySet()) {
            Tile tile = entry.getKey();
            Point2D pos = entry.getValue();

            // Draw tile background
            gc.setFill(getTileColor(tile));
            gc.fillRect(pos.getX() - 30, pos.getY() - 30, 60, 60);

            // Draw tile ID
            gc.setFill(Color.BLACK);
            gc.fillText(String.valueOf(tile.getIndex()), pos.getX(), pos.getY());

            // Draw special tile indicators (snakes, ladders, etc.)
            if (tile.getLandAction() != null) {
                drawTileAction(tile, pos);
            }
        }

        // Draw players
    }

    private void drawTileAction(Tile tile, Point2D pos) {
        // Draw the tileactions.
    }

    private Color getTileColor(Tile tile) {
        //gets the color of the tiles.
        return Color.LIGHTGRAY;
    }

    public Parent getView() {
        return view;
    }

}