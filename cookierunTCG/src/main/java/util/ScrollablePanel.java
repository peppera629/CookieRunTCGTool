package util;

import java.awt.Dimension;
import java.awt.Rectangle;
import javax.swing.JPanel;
import javax.swing.Scrollable;

// A JPanel variant implementing Scrollable to fix the behavior of components trying to fill in empty space
public class ScrollablePanel extends JPanel implements Scrollable {
    @Override
    public Dimension getPreferredScrollableViewportSize() {
        return getPreferredSize();
    }

    @Override
    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
        return 16; // matches your scrollbar unit increment
    }

    @Override
    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
        return Math.max(visibleRect.height - 16, 16);
    }

    @Override
    public boolean getScrollableTracksViewportWidth() {
        return true; // stretch horizontally to fit
    }

    @Override
    public boolean getScrollableTracksViewportHeight() {
        return false; // do NOT stretch vertically; keep preferred height
    }
}