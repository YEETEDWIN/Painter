import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.JToggleButton;

public class Toolbar extends JComponent {
	private static final int TOOLBAR_WIDTH = 900;
	private static final int TOOLBAR_HEIGHT = 50;

	public Toolbar(DrawFreelyTool drawTool, CommandHistory commandHistory) {
		setOpaque(false);

		JToggleButton drawButton = new JToggleButton("Draw", false);
		drawButton.setBounds(10, 10, 80, 30);
		drawButton.addActionListener(event -> drawTool.setDrawingEnabled(drawButton.isSelected()));
		add(drawButton);

		JTextField sizeField = new JTextField(5);
		sizeField.setBounds(110, 10, 80, 30);
		sizeField.setText("Size");
		add(sizeField);

		JButton ChangeSizeButton = new JButton("Change Size");
		ChangeSizeButton.setBounds(210, 10, 80, 30);
		ChangeSizeButton.addActionListener(event -> {
			int size = Integer.parseInt(sizeField.getText());
			drawTool.ChangeSize(size);
		});
		add(ChangeSizeButton);

		JButton colorButton = new JButton("Colour");
		colorButton.setBounds(300, 10, 80, 30);
		colorButton.setBackground(drawTool.GetColour());
		colorButton.setForeground(drawTool.GetColour());
		colorButton.addActionListener(event -> {
			Color chosenColor = JColorChooser.showDialog(this, "Choose a colour", drawTool.GetColour());
			if (chosenColor != null) {
				drawTool.SetColour(chosenColor);
				colorButton.setBackground(chosenColor);
				colorButton.setForeground(chosenColor);
			}
		});
		add(colorButton);

		JButton undoButton = new JButton("Undo");
		undoButton.setBounds(390, 10, 80, 30);
		undoButton.addActionListener(event -> commandHistory.undo());
		add(undoButton);

		JButton redoButton = new JButton("Redo");
		redoButton.setBounds(480, 10, 80, 30);
		redoButton.addActionListener(event -> commandHistory.redo());
		add(redoButton);
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
