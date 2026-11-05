package view;

import model.GameMode;
import model.GameState;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public class UIOverlay {
    private final Rectangle classicBtnBounds = new Rectangle(200, 240, 240, 50);
    private final Rectangle timeAttackBtnBounds = new Rectangle(200, 310, 240, 50);
    private final Rectangle resumeBtnBounds = new Rectangle(220, 250, 200, 50);
    private final Rectangle restartBtnBounds = new Rectangle(220, 320, 200, 50);
    private final Rectangle menuBtnBounds = new Rectangle(220, 390, 200, 50);

    public void renderMainMenu(Graphics2D g2d, int width, int height) {
        drawDimmedBackground(g2d, width, height);

        // Title
        g2d.setFont(new Font("Segoe UI", Font.BOLD, 36));
        g2d.setColor(Color.WHITE);
        drawCenteredString(g2d, "BLOCKED.exe", width, 140);

        g2d.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        g2d.setColor(new Color(180, 180, 190));
        drawCenteredString(g2d, "The Block Burst Challenge", width, 175);

        // Buttons
        drawButton(g2d, classicBtnBounds, "Play Classic Mode", new Color(46, 204, 113));
        drawButton(g2d, timeAttackBtnBounds, "Play Time Attack", new Color(52, 152, 219));
    }

    public void renderPauseScreen(Graphics2D g2d, int width, int height) {
        drawDimmedBackground(g2d, width, height);

        g2d.setFont(new Font("Segoe UI", Font.BOLD, 32));
        g2d.setColor(Color.WHITE);
        drawCenteredString(g2d, "GAME PAUSED", width, 180);

        drawButton(g2d, resumeBtnBounds, "Resume (P)", new Color(46, 204, 113));
        drawButton(g2d, restartBtnBounds, "Restart (R)", new Color(241, 196, 15));
        drawButton(g2d, menuBtnBounds, "Main Menu", new Color(231, 76, 60));
    }

    public void renderGameOverScreen(Graphics2D g2d, int width, int height, int score, int highScore, GameMode mode) {
        drawDimmedBackground(g2d, width, height);

        g2d.setFont(new Font("Segoe UI", Font.BOLD, 34));
        g2d.setColor(new Color(231, 76, 60));
        drawCenteredString(g2d, "GAME OVER", width, 140);

        g2d.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        g2d.setColor(Color.WHITE);
        drawCenteredString(g2d, "Final Score: " + score, width, 185);
        
        g2d.setColor(new Color(241, 196, 15));
        drawCenteredString(g2d, "Best Score: " + highScore, width, 215);

        drawButton(g2d, restartBtnBounds, "Play Again (R)", new Color(46, 204, 113));
        drawButton(g2d, menuBtnBounds, "Main Menu", new Color(231, 76, 60));
    }

    private void drawDimmedBackground(Graphics2D g2d, int width, int height) {
        g2d.setColor(new Color(15, 15, 25, 220));
        g2d.fillRect(0, 0, width, height);
    }

    private void drawButton(Graphics2D g2d, Rectangle rect, String text, Color accentColor) {
        g2d.setColor(new Color(35, 39, 50));
        g2d.fillRoundRect(rect.x, rect.y, rect.width, rect.height, 12, 12);

        g2d.setColor(accentColor);
        g2d.drawRoundRect(rect.x, rect.y, rect.width, rect.height, 12, 12);

        g2d.setFont(new Font("Segoe UI", Font.BOLD, 16));
        g2d.setColor(Color.WHITE);

        FontMetrics fm = g2d.getFontMetrics();
        int textX = rect.x + (rect.width - fm.stringWidth(text)) / 2;
        int textY = rect.y + ((rect.height - fm.getHeight()) / 2) + fm.getAscent();
        g2d.drawString(text, textX, textY);
    }

    private void drawCenteredString(Graphics2D g2d, String text, int width, int y) {
        FontMetrics fm = g2d.getFontMetrics();
        int x = (width - fm.stringWidth(text)) / 2;
        g2d.drawString(text, x, y);
    }

    public GameMode getClickedMode(int x, int y) {
        if (classicBtnBounds.contains(x, y)) return GameMode.CLASSIC;
        if (timeAttackBtnBounds.contains(x, y)) return GameMode.TIME_ATTACK;
        return null;
    }

    public OverlayAction getClickedAction(GameState state, int x, int y) {
        if (state == GameState.PAUSED) {
            if (resumeBtnBounds.contains(x, y)) return OverlayAction.RESUME;
            if (restartBtnBounds.contains(x, y)) return OverlayAction.RESTART;
            if (menuBtnBounds.contains(x, y)) return OverlayAction.MAIN_MENU;
        } else if (state == GameState.GAME_OVER) {
            if (restartBtnBounds.contains(x, y)) return OverlayAction.RESTART;
            if (menuBtnBounds.contains(x, y)) return OverlayAction.MAIN_MENU;
        }
        return OverlayAction.NONE;
    }

    public enum OverlayAction {
        NONE, RESUME, RESTART, MAIN_MENU
    }
}