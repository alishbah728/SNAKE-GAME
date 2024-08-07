package GUI;

import static org.junit.Assert.*;
import org.junit.*;

public class SnakeTest {

    private Snake snake;

    @Before
    public void setUp() {
        snake = new Snake(50, 50);
    }

    @Test
    public void testInitialPosition() {
        assertEquals(50, snake.getHead().getX());
        assertEquals(50, snake.getHead().getY());
    }

    @Test
    public void testMove() {
        snake.move();
        assertEquals(60, snake.getHead().getX()); // Initially moves to the right
        assertEquals(50, snake.getHead().getY());
    }

    @Test
    public void testAddTail() {
        int initialTailSize = snake.getTail().size();
        snake.addTail();
        assertEquals(initialTailSize + 1, snake.getTail().size());
    }

    @Test
    public void testTurn() {
        snake.turn(Direction.UP);
        snake.move();
        assertEquals(40, snake.getHead().getY()); // Moves up
    }
}
