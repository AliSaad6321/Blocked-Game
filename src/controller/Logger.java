package controller;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDateTime;

public class Logger {
    private static final String LOG_FILE = "logs/app.log";

    public static void log(String message) {
        try {
            File dir = new File("logs");
            if (!dir.exists()) dir.mkdirs();
            try (PrintWriter out = new PrintWriter(new FileWriter(LOG_FILE, true))) {
                out.println("[" + LocalDateTime.now() + "] " + message);
            }
        } catch (Exception ignored) {}
    }
}