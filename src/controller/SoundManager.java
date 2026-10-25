package controller;

import java.io.File;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * Loads and plays WAV effects for game actions.
 */
public class SoundManager implements AutoCloseable {
    public enum SoundEffect {
        SELECTION,
        PLACEMENT,
        LINE_CLEAR,
        INVALID_PLACEMENT,
        GAME_OVER
    }

    private final Map<SoundEffect, Clip> clips = new EnumMap<>(SoundEffect.class);
    private boolean muted;

    /**
     * Loads a WAV file for one game sound effect.
     */
    public boolean loadSound(SoundEffect effect, File soundFile) {
        if (effect == null || soundFile == null || !soundFile.isFile()) {
            return false;
        }

        try (AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile)) {
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);

            Clip previousClip = clips.put(effect, clip);
            if (previousClip != null) {
                previousClip.close();
            }

            return true;
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException exception) {
            return false;
        }
    }

    /**
     * Replays a loaded effect from its first frame.
     */
    public void playSound(SoundEffect effect) {
        if (muted || effect == null) {
            return;
        }

        Clip clip = clips.get(effect);
        if (clip == null || !clip.isOpen()) {
            return;
        }

        if (clip.isRunning()) {
            clip.stop();
        }

        clip.setFramePosition(0);
        clip.start();
    }

    public boolean isMuted() {
        return muted;
    }

    public void setMuted(boolean muted) {
        this.muted = muted;
    }

    public void toggleMute() {
        muted = !muted;
    }

    /**
     * Releases all loaded audio clips.
     */
    @Override
    public void close() {
        for (Clip clip : clips.values()) {
            clip.close();
        }
        clips.clear();
    }
}
