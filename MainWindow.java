import javax.swing.JFrame;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JLabel;


public class MainWindow {

	private JFrame window;
	private JButton button;
	private JPanel panel;
	private JLabel label;

	public MainWindow() {
		window = new JFrame();
		window.setTitle("Painter");
		window.setSize(800, 600);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setLocationRelativeTo(null);
		window.setLayout(new BorderLayout());
		// create and add UI components
		button = createButton();
		panel = createPanel();
		label = createLabel();

		window.add(panel, BorderLayout.NORTH);
		panel.add(label);
		panel.add(button);
	}

	public void show() {
		window.setVisible(true);
	}

	private JButton createButton() {
		JButton button = new JButton();
		button.setSize(10, 50);
		return button;
	}

	private JPanel createPanel() {
		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		panel.setBackground(Color.GRAY);
		return panel;
	}

	private JLabel createLabel() {
		JLabel label = new JLabel();
		return label;
	}
}
