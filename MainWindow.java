import java.awt.*;
import java.awt.event.*;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JFrame;


public class MainWindow {

	private JFrame window;
	private JButton button;
	private JPanel panel;
	private JLabel label;

	public MainWindow() {
		window = new JFrame();
		window.setTitle("Painter");
		window.setSize(1000, 600);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setLocationRelativeTo(null);
		window.setLayout(new BorderLayout());
		window.getContentPane().setBackground(Color.BLACK);
		// create and add UI components
		button = createButton();
		panel = createPanel();
		label = createLabel();

		window.add(panel, BorderLayout.NORTH);
		panel.add(label);
		panel.add(button);
		window.add(new DrawingCanvas(), BorderLayout.CENTER);
	}

	public void show() {
		window.setVisible(true);
	}

	private JButton createButton() {
		JButton button = new JButton();
		button.setPreferredSize(new Dimension(100, 30));
		button.setAlignmentX(Component.CENTER_ALIGNMENT);
		return button;
	}

	private JPanel createPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		panel.setBackground(Color.GRAY);
		panel.setPreferredSize(new Dimension(1000, 100));
		return panel;
	}

	private JLabel createLabel() {
		JLabel label = new JLabel();
		label.setLocation(0, 0);
		label.setText("Welcome to the Painter Application!");
		label.setAlignmentX(Component.CENTER_ALIGNMENT);
		return label;
	}

	class DrawingCanvas extends JPanel implements MouseListener, MouseMotionListener {
		DrawingCanvas() {
			setBackground(Color.WHITE);
			setPreferredSize(new Dimension(1000, 500));
			addMouseListener(this);
			addMouseMotionListener(this);
		}

		public void mouseClicked(MouseEvent e) {
			int x = e.getX();
			int y = e.getY();
			Graphics g = getGraphics();
			g.setColor(Color.BLACK);
			g.fillOval(x , y , 10, 10);
		}
		public void mouseDragged(MouseEvent e) {
			int[] x;
			int[] y;
			for(int i = 0; i < 3; i++){
				x = new int[3];
				y = new int[3];
				x[i] = e.getX();
				y[i] = e.getY();
				Graphics g = getGraphics();
				g.setColor(Color.BLACK);
				g.fillOval(x[i] , y[i] , 10, 10);
			}

		}
		public void mousePressed(MouseEvent e) {}
		public void mouseReleased(MouseEvent e) {}
		public void mouseEntered(MouseEvent e) {}
		public void mouseExited(MouseEvent e) {}
	}
}
