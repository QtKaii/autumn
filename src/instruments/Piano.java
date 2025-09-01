package instruments;

import logging.AssignmentLogger;

/**
 * Concrete instrument representing a piano. Plays a random "Piano*.wav" clip
 * via the shared {@link SoundPlayer}.
 */
public class Piano extends Instrument {
    /**
     * Creates a Piano with default metadata and resource paths.
     */
    public Piano() {
        super(
            "Piano",
            "A keyboard instrument producing sound by hammers striking strings.",
            "resources/images/piano.jpg",
            "resources/sounds/Piano"
        );
        AssignmentLogger.logConstructor(this);
    }

    @Override
    /**
     * Triggers playback of a random piano clip. Non-blocking.
     */
    public void playSound() {
        AssignmentLogger.logMethodEntry(this);
        SoundPlayer.playRandomClipByStem("Piano");
        AssignmentLogger.logMethodExit(this);
    }
}
