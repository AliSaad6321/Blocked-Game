package controller;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.File;

public class SoundManager {
    private boolean muted = false;

    public void toggleMute() {
        muted = !muted;
    }

    public boolean isMuted() { return muted; }

    public void playSelection() {
        playSound("Sound/select.wav");
    }

    public void playPlacement() {
        playSound("Sound/place.wav");
    }

    public void playLineClear() {
        playSound("Sound/clear.wav");
    }

    public void playInvalid() {
        playSound("Sound/invalid.wav");
    }

    public void playGameOver() {
        playSound("Sound/invalid.wav");
    }

    private void playSound(String path) {
        if (muted) return;
        try {
            File soundFile = new File(path);
            if (soundFile.exists()) {
                AudioInputStream audioInput = AudioSystem.getAudioInputStream(soundFile);
                Clip clip = AudioSystem.getClip();
                clip.open(audioInput);
                clip.start();
            }
        } catch (Exception ignored) {}
    }
}