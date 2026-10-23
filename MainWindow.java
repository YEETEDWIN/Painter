import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.SwingUtilities;

public class MainWindow {

	private final JFrame window;
	private Toolbar toolbar;
	private JLayeredPane toolbarOverlay;

	public MainWindow() {
		window = new JFrame();
		window.setTitle("Painter");
		window.setSize(1000, 600);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setLocationRelativeTo(null);
		window.setLayout(new BorderLayout());
		window.getContentPane().setBackground(Color.BLACK);

		DrawingCanvas canvas = new DrawingCanvas();
		canvas.setBackground(Color.WHITE);
		canvas.setPreferredSize(new Dimension(1000, 500));
		CommandHistory commandHistory = new CommandHistory();
		DrawingTool drawTool = new DrawingTool(canvas, commandHistory);
		window.add(canvas, BorderLayout.CENTER);

		toolbar = new Toolbar(drawTool, commandHistory, canvas);
		toolbarOverlay = window.getLayeredPane();
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
		SwingUtilities.invokeLater(() -> layoutToolbar(toolbarOverlay, toolbar));
	}

	private void layoutToolbar(JLayeredPane toolbarOverlay, Toolbar toolbar) {
		toolbar.setBounds((toolbarOverlay.getWidth() - toolbar.getToolbarWidth()) / 2,10, toolbar.getToolbarWidth(), toolbar.getToolbarHeight());
	}

}
