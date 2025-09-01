package tests;

import logging.AssignmentLogger;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Verifies that required i18n keys exist in EN and ES bundles.
 */
public class I18nCoverageTest {

    private static final String[] REQUIRED_KEYS = new String[] {
            "gui.title",
            "gui.inputLabel",
            "gui.searchButton",
            "gui.playButton",
            "gui.langButton",
            "instrument.guitar",
            "instrument.piano",
            "instrument.drum",
            "instrument.guitar.desc",
            "instrument.piano.desc",
            "instrument.drum.desc",
            "error.invalid",
            "error.noAudio",
            "error.soundsMissing",
            "error.imageMissing"
    };

    public static void main(String[] args) {
        AssignmentLogger.logMain();
        int failures = 0;
        failures += checkBundle("EN", ResourceBundle.getBundle("internationalization.MessagesBundle", Locale.ENGLISH));
        failures += checkBundle("ES", ResourceBundle.getBundle("internationalization.MessagesBundle", Locale.forLanguageTag("es")));

        if (failures == 0) {
            System.out.println("I18nCoverageTest: ALL PASSED");
        } else {
            System.err.println("I18nCoverageTest: FAILED keys missing=" + failures);
            System.exit(1);
        }
    }

    private static int checkBundle(String label, ResourceBundle bundle) {
        AssignmentLogger.logStaticMethodEntry();
        int misses = 0;
        for (String key : REQUIRED_KEYS) {
            try {
                String value = bundle.getString(key);
                if (value == null || value.trim().isEmpty()) {
                    System.err.println("[" + label + "] EMPTY: " + key);
                    misses++;
                } else {
                    System.out.println("[" + label + "] OK: " + key);
                }
            } catch (MissingResourceException e) {
                System.err.println("[" + label + "] MISSING: " + key);
                AssignmentLogger.logCatchException(e);
                misses++;
            }
        }
        AssignmentLogger.logStaticMethodExit();
        return misses;
    }
}
