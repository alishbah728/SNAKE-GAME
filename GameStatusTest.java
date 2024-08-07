package GUI;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class GameStatusTest {

    @Test
    public void testGameStatusEnum() {
        assertEquals("NOT_STARTED", GameStatus.NOT_STARTED.toString());
        assertEquals("RUNNING", GameStatus.RUNNING.toString());
        assertEquals("PAUSED", GameStatus.PAUSED.toString());
        assertEquals("GAME_OVER", GameStatus.GAME_OVER.toString());
    }
}
