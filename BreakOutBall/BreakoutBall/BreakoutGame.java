package BreakoutBall;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.IOException;

public class BreakoutGame extends JPanel implements KeyListener, ActionListener {

    private boolean play = false;
    private int score = 0;
    private int totalBricks = 21;

    private Timer timer;
    private int delay = 8;

    private int playerX = 310;

    private int ballPosX = 120;
    private int ballPosY = 350;
    private double speedMultiplier = 1.0;

    private int ballDirX = -1;
    private int ballDirY = -2;

    private BrickGenerator map;

    // Player info
    private static String playerName = "Player";
    private static int highScore = 0;

    public BreakoutGame(String name) {
        this.playerName = name;
        map = new BrickGenerator(3, 7);
        addKeyListener(this);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
        timer = new Timer(delay, this);
        timer.start();
    }

    public void paint(Graphics g) {
        // Background
        g.setColor(Color.black);
        g.fillRect(1, 1, 692, 592);

        // Drawing bricks
        map.draw((Graphics2D) g);

        // Borders
        g.setColor(Color.yellow);
        g.fillRect(0, 0, 3, 592);
        g.fillRect(0, 0, 692, 3);
        g.fillRect(691, 0, 3, 592);

        // Score & Player Name
        g.setColor(Color.white);
        g.setFont(new Font("serif", Font.BOLD, 20));
        g.drawString("Score: " + score, 590, 30);
        g.drawString("Player: " + playerName, 20, 30);
        g.drawString("High Score: " + highScore, 300, 30);
        //add line below
        g.drawString("Speed: " + String.format("%.1f", speedMultiplier), 460, 30);


        // Paddle
        g.setColor(Color.yellow);
        g.fillRect(playerX, 550, 100, 8);

        // Ball
        g.setColor(Color.red);
        g.fillOval(ballPosX, ballPosY, 20, 20);

        // Game win
        if (totalBricks <= 0) {
            play = false;
            ballDirX = 0;
            ballDirY = 0;
            g.setColor(Color.red);
            g.setFont(new Font("serif", Font.BOLD, 30));
            g.drawString("You Won!", 260, 300);
            g.drawString("Press Enter to Restart", 230, 350);
            updateHighScore();
        }

        // Game over replace

        if (ballPosY > 570 || map.bricksReachedBottom()) {
            play = false;
            ballDirX = 0;
            ballDirY = 0;
            g.setColor(Color.red);
            g.setFont(new Font("serif", Font.BOLD, 30));
            g.drawString("Game Over", 260, 300);
            g.drawString("Press Enter to Restart", 230, 350);
            updateHighScore();
        }

        g.dispose();
    }

    public void updateHighScore() {
        if (score > highScore) {
            highScore = score;
            saveGameData(); // Auto-save when high score changes
        }
    }

    private static void saveGameData() {
        try (PrintWriter writer = new PrintWriter("breakout_save.txt")) {
            writer.println(playerName);
            writer.println(highScore);

            System.out.println("Game saved successfully!");
        } catch (IOException e) {
            System.err.println("Error saving game: " + e.getMessage());
        }
    }

    private static void loadGameData() {
        try (BufferedReader reader = new BufferedReader(new FileReader("breakout_save.txt"))) {
            playerName = reader.readLine();
            highScore = Integer.parseInt(reader.readLine());

            System.out.println("Loaded saved game data");
        } catch (Exception e) {
            // Use defaults if file doesn't exist
            playerName = "Player";
            highScore = 0;

        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        timer.start();

        if (play) {
            // Ball-paddle collision
            if (new Rectangle(ballPosX, ballPosY, 20, 20)
                    .intersects(new Rectangle(playerX, 550, 100, 8))) {
                ballDirY = -ballDirY;
            }

            // Ball-brick collision
            A:
            for (int i = 0; i < map.map.length; i++) {
                for (int j = 0; j < map.map[0].length; j++) {
                    if (map.map[i][j] > 0) {
                        int brickX = j * map.brickWidth + 80;
                        int brickY = i * map.brickHeight + 50;
                        int brickWidth = map.brickWidth;
                        int brickHeight = map.brickHeight;

                        Rectangle rect = new Rectangle(brickX, brickY, brickWidth, brickHeight);
                        Rectangle ballRect = new Rectangle(ballPosX, ballPosY, 20, 20);

                        if (ballRect.intersects(rect)) {
                            map.setBrickValue(0, i, j);
                            totalBricks--;
                            score += 5;

                            // Add new layer when 1-2 bricks remain
                            if (map.countRemainingBricks() <= 2) {
                                map.addNewBrickLayer();
                                totalBricks += map.getMap()[0].length; // Add count of new bricks
                                speedMultiplier = Math.min(5.0, speedMultiplier + 0.1);
                            }

                            // Add new bricks when 1 or 2 remain
                            if (totalBricks <= 2) {
                                map.addNewBrickLayer();
                                totalBricks += map.getMap().length * map.getMap()[0].length - 1; // Add full rows minus the remaining bricks
                            }
                            //

                            if (ballPosX + 19 <= rect.x || ballPosX + 1 >= rect.x + rect.width) {
                                ballDirX = -ballDirX;
                            } else {
                                ballDirY = -ballDirY;
                            }
                            break A;
                        }
                    }
                }
            }

            ballPosX += (int) (ballDirX * speedMultiplier);
            ballPosY += (int) (ballDirY * speedMultiplier);


            if (ballPosX < 0) ballDirX = -ballDirX;
            if (ballPosY < 0) ballDirY = -ballDirY;
            if (ballPosX > 670) ballDirX = -ballDirX;
        }

        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        //cange the below padel speed
        int paddleSpeed = (int) (20 * speedMultiplier); // dynamic paddle speed

        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            if (playerX < 600) playerX += paddleSpeed;
            play = true;
        }

        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            if (playerX > 10) playerX -= paddleSpeed;
            play = true;
        }

        // 165-171
        if (e.getKeyCode() == KeyEvent.VK_W) { // Increase speed
            if (speedMultiplier < 5.0) speedMultiplier += 0.1;
        }

        if (e.getKeyCode() == KeyEvent.VK_S) { // Decrease speed
            if (speedMultiplier > 0.2) speedMultiplier -= 0.1;
        }


        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            if (!play) {
                play = true;
                ballPosX = 120;
                ballPosY = 350;
                ballDirX = -1;
                ballDirY = -2;
                playerX = 310;
                score = 0;
                totalBricks = 21;
                // add new line below
                speedMultiplier = 1.0;
                map = new BrickGenerator(3, 7);

                repaint();
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    // Entry point
    public static void main(String[] args) {
        loadGameData(); // Load saved data first

        String name = JOptionPane.showInputDialog(null, "Enter your name:", "Player Name", JOptionPane.PLAIN_MESSAGE);
        if (name != null && !name.trim().isEmpty()) {
            playerName = name;
        }

        JFrame obj = new JFrame();
        BreakoutGame gamePlay = new BreakoutGame(playerName);

        // Add window listener to save on close
        obj.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        obj.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                saveGameData();
                System.exit(0);
            }
        });

        obj.setBounds(10, 10, 700, 600);
        obj.setTitle("Breakout Ball Game");
        obj.setResizable(false);
        obj.add(gamePlay);
        obj.setVisible(true);
    }
}
