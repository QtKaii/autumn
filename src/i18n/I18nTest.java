package i18n;

import logging.AssignmentLogger;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Tiny console utility to verify that our i18n bundles load correctly and
 * return the expected strings for English and Spanish.
 */
public class I18nTest {
    /**
     * Prints a subset of keys for EN and ES to stdout.
     */
    public static void main(String[] args) {
        AssignmentLogger.logMain();

        Locale en = Locale.ENGLISH;
        Locale es = Locale.forLanguageTag("es");

        ResourceBundle enBundle = ResourceBundle.getBundle("internationalization.MessagesBundle", en);
        ResourceBundle esBundle = ResourceBundle.getBundle("internationalization.MessagesBundle", es);

        printBundle("EN", enBundle);
        printBundle("ES", esBundle);
    }

    /**
     * Prints common GUI labels from the provided bundle with a prefix label.
     *
     * @param label   prefix to distinguish outputs
     * @param bundle  resource bundle to read
     */
    private static void printBundle(String label, ResourceBundle bundle) {
        AssignmentLogger.logStaticMethodEntry();
        System.out.println("[" + label + "]");
        System.out.println("title=" + bundle.getString("gui.title"));
        System.out.println("inputLabel=" + bundle.getString("gui.inputLabel"));
        System.out.println("searchButton=" + bundle.getString("gui.searchButton"));
        System.out.println("playButton=" + bundle.getString("gui.playButton"));
        System.out.println("langButton=" + bundle.getString("gui.langButton"));
        System.out.println("guitar=" + bundle.getString("instrument.guitar"));
        System.out.println("piano=" + bundle.getString("instrument.piano"));
        System.out.println("drum=" + bundle.getString("instrument.drum"));
        System.out.println("error.invalid=" + bundle.getString("error.invalid"));
        AssignmentLogger.logStaticMethodExit();
    }
}
