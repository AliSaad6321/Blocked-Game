package controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class ScoreManager {
    private static final String HIGHSCORE_FILE = "data/highscore.txt";
    private int currentScore = 0;
    private int highScore = 0;

    public ScoreManager() {
        loadHighScore();
    }

    public void addPlacementPoints(int blockCount) {
        currentScore += blockCount * 10;
        checkHighScore();
    }

    public void addLineClearPoints(int linesCleared) {
        if (linesCleared <= 0) return;
        currentScore += linesCleared * 100 * linesCleared;
        checkHighScore();
    }

    public void resetScore() {
        currentScore = 0;
    }

    public int getCurrentScore() { return currentScore; }
    public int getHighScore() { return highScore; }

    private void checkHighScore() {
        if (currentScore > highScore) {
            highScore = currentScore;
            saveHighScore();
        }
    }

    private void loadHighScore() {
        File file = new File(HIGHSCORE_FILE);
        if (!file.exists()) return;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine();
            if (line != null) {
                highScore = Integer.parseInt(line.trim());
            }
        } catch (Exception ignored) {}
    }

    private void saveHighScore() {
        File dir = new File("data");
        if (!dir.exists()) dir.mkdirs();
        try (PrintWriter writer = new PrintWriter(new FileWriter(HIGHSCORE_FILE))) {
            writer.println(highScore);
        } catch (IOException ignored) {}
    }
}