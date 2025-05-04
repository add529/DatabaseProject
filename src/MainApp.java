// Class that launches the GUI application
public class MainApp {
    public static void launch() {
        MainFrame form = new MainFrame();
        form.setVisible(true);
    }

    // Entry point of the program (calls launcher)
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> launch());
    }
}
