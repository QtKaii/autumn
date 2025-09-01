import gui.InstrumentGUI;
import logging.AssignmentLogger;
import javax.swing.SwingUtilities;

/**
 * Application entry point. Starts the Swing GUI on the EDT.
 */
public class Main {
    /**
     * Launches the UI.
     *
     * @param args ignored
     */
    public static void main(String[] args) {
        AssignmentLogger.logMain();
        SwingUtilities.invokeLater(() -> {
            InstrumentGUI gui = new InstrumentGUI();
            gui.setVisible(true);
        });
    }
}
