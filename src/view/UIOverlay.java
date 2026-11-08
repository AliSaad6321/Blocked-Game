package view;

import model.GameMode;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class UIOverlay {
    public static final Rectangle PLAY_CLASSIC_BTN = new Rectangle(125, 220, 250, 50);
    public static final Rectangle PLAY_TIME_BTN = new Rectangle(125, 285, 250, 50);
    public static final Rectangle INSTRUCTIONS_BTN = new Rectangle(125, 350, 250, 50);
    public static final Rectangle EXIT_BTN = new Rectangle(125, 415, 250, 50);

    public static final Rectangle RESUME_BTN = new Rectangle(150, 260, 200, 48);
    public static final Rectangle RESTART_BTN = new Rectangle(150, 320, 200, 48);
    public static final Rectangle MAIN_MENU_BTN = new Rectangle(150, 380, 200, 48);

    private final Map<String, BufferedImage> images = new HashMap<>();
    private Font customFont;

    public UIOverlay() {
        loadCustomFont();
        loadImage("btn_classic", "UI/classic/button.png");
        loadImage("btn_time", "UI/time-attack/button.png");
        loadImage("btn_instructions", "UI/instructions/button.png");
        loadImage("btn_exit", "UI/exit/button.png");
        loadImage("btn_resume", "UI/resume/button.png");
        loadImage("btn_restart", "UI/restart/button.png");
        loadImage("btn_menu", "UI/menu/button.png");
        loadImage("star", "UI/best/star.png");
        loadImage("cross", "UI/game-over/cross.png");
        loadImage("check", "UI/win/check.png");
    }

    private void loadCustomFont() {
        try {
            File fontFile = new File("UI/Font/Kenney Future Narrow.ttf");
            if (fontFile.exists()) {
                customFont = Font.createFont(Font.TRUETYPE_FONT, fontFile);
            } else {
                customFont = new Font("SansSerif", Font.BOLD, 16);
            }
        } catch (Exception e) {
            customFont = new Font("SansSerif", Font.BOLD, 16);
        }
    }

    private Font getFont(float size) {
        return customFont.deriveFont(Font.BOLD, size);
    }

    private void loadImage(String key, String path) {
        try {
            File f = new File(path);
            if (f.exists()) {
                images.put(key, ImageIO.read(f));
            }
        } catch (Exception ignored) {}
    }

    public void renderMainMenu(Graphics2D g2d, int width, int height) {
        g2d.setColor(new Color(20, 20, 20, 240));
        g2d.fillRect(0, 0, width, height);

        g2d.setColor(new Color(231, 76, 60));
        g2d.setFont(getFont(32f));
        g2d.drawString("BLOCKED.exe", width / 2 - 110, 130);

        g2d.setColor(Color.LIGHT_GRAY);
        g2d.setFont(getFont(14f));
        g2d.drawString("The Block Burst Challenge", width / 2 - 115, 165);

        drawAssetButton(g2d, PLAY_CLASSIC_BTN, "btn_classic", "Classic Mode", new Color(46, 204, 113));
        drawAssetButton(g2d, PLAY_TIME_BTN, "btn_time", "Time Attack", new Color(52, 152, 219));
        drawAssetButton(g2d, INSTRUCTIONS_BTN, "btn_instructions", "Instructions", new Color(52, 152, 219));
        drawAssetButton(g2d, EXIT_BTN, "btn_exit", "Exit Game", new Color(231, 76, 60));
    }

    public void renderInstructions(Graphics2D g2d, int width, int height) {
        g2d.setColor(new Color(20, 20, 20, 245));
        g2d.fillRect(0, 0, width, height);

        g2d.setColor(Color.WHITE);
        g2d.setFont(getFont(24f));
        g2d.drawString("HOW TO PLAY", width / 2 - 95, 90);

        g2d.setFont(new Font("SansSerif", Font.PLAIN, 14));
        int y = 140;
        g2d.drawString("• Drag & drop shapes onto the 8x8 grid.", 50, y); y += 30;
        g2d.drawString("• Keyboard: Use 1, 2, 3 to select; Arrow keys to move;", 50, y); y += 22;
        g2d.drawString("  Press Enter / Space to place piece.", 65, y); y += 30;
        g2d.drawString("• Clear full rows & columns to burst blocks & score.", 50, y); y += 30;
        g2d.drawString("• Time Attack: Reach 500 points within 180 seconds!", 50, y); y += 30;
        g2d.drawString("• Hotkeys: P = Pause | R = Restart | M = Mute", 50, y); y += 45;

        drawAssetButton(g2d, MAIN_MENU_BTN, "btn_menu", "Main Menu", new Color(52, 152, 219));
    }

    public void renderPauseScreen(Graphics2D g2d, int width, int height) {
        g2d.setColor(new Color(0, 0, 0, 195));
        g2d.fillRect(0, 0, width, height);

        g2d.setColor(Color.WHITE);
        g2d.setFont(getFont(28f));
        g2d.drawString("GAME PAUSED", width / 2 - 110, 200);

        drawAssetButton(g2d, RESUME_BTN, "btn_resume", "Resume", new Color(46, 204, 113));
        drawAssetButton(g2d, RESTART_BTN, "btn_restart", "Restart", new Color(241, 196, 15));
        drawAssetButton(g2d, MAIN_MENU_BTN, "btn_menu", "Main Menu", new Color(231, 76, 60));
    }

    public void renderGameOverScreen(Graphics2D g2d, int width, int height, int score, int highScore, boolean isVictory) {
        g2d.setColor(new Color(0, 0, 0, 215));
        g2d.fillRect(0, 0, width, height);

        BufferedImage icon = isVictory ? images.get("check") : images.get("cross");
        if (icon != null) {
            g2d.drawImage(icon, width / 2 - 24, 105, 48, 48, null);
        }

        g2d.setColor(isVictory ? new Color(46, 204, 113) : new Color(231, 76, 60));
        g2d.setFont(getFont(28f));
        g2d.drawString(isVictory ? "VICTORY!" : "GAME OVER", width / 2 - (isVictory ? 70 : 90), 180);

        g2d.setColor(Color.WHITE);
        g2d.setFont(getFont(15f));
        g2d.drawString("Final Score: " + score, width / 2 - 65, 220);

        BufferedImage star = images.get("star");
        if (star != null) {
            g2d.drawImage(star, width / 2 - 90, 235, 20, 20, null);
        }
        g2d.drawString("Best Score: " + highScore, width / 2 - 65, 250);

        drawAssetButton(g2d, RESTART_BTN, "btn_restart", "Play Again", new Color(46, 204, 113));
        drawAssetButton(g2d, MAIN_MENU_BTN, "btn_menu", "Main Menu", new Color(52, 152, 219));
    }

    private void drawAssetButton(Graphics2D g2d, Rectangle rect, String imageKey, String text, Color fallbackColor) {
        BufferedImage img = images.get(imageKey);
        if (img != null) {
            g2d.drawImage(img, rect.x, rect.y, rect.width, rect.height, null);
        } else {
            g2d.setColor(fallbackColor);
            g2d.fillRoundRect(rect.x, rect.y, rect.width, rect.height, 15, 15);
        }

        g2d.setFont(getFont(15f));
        int textX = rect.x + (rect.width - g2d.getFontMetrics().stringWidth(text)) / 2;
        int textY = rect.y + ((rect.height - g2d.getFontMetrics().getHeight()) / 2) + g2d.getFontMetrics().getAscent() - 2;

        // Subtle drop shadow for crisp visibility
        g2d.setColor(new Color(255, 255, 255, 120));
        g2d.drawString(text, textX + 1, textY + 1);

        // Solid Black Text on All Buttons
        g2d.setColor(Color.BLACK);
        g2d.drawString(text, textX, textY);
    }
}