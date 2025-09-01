package instruments;

import logging.AssignmentLogger;

/**
 * Concrete instrument representing a drum. Plays a random "Drum*.wav" clip
 * via the shared {@link SoundPlayer}.
 */
public class Drum extends Instrument {
    /**
     * Creates a Drum with default metadata and resource paths.
     */
    public Drum() {
        super(
            "Drum",
            "A percussion instrument producing sound when struck.",
            "resources/images/drum.jpg",
            "resources/sounds/Drum"
        );
        AssignmentLogger.logConstructor(this);
    }

    @Override
    /**
     * Triggers playback of a random drum clip. Non-blocking.
     */
    public void playSound() {
        AssignmentLogger.logMethodEntry(this);
        SoundPlayer.playRandomClipByStem("Drum");
        AssignmentLogger.logMethodExit(this);
    }
}
