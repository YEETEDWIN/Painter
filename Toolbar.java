import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JComponent;

public class Toolbar extends JComponent {
	private static final int TOOLBAR_WIDTH = 300;
	private static final int TOOLBAR_HEIGHT = 40;

	public Toolbar() {
		setOpaque(false);
	}

	public int getToolbarWidth() {
		return TOOLBAR_WIDTH;
	}

	public int getToolbarHeight() {
		return TOOLBAR_HEIGHT;
	}

    @Override
    protected void paintComponent(Graphics graphics) {
        graphics.setColor(Color.BLACK);
        graphics.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
    }
}
