import java.awt.Color;
import java.awt.image.BufferedImage;

public class FillCommand implements Command {
    private final DrawingCanvas canvas;
    private final int x;
    private final int y;
    private final Color color;
    private BufferedImage fillLayer;

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
            fillLayer = Fill.createFillLayer(snapshot, x, y, color);
        }

        canvas.addFillLayer(fillLayer);
    }

    @Override 
    public void undo() {
        if (fillLayer != null) {
            canvas.removeFillLayer(fillLayer);
        }
    }

}
