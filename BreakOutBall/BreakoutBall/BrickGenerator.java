package BreakoutBall;

import java.awt.*;
import java.util.Random;

public class BrickGenerator {
    public int[][] map;
    public int brickWidth;
    public int brickHeight;
    private Random random;
    private int currentShape = 0; // 0 for rectangle, 1 for triangle

    public BrickGenerator(int row, int col) {
        map = new int[row][col];
        random = new Random();
        for (int[] rowArr : map) {
            java.util.Arrays.fill(rowArr, 1);
        }
        brickWidth = 540 / col;
        brickHeight = 150 / row;
    }

    public void draw(Graphics2D g) {
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                if (map[i][j] > 0) {
                    if (map[i][j] == 1) { // Regular rectangle brick
                        g.setColor(new Color(173, 216, 230)); // Light blue
                        g.fillRect(j * brickWidth + 80, i * brickHeight + 50,
                                brickWidth, brickHeight);
                    } else if (map[i][j] == 2) { // Triangle brick
                        g.setColor(new Color(255, 165, 0)); // Orange
                        int[] xPoints = {
                                j * brickWidth + 80,
                                j * brickWidth + 80 + brickWidth,
                                j * brickWidth + 80 + brickWidth/2
                        };
                        int[] yPoints = {
                                i * brickHeight + 50 + brickHeight,
                                i * brickHeight + 50 + brickHeight,
                                i * brickHeight + 50
                        };
                        g.fillPolygon(xPoints, yPoints, 3);
                    }

                    g.setStroke(new BasicStroke(3));
                    g.setColor(Color.black);
                    if (map[i][j] == 1) {
                        g.drawRect(j * brickWidth + 80, i * brickHeight + 50,
                                brickWidth, brickHeight);
                    } else if (map[i][j] == 2) {
                        int[] xPoints = {
                                j * brickWidth + 80,
                                j * brickWidth + 80 + brickWidth,
                                j * brickWidth + 80 + brickWidth/2
                        };
                        int[] yPoints = {
                                i * brickHeight + 50 + brickHeight,
                                i * brickHeight + 50 + brickHeight,
                                i * brickHeight + 50
                        };
                        g.drawPolygon(xPoints, yPoints, 3);
                    }
                }
            }
        }
    }

    public void setBrickValue(int value, int row, int col) {
        map[row][col] = value;
    }

    public int[][] getMap() {
        return map;
    }

    public int countRemainingBricks() {
        int count = 0;
        for (int[] row : map) {
            for (int brick : row) {
                if (brick > 0) count++;
            }
        }
        return count;
    }

    public void addNewBrickLayer() {
        currentShape = random.nextInt(2); // Randomly choose between 0 and 1

        // Move all bricks down one row
        for (int i = map.length - 1; i > 0; i--) {
            System.arraycopy(map[i-1], 0, map[i], 0, map[0].length);
        }

        // Add new bricks at top with different patterns
        if (currentShape == 0) {
            // Rectangle pattern (full row)
            java.util.Arrays.fill(map[0], 1);
        } else {
            // Triangle pattern (alternating)
            for (int j = 0; j < map[0].length; j++) {
                map[0][j] = (j % 2 == 0) ? 2 : 0; // Every even column gets a triangle
            }
        }
    }

    public boolean bricksReachedBottom() {
        int bottomRow = map.length - 1;
        int paddleAreaY = 500;
        for (int j = 0; j < map[0].length; j++) {
            if (map[bottomRow][j] > 0 &&
                    ((bottomRow * brickHeight + 50) >= paddleAreaY)) {
                return true;
            }
        }
        return false;
    }
}