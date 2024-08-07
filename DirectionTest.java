STEPS OF CREATING GIT REPOSITORY
public class DirectionTest {

    @Test
    public void testIsX() {
        assertTrue(Direction.LEFT.isX());
        assertTrue(Direction.RIGHT.isX());
        assertFalse(Direction.UP.isX());
        assertFalse(Direction.DOWN.isX());
    }

    @Test
    public void testIsY() {
        assertTrue(Direction.UP.isY());
        assertTrue(Direction.DOWN.isY());
        assertFalse(Direction.LEFT.isY());
        assertFalse(Direction.RIGHT.isY());
    }
}
