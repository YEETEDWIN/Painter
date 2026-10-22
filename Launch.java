import javax.swing.SwingUtilities;

public class Launch {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainWindow mainWindow = new MainWindow();
            mainWindow.show();
        });
    }
}
    
