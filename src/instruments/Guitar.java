package instruments;

import logging.AssignmentLogger;

public class Guitar extends Instrument {
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
    public void playSound() {
        AssignmentLogger.logMethodEntry(this);
        SoundPlayer.playRandomClipByStem("Guitar");
        AssignmentLogger.logMethodExit(this);
    }
}

