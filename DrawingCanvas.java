import javax.swing.JPanel;
import java.util.List;
import java.util.ArrayList;

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
