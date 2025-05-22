package idi.edu.idatt.mappe.views.game;

import idi.edu.idatt.mappe.models.Board;
import idi.edu.idatt.mappe.models.Tile;
import idi.edu.idatt.mappe.models.enums.GameType;
import idi.edu.idatt.mappe.models.enums.TileType;
import idi.edu.idatt.mappe.models.tileaction.*;
import idi.edu.idatt.mappe.services.ColorService;
import idi.edu.idatt.mappe.utils.CoordinateConverter;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * View component for displaying the game board.
 * Handles rendering the board with tiles and special connections.
 */
public class BoardView {
    private static final Logger logger = Logger.getLogger(BoardView.class.getName());

    private final Pane boardPane;
    private final ColorService colorService;
    private final Board board;

    private final double boardWidth;
    private final double boardHeight;

    private final Map<Integer, Rectangle> tileRectangles = new HashMap<>();
    private final Map<Integer, Circle> cityCircles = new HashMap<>();
    private final Map<Integer, Integer> ladderDestinations = new HashMap<>();
    private final Map<Integer, Integer> snakeDestinations = new HashMap<>();

    /**
     * Creates a new BoardView.
     *
     * @param boardPane The pane to draw the board on
     * @param colorService The color service for tile colors
     * @param board The board to display
     * @param boardWidth The width of the board
     * @param boardHeight The height of the board
     */
    public BoardView(Pane boardPane, ColorService colorService, Board board,
                     double boardWidth, double boardHeight) {
        this.boardPane = boardPane;
        this.colorService = colorService;
        this.board = board;
        this.boardWidth = boardWidth;
        this.boardHeight = boardHeight;

        if (board.getGameType() == GameType.SNAKES_AND_LADDERS) {
            identifySpecialTileDestinations();
        }
        drawBoard();

        logger.info("BoardView initialized");
    }

    /**
     * Identifies destinations of special tiles like ladders and snakes.
     */
    private void identifySpecialTileDestinations() {
        ladderDestinations.clear();
        snakeDestinations.clear();

        for (Tile tile : board.getTiles().values()) {
            if (tile == null) continue;

            TileAction action = tile.getLandAction();
            if (action instanceof LadderTileAction ladderTileAction) {
                int targetId = ladderTileAction.getDestinationTileId();
                ladderDestinations.put(targetId, tile.getIndex());
            } else if (action instanceof SnakeTileAction snakeTileAction) {
                int targetId = snakeTileAction.getDestinationTileId();
                snakeDestinations.put(targetId, tile.getIndex());
            }
        }
    }

    /**
     * Draws the game board with all tiles and special connections.
     */
    private void drawBoard() {
        if (board == null || board.getTiles() == null || board.getTiles().isEmpty()) {
            logger.severe("Board or tiles are not initialized or empty.");
            return;
        }

        boardPane.getChildren().clear();
        tileRectangles.clear();
        cityCircles.clear();

        GameType gameType = board.getGameType();

        switch (gameType) {
            case SNAKES_AND_LADDERS:
                drawSnakesAndLaddersBoard();
                break;
            case THE_LOST_DIAMOND:
                drawLostDiamondBoard();
                break;
            default:
                logger.warning("Unsupported game type: " + gameType);
                break;
        }

        logger.info("Board drawn with " + board.getTiles().size() + " tiles for game type: " + gameType);
    }

    /**
     * Draws the board for the Snakes and Ladders game.
     */
    private void drawSnakesAndLaddersBoard() {
        int rows = board.getRows() > 0 ? board.getRows() : 9;
        int cols = board.getColumns() > 0 ? board.getColumns() : 10;

        double tileWidth = boardWidth / cols;
        double tileHeight = boardHeight / rows;

        List<ConnectionView> connections = new ArrayList<>();

        for (Tile tile : board.getTiles().values()) {
            if (tile == null) continue;

            int x = tile.getX();
            int y = tile.getY();
            int tileIndex = tile.getIndex();

            double[] screenPos = CoordinateConverter.boardToScreen(
                    y, x, rows, cols, boardWidth, boardHeight);

            Color tileColor;
            TileAction action = tile.getLandAction();

            tileColor = colorService.getTileColor(action);

            Rectangle rect = createTileRectangle(screenPos[0], screenPos[1], tileWidth, tileHeight, tileColor);

            tileRectangles.put(tileIndex, rect);

            Text tileIdText = new Text(screenPos[0] + 10, screenPos[1] + 20, String.valueOf(tileIndex));
            tileIdText.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

            boardPane.getChildren().addAll(tileRectangles.get(tileIndex), tileIdText);
        }

        for (Tile tile : board.getTiles().values()) {
            if (tile == null) continue;
            TileAction action = tile.getLandAction();
            if (action instanceof LadderTileAction ladderTileAction) {
                int targetId = ladderTileAction.getDestinationTileId();
                Tile targetTile = board.getTileByIndex(targetId);
                if (targetTile != null) {
                    ConnectionView ladder = new ConnectionView(
                            tile,
                            targetTile,
                            ConnectionView.Type.LADDER,
                            rows,
                            cols,
                            boardWidth,
                            boardHeight
                    );
                    connections.add(ladder);
                }
            } else if (action instanceof SnakeTileAction snakeTileAction) {
                int targetId = snakeTileAction.getDestinationTileId();
                Tile targetTile = board.getTileByIndex(targetId);
                if (targetTile != null) {
                    ConnectionView snake = new ConnectionView(
                            tile,
                            targetTile,
                            ConnectionView.Type.SNAKE,
                            rows,
                            cols,
                            boardWidth,
                            boardHeight
                    );
                    connections.add(snake);
                }
            }
        }

        boardPane.getChildren().addAll(connections);
    }

    /**
     *
     * Draws the board for The Lost Diamond game.
     */
    private void drawLostDiamondBoard() {
        createMapTitle();
        drawLostDiamondRoutes();
        drawCities();
        addLostDiamondLegend();
    }

    /**
     * Creates a map title
     */
    private void createMapTitle() {
        Text title = new Text(boardWidth / 2 - 100, 30, "The Lost Diamond - Africa");
        title.setFont(Font.font("Georgia", FontWeight.BOLD, 24));
        title.setFill(Color.SADDLEBROWN);

        boardPane.getChildren().addAll(title);
    }

    /**
     * Draws routes with better visual hierarchy
     */
    private void drawLostDiamondRoutes() {
        for (Tile tile : board.getTiles().values()) {
            if (tile == null) continue;

            double fromX = tile.getX() * (boardWidth / 350.0) + 10;
            double fromY = tile.getY() * (boardHeight / 390.0) + 30;

            tile.getConnections().forEach((direction, connectedTile) -> {
                double toX = connectedTile.getX() * (boardWidth / 350.0) + 10;
                double toY = connectedTile.getY() * (boardHeight / 390.0) + 30;

                Line routeLine = new Line(fromX, fromY, toX, toY);

                int cost = tile.getTravelCost(direction);
                if (cost > 0) {
                    routeLine.setStroke(Color.RED);
                    routeLine.setStrokeWidth(3);
                    routeLine.getStrokeDashArray().addAll(10d, 5d);

                    double midX = (fromX + toX) / 2;
                    double midY = (fromY + toY) / 2;

                    Circle costBg = new Circle(midX, midY, 12);
                    costBg.setFill(Color.WHITE);
                    costBg.setStroke(Color.RED);
                    costBg.setStrokeWidth(2);

                    Text costText = new Text(midX - 8, midY + 4, cost + "c");
                    costText.setFont(Font.font("Arial", FontWeight.BOLD, 11));
                    costText.setFill(Color.RED);

                    boardPane.getChildren().addAll(routeLine, costBg, costText);
                } else {
                    routeLine.setStroke(Color.BROWN);
                    routeLine.setStrokeWidth(2.5);

                    boardPane.getChildren().addAll(routeLine);
                }
            });
        }
    }

    /**
     * Draws cities with better styling
     */
    private void drawCities() {
        for (Tile tile : board.getTiles().values()) {
            if (tile == null) continue;

            double x = tile.getX() * (boardWidth / 350) + 10;
            double y = tile.getY() * (boardHeight / 390.0) +30;

            Circle cityCircle = new Circle(x, y, 20);
            Color cityColor = getCityColor(tile);
            cityCircle.setFill(cityColor);

            if (tile.getTileType() == TileType.STARTING_CITY) {
                cityCircle.setStroke(Color.GOLD);
                cityCircle.setStrokeWidth(3);
                Circle innerGlow = new Circle(x, y, 16);
                innerGlow.setFill(Color.GOLD);
                innerGlow.setOpacity(0.5);
                boardPane.getChildren().add(innerGlow);
            } else {
                cityCircle.setStroke(Color.SADDLEBROWN);
                cityCircle.setStrokeWidth(2);
            }

            if (tile.hasToken()) {
                Circle treasureIndicator = new Circle(x + 12, y - 12, 7);
                treasureIndicator.setFill(Color.ORANGE);
                treasureIndicator.setStroke(Color.DARKORANGE);
                treasureIndicator.setStrokeWidth(2);

                Text treasureText = new Text(x + 8, y - 8, "?");
                treasureText.setFont(Font.font("Arial", FontWeight.BOLD, 11));
                treasureText.setFill(Color.WHITE);

                boardPane.getChildren().addAll(treasureIndicator, treasureText);
            }

            Rectangle nameBg = new Rectangle(x - 35, y + 25, 70, 18);
            nameBg.setFill(Color.WHITE);
            nameBg.setOpacity(0.8);
            nameBg.setArcWidth(8);
            nameBg.setArcHeight(8);
            nameBg.setStroke(Color.SADDLEBROWN);
            nameBg.setStrokeWidth(1);

            Text cityName = new Text(x - 30, y + 37, tile.getName());
            cityName.setFont(Font.font("Arial", FontWeight.BOLD, 11));
            cityName.setFill(Color.SIENNA);

            Text tileIdText = new Text(x - 6, y + 4, String.valueOf(tile.getIndex()));
            tileIdText.setFont(Font.font("Arial", FontWeight.BOLD, 12));
            tileIdText.setFill(Color.WHITE);

            boardPane.getChildren().addAll(cityCircle, nameBg, cityName, tileIdText);

            cityCircles.put(tile.getIndex(), cityCircle);
            Rectangle rect = new Rectangle(x - 20, y - 20, 40, 40);
            tileRectangles.put(tile.getIndex(), rect);
        }
    }

    /**
     * Updates the tokens
     */
    public void updateTokens() {
        if (board == null || board.getGameType() != GameType.THE_LOST_DIAMOND) {
            return;
        }

        removeExistingTreasureIndicators();
        addCurrentTreasureIndicators();
    }

    /**
     * Removes existing treasure indicators
     */
    private void removeExistingTreasureIndicators() {
        List<javafx.scene.Node> nodesToRemove = new ArrayList<>();

        for (javafx.scene.Node node : boardPane.getChildren()) {
            if (node instanceof Circle circle &&
                    circle.getFill().equals(Color.ORANGE) &&
                    circle.getRadius() == 7) {
                nodesToRemove.add(node);
            }
            else if (node instanceof Text text &&
                    "?".equals(text.getText()) &&
                    text.getFill().equals(Color.WHITE)) {
                nodesToRemove.add(node);
            }
        }

        boardPane.getChildren().removeAll(nodesToRemove);
    }

    /**
     * Adds current treasure indicators
     */
    private void addCurrentTreasureIndicators() {
        for (Tile tile : board.getTiles().values()) {
            if (tile != null && tile.hasToken()) {
                double x = tile.getX() * (boardWidth / 350.0) + 10;
                double y = tile.getY() * (boardHeight / 390.0) + 30;

                Circle treasureIndicator = new Circle(x + 12, y - 12, 7);
                treasureIndicator.setFill(Color.ORANGE);
                treasureIndicator.setStroke(Color.DARKORANGE);
                treasureIndicator.setStrokeWidth(2);

                Text treasureText = new Text(x + 8, y - 8, "?");
                treasureText.setFont(Font.font("Arial", FontWeight.BOLD, 11));
                treasureText.setFill(Color.WHITE);

                boardPane.getChildren().addAll(treasureIndicator, treasureText);
            }
        }
    }



    /**
     * Gets colors for cities based on their characteristics
     */
    private Color getCityColor(Tile tile) {
        if (tile.getTileType() == TileType.STARTING_CITY) {
            return Color.GOLD;
        } else if (tile.hasToken()) {
            return Color.LIGHTBLUE;
        } else {
            String cityName = tile.getName().toLowerCase();
            if (cityName.contains("sahara") || cityName.contains("darfur") || cityName.contains("wadai")) {
                return Color.LIGHTSALMON;
            } else if (cityName.contains("dakar") || cityName.contains("lagos") || cityName.contains("accra") ||
                        cityName.contains("abidjan") || cityName.contains("tunis") || cityName.contains("algiers") ||
                        cityName.contains("tripoli")) {
                return Color.LIGHTGREEN;
            } else if (cityName.contains("congo") || cityName.contains("victoria") || cityName.contains("cape")) {
                return Color.MEDIUMSEAGREEN;
            } else if (cityName.contains("zanzibar") || cityName.contains("addis") || cityName.contains("somalia") ||
                    cityName.contains("khartoum")) {
                return Color.PURPLE;
            }
            else if (cityName.contains("madagascar")) {
                return Color.PINK;
            } else {
                return Color.SKYBLUE;
            }
        }
    }

    /**
     * Adds legend with better styling
     */
    private void addLostDiamondLegend() {
        VBox legend = new VBox(8);
        legend.setLayoutX(boardWidth - 200);
        legend.setLayoutY(30);
        legend.setPadding(new Insets(12));
        legend.setStyle("-fx-background-color: rgba(255,255,255,0.95); " +
                "-fx-border-color: saddlebrown; " +
                "-fx-border-width: 2; " +
                "-fx-border-radius: 8; " +
                "-fx-background-radius: 8;");

        Label legendTitle = new Label("Legend:");
        legendTitle.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        legendTitle.setTextFill(Color.SIENNA);

        HBox startingCity = createLegendItem(Color.GOLD, "Starting Cities", 2);
        HBox regularCity = createLegendItem(Color.LIGHTBLUE, "Cities with Treasures", 1);
        HBox normalCity = createLegendItem(Color.SKYBLUE, "Regular Cities", 1);

        HBox freeRoute = new HBox(8);
        freeRoute.setAlignment(Pos.CENTER_LEFT);
        Line freeLine = new Line(0, 0, 25, 0);
        freeLine.setStroke(Color.SADDLEBROWN);
        freeLine.setStrokeWidth(3);
        Label freeLabel = new Label("Free Land Routes");
        freeLabel.setFont(Font.font("Arial", 11));
        freeRoute.getChildren().addAll(freeLine, freeLabel);

        HBox paidRoute = new HBox(8);
        paidRoute.setAlignment(Pos.CENTER_LEFT);
        Line paidLine = new Line(0, 0, 25, 0);
        paidLine.setStroke(Color.RED);
        paidLine.setStrokeWidth(3);
        paidLine.getStrokeDashArray().addAll(8d, 4d);
        Label paidLabel = new Label("Sea Routes (Cost Money)");
        paidLabel.setFont(Font.font("Arial", 11));
        paidRoute.getChildren().addAll(paidLine, paidLabel);

        HBox treasure = new HBox(8);
        treasure.setAlignment(Pos.CENTER_LEFT);
        Circle treasureCircle = new Circle(8, Color.ORANGE);
        treasureCircle.setStroke(Color.DARKORANGE);
        treasureCircle.setStrokeWidth(1);
        Label treasureLabel = new Label("Hidden Treasures");
        treasureLabel.setFont(Font.font("Arial", 11));
        treasure.getChildren().addAll(treasureCircle, treasureLabel);

        legend.getChildren().addAll(legendTitle, startingCity, regularCity, normalCity,
                freeRoute, paidRoute, treasure);
        boardPane.getChildren().add(legend);
    }

    /**
     * Creates a styled legend item
     */
    private HBox createLegendItem(Color color, String text, int strokeWidth) {
        HBox item = new HBox(8);
        item.setAlignment(Pos.CENTER_LEFT);

        Circle swatch = new Circle(8, color);
        swatch.setStroke(strokeWidth > 1 ? Color.GOLD : Color.SADDLEBROWN);
        swatch.setStrokeWidth(strokeWidth);

        Label label = new Label(text);
        label.setFont(Font.font("Arial", 11));

        item.getChildren().addAll(swatch, label);
        return item;
    }


    /**
     * Creates a rectangle for a tile with the specified properties.
     */
    private Rectangle createTileRectangle(double x, double y, double width, double height, Color color) {
        Rectangle rect = new Rectangle(x, y, width, height);
        rect.setFill(color);
        rect.setStroke(Color.BLACK);
        rect.setStrokeWidth(1.5);
        rect.setArcWidth(15);
        rect.setArcHeight(15);
        rect.getStyleClass().add("tile");
        return rect;
    }

    /**
     * Creates a legend explaining the meaning of different tile colors.
     */
    public Pane createTileLegend() {
        VBox legendContainer = new VBox(10);
        legendContainer.setPadding(new Insets(15, 10, 15, 10));
        legendContainer.setStyle("-fx-background-color: white; -fx-border-color: #cccccc; -fx-border-radius: 5;");

        Label titleLabel = new Label("Tile Legend");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        titleLabel.setAlignment(Pos.CENTER);
        titleLabel.setPrefWidth(Double.MAX_VALUE);

        FlowPane legendFlow = new FlowPane(10, 10);
        legendFlow.setPadding(new Insets(5));

        if (board.getGameType() == GameType.SNAKES_AND_LADDERS) {
            addLegendItem(legendFlow, Color.BEIGE, "Regular Tile");
            addLegendItem(legendFlow, colorService.getTileColor(new LadderTileAction(0, "", board)), "Ladder Start");
            addLegendItem(legendFlow, colorService.getLadderDestinationColor(), "Ladder End");
            addLegendItem(legendFlow, colorService.getTileColor(new SnakeTileAction(0, "", board)), "Snake Start");
            addLegendItem(legendFlow, colorService.getSnakeDestinationColor(), "Snake End");
            addLegendItem(legendFlow, colorService.getTileColor(new RandomTeleportTileAction(board, "")), "Random Teleport");
            addLegendItem(legendFlow, colorService.getTileColor(new MissingTurnTileAction("", board)), "Skip Turn");
            addLegendItem(legendFlow, colorService.getTileColor(new ExtraThrowAction("", board)), "Extra Turn");
            addLegendItem(legendFlow, colorService.getTileColor(new SwapAction(null, "")), "Swap Positions");
        }

        legendContainer.getChildren().addAll(titleLabel, legendFlow);
        return legendContainer;
    }

    /**
     * Adds a legend item to the flow pane.
     */
    private void addLegendItem(FlowPane flow, Color color, String description) {
        HBox item = new HBox(5);
        item.setAlignment(Pos.CENTER_LEFT);

        Rectangle swatch = new Rectangle(15, 15, color);
        swatch.setStroke(Color.BLACK);
        swatch.setStrokeWidth(0.5);
        swatch.setArcWidth(3);
        swatch.setArcHeight(3);

        Label label = new Label(description);
        label.setFont(Font.font("Arial", 12));

        item.getChildren().addAll(swatch, label);
        flow.getChildren().add(item);
    }

    public Board getBoard() { return board; }
    public Rectangle getTileRectangle(int tileIndex) { return tileRectangles.get(tileIndex); }

    /**
     * Refreshes the board display to reflect current game state
     */
    public void refreshBoard() {
        if (board == null || board.getGameType() != GameType.THE_LOST_DIAMOND) {
            return;
        }

        logger.fine("Refreshing Lost Diamond board view");

        boardPane.getChildren().clear();
        tileRectangles.clear();
        cityCircles.clear();

        drawLostDiamondBoard();

        logger.info("Lost Diamond board refreshed - treasure indicators updated");
    }
}