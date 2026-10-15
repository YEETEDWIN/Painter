import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;


public class DrawFreelyTool implements MouseListener, MouseMotionListener {
	private int[] x = new int[100];
	private int[] y = new int[100];
	private int pointCount = 0;
	private boolean drawingEnabled = false;
	private int newSize = 6;
	private Color colour = Color.BLACK;
	private final DrawingCanvas canvas;
	private final CommandHistory commandHistory;
	private Color strokeColour;
	private int strokeSize;

	public DrawFreelyTool(DrawingCanvas canvas, CommandHistory commandHistory) {
		this.canvas = canvas;
		this.commandHistory = commandHistory;
		canvas.addMouseListener(this);
		canvas.addMouseMotionListener(this);
	}

	private void addPoint(MouseEvent e) {
		if (pointCount == x.length) {
			x = java.util.Arrays.copyOf(x, pointCount * 2);
			y = java.util.Arrays.copyOf(y, pointCount * 2);
		}
		x[pointCount] = e.getX();
		y[pointCount] = e.getY();
		pointCount++;
	}

	private void updatePreview() {
		if (pointCount > 0) {
			canvas.setPreviewStroke(new Stroke(x, y, pointCount, strokeColour, strokeSize));
		}
	}

	public void setDrawingEnabled(boolean drawingEnabled) {
		this.drawingEnabled = drawingEnabled;
		if (!drawingEnabled) {
			pointCount = 0;
			canvas.setPreviewStroke(null);
		}
	}

	public void ChangeSize(int Size) {
		this.newSize = Size;
	}

	public void SetColour(Color colour) {
		this.colour = colour;
	}

	public Color GetColour() {
		return this.colour;
	}

	@Override
	public void mouseClicked(MouseEvent e) {	
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (drawingEnabled && pointCount > 0) {
			addPoint(e);
			updatePreview();
		}

	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (!drawingEnabled) {
			return;
		}

		pointCount = 0;
		strokeColour = colour;
		strokeSize = newSize;
		addPoint(e);
		updatePreview();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (!drawingEnabled || pointCount == 0) {
			return;
		}

		if (x[pointCount - 1] != e.getX() || y[pointCount - 1] != e.getY()) {
			addPoint(e);
			updatePreview();
		}

		Stroke stroke = new Stroke(x, y, pointCount, strokeColour, strokeSize);
		commandHistory.execute(new DrawStrokeCommand(canvas, stroke));
		canvas.setPreviewStroke(null);
		pointCount = 0;
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mouseMoved(MouseEvent e) {
	}
}
