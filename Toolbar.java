import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.JToggleButton;

public class Toolbar extends JComponent {
	private static final int TOOLBAR_WIDTH = 900;
	private static final int TOOLBAR_HEIGHT = 50;
	private static final Color ACTIVE_BUTTON_TEXT_COLOR = new Color(46, 125, 50);

	public Toolbar(DrawingTool drawTool, CommandHistory commandHistory, DrawingCanvas canvas) {
		setOpaque(false);

		JTextField sizeField = new JTextField(5);
		sizeField.setBounds(190, 10, 80, 30);
		sizeField.setText("Size");
		add(sizeField);

		JButton ChangeSizeButton = new JButton("Change Size");
		ChangeSizeButton.setBounds(280, 10, 80, 30);
		ChangeSizeButton.addActionListener(event -> {
			int size = Integer.parseInt(sizeField.getText());
			drawTool.ChangeSize(size);
		});
		add(ChangeSizeButton);

		JButton colorButton = new JButton("Colour");
		colorButton.setBounds(370, 10, 80, 30);
		colorButton.setForeground(drawTool.GetColour());
		colorButton.addActionListener(event -> {
			Color chosenColor = JColorChooser.showDialog(this, "Choose a colour", drawTool.GetColour());
			if (chosenColor != null) {
				colorButton.setForeground(chosenColor);
				if (!drawTool.isEraseEnabled()) {
					drawTool.SetColour(chosenColor);
				}
			}
		});
		add(colorButton);

		JButton undoButton = new JButton("Undo");
		undoButton.setBounds(460, 10, 80, 30);
		undoButton.addActionListener(event -> commandHistory.undo());
		add(undoButton);

		JButton redoButton = new JButton("Redo");
		redoButton.setBounds(550, 10, 80, 30);
		redoButton.addActionListener(event -> commandHistory.redo());
		add(redoButton);

		JButton clearButton = new JButton("Clear");
		clearButton.setBounds(640, 10, 80, 30);
		clearButton.addActionListener(event -> {
			commandHistory.execute(new ClearCanvasCommand(canvas));
		});
		add(clearButton);

		JToggleButton eraseButton = new JToggleButton("Erase", false);
		eraseButton.setBounds(100, 10, 80, 30);
		Color eraseTextColor = eraseButton.getForeground();
		JToggleButton drawButton = new JToggleButton("Draw", false);
		drawButton.setBounds(10, 10, 80, 30);
		Color drawTextColor = drawButton.getForeground();
		JToggleButton fillButton = new JToggleButton("Fill", false);
		fillButton.setBounds(730, 10, 80, 30);
		Color fillTextColor = fillButton.getForeground();

		JPopupMenu shapeMenu = new JPopupMenu();
		JMenuItem lineItem = new JMenuItem("Line");
		lineItem.addActionListener(event -> selectShape(drawTool, colorButton, drawButton, eraseButton, drawTextColor, eraseTextColor, DrawingTool.ShapeType.LINE));
		shapeMenu.add(lineItem);
		JMenuItem rectangleItem = new JMenuItem("Rectangle");
		rectangleItem.addActionListener(event -> selectShape(drawTool, colorButton, drawButton, eraseButton, drawTextColor, eraseTextColor, DrawingTool.ShapeType.RECTANGLE));
		shapeMenu.add(rectangleItem);
		JMenuItem ellipseItem = new JMenuItem("Ellipse");
		ellipseItem.addActionListener(event -> selectShape(drawTool, colorButton, drawButton, eraseButton, drawTextColor, eraseTextColor, DrawingTool.ShapeType.ELLIPSE));
		shapeMenu.add(ellipseItem);

		JButton shapeButton = new JButton("Shape");
		shapeButton.setBounds(730, 10, 80, 30);
		shapeButton.addActionListener(event -> shapeMenu.show(shapeButton, 0, shapeButton.getHeight()));
		add(shapeButton);

		eraseButton.addActionListener(event -> {
			boolean enabled = eraseButton.isSelected();
			eraseButton.setForeground(enabled ? ACTIVE_BUTTON_TEXT_COLOR : eraseTextColor);
			if (enabled) {
				drawButton.setSelected(false);
				fillButton.setSelected(false);
				drawButton.setForeground(drawTextColor);
				fillButton.setForeground(fillTextColor);
				drawTool.setDrawingEnabled(false);
				drawTool.setFillEnabled(false);
			}
			drawTool.setEraseEnabled(enabled);
			if (enabled) {
				drawTool.SetColour(Color.WHITE);
			} else {
				drawTool.SetColour(colorButton.getForeground());
			}
		});
		add(eraseButton);

		drawButton.addActionListener(event -> {
			boolean enabled = drawButton.isSelected();
			drawButton.setForeground(enabled ? ACTIVE_BUTTON_TEXT_COLOR : drawTextColor);
			if (enabled) {
				eraseButton.setSelected(false);
				fillButton.setSelected(false);
				eraseButton.setForeground(eraseTextColor);
				fillButton.setForeground(fillTextColor);
				drawTool.setEraseEnabled(false);
				drawTool.setFillEnabled(false);
			}
			drawTool.setDrawingEnabled(enabled);
			drawTool.SetColour(colorButton.getForeground());
		});
		add(drawButton);

		fillButton.addActionListener(event -> {
			boolean enabled = fillButton.isSelected();
			fillButton.setForeground(enabled ? ACTIVE_BUTTON_TEXT_COLOR : fillTextColor);
			if (enabled) {
				eraseButton.setSelected(false);
				drawButton.setSelected(false);
				eraseButton.setForeground(eraseTextColor);
				drawButton.setForeground(drawTextColor);
				drawTool.setDrawingEnabled(false);
				drawTool.setEraseEnabled(false);
			}
			drawTool.setFillEnabled(enabled);
			drawTool.SetColour(colorButton.getForeground());
		});
		add(fillButton);
	}

	private void selectShape(DrawingTool drawTool, JButton colorButton, JToggleButton drawButton, JToggleButton eraseButton, Color drawTextColor, Color eraseTextColor, DrawingTool.ShapeType shapeType) {
		drawButton.setSelected(false);
		drawButton.setForeground(drawTextColor);
		eraseButton.setSelected(false);
		eraseButton.setForeground(eraseTextColor);
		drawTool.setEraseEnabled(false);
		drawTool.SetColour(colorButton.getForeground());
		drawTool.setShapeType(shapeType);
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
