import controller.InputHandler;
import controller.ScoreManager;
import controller.SoundManager;
import model.Board;
import model.GameMode;
import model.GameState;
import view.GameFrame;
import view.GamePanel;

import javax.swing.SwingUtilities;
import javax.swing.Timer;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Application Entry Point & Global Game Loop Controller.
 * Integrates Model (M1), View (M2), and Controller (M3) subsystems.
 * Assigned to Member 3.
 */
public class Main {
    private static final String LOG_FILE = "logs/app.log";

    public static void main(String[] args) {
        log("================================================");
        log("BLOCKED.exe — Launching Application Architecture");
        log("================================================");

        // Ensure target data and logs directories exist
        ensureDirectoriesExist();

        SwingUtilities.invokeLater(() -> {
            try {
                // Initialize View, Model, and Controller components
                GamePanel gamePanel = new GamePanel();
                GameFrame gameFrame = new GameFrame(gamePanel);

                // Set up global 1-second Swing Timer for game loop ticks
                Timer gameTimer = new Timer(1000, e -> {
                    gamePanel.updateGameTick();
                });

                gameTimer.start();
                log("Swing Game Loop Timer initialized at 1000ms tick interval.");

                // Render window
                gameFrame.setVisible(true);
                log("Main GameFrame initialized and rendered successfully.");

            } catch (Exception e) {
                log("CRITICAL ERROR during application startup: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    private static void ensureDirectoriesExist() {
        File dataDir = new File("data");
        File logsDir = new File("logs");

        if (!dataDir.exists()) {
            if (dataDir.mkdirs()) {
                log("Created missing directory: /data");
            }
        }
        if (!logsDir.exists()) {
            if (logsDir.mkdirs()) {
                log("Created missing directory: /logs");
            }
        }
    }

    public static void log(String message) {
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String logEntry = "[" + timestamp + "] " + message;

        System.out.println(logEntry);

        try (PrintWriter writer = new PrintWriter(new FileWriter(LOG_FILE, true))) {
            writer.println(logEntry);
        } catch (IOException e) {
            System.err.println("Failed to write to app.log: " + e.getMessage());
        }
    }
}