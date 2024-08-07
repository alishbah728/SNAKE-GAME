package GUI;

import java.util.*;
import javax.imageio.ImageIO;
import java.util.Timer;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import javax.swing.*;



class Game extends JPanel {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Timer timer;
    protected Snake snake;
    protected Point cherry;
    protected int points = 0;
    private int best = 0;
    private BufferedImage image;
    protected GameStatus status;
    private boolean didLoadCherryImage = true;
    private boolean isSnakeDead = false;

    private static Font FONT_M = new Font("Arial", Font.BOLD, 24);
    private static Font FONT_M_ITALIC = new Font("Arial", Font.ITALIC, 24);
    private static Font FONT_L = new Font("MV Boli", Font.BOLD, 84);
    private static Font FONT_XL = new Font("MV Boli", Font.PLAIN, 150);
    private static int INITIAL_DELAY = 100;
    private int delay = INITIAL_DELAY;

    // Constructor
    public Game() {
        try {
        	image = ImageIO.read(getClass().getResourceAsStream("/GUI/cherry.png"));
        } catch (IOException e) {
            didLoadCherryImage = false;
        }

        addKeyListener(new KeyListener());
        setFocusable(true);
        setBackground(new Color(173, 216, 230));
        setDoubleBuffered(true);

        // Initialize the snake away from the boundaries
        snake = new Snake(200, 200); // Example position (200, 200), adjust as needed
        status = GameStatus.NOT_STARTED;
        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        render(g);
        Toolkit.getDefaultToolkit().sync();
    }

    protected void update() {
        snake.move();

    

        if (cherry != null && snake.getHead().intersects(cherry, 8)) {
            snake.addTail();
            cherry = null;
            points++;  // Points are incremented when the cherry is eaten
            adjustGameSpeed();  // Adjust game speed when the player scores
        }

        if (cherry == null) {
            spawnCherry();
        }

        checkForGameOver();
    }

    private void adjustGameSpeed() {
        delay = Math.max(30, INITIAL_DELAY - points * 2);  // Example adjustment
        if (status == GameStatus.RUNNING) {
            timer.cancel();
            timer = new Timer();
            timer.schedule(new GameLoop(), 0, delay);
        }
    }

    protected void reset() {
        points = 0;
        cherry = null;
        snake = new Snake(getWidth() / 2, getHeight() / 2);
        setSnakeDead(false);
        delay = INITIAL_DELAY;
        setStatus(GameStatus.RUNNING);
    }

    protected void setStatus(GameStatus newStatus) {
        switch (newStatus) {
            case RUNNING:
                if (timer != null) {
                    timer.cancel();
                }
                timer = new Timer();
                timer.schedule(new GameLoop(), 0, delay);
                break;
            case PAUSED:
                if (timer != null) {
                    timer.cancel();
                }
                break;
            case GAME_OVER:
                if (timer != null) {
                    timer.cancel();
                }
                best = points > best ? points : best;
                break;
            default:
                break;
        }
        status = newStatus;
    }


    protected void togglePause() {
        setStatus(status == GameStatus.PAUSED ? GameStatus.RUNNING : GameStatus.PAUSED);
    }

    private void checkForGameOver() {
        if (status != GameStatus.RUNNING) {
            return; // Only check for game over when the game is running
        }

        Point head = snake.getHead();
        boolean hitBoundary = head.getX() <= 20
                || head.getX() >= getWidth() - 20
                || head.getY() <= 40
                || head.getY() >= getHeight() - 20;

        boolean ateItself = false;
        for (Point t : snake.getTail()) {
            ateItself = ateItself || head.equals(t);
        }

        if (hitBoundary || ateItself) {
            setSnakeDead(true);
            setStatus(GameStatus.GAME_OVER);
        }
    }

    public void drawCenteredString(Graphics g, String text, Font font, int y) {
        FontMetrics metrics = g.getFontMetrics(font);
        int x = (getWidth() - metrics.stringWidth(text)) / 2;

        g.setFont(font);
        g.drawString(text, x, y);
    }

    private void render(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        g2d.setColor(Color.BLACK);
        g2d.setFont(FONT_M);

        if (status == GameStatus.NOT_STARTED) {
            drawCenteredString(g2d, "SNAKE", FONT_XL, 180);
            drawCenteredString(g2d, "GAME", FONT_XL, 300);
            drawCenteredString(g2d, "Press any key to begin", FONT_M_ITALIC, 330);
            return;
        }

        Point p = snake.getHead();

        g2d.drawString("SCORE: " + String.format ("%02d", points), 20, 30);
        g2d.drawString("BEST: " + String.format ("%02d", best), getWidth() - 130, 30);

        if (cherry != null) {
            if (didLoadCherryImage) {
                g2d.drawImage(image, cherry.getX(), cherry.getY(), 60, 60, null);
            } else {
                g2d.setColor(Color.BLACK);
                g2d.fillOval(cherry.getX(), cherry.getY(), 10, 10);
            }
        }

        if (status == GameStatus.GAME_OVER) {
            drawCenteredString(g2d, "Press enter to start again", FONT_M_ITALIC, getHeight() / 2 + 30);
            drawCenteredString(g2d, "GAME OVER", FONT_L, getHeight() / 2);

            // Draw snake head with death animation
            g2d.setColor(Color.RED); // Change color to red
            g2d.fillRect(p.getX(), p.getY(), 10, 10); // Draw head

            // Draw an "X" on the head to indicate death
            g2d.setColor(Color.BLACK);
            g2d.drawLine(p.getX(), p.getY(), p.getX() + 10, p.getY() + 10);
            g2d.drawLine(p.getX() + 10, p.getY(), p.getX(), p.getY() + 10);

            // Draw the rest of the snake tail
            g2d.setColor(new Color(33, 70, 199));
            for (int i = 0, size = snake.getTail().size(); i < size; i++) {
                Point t = snake.getTail().get(i);
                g2d.fillRect(t.getX(), t.getY(), 10, 10);
            }

            return;
        }

        if (status == GameStatus.PAUSED) {
        	drawCenteredString(g2d, "PAUSED", FONT_L, getHeight() / 2);
        }

        // Draw snake head
        g2d.setColor(new Color(33, 70, 199));
        g2d.fillRect(p.getX(), p.getY(), 10, 10); // Adjust size for head to make it thinner

        // Draw eyes on the head
        g2d.setColor(Color.WHITE);
        int eyeSize = 2; // Make the eyes smaller to match the thinner head
        if (snake.getDirection() == Direction.RIGHT || snake.getDirection() == Direction.LEFT) {
            g2d.fillOval(p.getX() + 2, p.getY() + 2, eyeSize, eyeSize);
            g2d.fillOval(p.getX() + 2, p.getY() + 6, eyeSize, eyeSize);
        } else {
            g2d.fillOval(p.getX() + 2, p.getY() + 2, eyeSize, eyeSize);
            g2d.fillOval(p.getX() + 6, p.getY() + 2, eyeSize, eyeSize);
        }

        // Draw snake tail
        for (int i = 0, size = snake.getTail().size(); i < size; i++) {
            Point t = snake.getTail().get(i);
            g2d.fillRect(t.getX(), t.getY(), 10, 10); // Adjust size for tail segments to make them thinner
        }

        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(4));
        g2d.drawRect(20, 40, getWidth() - 40, getHeight() - 60);
    }

    public void spawnCherry() {
        int panelWidth = getWidth();
        int panelHeight = getHeight();
        
        if (panelWidth <= 60 || panelHeight <= 60) {
            return; // Avoid spawning if the panel size is too small
        }

        cherry = new Point(
                (new Random()).nextInt(panelWidth - 60) + 20,
                (new Random()).nextInt(panelHeight - 60) + 40
        );
    }

    public boolean isSnakeDead() {
		return isSnakeDead;
	}

	public void setSnakeDead(boolean isSnakeDead) {
		this.isSnakeDead = isSnakeDead;
	}

	private class KeyListener extends KeyAdapter {
	    @Override
	    public void keyPressed(KeyEvent e) {
	        int key = e.getKeyCode();

	        if (status == GameStatus.RUNNING) {
	            switch (key) {
	                case KeyEvent.VK_LEFT:
	                    snake.turn(Direction.LEFT);
	                    break;
	                case KeyEvent.VK_RIGHT:
	                    snake.turn(Direction.RIGHT);
	                    break;
	                case KeyEvent.VK_UP:
	                    snake.turn(Direction.UP);
	                    break;
	                case KeyEvent.VK_DOWN:
	                    snake.turn(Direction.DOWN);
	                    break;
	            }
	        }

	        if (status == GameStatus.NOT_STARTED) {
	            setStatus(GameStatus.RUNNING);
	        }

	        if (status == GameStatus.GAME_OVER && key == KeyEvent.VK_ENTER) {
	            reset();
	        }

	        if (key == KeyEvent.VK_P) {
	            togglePause();
	        }
	    }
	}


    private class GameLoop extends java.util.TimerTask {
        public void run() {
            update();
            repaint();
        }
    }
}

// Enums
enum GameStatus {
    NOT_STARTED, RUNNING, PAUSED, GAME_OVER
}

enum Direction {
    UP, DOWN, LEFT, RIGHT;

    public boolean isX() {
        return this == LEFT || this == RIGHT;
    }

    public boolean isY() {
        return this == UP || this == DOWN;
    }
}

// Point class
class Point {
    private int x;
    private int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Point(Point p) {
        this.x = p.getX();
        this.y = p.getY();
    }

    public void move(Direction d, int value) {
        switch (d) {
            case UP: this.y -= value; break;
            case DOWN: this.y += value; break;
            case RIGHT: this.x += value; break;
            case LEFT: this.x -= value; break;
        }
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Point setX(int x) {
        this.x = x;
        return this;
    }

    public Point setY(int y) {
        this.y = y;
        return this;
    }

    public boolean equals(Point p) {
        return this.x == p.getX() && this.y == p.getY();
    }

    public String toString() {
        return "(" + x + ", " + y + ")";
    }

    public boolean intersects(Point p) {
        return intersects(p, 5); // Adjust tolerance to match the new size
    }

    public boolean intersects(Point p, int tolerance) {
        int diffX = Math.abs(x - p.getX());
        int diffY = Math.abs(y - p.getY());

        return this.equals(p) || (diffX <= tolerance && diffY <= tolerance);
    }
}

// Snake class
class Snake {
    private Direction direction;
    private Point head;
    private ArrayList<Point> tail;

    public Snake(int x, int y) {
        this.head = new Point(x, y);
        this.direction = Direction.RIGHT;
        this.tail = new ArrayList<Point>();

        this.tail.add(new Point(0, 0));
        this.tail.add(new Point(0, 0));
        this.tail.add(new Point(0, 0));
    }

    public void move() {
        ArrayList<Point> newTail = new ArrayList<Point>();

        for (int i = 0, size = tail.size(); i < size; i++) {
            Point previous = i == 0 ? head : tail.get(i - 1);
            newTail.add(new Point(previous.getX(), previous.getY()));
        }

        this.tail = newTail;
        this.head.move(this.direction, 10); // Ensure movement matches the new size
    }

    public void addTail() {
        this.tail.add(new Point(-10, -10));
    }

    public void turn(Direction d) {
        direction = d;
    }


    public Direction getDirection() {
        return direction;
    }

    public ArrayList<Point> getTail() {
        return this.tail;
    }

    public Point getHead() {
        return this.head;
    }
}

public class Main extends JFrame {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public Main() {
        initUI();
    }

    private void initUI() {
        Game gamePanel = new Game();
        add(gamePanel);

        setTitle("Snake");
        setSize(800, 610);
        setMinimumSize(new Dimension(400, 300)); // Ensure minimum size for visibility
        setLocationRelativeTo(null);
        setResizable(true); // Allow resizing
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            Main ex = new Main();
            ex.setVisible(true);
        });
    }
}

