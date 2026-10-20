import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class PongGame extends JPanel implements ActionListener, KeyListener {
    // Window dimensions
    private static final int WIDTH = 800;
    private static final int HEIGHT = 500;

    // Game Config
    private static final int MAX_SCORE = 5;

    // Paddle properties
    private static final int PADDLE_WIDTH = 15;
    private static final int PADDLE_HEIGHT = 80;
    private static final int PADDLE_SPEED = 6;
    private int player1Y;
    private int player2Y;

    // Ball properties
    private static final int BALL_SIZE = 15;
    private int ballX;
    private int ballY;
    private int ballXSpeed = 4;
    private int ballYSpeed = 4;

    // Scores & Game State
    private int score1 = 0;
    private int score2 = 0;
    private boolean gameOver = false;
    private String winnerText = "";

    // Key states
    private boolean wPressed = false;
    private boolean sPressed = false;
    private boolean upPressed = false;
    private boolean downPressed = false;

    public PongGame() {
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        this.setBackground(new Color(20, 20, 40));
        this.setFocusable(true);
        this.addKeyListener(this);

        resetPositions();

        // Game loop timer (~60 FPS)
        Timer timer = new Timer(16, this);
        timer.start();
    }

    private void resetPositions() {
        player1Y = HEIGHT / 2 - PADDLE_HEIGHT / 2;
        player2Y = HEIGHT / 2 - PADDLE_HEIGHT / 2;
        ballX = WIDTH / 2 - BALL_SIZE / 2;
        ballY = HEIGHT / 2 - BALL_SIZE / 2;
    }

    private void restartGame() {
        score1 = 0;
        score2 = 0;
        gameOver = false;
        winnerText = "";
        resetPositions();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!gameOver) {
            // Move Paddles
            if (wPressed && player1Y > 0) player1Y -= PADDLE_SPEED;
            if (sPressed && player1Y < HEIGHT - PADDLE_HEIGHT) player1Y += PADDLE_SPEED;
            if (upPressed && player2Y > 0) player2Y -= PADDLE_SPEED;
            if (downPressed && player2Y < HEIGHT - PADDLE_HEIGHT) player2Y += PADDLE_SPEED;

            // Move Ball
            ballX += ballXSpeed;
            ballY += ballYSpeed;

            // Top and Bottom Wall Collisions
            if (ballY <= 0 || ballY >= HEIGHT - BALL_SIZE) {
                ballYSpeed = -ballYSpeed;
            }

            // Paddle Collisions
            if (ballX <= 30 + PADDLE_WIDTH && ballY + BALL_SIZE >= player1Y && ballY <= player1Y + PADDLE_HEIGHT) {
                ballXSpeed = Math.abs(ballXSpeed);
            }
            if (ballX + BALL_SIZE >= WIDTH - 30 - PADDLE_WIDTH && ballY + BALL_SIZE >= player2Y && ballY <= player2Y + PADDLE_HEIGHT) {
                ballXSpeed = -Math.abs(ballXSpeed);
            }

            // Scoring & Game End Check
            if (ballX < 0) {
                score2++;
                checkWinCondition("PLAYER 2 WINS!");
            } else if (ballX > WIDTH) {
                score1++;
                checkWinCondition("PLAYER 1 WINS!");
            }
        }

        repaint();
    }

    private void checkWinCondition(String winnerMessage) {
        if (score1 >= MAX_SCORE || score2 >= MAX_SCORE) {
            gameOver = true;
            winnerText = winnerMessage;
        } else {
            resetBall();
        }
    }

    private void resetBall() {
        ballX = WIDTH / 2 - BALL_SIZE / 2;
        ballY = HEIGHT / 2 - BALL_SIZE / 2;
        ballXSpeed = -ballXSpeed;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Center Line
        g.setColor(new Color(80, 80, 100));
        for (int i = 0; i < HEIGHT; i += 30) {
            g.fillRect(WIDTH / 2 - 2, i, 4, 15);
        }

        // Paddles
        g.setColor(Color.WHITE);
        g.fillRect(30, player1Y, PADDLE_WIDTH, PADDLE_HEIGHT);
        g.fillRect(WIDTH - 30 - PADDLE_WIDTH, player2Y, PADDLE_WIDTH, PADDLE_HEIGHT);

        // Ball
        if (!gameOver) {
            g.fillRect(ballX, ballY, BALL_SIZE, BALL_SIZE);
        }

        // Scores
        g.setFont(new Font("Monospaced", Font.BOLD, 36));
        g.drawString(String.valueOf(score1), WIDTH / 2 - 60, 50);
        g.drawString(String.valueOf(score2), WIDTH / 2 + 35, 50);

        // Game Over Overlay
        if (gameOver) {
            g.setColor(new Color(0, 0, 0, 200));
            g.fillRect(0, 0, WIDTH, HEIGHT);

            g.setColor(Color.YELLOW);
            g.setFont(new Font("Monospaced", Font.BOLD, 48));
            FontMetrics metrics = g.getFontMetrics();
            int x = (WIDTH - metrics.stringWidth(winnerText)) / 2;
            g.drawString(winnerText, x, HEIGHT / 2 - 40);

            g.setColor(Color.WHITE);
            g.setFont(new Font("Monospaced", Font.BOLD, 22));
            String restartText = "Press 'R' for Rematch / Restart";
            int rx = (WIDTH - g.getFontMetrics().stringWidth(restartText)) / 2;
            g.drawString(restartText, rx, HEIGHT / 2 + 30);
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_W) wPressed = true;
        if (e.getKeyCode() == KeyEvent.VK_S) sPressed = true;
        if (e.getKeyCode() == KeyEvent.VK_UP) upPressed = true;
        if (e.getKeyCode() == KeyEvent.VK_DOWN) downPressed = true;

        // Restart Trigger
        if (gameOver && e.getKeyCode() == KeyEvent.VK_R) {
            restartGame();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_W) wPressed = false;
        if (e.getKeyCode() == KeyEvent.VK_S) sPressed = false;
        if (e.getKeyCode() == KeyEvent.VK_UP) upPressed = false;
        if (e.getKeyCode() == KeyEvent.VK_DOWN) downPressed = false;
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    public static void main(String[] args) {
        JFrame frame = new JFrame("Pong");
        PongGame gamePanel = new PongGame();

        frame.add(gamePanel);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setVisible(true);
    }
}
