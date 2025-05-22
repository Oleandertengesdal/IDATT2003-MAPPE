package idi.edu.idatt.mappe.utils.factory;

import idi.edu.idatt.mappe.models.Board;
import idi.edu.idatt.mappe.models.Tile;
import idi.edu.idatt.mappe.models.enums.Direction;
import idi.edu.idatt.mappe.models.enums.GameType;
import idi.edu.idatt.mappe.models.enums.TileType;
import idi.edu.idatt.mappe.models.enums.TokenType;
import idi.edu.idatt.mappe.models.tileaction.*;

import java.util.*;
import java.util.logging.Logger;

/**
 * Factory class for creating different variants of game boards.
 * Follows single responsibility principle by focusing only on board creation.
 */
public class BoardFactory {
    private static final Logger logger = Logger.getLogger(BoardFactory.class.getName());
    private static final Random random = new Random();

    /**
     * Creates a classic board with standard snakes and ladders layout
     *
     * @return A classic board
     */
    public static Board createClassicBoard() {
        Board board = new Board(9, 10, GameType.SNAKES_AND_LADDERS);

        // Add ladders
        board.getTileByIndex(2).setLandAction(new LadderTileAction(38, "Ladder from 2 to 38", board));
        board.getTileByIndex(4).setLandAction(new LadderTileAction(14, "Ladder from 4 to 14", board));
        board.getTileByIndex(9).setLandAction(new LadderTileAction(31, "Ladder from 9 to 31", board));
        board.getTileByIndex(21).setLandAction(new LadderTileAction(42, "Ladder from 21 to 42", board));
        board.getTileByIndex(28).setLandAction(new LadderTileAction(84, "Ladder from 28 to 84", board));
        board.getTileByIndex(36).setLandAction(new LadderTileAction(44, "Ladder from 36 to 44", board));
        board.getTileByIndex(51).setLandAction(new LadderTileAction(67, "Ladder from 51 to 67", board));
        board.getTileByIndex(71).setLandAction(new LadderTileAction(84, "Ladder from 71 to 84", board));

        // Add snakes
        board.getTileByIndex(16).setLandAction(new SnakeTileAction(6, "Snake from 16 to 6", board));
        board.getTileByIndex(47).setLandAction(new SnakeTileAction(26, "Snake from 47 to 26", board));
        board.getTileByIndex(49).setLandAction(new SnakeTileAction(11, "Snake from 49 to 11", board));
        board.getTileByIndex(56).setLandAction(new SnakeTileAction(53, "Snake from 56 to 53", board));
        board.getTileByIndex(62).setLandAction(new SnakeTileAction(19, "Snake from 62 to 19", board));
        board.getTileByIndex(65).setLandAction(new SnakeTileAction(60, "Snake from 65 to 60", board));
        board.getTileByIndex(77).setLandAction(new SnakeTileAction(24, "Snake from 77 to 24", board));
        board.getTileByIndex(83).setLandAction(new SnakeTileAction(72, "Snake from 83 to 72", board));
        board.getTileByIndex(85).setLandAction(new SnakeTileAction(64, "Snake from 85 to 64", board));

        return board;
    }

    /**
     * Creates a chaos board with many random snakes, ladders, and teleports
     *
     * @return A board with chaos layout
     */
    public static Board createChaosBoard() {
        Board board = new Board(9, 10, GameType.SNAKES_AND_LADDERS);

        int numSnakes = 12;
        int numLadders = 10;
        int numTeleports = 5;

        createLadders(board, numLadders);

        createSnakes(board, numSnakes);

        for (int i = 0; i < numTeleports; i++) {
            int index = randomTileIndex(10, 85, board);

            if (board.getTileByIndex(index).getLandAction() == null) {
                board.getTileByIndex(index).setLandAction(
                        new RandomTeleportTileAction(board, "Random teleport from " + index)
                );
            }
        }

        return board;
    }

    /**
     * Creates a ladder-heavy board with more ladders than snakes for quicker games
     *
     * @return A board with many ladders
     */
    public static Board createLadderHeavyBoard() {
        Board board = new Board(9, 10, GameType.SNAKES_AND_LADDERS);

        int numSnakes = 5;
        int numLadders = 15;

        createLadders(board, numLadders);
        createSnakes(board, numSnakes);

        return board;
    }

    /**
     * Creates a snake-heavy board with more snakes than ladders for challenging games
     *
     * @return A board with many snakes
     */
    public static Board createSnakeHeavyBoard() {
        Board board = new Board(9, 10, GameType.SNAKES_AND_LADDERS);

        int numSnakes = 15;
        int numLadders = 5;

        createLadders(board, numLadders);
        createSnakes(board, numSnakes);

        return board;
    }

    /**
     * Helper method to create ladders on the board
     *
     * @param board The board to add ladders to
     * @param numLadders Number of ladders to create
     */
    private static void createLadders(Board board, int numLadders) {
        for (int i = 0; i < numLadders; i++) {
            int start = randomTileIndex(5, 80, board);
            int end = randomDestinationForLadder(start, board);

            if (board.getTileByIndex(start).getLandAction() == null) {
                board.getTileByIndex(start).setLandAction(
                        new LadderTileAction(end, "Ladder from " + start + " to " + end, board)
                );
            }
        }
    }

    /**
     * Helper method to create snakes on the board
     *
     * @param board The board to add snakes to
     * @param numSnakes Number of snakes to create
     */
    private static void createSnakes(Board board, int numSnakes) {
        for (int i = 0; i < numSnakes; i++) {
            int start = randomTileIndex(15, 88, board);
            int end = randomDestinationForSnake(start, board);

            if (board.getTileByIndex(start).getLandAction() == null) {
                board.getTileByIndex(start).setLandAction(
                        new SnakeTileAction(end, "Snake from " + start + " to " + end, board)
                );
            }
        }
    }

    /**
     * Creates a custom board with the specified number of snakes, ladders and other actions
     *
     * @param snakeCount Number of snakes to include
     * @param ladderCount Number of ladders to include
     * @param randomTeleportCount Number of random teleport tiles
     * @param skipTurnCount Number of skip turn tiles
     * @param extraTurnCount Number of extra turn tiles
     * @return A custom configured board
     */
    public static Board createCustomBoard(int snakeCount, int ladderCount,
                                          int randomTeleportCount, int skipTurnCount,
                                          int extraTurnCount) {
        Board board = new Board(9, 10, GameType.SNAKES_AND_LADDERS);

        snakeCount = Math.min(Math.max(0, snakeCount), 20);
        ladderCount = Math.min(Math.max(0, ladderCount), 20);
        randomTeleportCount = Math.min(Math.max(0, randomTeleportCount), 10);
        skipTurnCount = Math.min(Math.max(0, skipTurnCount), 10);
        extraTurnCount = Math.min(Math.max(0, extraTurnCount), 10);

        createLadders(board, ladderCount);

        createSnakes(board, snakeCount);

        for (int i = 0; i < randomTeleportCount; i++) {
            int index = randomTileIndex(10, 85, board);

            if (board.getTileByIndex(index).getLandAction() == null) {
                board.getTileByIndex(index).setLandAction(
                        new RandomTeleportTileAction(board, "Random teleport from " + index)
                );
            }
        }

        for (int i = 0; i < skipTurnCount; i++) {
            int index = randomTileIndex(10, 85, board);

            if (board.getTileByIndex(index).getLandAction() == null) {
                board.getTileByIndex(index).setLandAction(
                        new MissingTurnTileAction("Skip turn at " + index, board)
                );
            }
        }

        for (int i = 0; i < extraTurnCount; i++) {
            int index = randomTileIndex(10, 85, board);

            if (board.getTileByIndex(index).getLandAction() == null) {
                board.getTileByIndex(index).setLandAction(
                        new ExtraThrowAction("Extra turn at " + index, board)
                );
            }
        }

        return board;
    }

    /**
     * Creates a generic board with basic structure for the given game type
     *
     * @param gameType The game type
     * @param rows Number of rows
     * @param columns Number of columns
     * @return A basic board
     */
    public static Board createGenericBoard(GameType gameType, int rows, int columns) {
        return new Board(rows, columns, gameType);
    }

    /**
     * Helper method to get a random tile index within a range
     *
     * @param min Minimum index (inclusive)
     * @param max Maximum index (inclusive)
     * @param board The board to check against
     * @return A random valid tile index
     */
    private static int randomTileIndex(int min, int max, Board board) {
        if (min < 0 || max >= board.getTiles().size()) {
            throw new IllegalArgumentException("Invalid tile index range");
        }
        int range = max - min + 1;
        return min + random.nextInt(range);
    }

    /**
     * Helper method to get a random destination for a ladder
     *
     * @param start The starting tile index
     * @param board The board to check against
     * @return A valid destination tile index for a ladder
     */
    private static int randomDestinationForLadder(int start, Board board) {
        int minEnd = start + 5;
        int maxEnd = Math.min(start + 30, 89);

        maxEnd = Math.min(maxEnd, board.getTiles().size() - 1);

        return randomTileIndex(minEnd, maxEnd, board);
    }

    /**
     * Helper method to get a random destination for a snake
     *
     * @param start The starting tile index
     * @param board The board to check against
     * @return A valid destination tile index for a snake
     */
    private static int randomDestinationForSnake(int start, Board board) {
        int maxEnd = start - 5;
        int minEnd = Math.max(1, start - 30);

        return randomTileIndex(minEnd, maxEnd, board);
    }
    /**
     * Creates a standard Lost Diamond board with authentic layout
     *
     * @return A standard Lost Diamond board
     */
    public static Board createLostDiamondBoard() {
        Board board = new Board(GameType.THE_LOST_DIAMOND);
        setupLostDiamondBoard(board, "Standard");
        return board;
    }

    /**
     * Creates an easy version of Lost Diamond board with more treasures
     * and fewer thieves
     *
     * @return An easy Lost Diamond board
     */
    public static Board createEasyLostDiamondBoard() {
        Board board = new Board(GameType.THE_LOST_DIAMOND);
        setupLostDiamondBoard(board, "Easy");
        return board;
    }

    /**
     * Creates a challenging version of Lost Diamond board with more thieves
     * and expensive sea routes
     *
     * @return A challenging Lost Diamond board
     */
    public static Board createChallengingLostDiamondBoard() {
        Board board = new Board(GameType.THE_LOST_DIAMOND);
        setupLostDiamondBoard(board, "Challenging");
        return board;
    }

    /**
     * Sets up a board for The Lost Diamond game with specified difficulty
     *
     * @param board The board to set up
     * @param difficulty The difficulty level (Easy, Standard, Challenging)
     */
    private static void setupLostDiamondBoard(Board board, String difficulty) {
        createLostDiamondCities(board);
        createLostDiamondRoutes(board, difficulty);
        distributeTokens(board, difficulty);
    }

    /**
     * Creates cities for The Lost Diamond game
     *
     * @param board The board to add cities to
     */
    private static void createLostDiamondCities(Board board) {
        Tile tangier = new Tile(1, "Tangier", TileType.STARTING_CITY, 50, 100);
        Tile cairo = new Tile(2, "Cairo", TileType.STARTING_CITY, 300, 130);

        board.addTile(1, tangier);
        board.addTile(2, cairo);

        Tile tunis = new Tile(3, "Tunis", TileType.CITY, 130, 100);
        Tile algiers = new Tile(4, "Algiers", TileType.CITY, 90, 110);
        Tile tripoli = new Tile(5, "Tripoli", TileType.CITY, 160, 130);

        Tile dakar = new Tile(6, "Dakar", TileType.CITY, 20, 200);
        Tile monrovia = new Tile(7, "Monrovia", TileType.CITY, 40, 240);
        Tile abidjan = new Tile(8, "Abidjan", TileType.CITY, 60, 250);
        Tile accra = new Tile(9, "Accra", TileType.CITY, 80, 240);
        Tile lagos = new Tile(10, "Lagos", TileType.CITY, 110, 240);

        Tile sahara = new Tile(11, "Sahara", TileType.CITY, 100, 150);
        Tile wadai = new Tile(12, "Wadai", TileType.CITY, 170, 180);
        Tile darfur = new Tile(13, "Darfur", TileType.CITY, 200, 190);
        Tile khartoum = new Tile(14, "Khartoum", TileType.CITY, 220, 210);
        Tile addisAbaba = new Tile(15, "Addis Ababa", TileType.CITY, 260, 230);
        Tile somaliland = new Tile(16, "Somaliland", TileType.CITY, 300, 200);

        Tile congo = new Tile(17, "Congo", TileType.CITY, 150, 260);
        Tile victoria = new Tile(18, "Victoria Lake", TileType.CITY, 200, 290);
        Tile zanzibar = new Tile(19, "Zanzibar", TileType.CITY, 230, 300);
        Tile tanganyika = new Tile(20, "Tanganyika", TileType.CITY, 210, 330);
        Tile mozambique = new Tile(21, "Mozambique", TileType.CITY, 220, 350);
        Tile madagascar = new Tile(22, "Madagascar", TileType.CITY, 280, 360);

        Tile namibia = new Tile(23, "Namibia", TileType.CITY, 140, 370);
        Tile capeTown = new Tile(24, "Cape Town", TileType.CITY, 140, 410);
        Tile goldCoast = new Tile(25, "Gold Coast", TileType.CITY, 180, 390);

        board.addTile(3, tunis);
        board.addTile(4, algiers);
        board.addTile(5, tripoli);
        board.addTile(6, dakar);
        board.addTile(7, monrovia);
        board.addTile(8, abidjan);
        board.addTile(9, accra);
        board.addTile(10, lagos);
        board.addTile(11, sahara);
        board.addTile(12, wadai);
        board.addTile(13, darfur);
        board.addTile(14, khartoum);
        board.addTile(15, addisAbaba);
        board.addTile(16, somaliland);
        board.addTile(17, congo);
        board.addTile(18, victoria);
        board.addTile(19, zanzibar);
        board.addTile(20, tanganyika);
        board.addTile(21, mozambique);
        board.addTile(22, madagascar);
        board.addTile(23, namibia);
        board.addTile(24, capeTown);
        board.addTile(25, goldCoast);
    }

    /**
     * Creates routes connecting cities for The Lost Diamond game
     *
     * @param board The board to add routes to
     * @param difficulty The difficulty level affecting travel costs
     */
    private static void createLostDiamondRoutes(Board board, String difficulty) {
        Map<Integer, Tile> tiles = board.getTiles();

        double costModifier = switch (difficulty) {
            case "Easy" -> 0.7;
            case "Challenging" -> 1.5;
            default -> 1.0;
        };

        Tile tangier = tiles.get(1);
        Tile cairo = tiles.get(2);
        Tile tunis = tiles.get(3);
        Tile algiers = tiles.get(4);
        Tile tripoli = tiles.get(5);
        Tile dakar = tiles.get(6);
        Tile monrovia = tiles.get(7);
        Tile abidjan = tiles.get(8);
        Tile accra = tiles.get(9);
        Tile lagos = tiles.get(10);
        Tile sahara = tiles.get(11);
        Tile wadai = tiles.get(12);
        Tile darfur = tiles.get(13);
        Tile khartoum = tiles.get(14);
        Tile addisAbaba = tiles.get(15);
        Tile somaliland = tiles.get(16);
        Tile congo = tiles.get(17);
        Tile victoria = tiles.get(18);
        Tile zanzibar = tiles.get(19);
        Tile tanganyika = tiles.get(20);
        Tile mozambique = tiles.get(21);
        Tile madagascar = tiles.get(22);
        Tile namibia = tiles.get(23);
        Tile capeTown = tiles.get(24);
        Tile goldCoast = tiles.get(25);


        tangier.addConnection(Direction.EAST, algiers, 0);
        algiers.addConnection(Direction.WEST, tangier, 0);

        algiers.addConnection(Direction.EAST, tunis, 0);
        tunis.addConnection(Direction.WEST, algiers, 0);

        tunis.addConnection(Direction.SOUTHEAST, tripoli, 0);
        tripoli.addConnection(Direction.NORTHWEST, tunis, 0);

        tripoli.addConnection(Direction.EAST, cairo, 0);
        cairo.addConnection(Direction.WEST, tripoli, 0);

        algiers.addConnection(Direction.SOUTH, sahara, 0);
        sahara.addConnection(Direction.NORTH, algiers, 0);

        tripoli.addConnection(Direction.SOUTH, wadai, 0);
        wadai.addConnection(Direction.NORTH, tripoli, 0);

        cairo.addConnection(Direction.SOUTH, khartoum, 0);
        khartoum.addConnection(Direction.NORTH, cairo, 0);

        sahara.addConnection(Direction.EAST, wadai, 0);
        wadai.addConnection(Direction.WEST, sahara, 0);

        wadai.addConnection(Direction.EAST, darfur, 0);
        darfur.addConnection(Direction.WEST, wadai, 0);

        darfur.addConnection(Direction.EAST, khartoum, 0);
        khartoum.addConnection(Direction.WEST, darfur, 0);

        khartoum.addConnection(Direction.EAST, addisAbaba, 0);
        addisAbaba.addConnection(Direction.WEST, khartoum, 0);

        addisAbaba.addConnection(Direction.EAST, somaliland, 0);
        somaliland.addConnection(Direction.WEST, addisAbaba, 0);

        sahara.addConnection(Direction.SOUTH, lagos, 0);
        lagos.addConnection(Direction.NORTH, sahara, 0);

        wadai.addConnection(Direction.SOUTH, congo, 0);
        congo.addConnection(Direction.NORTH, wadai, 0);

        darfur.addConnection(Direction.SOUTH, victoria, 0);
        victoria.addConnection(Direction.NORTH, darfur, 0);

        khartoum.addConnection(Direction.SOUTHWEST, victoria, 0);
        victoria.addConnection(Direction.NORTHEAST, khartoum, 0);

        lagos.addConnection(Direction.EAST, congo, 0);
        congo.addConnection(Direction.WEST, lagos, 0);

        congo.addConnection(Direction.EAST, victoria, 0);
        victoria.addConnection(Direction.WEST, congo, 0);

        victoria.addConnection(Direction.EAST, zanzibar, 0);
        zanzibar.addConnection(Direction.WEST, victoria, 0);

        victoria.addConnection(Direction.SOUTH, tanganyika, 0);
        tanganyika.addConnection(Direction.NORTH, victoria, 0);

        tanganyika.addConnection(Direction.SOUTH, mozambique, 0);
        mozambique.addConnection(Direction.NORTH, tanganyika, 0);

        congo.addConnection(Direction.SOUTH, namibia, 0);
        namibia.addConnection(Direction.NORTH, congo, 0);

        namibia.addConnection(Direction.SOUTH, capeTown, 0);
        capeTown.addConnection(Direction.NORTH, namibia, 0);

        namibia.addConnection(Direction.EAST, goldCoast, 0);
        goldCoast.addConnection(Direction.WEST, namibia, 0);

        goldCoast.addConnection(Direction.EAST, mozambique, 0);
        mozambique.addConnection(Direction.WEST, goldCoast, 0);

        tangier.addConnection(Direction.SOUTHWEST, dakar, (int)(100 * costModifier));
        dakar.addConnection(Direction.NORTHEAST, tangier, (int)(100 * costModifier));

        dakar.addConnection(Direction.SOUTHEAST, monrovia, (int)(80 * costModifier));
        monrovia.addConnection(Direction.NORTHWEST, dakar, (int)(80 * costModifier));

        monrovia.addConnection(Direction.EAST, abidjan, (int)(70 * costModifier));
        abidjan.addConnection(Direction.WEST, monrovia, (int)(70 * costModifier));

        abidjan.addConnection(Direction.EAST, accra, (int)(60 * costModifier));
        accra.addConnection(Direction.WEST, abidjan, (int)(60 * costModifier));

        accra.addConnection(Direction.EAST, lagos, (int)(70 * costModifier));
        lagos.addConnection(Direction.WEST, accra, (int)(70 * costModifier));

        cairo.addConnection(Direction.SOUTHEAST, somaliland, (int)(120 * costModifier));
        somaliland.addConnection(Direction.NORTHWEST, cairo, (int)(120 * costModifier));

        somaliland.addConnection(Direction.SOUTH, zanzibar, (int)(140 * costModifier));
        zanzibar.addConnection(Direction.NORTH, somaliland, (int)(140 * costModifier));

        zanzibar.addConnection(Direction.SOUTHWEST, mozambique, (int)(100 * costModifier));
        mozambique.addConnection(Direction.NORTHEAST, zanzibar, (int)(100 * costModifier));
        mozambique.addConnection(Direction.EAST, madagascar, (int)(90 * costModifier));
        madagascar.addConnection(Direction.WEST, mozambique, (int)(90 * costModifier));

        mozambique.addConnection(Direction.SOUTHWEST, capeTown, (int)(130 * costModifier));
        capeTown.addConnection(Direction.NORTHEAST, mozambique, (int)(130 * costModifier));

        dakar.addConnection(Direction.SOUTH, capeTown, (int)(200 * costModifier));
        capeTown.addConnection(Direction.NORTHWEST, dakar, (int)(200 * costModifier));

        lagos.addConnection(Direction.SOUTHWEST, namibia, (int)(150 * costModifier));
        namibia.addConnection(Direction.NORTHWEST, lagos, (int)(150 * costModifier));
    }

    /**
     * Distributes tokens to cities based on difficulty level
     *
     * @param board The board to add tokens to
     * @param difficulty The difficulty level
     */
    private static void distributeTokens(Board board, String difficulty) {
        Map<Integer, Tile> tiles = board.getTiles();
        List<TokenType> tokenTypes = new ArrayList<>();

        switch (difficulty) {
            case "Easy":
                tokenTypes.add(TokenType.DIAMOND);

                tokenTypes.add(TokenType.RUBY);
                tokenTypes.add(TokenType.RUBY);
                tokenTypes.add(TokenType.RUBY);
                tokenTypes.add(TokenType.EMERALD);
                tokenTypes.add(TokenType.EMERALD);
                tokenTypes.add(TokenType.EMERALD);
                tokenTypes.add(TokenType.EMERALD);
                tokenTypes.add(TokenType.TOPAZ);
                tokenTypes.add(TokenType.TOPAZ);
                tokenTypes.add(TokenType.TOPAZ);
                tokenTypes.add(TokenType.TOPAZ);
                tokenTypes.add(TokenType.TOPAZ);

                tokenTypes.add(TokenType.THIEF);
                tokenTypes.add(TokenType.THIEF);

                for (int i = tokenTypes.size(); i <= tiles.size() ; i++) {
                    tokenTypes.add(TokenType.EMPTY);
                }
                break;

            case "Challenging":
                tokenTypes.add(TokenType.DIAMOND);

                tokenTypes.add(TokenType.RUBY);
                tokenTypes.add(TokenType.EMERALD);
                tokenTypes.add(TokenType.EMERALD);
                tokenTypes.add(TokenType.TOPAZ);
                tokenTypes.add(TokenType.TOPAZ);
                tokenTypes.add(TokenType.TOPAZ);

                tokenTypes.add(TokenType.THIEF);
                tokenTypes.add(TokenType.THIEF);
                tokenTypes.add(TokenType.THIEF);
                tokenTypes.add(TokenType.THIEF);
                tokenTypes.add(TokenType.THIEF);

                for (int i = tokenTypes.size(); i <= tiles.size() ; i++) {
                    tokenTypes.add(TokenType.EMPTY);
                }
                break;

            case "Standard":
            default:
                tokenTypes.add(TokenType.DIAMOND);

                tokenTypes.add(TokenType.RUBY);
                tokenTypes.add(TokenType.RUBY);
                tokenTypes.add(TokenType.EMERALD);
                tokenTypes.add(TokenType.EMERALD);
                tokenTypes.add(TokenType.EMERALD);
                tokenTypes.add(TokenType.TOPAZ);
                tokenTypes.add(TokenType.TOPAZ);
                tokenTypes.add(TokenType.TOPAZ);
                tokenTypes.add(TokenType.TOPAZ);

                tokenTypes.add(TokenType.THIEF);
                tokenTypes.add(TokenType.THIEF);
                tokenTypes.add(TokenType.THIEF);

                for (int i = tokenTypes.size(); i <= tiles.size() ; i++) {
                    tokenTypes.add(TokenType.EMPTY);
                }
                break;
        }

        TokenType diamond = TokenType.DIAMOND;
        tokenTypes.remove(diamond);
        Collections.shuffle(tokenTypes);

        if (difficulty.equals("Easy")) {
            tiles.get(17).setHiddenToken(diamond);
            tiles.get(17).setLandAction(new DiamondTileAction());
        } else if (difficulty.equals("Challenging")) {
            tiles.get(22).setHiddenToken(diamond);
            tiles.get(22).setLandAction(new DiamondTileAction());
        } else {
            tiles.get(19).setHiddenToken(diamond);
            tiles.get(19).setLandAction(new DiamondTileAction());
        }

        int tokenIndex = 0;
        for (Tile tile : tiles.values()) {
            if (tile.getTileType() == TileType.CITY && tokenIndex < tokenTypes.size()) {
                TokenType tokenType = tokenTypes.get(tokenIndex++);
                tile.setHiddenToken(tokenType);

                if (tokenType == TokenType.THIEF) {
                    tile.setLandAction(new ThiefTileAction());
                } else if (tokenType == TokenType.EMPTY) {
                } else {
                    tile.setLandAction(new TreasureTileAction(tokenType));
                }
            }
        }

        int basePrice = switch (difficulty) {
            case "Easy" -> 80;
            case "Challenging" -> 120;
            default -> 100;
        };

        for (Tile tile : tiles.values()) {
            if (tile.getTileType() == TileType.CITY) {
                int locationVariance = random.nextInt(41) - 20;
                tile.setTokenPrice(basePrice + locationVariance);
            }
        }
    }
}