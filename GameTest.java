package GUI;

import static org.junit.Assert.*;
import org.junit.*;

import java.awt.event.KeyEvent;

public class GameTest {

    private Game game;

    @Before
    public void setUp() {
        game = new Game();
        game.setSize(800, 610); // Set the initial size for the game panel
    }

    @Test
    public void testGameInitialization() {
        assertNotNull(game);
        assertEquals(GameStatus.NOT_STARTED, game.status);
    }

    @Test
    public void testResetGame() {
        game.reset();
        assertEquals(0, game.points);
        assertEquals(GameStatus.RUNNING, game.status);
        assertFalse(game.isSnakeDead());
        assertNotNull(game.snake);
    }

    @Test
    public void testStartGame() {
        game.setStatus(GameStatus.RUNNING);
        assertEquals(GameStatus.RUNNING, game.status);
    }

    @Test
    public void testPauseGame() {
        game.setStatus(GameStatus.RUNNING);
        game.togglePause();
        assertEquals(GameStatus.PAUSED, game.status);
    }

    @Test
    public void testResumeGame() {
        game.setStatus(GameStatus.RUNNING);
        game.togglePause(); // Pauses the game
        game.togglePause(); // Resumes the game
        assertEquals(GameStatus.RUNNING, game.status);
    }

    @Test
    public void testGameOver() {
        game.setStatus(GameStatus.GAME_OVER);
        assertEquals(GameStatus.GAME_OVER, game.status);
    }

    @Test
    public void testKeyListener() {
        game.reset();
        game.setStatus(GameStatus.RUNNING);
        
        // Ensure the snake is initially moving RIGHT
        game.snake.turn(Direction.RIGHT);
        
        KeyEvent leftEvent = new KeyEvent(game, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, KeyEvent.VK_LEFT, KeyEvent.CHAR_UNDEFINED);
        game.getKeyListeners()[0].keyPressed(leftEvent); // Dispatch the key event to the game
        
        Direction newDirection = game.snake.getDirection();

        // Ensure direction changes to LEFT
        assertEquals(Direction.LEFT, newDirection);
    }


    @Test
    public void testCherrySpawning() {
        game.reset();
        game.setSize(800, 610); // Ensure game panel size
        game.spawnCherry();
        Point cherry = game.cherry;
        assertNotNull(cherry); // Check if cherry is spawned
        
        int panelWidth = game.getWidth();
        int panelHeight = game.getHeight();
        
        // Check if cherry's position is within valid bounds
        assertTrue("Cherry's X position is within valid bounds", cherry.getX() >= 20 && cherry.getX() <= panelWidth - 60);
        assertTrue("Cherry's Y position is within valid bounds", cherry.getY() >= 40 && cherry.getY() <= panelHeight - 60);
    }

    @Test
    public void testSnakeMovement() {
        game.reset();
        Point initialHead = new Point(game.snake.getHead());
        game.snake.move();
        Point newHead = game.snake.getHead();
        assertNotEquals(initialHead, newHead);
    }
    
    @Test
    public void testSnakeEatingCherry() {
        game.reset();
        game.snake = new Snake(100, 100);
        game.cherry = new Point(110, 100); // Set the cherry one move ahead of the snake's initial position

        game.update(); // Update the game state


        assertEquals(1, game.points); // Assert that the points have increased after eating the cherry
        assertNotEquals(new Point(110, 100), game.cherry); // Assert that the cherry has moved
    }




    @Test
    public void testSnakeCollisionWithWall() {
        game.reset();
        game.snake = new Snake(10, 10); // Position the snake near the boundary
        game.snake.turn(Direction.LEFT); // Move towards the wall
        game.update();
        assertTrue(game.isSnakeDead());
        assertEquals(GameStatus.GAME_OVER, game.status);
    }

    @Test
    public void testSnakeCollisionWithItself() {
        game.reset();
        game.snake = new Snake(100, 100);
        game.snake.addTail();
        game.snake.addTail();
        game.snake.turn(Direction.RIGHT);
        game.update();
        game.snake.turn(Direction.DOWN);
        game.update();
        game.snake.turn(Direction.LEFT);
        game.update();
        game.snake.turn(Direction.UP);
        game.update();
        assertTrue(game.isSnakeDead());
        assertEquals(GameStatus.GAME_OVER, game.status);
    }
}
