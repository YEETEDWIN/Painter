import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

public class Stroke {
    private final int[] x;
    private final int[] y;
    private final int pointCount;
    private final Color colour;
    private final int size;

    public Stroke(int[] x, int[] y, int pointCount, Color colour, int size) {
        this.x = java.util.Arrays.copyOf(x, pointCount);
        this.y = java.util.Arrays.copyOf(y, pointCount);
        this.pointCount = pointCount;
        this.colour = colour;
        this.size = size;
    }

    public void draw(Graphics2D g) {
        if (pointCount == 0) {
            return;
        }

        java.awt.Stroke originalStroke = g.getStroke();
        java.awt.Color originalColor = g.getColor();

        g.setColor(colour);
        g.setStroke(new BasicStroke(size, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        if (pointCount == 1) {
            g.drawLine(x[0], y[0], x[0], y[0]);
        } else {
            for (int i = 1; i < pointCount; i++) {
                g.drawLine(x[i - 1], y[i - 1], x[i], y[i]);
            }
        }
        g.setStroke(originalStroke);
        g.setColor(originalColor);
    } // draws a line
}
