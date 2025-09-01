package instruments;

import exceptions.UnrecognizedInstrumentException;
import logging.AssignmentLogger;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Factory that converts free-text input (English or Spanish) into a concrete
 * {@link Instrument} instance. It relies on the i18n bundles to resolve the
 * localized names and also accepts raw keys like "guitar".
 */
public class InstrumentFactory {
    /**
     * Parses the user input and returns the corresponding instrument.
     *
     * @param rawInput user-entered instrument name
     * @param locale   locale used to normalize case and match i18n labels
     * @return the matching instrument instance
     * @throws UnrecognizedInstrumentException when the input cannot be mapped
     */
    public static Instrument fromInput(String rawInput, Locale locale) throws UnrecognizedInstrumentException {
        AssignmentLogger.logStaticMethodEntry();
        if (rawInput == null) {
            AssignmentLogger.logStaticMethodExit();
            throw new UnrecognizedInstrumentException("Null input");
        }

        String input = rawInput.trim().toLowerCase(locale == null ? Locale.getDefault() : locale);

        Map<String, String> nameToKey = new HashMap<>();

        // English names
        ResourceBundle en = ResourceBundle.getBundle("internationalization.MessagesBundle", Locale.ENGLISH);
        nameToKey.put(en.getString("instrument.guitar").toLowerCase(Locale.ENGLISH), "guitar");
        nameToKey.put(en.getString("instrument.piano").toLowerCase(Locale.ENGLISH), "piano");
        nameToKey.put(en.getString("instrument.drum").toLowerCase(Locale.ENGLISH), "drum");

        // Spanish names
        ResourceBundle es = ResourceBundle.getBundle("internationalization.MessagesBundle", Locale.forLanguageTag("es"));
        nameToKey.put(es.getString("instrument.guitar").toLowerCase(Locale.forLanguageTag("es")), "guitar");
        nameToKey.put(es.getString("instrument.piano").toLowerCase(Locale.forLanguageTag("es")), "piano");
        nameToKey.put(es.getString("instrument.drum").toLowerCase(Locale.forLanguageTag("es")), "drum");

        // Raw keys
        nameToKey.put("guitar", "guitar");
        nameToKey.put("piano", "piano");
        nameToKey.put("drum", "drum");

        String key = nameToKey.get(input);
        if (key == null) {
            AssignmentLogger.logStaticMethodExit();
            throw new UnrecognizedInstrumentException("Instrument not recognized: " + rawInput);
        }

        Instrument result;
        switch (key) {
            case "guitar":
                result = new Guitar();
                break;
            case "piano":
                result = new Piano();
                break;
            case "drum":
                result = new Drum();
                break;
            default:
                AssignmentLogger.logStaticMethodExit();
                throw new UnrecognizedInstrumentException("Instrument key not recognized: " + key);
        }

        AssignmentLogger.logStaticMethodExit();
        return result;
    }
}
