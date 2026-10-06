import javax.swing.SwingUtilities;

public class Launch {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                MainWindow mainWindow = new MainWindow();
                mainWindow.show();
            }
        });
    }
}
    
