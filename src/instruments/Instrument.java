package instruments;

import logging.AssignmentLogger;

/**
 * Base type for musical instruments used by the app. Each concrete
 * implementation supplies a human-friendly name, description, an image path
 * and a sound stem, and implements how its sound is played.
 */
public abstract class Instrument {
    private String name;
    private String description;
    private String imagePath;
    private String soundPath;

    /**
     * Creates a new instrument.
     *
     * @param name        display name of the instrument
     * @param description short description shown in the UI
     * @param imagePath   path to an image on disk
     * @param soundPath   stem used to locate matching sound files
     */
    public Instrument(String name, String description, String imagePath, String soundPath) {
        AssignmentLogger.logConstructor(this);
        this.name = name;
        this.description = description;
        this.imagePath = imagePath;
        this.soundPath = soundPath;
    }

    /**
     * Play the instrument's sound. Implementations should be non-blocking and
     * return immediately after triggering playback.
     */
    public abstract void playSound();

    /**
     * @return the display name
     */
    public String getName() {
        AssignmentLogger.logMethodEntry(this);
        AssignmentLogger.logMethodExit(this);
        return name;
    }

    /**
     * @return the description for the UI
     */
    public String getDescription() {
        AssignmentLogger.logMethodEntry(this);
        AssignmentLogger.logMethodExit(this);
        return description;
    }

    /**
     * @return the relative/absolute image path used by the GUI
     */
    public String getImagePath() {
        AssignmentLogger.logMethodEntry(this);
        AssignmentLogger.logMethodExit(this);
        return imagePath;
    }

    /**
     * @return the sound stem used by the player utility
     */
    public String getSoundPath() {
        AssignmentLogger.logMethodEntry(this);
        AssignmentLogger.logMethodExit(this);
        return soundPath;
    }
}
