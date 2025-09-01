import gui.InstrumentGUI;
import logging.AssignmentLogger;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        AssignmentLogger.logMain();
        SwingUtilities.invokeLater(() -> {
            InstrumentGUI gui = new InstrumentGUI();
            gui.setVisible(true);
        });
    }
}
