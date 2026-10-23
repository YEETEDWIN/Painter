import java.util.ArrayList;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import javax.swing.JPanel;
import java.util.List;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;

public class DrawingCanvas extends JPanel {
    private final List<Stroke> strokes = new ArrayList<>();
    private final List<BufferedImage> fillLayers = new ArrayList<>();
    private Stroke previewStroke;
    private BufferedImage strokeLayer;

    public void addStroke(Stroke stroke) {
        ensureStrokeLayer();

        Graphics2D g = strokeLayer.createGraphics();
        try {
            stroke.draw(g);
        } finally {
            g.dispose();
        }

        strokes.add(stroke);
        repaint();
    }

    public void removeStroke(Stroke stroke) {
        strokes.remove(stroke);
        repaint();
    }

    public void addFillLayer(BufferedImage fillLayer) {
        fillLayers.add(fillLayer);
        repaint();
    }

    public void removeFillLayer(BufferedImage fillLayer) {
        fillLayers.remove(fillLayer);
        repaint();
    }

    public void setPreviewStroke(Stroke stroke) {
        this.previewStroke = stroke;
        repaint();
    }

    List<Stroke> saveCanvas() {
        return new ArrayList<>(strokes);
    }

    List<BufferedImage> saveFillLayer() {
        return new ArrayList<>(fillLayers);
    }

    public List<BufferedImage> copyFillLayers() {
        List<BufferedImage> copies = new ArrayList<>();

        for (BufferedImage layer : fillLayers) {
            BufferedImage copy = new BufferedImage(
                    layer.getWidth(), layer.getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = copy.createGraphics();

            try {
                g.drawImage(layer, 0, 0, null);
            } finally {
                g.dispose();
            }
            copies.add(copy);
        }

        return copies;
    }

    void clear() {
        strokes.clear();
        fillLayers.clear();
        previewStroke = null;

        int width = getWidth();
        int height = getHeight();
        strokeLayer = width > 0 && height > 0
                ? new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
                : null;

        repaint();
    }

    void restoreCanvasState(
            BufferedImage savedStrokeLayer,
            List<Stroke> savedStrokes,
            List<BufferedImage> savedFillLayers) {
        restoreStrokeLayer(savedStrokeLayer);

        strokes.clear();
        strokes.addAll(savedStrokes);

        fillLayers.clear();
        fillLayers.addAll(savedFillLayers);

        previewStroke = null;
        repaint();
    }

    void restoreStrokes(List<Stroke> strokesToRestore) {
        strokes.clear();
        strokes.addAll(strokesToRestore);
        repaint();
    }

    private void rebuildStrokeLayer() {
        int width = getWidth();
        int height = getHeight();

        if (width <= 0 || height <= 0) {
            strokeLayer = null;
            return;
        }

        strokeLayer = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = strokeLayer.createGraphics();
        try {
            for (Stroke stroke : strokes) {
                stroke.draw(g);
            }
        } finally {
            g.dispose();
        }
    }

    private void clearLineFromLayer(BufferedImage layer, int x1, int x2, int y1, int y2, int size) {
        Graphics2D g = layer.createGraphics();
        try {
            g.setComposite(AlphaComposite.Clear);
            g.setStroke(new BasicStroke(size, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.drawLine(x1, y1, x2, y2);
        } finally {
            g.dispose();
        }
    }

    public void eraseLine(int x1, int y1, int x2, int y2, int size) {
        if (strokeLayer == null
                || strokeLayer.getWidth() != getWidth()
                || strokeLayer.getHeight() != getHeight()) {
            rebuildStrokeLayer();
        }

        if (strokeLayer == null) {
            return;
        }

        clearLineFromLayer(strokeLayer, x1, x2, y1, y2, size);

        for (BufferedImage fillLayer : fillLayers) {
            clearLineFromLayer(fillLayer, x1, x2, y1, y2, size);
        }

        repaint();
    }

    private void ensureStrokeLayer() {
        int width = getWidth();
        int height = getHeight();

        if (width <= 0 || height <= 0) {
            throw new IllegalStateException("Canvas must have a size before drawing");
        }

        if (strokeLayer == null
                || strokeLayer.getWidth() != width
                || strokeLayer.getHeight() != height) {
            strokeLayer = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        }
    }

    public BufferedImage copyStrokeLayer() {
        ensureStrokeLayer();

        BufferedImage copy = new BufferedImage(
                strokeLayer.getWidth(), strokeLayer.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = copy.createGraphics();
        try {
            g.drawImage(strokeLayer, 0, 0, null);
        } finally {
            g.dispose();
        }
        return copy;
    }

    public void restoreStrokeLayer(BufferedImage savedLayer) {
        if (savedLayer == null) {
            throw new IllegalArgumentException("Saved stroke layer cannot be empty");
        }

        strokeLayer = new BufferedImage(
                savedLayer.getWidth(), savedLayer.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = strokeLayer.createGraphics();
        try {
            g.drawImage(savedLayer, 0, 0, null);
        } finally {
            g.dispose();
        }

        repaint();
    }

    public void restoreFillLayers(List<BufferedImage> savedLayers) {
        if (savedLayers == null) {
            throw new IllegalArgumentException("saved fill layers cannot be empty");
        }
        if (fillLayers.size() != savedLayers.size()) {
            throw new IllegalStateException("Current and saved fill-layer counts do not match");
        }

        for (int i = 0; i < fillLayers.size(); i++) {
            BufferedImage current = fillLayers.get(i);
            BufferedImage saved = savedLayers.get(i);

            if (current.getWidth() != saved.getWidth()
                    || current.getHeight() != saved.getHeight()) {
                throw new IllegalArgumentException("Saved fill layer dimensions do not match");
            }

            Graphics2D g = current.createGraphics();
            try {
                g.setComposite(java.awt.AlphaComposite.Src);
                g.drawImage(saved, 0, 0, null);
            } finally {
                g.dispose();
            }

            repaint();
        }

    }

    @Override
    protected void paintComponent(java.awt.Graphics g) {
        super.paintComponent(g);
        for (BufferedImage fillLayer : fillLayers) {
            g.drawImage(fillLayer, 0, 0, null);
        }
        if (strokeLayer != null) {
            g.drawImage(strokeLayer, 0, 0, null);
        }

        if (previewStroke != null) {
            previewStroke.draw((java.awt.Graphics2D) g);
        }
    }

    public BufferedImage createSnapshot() {
        int width = getWidth();
        int height = getHeight();

        if (width <= 0 || height <= 0) {
            throw new IllegalStateException("Canvas must have a size before taking a snapshot");
        }

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();

        try {
            paintComponent(g2d);
        } finally {
            g2d.dispose();
        }

        return image;
    }
}
