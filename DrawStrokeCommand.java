import java.awt.image.BufferedImage;

public class DrawStrokeCommand implements Command {
    private final DrawingCanvas canvas;
    private final Stroke stroke;
    private BufferedImage before;
    private BufferedImage after;

    public DrawStrokeCommand(DrawingCanvas canvas, Stroke stroke) {
        this.canvas = canvas;
        this.stroke = stroke;
    }

    @Override
    public void execute() {
        if (after == null) {
            before = canvas.copyStrokeLayer();
            canvas.addStroke(stroke);
            after = canvas.copyStrokeLayer();
        } else {
            canvas.addStroke(stroke);
            canvas.restoreStrokeLayer(after);
        }
    }

    @Override
    public void undo() {
        canvas.removeStroke(stroke);
        canvas.restoreStrokeLayer(before);
    }
}
