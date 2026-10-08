import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JComponent;
import javax.swing.JToggleButton;

public class Toolbar extends JComponent {
	private static final int TOOLBAR_WIDTH = 300;
	private static final int TOOLBAR_HEIGHT = 40;

	public Toolbar(DrawFreelyTool drawTool) {
		setOpaque(false);
		
		JToggleButton drawButton = new JToggleButton("Draw", false);
		drawButton.setBounds(10, 5, 80, 30);
		drawButton.addActionListener(event -> drawTool.setDrawingEnabled(drawButton.isSelected()));
		add(drawButton);
	}

	public int getToolbarWidth() {
		return TOOLBAR_WIDTH;
	}

	public int getToolbarHeight() {
		return TOOLBAR_HEIGHT;
	}

    @Override
    public void paintComponent(Graphics graphics) {
        graphics.setColor(Color.BLACK);
        graphics.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
    }
}
