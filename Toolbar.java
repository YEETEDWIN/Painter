import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JComponent;
import javax.swing.JToggleButton;
import javax.swing.JTextField;

public class Toolbar extends JComponent {
	private static final int TOOLBAR_WIDTH = 700;
	private static final int TOOLBAR_HEIGHT = 80;

	public Toolbar(DrawFreelyTool drawTool) {
		setOpaque(false);

		JToggleButton drawButton = new JToggleButton("Draw", false);
		drawButton.setBounds(10, 5, 80, 30);
		drawButton.addActionListener(event -> drawTool.setDrawingEnabled(drawButton.isSelected()));
		add(drawButton);

		JTextField sizeField = new JTextField(5);
		sizeField.setBounds(100, 5, 80, 30);
		sizeField.setText("Size");
		add(sizeField);

		JToggleButton ChangeSizeButton = new JToggleButton("Change Size", false);
		ChangeSizeButton.setBounds(200, 5, 80, 30);
		ChangeSizeButton.addActionListener(event -> {
			int size = Integer.parseInt(sizeField.getText());
			drawTool.ChangeSize(size);
		});
		add(ChangeSizeButton);

		JTextField RedField = new JTextField(5);
		RedField.setBounds(10, 45, 80, 30);
		RedField.setText("Red (0-255)");
		add(RedField);

		JTextField GreenField = new JTextField(5);
		GreenField.setBounds(100, 45, 80, 30);
		GreenField.setText("Green (0-255)");
		add(GreenField);

		JTextField BlueField = new JTextField(5);
		BlueField.setBounds(200, 45, 80, 30);
		BlueField.setText("Blue (0-255)");
		add(BlueField);

		JToggleButton ChangeColourButton = new JToggleButton("Change Colour", false);
		ChangeColourButton.setBounds(300, 45, 80, 30);
		ChangeColourButton.addActionListener(event -> {
			int red = Integer.parseInt(RedField.getText());
			int green = Integer.parseInt(GreenField.getText());
			int blue = Integer.parseInt(BlueField.getText());
			drawTool.SetColour(new Color(red, green, blue));
		});
		add(ChangeColourButton);
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
