import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.List;

public class DrawingTool extends MouseAdapter {
	public enum ShapeType {
		LINE,
		RECTANGLE,
		ELLIPSE
	}

	private int[] strokeX = new int[100];
	private int[] strokeY = new int[100];
	private int pointCount = 0;
	private boolean drawingEnabled = false;
	private boolean eraseEnabled = false;
	private boolean fillEnabled = false;
	private ShapeType shapeType;
	private boolean shapeInProgress;
	private int shapeStartX;
	private int shapeStartY;
	private int brushSize = 6;
	private Color currentColour = Color.BLACK;
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
		if (pointCount == strokeX.length) {
			strokeX = java.util.Arrays.copyOf(strokeX, pointCount * 2);
			strokeY = java.util.Arrays.copyOf(strokeY, pointCount * 2);
		}
		strokeX[pointCount] = e.getX();
		strokeY[pointCount] = e.getY();
		pointCount++;
	}

	private void updatePreview() {
		if (pointCount > 0) {
			canvas.setPreviewStroke(new Stroke(strokeX, strokeY, pointCount, strokeColour, strokeSize));
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
	}
		
	public void setShapeType(ShapeType shapeType) {
		this.shapeType = shapeType;
		this.shapeInProgress = false;
		this.drawingEnabled = false;
		this.eraseEnabled = false;
		this.fillEnabled = false;
		pointCount = 0;
		canvas.setPreviewStroke(null);
	}

	public void changeSize(int size) {
		this.brushSize = Math.max(2, size + 1);
	}

	public int getBrushSize() {
		return brushSize;
	}

	public void SetColour(Color colour) {
		this.currentColour = colour;
	}

	public Color GetColour() {
		return this.currentColour;
	}

@Override
public void mouseDragged(MouseEvent e) {
    if (shapeType != null && shapeInProgress) {
        updateShapePreview(e.getX(), e.getY());
        return;
    }

    if (eraseEnabled && pointCount > 0) {
        int last = pointCount - 1;
        canvas.eraseLine(strokeX[last], strokeY[last], e.getX(), e.getY(), brushSize);
        addPoint(e);
    } else if (drawingEnabled && pointCount > 0) {
        addPoint(e);
        updatePreview();
    }
}

@Override
public void mousePressed(MouseEvent e) {
    if (fillEnabled) {
        commandHistory.execute(new FillCommand(canvas, e.getX(), e.getY(), currentColour));
        return;
    }

    if (shapeType != null) {
        shapeStartX = e.getX();
        shapeStartY = e.getY();
        shapeInProgress = true;
        strokeColour = currentColour;
        strokeSize = brushSize;
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
        canvas.eraseLine(e.getX(), e.getY(), e.getX(), e.getY(), brushSize);
        return;
    }

    strokeColour = currentColour;
    strokeSize = brushSize;
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
            if (strokeX[last] != e.getX() || strokeY[last] != e.getY()) {
                canvas.eraseLine(strokeX[last], strokeY[last], e.getX(), e.getY(), brushSize);
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

    if (strokeX[pointCount - 1] != e.getX() || strokeY[pointCount - 1] != e.getY()) {
        addPoint(e);
        updatePreview();
    }

    Stroke stroke = new Stroke(strokeX, strokeY, pointCount, strokeColour, strokeSize);
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
