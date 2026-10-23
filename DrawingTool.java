import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.awt.image.BufferedImage;
import java.util.List;

public class DrawingTool extends MouseAdapter {
	private int[] x = new int[100];
	private int[] y = new int[100];
	private int pointCount = 0;
	private boolean drawingEnabled = false;
	private boolean eraseEnabled = false;
	private boolean fillEnabled = false;
	private int newSize = 6;
	private Color colour = Color.BLACK;
	private final DrawingCanvas canvas;
	private final CommandHistory commandHistory;
	private Color strokeColour;
	private int strokeSize;
	private BufferedImage eraseBefore;
	private List<BufferedImage> eraseFillBefore;

	public boolean isEraseEnabled() {
		return eraseEnabled;
	}

	public DrawingTool(DrawingCanvas canvas, CommandHistory commandHistory) {
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

	public void setEraseEnabled(boolean eraseEnabled) {
		this.eraseEnabled = eraseEnabled;
		if (!eraseEnabled) {
			pointCount = 0;
			canvas.setPreviewStroke(null);
		}
	}

	public void setFillEnabled(boolean fillEnabled) {
		this.fillEnabled = fillEnabled;

		if (fillEnabled) {
			drawingEnabled = false;
			eraseEnabled = false;
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
	public void mouseDragged(MouseEvent e) {
		if (eraseEnabled && pointCount > 0) {
			int last = pointCount - 1;
			canvas.eraseLine(x[last], y[last], e.getX(), e.getY(), newSize);
			addPoint(e);
		} else if (drawingEnabled && pointCount > 0) {
			addPoint(e);
			updatePreview();
		}

	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (fillEnabled) {
			commandHistory.execute(
					new FillCommand(canvas, e.getX(), e.getY(), colour));
			return;
		}

		if (!drawingEnabled && !eraseEnabled) {
			return;
		}

		pointCount = 0;

		if (eraseEnabled) {
			eraseBefore = canvas.copyStrokeLayer();
			eraseFillBefore = canvas.copyFillLayers();
			addPoint(e);
			canvas.eraseLine(e.getX(), e.getY(), e.getX(), e.getY(), newSize);
			return;
		}
		strokeColour = colour;
		strokeSize = newSize;
		addPoint(e);
		updatePreview();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (eraseEnabled) {
			if (pointCount > 0) {
				int last = pointCount - 1;
				if (x[last] != e.getX() || y[last] != e.getY()) {
					canvas.eraseLine(x[last], y[last], e.getX(), e.getY(), newSize);
				}

				BufferedImage eraseAfter = canvas.copyStrokeLayer();
				List<BufferedImage> eraseFillAfter = canvas.copyFillLayers();
				commandHistory.execute(new EraseCommand(
					canvas, eraseBefore, eraseAfter, eraseFillBefore, eraseFillAfter));
			}

			eraseBefore = null;
			eraseFillBefore = null;
			pointCount = 0;
			return;
		}

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

}
