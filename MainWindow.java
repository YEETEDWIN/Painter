import javax.swing.JFrame;

public class MainWindow {
	private JFrame window;

	public MainWindow() {
		window = new JFrame();
		window.setTitle("My Application");
		window.setSize(800, 600);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setLocationRelativeTo(null);
	}

	public void show() {
		window.setVisible(true);
	}
}
