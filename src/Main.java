import instruments.Drum;
import instruments.Guitar;
import instruments.Piano;
import logging.AssignmentLogger;

public class Main {
    public static void main(String[] args) {
        AssignmentLogger.logMain();

        Guitar guitar = new Guitar();
        Piano piano = new Piano();
        Drum drum = new Drum();

        guitar.playSound();
        piano.playSound();
        drum.playSound();
    }
}

