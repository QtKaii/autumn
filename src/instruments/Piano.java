package instruments;

import logging.AssignmentLogger;

public class Piano extends Instrument {
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
    public void playSound() {
        AssignmentLogger.logMethodEntry(this);
        SoundPlayer.playRandomClipByStem("Piano");
        AssignmentLogger.logMethodExit(this);
    }
}

