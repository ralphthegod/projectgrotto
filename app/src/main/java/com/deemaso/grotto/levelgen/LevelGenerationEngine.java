package com.deemaso.grotto.levelgen;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

/**
 * LevelGenerationEngine is responsible for generating a dungeon level with rooms, corridors, and elements.
 */
public class LevelGenerationEngine {

    private static final Logger LOGGER = Logger.getLogger(LevelGenerationEngine.class.getName());
    private static final int MAX_ATTEMPTS = 10;

    /**
     * Room represents a rectangular room in the dungeon.
     */
    private static class Room {
        int x, y, w, h;
        int centerX, centerY;

        /**
         * Constructs a Room with specified position and size.
         *
         * @param x the x-coordinate of the top-left corner
         * @param y the y-coordinate of the top-left corner
         * @param w the width of the room
         * @param h the height of the room
         */
        public Room(int x, int y, int w, int h) {
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
            this.centerX = x + w / 2;
            this.centerY = y + h / 2;
        }

        /**
         * Checks if this room intersects with another room.
         *
         * @param other the other room to check intersection with
         * @return true if the rooms intersect, false otherwise
         */
        public boolean intersects(Room other) {
            return (this.x <= other.x + other.w + 1 && this.x + this.w + 1 >= other.x &&
                    this.y <= other.y + other.h + 1 && this.y + this.h + 1 >= other.y);
        }
    }

    // Symbols used in the dungeon grid
    private static final char WALL = '#';
    private static final char SIDE_WALL = '|';
    private static final char FLOOR = '.';
    private static final char BOTTOM_WALL = '_';
    private static final char EMPTY = ' ';
    private static final char START = 'S';
    private static final char END = 'E';

    private final int minRoomSize;
    private final int maxRoomSize;
    private final int maxRooms;
    private final int gridWidth;
    private final int gridHeight;
    private final List<LevelGenerationElementDefinition> elementDefinitions;

    private final char[][] grid;
    private final List<Room> rooms;
    private final Random random;

    /**
     * Constructs a LevelGenerationEngine with specified parameters.
     *
     * @param gridWidth the width of the dungeon grid
     * @param gridHeight the height of the dungeon grid
     * @param minRoomSize the minimum size of a room
     * @param maxRoomSize the maximum size of a room
     * @param maxRooms the maximum number of rooms
     * @param elementDefinitions the list of element definitions to place in the dungeon
     * @param seed the random seed for generation
     */
    public LevelGenerationEngine(int gridWidth, int gridHeight, int minRoomSize, int maxRoomSize, int maxRooms, List<LevelGenerationElementDefinition> elementDefinitions, long seed) {
        this.elementDefinitions = elementDefinitions;
        this.grid = new char[gridHeight][gridWidth];
        for (int y = 0; y < gridHeight; y++) {
            for (int x = 0; x < gridWidth; x++) {
                grid[y][x] = EMPTY;
            }
        }
        this.rooms = new ArrayList<>();
        this.random = new Random(seed);
        this.gridWidth = gridWidth;
        this.gridHeight = gridHeight;
        this.minRoomSize = minRoomSize;
        this.maxRoomSize = maxRoomSize;
        this.maxRooms = maxRooms;
    }

    /**
     * Creates a room by filling the grid with floor tiles.
     *
     * @param room the room to create
     */
    private void createRoom(Room room) {
        for (int x = room.x; x < room.x + room.w; x++) {
            for (int y = room.y; y < room.y + room.h; y++) {
                grid[y][x] = FLOOR;
            }
        }
    }

    /**
     * Creates a horizontal tunnel connecting two points.
     *
     * @param x1 the starting x-coordinate
     * @param x2 the ending x-coordinate
     * @param y the y-coordinate
     */
    private void createHTunnel(int x1, int x2, int y) {
        for (int x = Math.min(x1, x2); x <= Math.max(x1, x2); x++) {
            for (int i = 0; i < 3; i++) {
                if (isInBounds(y + i, x)) {
                    grid[y + i][x] = FLOOR;
                }
            }
        }
    }

    /**
     * Creates a vertical tunnel connecting two points.
     *
     * @param y1 the starting y-coordinate
     * @param y2 the ending y-coordinate
     * @param x the x-coordinate
     */
    private void createVTunnel(int y1, int y2, int x) {
        for (int y = Math.min(y1, y2); y <= Math.max(y1, y2); y++) {
            for (int i = 0; i < 3; i++) {
                if (isInBounds(y, x + i)) {
                    grid[y][x + i] = FLOOR;
                }
            }
        }
    }

    /**
     * Adds borders to all rooms and tunnels in the grid.
     */
    private void addRoomAndTunnelBorders() {
        for (Room room : rooms) {
            addRoomBorders(room);
        }
        addTunnelBorders();
    }

    /**
     * Checks if the specified coordinates are within the grid bounds.
     *
     * @param y the y-coordinate
     * @param x the x-coordinate
     * @return true if the coordinates are within bounds, false otherwise
     */
    private boolean isInBounds(int y, int x) {
        return y >= 0 && y < gridHeight && x >= 0 && x < gridWidth;
    }

    /**
     * Adds borders to a specific room.
     *
     * @param room the room to add borders to
     */
    private void addRoomBorders(Room room) {
        for (int x = room.x - 1; x <= room.x + room.w; x++) {
            if (isInBounds(room.y - 1, x) && grid[room.y - 1][x] == EMPTY) grid[room.y - 1][x] = WALL;
            if (isInBounds(room.y + room.h, x) && grid[room.y + room.h][x] == EMPTY) grid[room.y + room.h][x] = BOTTOM_WALL;
        }
        for (int y = room.y; y < room.y + room.h; y++) {
            if (isInBounds(y, room.x - 1) && grid[y][room.x - 1] == EMPTY) grid[y][room.x - 1] = SIDE_WALL;
            if (isInBounds(y, room.x + room.w) && grid[y][room.x + room.w] == EMPTY) grid[y][room.x + room.w] = SIDE_WALL;
        }
        if (isInBounds(room.y - 1, room.x - 1)) grid[room.y - 1][room.x - 1] = SIDE_WALL;
        if (isInBounds(room.y - 1, room.x + room.w)) grid[room.y - 1][room.x + room.w] = SIDE_WALL;
        if (isInBounds(room.y + room.h, room.x - 1)) grid[room.y + room.h][room.x - 1] = SIDE_WALL;
        if (isInBounds(room.y + room.h, room.x + room.w)) grid[room.y + room.h][room.x + room.w] = SIDE_WALL;
    }

    /**
     * Adds borders to all tunnels in the grid.
     */
    private void addTunnelBorders() {
        for (int y = 0; y < gridHeight; y++) {
            for (int x = 0; x < gridWidth; x++) {
                if (grid[y][x] == FLOOR) {
                    if (y - 1 >= 0 && grid[y - 1][x] == EMPTY) grid[y - 1][x] = WALL;
                    if (y + 1 < gridHeight && grid[y + 1][x] == EMPTY) grid[y + 1][x] = BOTTOM_WALL;
                    if (x - 1 >= 0 && grid[y][x - 1] == EMPTY) grid[y][x - 1] = SIDE_WALL;
                    if (x + 1 < gridWidth && grid[y][x + 1] == EMPTY) grid[y][x + 1] = SIDE_WALL;
                }
            }
        }
        adjustInvertedCorners();
    }

    /**
     * Adjusts inverted corners in the grid for visual consistency.
     */
    private void adjustInvertedCorners() {
        for (int y = 0; y < gridHeight; y++) {
            for (int x = 0; x < gridWidth; x++) {
                if (grid[y][x] == SIDE_WALL) {
                    if (y + 1 < gridHeight && grid[y + 1][x] == BOTTOM_WALL) grid[y][x] = BOTTOM_WALL;
                    if (y - 1 >= 0 && grid[y - 1][x] == WALL) grid[y][x] = WALL;
                }
            }
        }
    }

    /**
     * Fills any empty spaces inside rooms or tunnels with floor tiles.
     */
    private void fillRoomHoles() {
        for (int y = 0; y < gridHeight; y++) {
            for (int x = 0; x < gridWidth; x++) {
                if (grid[y][x] == EMPTY && isSurroundedByFloor(x, y)) {
                    grid[y][x] = FLOOR;
                }
            }
        }
    }

    /**
     * Checks if a position is surrounded by floor tiles.
     *
     * @param x the x-coordinate
     * @param y the y-coordinate
     * @return true if the position is surrounded by floor tiles, false otherwise
     */
    private boolean isSurroundedByFloor(int x, int y) {
        int[] dx = {-1, 0, 1, 0};
        int[] dy = {0, 1, 0, -1};

        for (int i = 0; i < 4; i++) {
            int newX = x + dx[i];
            int newY = y + dy[i];

            if (newX < 0 || newX >= gridWidth || newY < 0 || newY >= gridHeight || grid[newY][newX] != FLOOR) {
                return false;
            }
        }

        return true;
    }

    /**
     * Checks if an element can be placed at the specified position while respecting the minimum distance between elements.
     *
     * @param x the x-coordinate
     * @param y the y-coordinate
     * @param placedPositions the list of already placed element positions
     * @param minDistance the minimum distance required between elements
     * @return true if the element can be placed, false otherwise
     */
    private boolean canPlaceElementAtPosition(int x, int y, List<int[]> placedPositions, int minDistance) {
        for (int[] pos : placedPositions) {
            int distance = calculateManhattanDistance(x, y, pos[0], pos[1]);
            // Log.d("LevelGen", "Checking position (" + x + ", " + y + ") against (" + pos[0] + ", " + pos[1] + ") with distance " + distance);
            if (distance <= minDistance) {
                return false;
            }
        }
        return true;
    }

    /**
     * Calculates the Manhattan distance between two points.
     *
     * @param x1 the x-coordinate of the first point
     * @param y1 the y-coordinate of the first point
     * @param x2 the x-coordinate of the second point
     * @param y2 the y-coordinate of the second point
     * @return the Manhattan distance between the two points
     */
    private int calculateManhattanDistance(int x1, int y1, int x2, int y2) {
        return Math.abs(x1 - x2) + Math.abs(y1 - y2);
    }

    /**
     * Places elements in rooms or corridors according to the given element definition and quantity.
     *
     * @param elementDef the element definition
     * @param totalQuantity the total quantity of elements to place
     */
    private void placeElementInRoomOrCorridor(LevelGenerationElementDefinition elementDef, int totalQuantity) {
        List<int[]> placedPositions = new ArrayList<>();
        List<int[]> validPositions = new ArrayList<>();

        // Collect all valid positions
        for (int y = 0; y < gridHeight; y++) {
            for (int x = 0; x < gridWidth; x++) {
                if (grid[y][x] == FLOOR && canPlaceElementAtPosition(x, y, placedPositions, elementDef.getMinDistanceBetweenElements())) {
                    boolean canPlaceInRoom = elementDef.isCanBeInRoom() && isRoom(x, y);
                    boolean canPlaceInCorridor = elementDef.isCanBeInCorridor() && isTunnel(x, y);
                    if (canPlaceInRoom || canPlaceInCorridor) {
                        validPositions.add(new int[]{x, y});
                    }
                }
            }
        }

        // Shuffle valid positions
        Collections.shuffle(validPositions, random);

        int placedQuantity = 0;
        for (int[] pos : validPositions) {
            if (placedQuantity >= totalQuantity) {
                break;
            }

            int x = pos[0];
            int y = pos[1];

            if (elementDef.getDistribution() == DistributionType.CLUSTERED) {
                placedQuantity += placeCluster(x, y, elementDef, totalQuantity - placedQuantity, placedPositions);
            } else {
                if (canPlaceElementAtPosition(x, y, placedPositions, elementDef.getMinDistanceBetweenElements())) {
                    grid[y][x] = elementDef.getSymbol();
                    placedPositions.add(new int[]{x, y});
                    placedQuantity++;
                    // Log.d("LevelGen", "Placed element at (" + x + ", " + y + ")");
                }
            }
        }

        if (placedQuantity < totalQuantity) {
            LOGGER.warning("Failed to place all elements for " + elementDef.getSymbol() + ". Placed: " + placedQuantity + " out of " + totalQuantity);
        }
    }

    /**
     * Places a cluster of elements around a central position.
     *
     * @param centerX the x-coordinate of the cluster center
     * @param centerY the y-coordinate of the cluster center
     * @param elementDef the element definition
     * @param remainingQuantity the remaining quantity of elements to place
     * @param placedPositions the list of already placed element positions
     * @return the number of elements placed in the cluster
     */
    private int placeCluster(int centerX, int centerY, LevelGenerationElementDefinition elementDef, int remainingQuantity, List<int[]> placedPositions) {
        int clusterSize = random.nextInt(Math.min(remainingQuantity, 5)) + 1;
        int placedInCluster = 0;
        List<int[]> clusterPositions = new ArrayList<>();

        // Collect all valid positions around the cluster center
        for (int dx = -2; dx <= 2; dx++) {
            for (int dy = -2; dy <= 2; dy++) {
                int x = centerX + dx;
                int y = centerY + dy;
                if (x >= 0 && x < gridWidth && y >= 0 && y < gridHeight && grid[y][x] == FLOOR &&
                        canPlaceElementAtPosition(x, y, placedPositions, elementDef.getMinDistanceBetweenElements())) {
                    clusterPositions.add(new int[]{x, y});
                }
            }
        }

        // Shuffle cluster valid positions
        Collections.shuffle(clusterPositions, random);

        for (int[] pos : clusterPositions) {
            if (placedInCluster >= clusterSize) {
                break;
            }

            int x = pos[0];
            int y = pos[1];

            if (canPlaceElementAtPosition(x, y, placedPositions, elementDef.getMinDistanceBetweenElements())) {
                grid[y][x] = elementDef.getSymbol();
                placedPositions.add(new int[]{x, y});
                placedInCluster++;
                Log.d("LevelGen", "Placed clustered element at (" + x + ", " + y + ")");
            }
        }

        return placedInCluster;
    }

    /**
     * Checks if a position is inside a room.
     *
     * @param x the x-coordinate
     * @param y the y-coordinate
     * @return true if the position is inside a room, false otherwise
     */
    private boolean isRoom(int x, int y) {
        return rooms.stream().anyMatch(room -> x >= room.x && x < room.x + room.w && y >= room.y && y < room.y + room.h);
    }

    /**
     * Checks if a position is inside a tunnel.
     *
     * @param x the x-coordinate
     * @param y the y-coordinate
     * @return true if the position is inside a tunnel, false otherwise
     */
    private boolean isTunnel(int x, int y) {
        return grid[y][x] == FLOOR && !isRoom(x, y);
    }

    /**
     * Ensures all rooms are connected by creating tunnels.
     */
    private void ensureConnectivity() {
        boolean[] visited = new boolean[rooms.size()];
        visitRoom(0, visited);

        for (int i = 1; i < rooms.size(); i++) {
            if (!visited[i]) {
                Room connectedRoom = rooms.get(i - 1);
                Room isolatedRoom = rooms.get(i);
                createHTunnel(connectedRoom.centerX, isolatedRoom.centerX, connectedRoom.centerY);
                createVTunnel(connectedRoom.centerY, isolatedRoom.centerY, isolatedRoom.centerX);
                visitRoom(i, visited);
            }
        }
    }

    /**
     * Visits a room and marks it as connected.
     *
     * @param roomIndex the index of the room to visit
     * @param visited the array of visited room statuses
     */
    private void visitRoom(int roomIndex, boolean[] visited) {
        if (visited[roomIndex]) return;
        visited[roomIndex] = true;

        Room room = rooms.get(roomIndex);
        for (int i = 0; i < rooms.size(); i++) {
            if (!visited[i] && rooms.get(i).intersects(room)) {
                visitRoom(i, visited);
            }
        }
    }

    /**
     * Clears the content of a room, filling it with floor tiles.
     *
     * @param room the room to clear
     */
    private void clearRoom(Room room) {
        for (int x = room.x; x < room.x + room.w; x++) {
            for (int y = room.y; y < room.y + room.h; y++) {
                if (grid[y][x] != WALL) {
                    grid[y][x] = FLOOR;
                }
            }
        }
    }

    /**
     * Places the starting point in the smallest room.
     */
    private void placeStartingPoint() {
        if (!rooms.isEmpty()) {
            Room smallestRoom = rooms.stream().min(Comparator.comparingInt(r -> r.w * r.h)).orElse(null);
            clearRoom(smallestRoom);
            int x = smallestRoom.centerX;
            int y = smallestRoom.centerY;
            grid[y][x] = START;
        }
    }

    /**
     * Places the ending point in the room farthest from the starting room.
     */
    private void placeEndingPoint() {
        if (!rooms.isEmpty()) {
            Room startRoom = rooms.get(0);
            Room farthestRoom = rooms.stream()
                    .max((r1, r2) -> Double.compare(calculateDistance(startRoom.centerX, startRoom.centerY, r1.centerX, r1.centerY),
                            calculateDistance(startRoom.centerX, startRoom.centerY, r2.centerX, r2.centerY)))
                    .orElse(null);
            int x = farthestRoom.centerX;
            int y = farthestRoom.centerY;
            grid[y][x] = END;
        }
    }

    /**
     * Calculates the Euclidean distance between two points.
     *
     * @param x1 the x-coordinate of the first point
     * @param y1 the y-coordinate of the first point
     * @param x2 the x-coordinate of the second point
     * @param y2 the y-coordinate of the second point
     * @return the Euclidean distance between the two points
     */
    private double calculateDistance(int x1, int y1, int x2, int y2) {
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }

    /**
     * Generates the dungeon by creating rooms, tunnels, and placing elements.
     *
     * @return the generated dungeon grid
     */
    public char[][] generateDungeon() {
        int numRooms = 0;

        Log.d("LevelGenerationEngine", "Generating dungeon, max rooms: " + maxRooms + ", min room size: " + minRoomSize + ", max room size: " + maxRoomSize);

        for (int i = 0; i < maxRooms; i++) {
            int w = random.nextInt(maxRoomSize - minRoomSize + 1) + minRoomSize;
            int h = random.nextInt(maxRoomSize - minRoomSize + 1) + minRoomSize;
            int x = random.nextInt(gridWidth - w - 1);
            int y = random.nextInt(gridHeight - h - 1);

            Room newRoom = new Room(x, y, w, h);

            if (rooms.stream().anyMatch(newRoom::intersects)) {
                continue;
            }

            createRoom(newRoom);

            if (numRooms != 0) {
                Room prevRoom = rooms.get(numRooms - 1);
                if (random.nextBoolean()) {
                    createHTunnel(prevRoom.centerX, newRoom.centerX, prevRoom.centerY);
                    createVTunnel(prevRoom.centerY, newRoom.centerY, newRoom.centerX);
                } else {
                    createVTunnel(prevRoom.centerY, newRoom.centerY, prevRoom.centerX);
                    createHTunnel(prevRoom.centerX, newRoom.centerX, newRoom.centerY);
                }
            }

            rooms.add(newRoom);
            numRooms++;
        }

        Log.d("LevelGenerationEngine", "Rooms generated: " + numRooms);

        ensureConnectivity();

        Log.d("LevelGenerationEngine", "Dungeon connected");

        addRoomAndTunnelBorders();

        Log.d("LevelGenerationEngine", "Borders added");

        fillRoomHoles();

        Log.d("LevelGenerationEngine", "Holes filled");

        try {
            if (elementDefinitions != null) {
                for (LevelGenerationElementDefinition elementDef : elementDefinitions) {
                    int totalQuantity = random.nextInt(elementDef.getGlobalMaxQuantity() - elementDef.getGlobalMinQuantity() + 1) + elementDef.getGlobalMinQuantity();
                    placeElementInRoomOrCorridor(elementDef, totalQuantity);
                }
            }
        } catch (Exception e) {
            Log.d("LevelGenerationEngine", "Error placing elements: " + e.getMessage());
        }

        Log.d("LevelGenerationEngine", "Elements placed");

        placeStartingPoint();
        placeEndingPoint();

        Log.d("LevelGenerationEngine", "Starting and ending points placed");
        Log.d("LevelGenerationEngine", "Finishing dungeon generation...");

        return grid;
    }

    /**
     * Prints the dungeon grid to the console.
     */
    public void printDungeon() {
        for (int y = 0; y < gridHeight; y++) {
            for (int x = 0; x < gridWidth; x++) {
                System.out.print(grid[y][x]);
            }
            System.out.println();
        }
    }
}
