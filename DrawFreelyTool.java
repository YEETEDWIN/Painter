import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.JPanel;

public class DrawFreelyTool implements MouseListener, MouseMotionListener {
	private final JPanel canvas;
	private int[] x = new int[100];
	private int[] y = new int[100];
	private int pointCount = 0;
	private boolean drawingEnabled = false;

	public DrawFreelyTool(JPanel canvas) {
		this.canvas = canvas;
		canvas.addMouseListener(this);
		canvas.addMouseMotionListener(this);
	}

	public void setDrawingEnabled(boolean drawingEnabled) {
		this.drawingEnabled = drawingEnabled;
		if (!drawingEnabled) {
			pointCount = 0;
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (!drawingEnabled) {
			return;
		}

		Graphics g = canvas.getGraphics();
		g.setColor(Color.BLACK);
		g.fillRect(e.getX(), e.getY(), 6, 6);
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (!drawingEnabled) {
			return;
		}

		if (pointCount == x.length) {
			x = java.util.Arrays.copyOf(x, pointCount * 2);
			y = java.util.Arrays.copyOf(y, pointCount * 2);
		}
		x[pointCount] = e.getX();
		y[pointCount] = e.getY();
		pointCount++;

		Graphics g = canvas.getGraphics();
		g.setColor(Color.BLACK);
		((Graphics2D) g).setStroke(new BasicStroke(6));
		g.drawPolyline(x, y, pointCount);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (drawingEnabled) {
			pointCount = 0;
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mouseMoved(MouseEvent e) {
	}
}
