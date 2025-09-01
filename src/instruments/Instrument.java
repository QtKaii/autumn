package instruments;

import logging.AssignmentLogger;

public abstract class Instrument {
    private String name;
    private String description;
    private String imagePath;
    private String soundPath;

    public Instrument(String name, String description, String imagePath, String soundPath) {
        AssignmentLogger.logConstructor(this);
        this.name = name;
        this.description = description;
        this.imagePath = imagePath;
        this.soundPath = soundPath;
    }

    public abstract void playSound();

    public String getName() {
        AssignmentLogger.logMethodEntry(this);
        AssignmentLogger.logMethodExit(this);
        return name;
    }

    public String getDescription() {
        AssignmentLogger.logMethodEntry(this);
        AssignmentLogger.logMethodExit(this);
        return description;
    }

    public String getImagePath() {
        AssignmentLogger.logMethodEntry(this);
        AssignmentLogger.logMethodExit(this);
        return imagePath;
    }

    public String getSoundPath() {
        AssignmentLogger.logMethodEntry(this);
        AssignmentLogger.logMethodExit(this);
        return soundPath;
    }
}
