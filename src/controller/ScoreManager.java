package controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Tracks the current score and the saved best score.
 */
public class ScoreManager {
    private static final File DEFAULT_HIGH_SCORE_FILE = new File("data", "highscore.txt");

    private final File highScoreFile;
    private int currentScore;
    private int bestScore;

    public ScoreManager() {
        this(DEFAULT_HIGH_SCORE_FILE);
    }

    public ScoreManager(File highScoreFile) {
        if (highScoreFile == null) {
            throw new IllegalArgumentException("High score file is required.");
        }

        this.highScoreFile = highScoreFile;
        loadBestScore();
    }

    public int getCurrentScore() {
        return currentScore;
    }

    public int getBestScore() {
        return bestScore;
    }

    public void addScore(int points) {
        if (points < 0) {
            throw new IllegalArgumentException("Points cannot be negative.");
        }

        currentScore += points;
        if (currentScore > bestScore) {
            bestScore = currentScore;
            saveBestScore();
        }
    }

    public void resetCurrentScore() {
        currentScore = 0;
    }

    /**
     * Loads a saved best score, treating missing or invalid data as zero.
     */
    public void loadBestScore() {
        if (!highScoreFile.isFile()) {
            bestScore = 0;
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(highScoreFile))) {
            String value = reader.readLine();
            bestScore = value == null ? 0 : Math.max(0, Integer.parseInt(value.trim()));
        } catch (IOException | NumberFormatException exception) {
            bestScore = 0;
        }
    }

    /**
     * Saves the best score and returns whether writing succeeded.
     */
    public boolean saveBestScore() {
        File parentDirectory = highScoreFile.getParentFile();
        if (parentDirectory != null && !parentDirectory.exists() && !parentDirectory.mkdirs()) {
            return false;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(highScoreFile))) {
            writer.write(Integer.toString(bestScore));
            writer.newLine();
            return true;
        } catch (IOException exception) {
            return false;
        }
    }
}
