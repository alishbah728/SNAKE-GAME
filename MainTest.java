package GUI;

import static org.junit.Assert.*;
import org.junit.Test;
import java.awt.Dimension;

import javax.swing.JFrame;

public class MainTest {

    @Test
    public void testMainWindowSetup() {
        Main main = new Main();

        // Test if the title is set correctly
        assertEquals("Snake", main.getTitle());

        // Test if the size is set correctly
        assertEquals(new Dimension(800, 610), main.getSize());

        // Test if the minimum size is set correctly
        assertEquals(new Dimension(400, 300), main.getMinimumSize());

        // Test if the default close operation is set to exit on close
        assertEquals(JFrame.EXIT_ON_CLOSE, main.getDefaultCloseOperation());
    }
}

