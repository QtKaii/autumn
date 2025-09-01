package tests;

import exceptions.UnrecognizedInstrumentException;
import instruments.Drum;
import instruments.Guitar;
import instruments.Instrument;
import instruments.InstrumentFactory;
import instruments.Piano;
// import logging.AssignmentLogger;

import java.util.Locale;

/**
 * Lightweight test harness for InstrumentFactory without external frameworks.
 * Exits with a non-zero code on failure.
 */
public class InstrumentFactoryTest {

    public static void main(String[] args) {
        // AssignmentLogger.logMain();
        int failures = 0;

        failures += expectInstrument("Guitar -> Guitar", "Guitar", Locale.ENGLISH, Guitar.class);
        failures += expectInstrument("Piano -> Piano", "Piano", Locale.ENGLISH, Piano.class);
        failures += expectInstrument("Drum -> Drum", "Drum", Locale.ENGLISH, Drum.class);

        // Spanish common names from bundles
        failures += expectInstrument("Guitarra -> Guitar", "Guitarra", Locale.forLanguageTag("es"), Guitar.class);
        failures += expectInstrument("Piano(ES) -> Piano", "Piano", Locale.forLanguageTag("es"), Piano.class);
        failures += expectInstrument("Tambor -> Drum", "Tambor", Locale.forLanguageTag("es"), Drum.class);

        // Raw keys accepted regardless of locale
        failures += expectInstrument("raw 'guitar'", "guitar", Locale.ENGLISH, Guitar.class);

        failures += expectThrows("invalid name throws", "not-an-instrument", Locale.ENGLISH);

        if (failures == 0) {
            System.out.println("InstrumentFactoryTest: ALL PASSED");
        } else {
            System.err.println("InstrumentFactoryTest: FAILED tests=" + failures);
            System.exit(1);
        }
    }

    private static int expectInstrument(String label, String input, Locale locale, Class<? extends Instrument> expected) {
        // AssignmentLogger.logStaticMethodEntry();
        try {
            Instrument inst = InstrumentFactory.fromInput(input, locale);
            if (!expected.isInstance(inst)) {
                System.err.println("[FAIL] " + label + " expected=" + expected.getSimpleName() + " got=" + inst.getClass().getSimpleName());
                // AssignmentLogger.logStaticMethodExit();
                return 1;
            }
            System.out.println("[PASS] " + label);
            // AssignmentLogger.logStaticMethodExit();
            return 0;
        } catch (Exception e) {
            System.err.println("[FAIL] " + label + " threw " + e);
            // AssignmentLogger.logCatchException(e);
            // AssignmentLogger.logStaticMethodExit();
            return 1;
        }
    }

    private static int expectThrows(String label, String input, Locale locale) {
        // AssignmentLogger.logStaticMethodEntry();
        try {
            InstrumentFactory.fromInput(input, locale);
            System.err.println("[FAIL] " + label + " (expected exception)");
            // AssignmentLogger.logStaticMethodExit();
            return 1;
        } catch (UnrecognizedInstrumentException expected) {
            System.out.println("[PASS] " + label);
            // AssignmentLogger.logStaticMethodExit();
            return 0;
        } catch (Exception e) {
            System.err.println("[FAIL] " + label + " wrong exception: " + e);
            // AssignmentLogger.logCatchException(e);
            // AssignmentLogger.logStaticMethodExit();
            return 1;
        }
    }
}

