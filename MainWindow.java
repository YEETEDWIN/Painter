import java.awt.*;
import java.awt.event.*;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

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
		button = createButton();
		panel = createPanel();
		label = createLabel();

		window.add(panel, BorderLayout.NORTH);
		panel.add(label);
		panel.add(button);
		JPanel canvas = new JPanel();
		canvas.setBackground(Color.WHITE);
		canvas.setPreferredSize(new Dimension(1000, 500));
		DrawFreelyTool drawTool = new DrawFreelyTool(canvas);
		window.add(canvas, BorderLayout.CENTER);

		Toolbar toolbar = new Toolbar(drawTool);
		JLayeredPane toolbarOverlay = window.getLayeredPane();
		toolbarOverlay.add(toolbar, JLayeredPane.PALETTE_LAYER);
		toolbarOverlay.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent event) {
				layoutToolbar(toolbarOverlay, toolbar);
			}
		});
		layoutToolbar(toolbarOverlay, toolbar);
	}

	public void show() {
		window.setVisible(true);
	}

	private void layoutToolbar(JLayeredPane toolbarOverlay, Toolbar toolbar) {
		toolbar.setBounds((toolbarOverlay.getWidth() - toolbar.getToolbarWidth()) / 2,10, toolbar.getToolbarWidth(), toolbar.getToolbarHeight());
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
}
