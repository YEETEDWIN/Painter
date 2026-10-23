import java.awt.Color;
import java.awt.image.BufferedImage;

public class FillCommand implements Command {
    private final DrawingCanvas canvas;
    private final int x;
    private final int y;
    private final Color color;
    private BufferedImage fillLayer;
    private BufferedImage strokeLayerBefore;
    private BufferedImage strokeLayerAfter;
    private boolean recolorsStroke;

    public FillCommand(DrawingCanvas canvas, int x, int y,Color color) {
        this.canvas = canvas;
        this.x = x;
        this.y = y;
        this.color = color;
    }

    @Override
    public void execute() {
        if (fillLayer == null) {
            BufferedImage snapshot = canvas.createSnapshot();
            recolorsStroke = canvas.isStrokeAt(x, y);
            fillLayer = Fill.createFillLayer(snapshot, x, y, color);

            if (recolorsStroke) {
                strokeLayerBefore = canvas.copyStrokeLayer();
                canvas.applyFillToStrokeLayer(fillLayer);
                strokeLayerAfter = canvas.copyStrokeLayer();
                return;
            }
        }

        if (recolorsStroke) {
            canvas.restoreStrokeLayer(strokeLayerAfter);
        } else {
            canvas.addFillLayer(fillLayer);
        }
    }

    @Override
    public void undo() {
        if (recolorsStroke) {
            canvas.restoreStrokeLayer(strokeLayerBefore);
        } else if (fillLayer != null) {
            canvas.removeFillLayer(fillLayer);
        }
    }
}
