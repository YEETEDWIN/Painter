import java.util.List;

public class ClearCanvasCommand implements Command {
    private final DrawingCanvas canvas;
    private final List<Stroke> strokes;

    public ClearCanvasCommand(DrawingCanvas canvas) {
        this.canvas = canvas;
        this.strokes = canvas.saveCanvas();
    }

    @Override
    public void execute() {
        canvas.clear();
    }

    @Override
    public void undo() {
        canvas.restoreStrokes(strokes);
    }
}
