import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.image.BufferedImage;
import java.util.List;
import java.awt.event.MouseEvent;

public class DrawingTool extends MouseAdapter {
	public enum ShapeType {
		LINE,
		RECTANGLE,
		ELLIPSE
	}

	private int[] x = new int[100];
	private int[] y = new int[100];
	private int pointCount = 0;
	private boolean drawingEnabled = false;
	private boolean eraseEnabled = false;
	private boolean fillEnabled = false;
	private ShapeType shapeType;
	private boolean shapeInProgress;
	private int shapeStartX;
	private int shapeStartY;
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
		if (drawingEnabled) {
			shapeType = null;
		}
		if (!drawingEnabled) {
			pointCount = 0;
			canvas.setPreviewStroke(null);
		}
	}

	public void setEraseEnabled(boolean eraseEnabled) {
		this.eraseEnabled = eraseEnabled;
		if (eraseEnabled) {
			shapeType = null;
		}
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
	public void setShapeType(ShapeType shapeType) {
		this.shapeType = shapeType;
		this.shapeInProgress = false;
		this.drawingEnabled = false;
		this.eraseEnabled = false;
		pointCount = 0;
		canvas.setPreviewStroke(null);
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
    if (shapeType != null && shapeInProgress) {
        updateShapePreview(e.getX(), e.getY());
        return;
    }

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

    if (shapeType != null) {
        shapeStartX = e.getX();
        shapeStartY = e.getY();
        shapeInProgress = true;
        strokeColour = colour;
        strokeSize = newSize;
        updateShapePreview(e.getX(), e.getY());
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
    if (shapeType != null) {
        if (!shapeInProgress) {
            return;
        }

        Stroke shape = createShape(e.getX(), e.getY());
        commandHistory.execute(new DrawStrokeCommand(canvas, shape));
        canvas.setPreviewStroke(null);
        shapeInProgress = false;
        return;
    }

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

	private void updateShapePreview(int endX, int endY) {
		canvas.setPreviewStroke(createShape(endX, endY));
	}

	private Stroke createShape(int endX, int endY) {
		switch (shapeType) {
			case LINE:
				return new Stroke(
					new int[] {shapeStartX, endX},
					new int[] {shapeStartY, endY},
					2, strokeColour, strokeSize);
			case RECTANGLE:
				int left = Math.min(shapeStartX, endX);
				int right = Math.max(shapeStartX, endX);
				int top = Math.min(shapeStartY, endY);
				int bottom = Math.max(shapeStartY, endY);
				return new Stroke(
					new int[] {left, right, right, left, left},
					new int[] {top, top, bottom, bottom, top},
					5, strokeColour, strokeSize);
			case ELLIPSE:
				int centerX = (shapeStartX + endX) / 2;
				int centerY = (shapeStartY + endY) / 2;
				int radiusX = Math.abs(endX - shapeStartX) / 2;
				int radiusY = Math.abs(endY - shapeStartY) / 2;

				int numPoints = 1000;
				int[] ellipseX = new int[numPoints];
				int[] ellipseY = new int[numPoints];

				for (int i = 0; i < numPoints; i++) {
					double angle = 2 * Math.PI * i / numPoints;
					ellipseX[i] = centerX + (int)(radiusX * Math.cos(angle));
					ellipseY[i] = centerY + (int)(radiusY * Math.sin(angle));
				}

				return new Stroke(ellipseX, ellipseY, numPoints, strokeColour, strokeSize);
			default:
				return null;
		}
	}
}
