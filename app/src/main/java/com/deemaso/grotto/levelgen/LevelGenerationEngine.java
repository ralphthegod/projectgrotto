package com.deemaso.grotto.levelgen;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

public class LevelGenerationEngine {

    private static final Logger LOGGER = Logger.getLogger(LevelGenerationEngine.class.getName());

    private static class Room {
        int x, y, w, h;
        int centerX, centerY;

        public Room(int x, int y, int w, int h) {
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
            this.centerX = x + w / 2;
            this.centerY = y + h / 2;
        }

        public boolean intersects(Room other) {
            return (this.x <= other.x + other.w + 1 && this.x + this.w + 1 >= other.x &&
                    this.y <= other.y + other.h + 1 && this.y + this.h + 1 >= other.y);
        }
    }

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

    private void createRoom(Room room) {
        for (int x = room.x; x < room.x + room.w; x++) {
            for (int y = room.y; y < room.y + room.h; y++) {
                grid[y][x] = FLOOR;
            }
        }
    }

    private void createHTunnel(int x1, int x2, int y) {
        for (int x = Math.min(x1, x2); x <= Math.max(x1, x2); x++) {
            for (int i = 0; i < 3; i++) {
                if (isInBounds(y + i, x)) {
                    grid[y + i][x] = FLOOR;
                }
            }
        }
    }

    private void createVTunnel(int y1, int y2, int x) {
        for (int y = Math.min(y1, y2); y <= Math.max(y1, y2); y++) {
            for (int i = 0; i < 3; i++) {
                if (isInBounds(y, x + i)) {
                    grid[y][x + i] = FLOOR;
                }
            }
        }
    }

    private void addRoomAndTunnelBorders() {
        for (Room room : rooms) {
            addRoomBorders(room);
        }
        addTunnelBorders();
    }

    private boolean isInBounds(int y, int x) {
        return y >= 0 && y < gridHeight && x >= 0 && x < gridWidth;
    }

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

    private void fillRoomHoles() {
        for (Room room : rooms) {
            for (int x = room.x; x < room.x + room.w; x++) {
                for (int y = room.y; y < room.y + room.h; y++) {
                    if (grid[y][x] == EMPTY) {
                        grid[y][x] = FLOOR;
                    }
                }
            }
        }
    }

    private void placeElementInRoomOrCorridor(LevelGenerationElementDefinition elementDef, int totalQuantity) {
        List<int[]> placedPositions = new ArrayList<>();
        int placedQuantity = 0;

        while (placedQuantity < totalQuantity) {
            int x = random.nextInt(gridWidth);
            int y = random.nextInt(gridHeight);

            if (grid[y][x] == FLOOR && canPlaceElementAtPosition(x, y, placedPositions, elementDef.getMinDistanceBetweenElements())) {
                boolean canPlaceInRoom = elementDef.isCanBeInRoom() && isRoom(x, y);
                boolean canPlaceInCorridor = elementDef.isCanBeInCorridor() && isTunnel(x, y);
                if (canPlaceInRoom || canPlaceInCorridor) {
                    if (elementDef.getDistribution() == DistributionType.CLUSTERED) {
                        placedQuantity += placeCluster(x, y, elementDef, totalQuantity - placedQuantity, placedPositions);
                    } else {
                        grid[y][x] = elementDef.getSymbol();
                        placedPositions.add(new int[]{x, y});
                        placedQuantity++;
                    }
                }
            }
        }
    }

    private int placeCluster(int centerX, int centerY, LevelGenerationElementDefinition elementDef, int remainingQuantity, List<int[]> placedPositions) {
        int clusterSize = random.nextInt(Math.min(remainingQuantity, 5)) + 1;
        int placedInCluster = 0;

        for (int i = 0; i < clusterSize; i++) {
            int x, y;
            boolean placed = false;
            while (!placed && placedInCluster < clusterSize) {
                x = centerX + random.nextInt(5) - 2;
                y = centerY + random.nextInt(5) - 2;
                if (x >= 0 && x < gridWidth && y >= 0 && y < gridHeight && grid[y][x] == FLOOR &&
                        canPlaceElementAtPosition(x, y, placedPositions, elementDef.getMinDistanceBetweenElements())) {
                    grid[y][x] = elementDef.getSymbol();
                    placedPositions.add(new int[]{x, y});
                    placed = true;
                    placedInCluster++;
                }
            }
        }
        return placedInCluster;
    }

    private boolean canPlaceElementAtPosition(int x, int y, List<int[]> placedPositions, int minDistance) {
        for (int[] pos : placedPositions) {
            if (calculateDistance(x, y, pos[0], pos[1]) < minDistance) {
                return false;
            }
        }
        return true;
    }

    private boolean isRoom(int x, int y) {
        return rooms.stream().anyMatch(room -> x >= room.x && x < room.x + room.w && y >= room.y && y < room.y + room.h);
    }

    private boolean isTunnel(int x, int y) {
        return grid[y][x] == FLOOR && !isRoom(x, y);
    }

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

    private void clearRoom(Room room) {
        for (int x = room.x; x < room.x + room.w; x++) {
            for (int y = room.y; y < room.y + room.h; y++) {
                if (grid[y][x] != WALL) {
                    grid[y][x] = FLOOR;
                }
            }
        }
    }

    private void placeStartingPoint() {
        if (!rooms.isEmpty()) {
            Room smallestRoom = rooms.stream().min((r1, r2) -> Integer.compare(r1.w * r1.h, r2.w * r2.h)).orElse(null);
            if (smallestRoom != null) {
                clearRoom(smallestRoom);
                int x = smallestRoom.centerX;
                int y = smallestRoom.centerY;
                grid[y][x] = START;
            }
        }
    }

    private void placeEndingPoint() {
        if (!rooms.isEmpty()) {
            Room startRoom = rooms.get(0);
            Room farthestRoom = rooms.stream()
                    .max((r1, r2) -> Double.compare(calculateDistance(startRoom.centerX, startRoom.centerY, r1.centerX, r1.centerY),
                            calculateDistance(startRoom.centerX, startRoom.centerY, r2.centerX, r2.centerY)))
                    .orElse(null);
            if (farthestRoom != null) {
                int x = farthestRoom.centerX;
                int y = farthestRoom.centerY;
                grid[y][x] = END;
            }
        }
    }

    private double calculateDistance(int x1, int y1, int x2, int y2) {
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }

    public char[][] generateDungeon() {
        int numRooms = 0;

        for (int i = 0; i < maxRooms; i++) {
            int w = random.nextInt(maxRoomSize - minRoomSize + 1) + minRoomSize;
            int h = random.nextInt(maxRoomSize - minRoomSize + 1) + minRoomSize;
            int x = random.nextInt(gridWidth - w - 1);
            int y = random.nextInt(gridHeight - h - 1);

            Room newRoom = new Room(x, y, w, h);

            if (rooms.stream().anyMatch(other -> newRoom.intersects(other))) {
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

        ensureConnectivity();
        addRoomAndTunnelBorders();
        fillRoomHoles();

        if (elementDefinitions != null) {
            for (LevelGenerationElementDefinition elementDef : elementDefinitions) {
                int totalQuantity = random.nextInt(elementDef.getGlobalMaxQuantity() - elementDef.getGlobalMinQuantity() + 1) + elementDef.getGlobalMinQuantity();
                placeElementInRoomOrCorridor(elementDef, totalQuantity);
            }
        }

        placeStartingPoint();
        placeEndingPoint();

        return grid;
    }

    public void printDungeon() {
        for (int y = 0; y < gridHeight; y++) {
            for (int x = 0; x < gridWidth; x++) {
                System.out.print(grid[y][x]);
            }
            System.out.println();
        }
    }
}
