package instruments;

import logging.AssignmentLogger;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Small utility responsible for locating and playing audio clips from disk
 * using the Java Sound API. Clips are expected to be uncompressed WAV files.
 */
class SoundPlayer {
    private static final Random RNG = new Random();

    /**
     * Picks a random WAV in resources/sounds whose filename starts with the
     * provided stem and plays it via a {@link Clip}.
     *
     * @param stem filename prefix (e.g. "Guitar")
     */
    static void playRandomClipByStem(String stem) {
        AssignmentLogger.logStaticMethodEntry();
        List<File> candidates = findMatchingSounds(stem);
        if (candidates.isEmpty()) {
            AssignmentLogger.logStaticMethodExit();
            return;
        }
        File pick = candidates.get(RNG.nextInt(candidates.size()));
        playClipFromFile(pick);
        AssignmentLogger.logStaticMethodExit();
    }

    /**
     * Returns all WAV files in resources/sounds that start with the given stem.
     */
    private static List<File> findMatchingSounds(String stem) {
        AssignmentLogger.logStaticMethodEntry();
        List<File> matches = new ArrayList<>();
        File dir = new File("resources/sounds");
        File[] files = dir.listFiles((_, name) -> name.startsWith(stem) && name.toLowerCase().endsWith(".wav"));
        if (files != null) {
            for (File f : files) {
                matches.add(f);
            }
        }
        AssignmentLogger.logStaticMethodExit();
        return matches;
    }

    /**
     * Opens and starts the given WAV file as a {@link Clip}. Playback starts
     * asynchronously and this method returns immediately.
     */
    private static void playClipFromFile(File file) {
        AssignmentLogger.logStaticMethodEntry();
        try (AudioInputStream audioIn = AudioSystem.getAudioInputStream(file)) {
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            AssignmentLogger.logCatchException(e);
        } finally {
            AssignmentLogger.logStaticMethodExit();
        }
    }
}
