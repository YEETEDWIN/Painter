import java.awt.image.BufferedImage;
import java.util.List;

public class EraseCommand implements Command {
    private final DrawingCanvas canvas;
    private final BufferedImage before;
    private final BufferedImage after;
    private final List<BufferedImage> fillLayersBefore;
    private final List<BufferedImage> fillLayersAfter;

    public EraseCommand (
        DrawingCanvas canvas,
        BufferedImage before,
        BufferedImage after,
        List<BufferedImage> fillLayersBefore,
        List<BufferedImage> fillLayersAfter) {
            this.canvas = canvas;
            this.before = before;
            this.after = after;
            this.fillLayersBefore = fillLayersBefore;
            this.fillLayersAfter = fillLayersAfter;
    }

    @Override 
    public void execute() {
        canvas.restoreStrokeLayer(after);
        canvas.restoreFillLayers(fillLayersAfter);
    }

    @Override 
    public void undo() {
        canvas.restoreStrokeLayer(before);
        canvas.restoreFillLayers(fillLayersBefore);
    }
}
