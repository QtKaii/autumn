package instruments;

import logging.AssignmentLogger;

/**
 * Concrete instrument representing a guitar. Uses the shared {@link SoundPlayer}
 * to play a random "Guitar*.wav" clip from the resources/sounds folder.
 */
public class Guitar extends Instrument {
    /**
     * Creates a Guitar with default name, description, image and sound stem.
     */
    public Guitar() {
        super(
            "Guitar",
            "A string instrument played by strumming or plucking.",
            "resources/images/guitar.jpg",
            "resources/sounds/Guitar"
        );
        AssignmentLogger.logConstructor(this);
    }

    @Override
    /**
     * Triggers playback of a random guitar clip. Non-blocking.
     */
    public void playSound() {
        AssignmentLogger.logMethodEntry(this);
        SoundPlayer.playRandomClipByStem("Guitar");
        AssignmentLogger.logMethodExit(this);
    }
}
