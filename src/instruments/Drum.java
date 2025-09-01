package instruments;

import logging.AssignmentLogger;

public class Drum extends Instrument {
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
    public void playSound() {
        AssignmentLogger.logMethodEntry(this);
        SoundPlayer.playRandomClipByStem("Drum");
        AssignmentLogger.logMethodExit(this);
    }
}

