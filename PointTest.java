package GUI;


import static org.junit.Assert.*;
import org.junit.*;

public class PointTest {

    private Point point;

    @Before
    public void setUp() {
        point = new Point(10, 10);
    }

    @Test
    public void testMoveUp() {
        point.move(Direction.UP, 10);
        assertEquals(0, point.getY());
    }

    @Test
    public void testMoveDown() {
        point.move(Direction.DOWN, 10);
        assertEquals(20, point.getY());
    }

    @Test
    public void testMoveLeft() {
        point.move(Direction.LEFT, 10);
        assertEquals(0, point.getX());
    }

    @Test
    public void testMoveRight() {
        point.move(Direction.RIGHT, 10);
        assertEquals(20, point.getX());
    }

    @Test
    public void testIntersects() {
        Point otherPoint = new Point(15, 15);
        assertTrue(point.intersects(otherPoint, 5));
    }
}
