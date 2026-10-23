import java.util.List;
import java.awt.image.BufferedImage;

public class ClearCanvasCommand implements Command {
    private final DrawingCanvas canvas;
    private final List<Stroke> strokes;
    private final BufferedImage strokeLayer;
    private final List<BufferedImage> fillLayers;

    public ClearCanvasCommand(DrawingCanvas canvas) {
        this.canvas = canvas;
        this.strokes = canvas.saveCanvas();
        this.strokeLayer = canvas.copyStrokeLayer();
        this.fillLayers = canvas.saveFillLayer();
    }

    @Override
    public void execute() {
        canvas.clear();
    }

    @Override
    public void undo() {
        canvas.restoreCanvasState(strokeLayer, strokes, fillLayers);
    }
}
