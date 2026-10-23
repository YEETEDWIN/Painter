import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Objects;

// Handles mouse input for drawing/erasing strokes and geometric shapes
// Tracks states such as brush size, color, fill mode, and shape mode
public class DrawingTool extends MouseAdapter {
	public enum ToolMode {
		NONE,
		DRAW,
		ERASE,
		FILL,
		SHAPE
	}

	public enum ShapeType {
		LINE,
		RECTANGLE,
		ELLIPSE
	}

	private int[] strokeX = new int[100];
	private int[] strokeY = new int[100];
	private int pointCount = 0;
	private ToolMode toolMode = ToolMode.NONE;
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

	public ToolMode getToolMode() {
		return toolMode;
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

	public void setToolMode(ToolMode toolMode) {
		Objects.requireNonNull(toolMode, "toolMode");
		if (toolMode == ToolMode.SHAPE && shapeType == null) {
			throw new IllegalStateException("Select a shape before enabling shape mode");
		}

		activateToolMode(toolMode);
	}

	private void activateToolMode(ToolMode toolMode) {
		this.toolMode = toolMode;
		if (toolMode != ToolMode.SHAPE) {
			shapeType = null;
		}
		shapeInProgress = false;
		pointCount = 0;
		canvas.setPreviewStroke(null);
	}
		
	public void selectShape(ShapeType shapeType) {
		if (shapeType == null) {
			throw new IllegalArgumentException("Shape type cannot be null");
		}
		this.shapeType = shapeType;
		activateToolMode(ToolMode.SHAPE);
	}

	public void changeSize(int size) {
		this.brushSize = Math.max(2, size + 1);
	}

	public int getBrushSize() {
		return brushSize;
	}

	public void setColour(Color colour) {
		this.currentColour = colour;
	}

	public Color getColour() {
		return this.currentColour;
	}

// Updates the preview or erases in the path of the cursor
@Override
public void mouseDragged(MouseEvent e) {
    if (toolMode == ToolMode.SHAPE && shapeInProgress) {
        updateShapePreview(e.getX(), e.getY());
        return;
    }

    if (toolMode == ToolMode.ERASE && pointCount > 0) {
        int last = pointCount - 1;
        canvas.eraseLine(strokeX[last], strokeY[last], e.getX(), e.getY(), brushSize);
        addPoint(e);
    } else if (toolMode == ToolMode.DRAW && pointCount > 0) {
        addPoint(e);
        updatePreview();
    }
} 

// Starts an action, fills the canvas, or begins a shape, drawing or erasing
@Override
public void mousePressed(MouseEvent e) {
    if (toolMode == ToolMode.FILL) {
        commandHistory.execute(new FillCommand(canvas, e.getX(), e.getY(), currentColour));
        return;
    }

    if (toolMode == ToolMode.SHAPE && shapeType != null) {
        shapeStartX = e.getX();
        shapeStartY = e.getY();
        shapeInProgress = true;
        strokeColour = currentColour;
        strokeSize = brushSize;
        updateShapePreview(e.getX(), e.getY());
        return;
    }

    if (toolMode != ToolMode.DRAW && toolMode != ToolMode.ERASE) {
        return;
    }

    pointCount = 0;

    if (toolMode == ToolMode.ERASE) {
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

// Finishes an shape, drawing or erasing and adds it into the command history
@Override
public void mouseReleased(MouseEvent e) {
    if (toolMode == ToolMode.SHAPE && shapeType != null) {
        if (!shapeInProgress) {
            return;
        }

        Stroke shape = createShape(e.getX(), e.getY());
        commandHistory.execute(new DrawStrokeCommand(canvas, shape));
        canvas.setPreviewStroke(null);
        shapeInProgress = false;
        return;
    }

    if (toolMode == ToolMode.ERASE) {
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

    if (toolMode != ToolMode.DRAW || pointCount == 0) {
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
