import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
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

		button = createButton();
		panel = createPanel();
		label = createLabel();

		panel.add(label);
		panel.add(button);
		window.add(panel, BorderLayout.CENTER);
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
		return panel;
	}

	private JLabel createLabel() {
		JLabel label = new JLabel("Welcome to the Painter Application!");
		label.setAlignmentX(Component.CENTER_ALIGNMENT);
		return label;
	}
}
