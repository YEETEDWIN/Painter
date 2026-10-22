import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;

public class DrawingCanvas extends JPanel {
    private final List<Stroke> strokes = new ArrayList<>();
    private Stroke previewStroke;

    public void addStroke(Stroke stroke) {
        strokes.add(stroke);
        repaint();
    }

    public void removeStroke(Stroke stroke) {
        strokes.remove(stroke);
        repaint();
    }

    public void setPreviewStroke(Stroke stroke) {
        this.previewStroke = stroke;
        repaint();
    }

    List<Stroke> saveCanvas() {
        return new ArrayList<>(strokes);
    }

    void clear() {
        strokes.clear();
        previewStroke = null;
        repaint();
    }

    void restoreStrokes(List<Stroke> strokesToRestore) {
        strokes.clear();
        strokes.addAll(strokesToRestore);
        repaint();
    }

    @Override
    protected void paintComponent(java.awt.Graphics g) {
        super.paintComponent(g);
        for (Stroke stroke : strokes) {
            stroke.draw((java.awt.Graphics2D) g);
        }
        if (previewStroke != null) {
            previewStroke.draw((java.awt.Graphics2D) g);
        }
    }
}
