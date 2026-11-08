package view.anim;

import java.awt.Color;
import java.awt.Graphics2D;

public class FlashAnimation implements IDrawable {
    private int alpha = 180;

    @Override
    public void update() {
        alpha = Math.max(0, alpha - 30);
    }

    @Override
    public void render(Graphics2D g2d, int boardOriginX, int boardOriginY, int tileSize) {
        if (alpha <= 0) return;
        g2d.setColor(new Color(255, 255, 255, alpha));
        g2d.fillRect(boardOriginX, boardOriginY, tileSize * 8, tileSize * 8);
    }

    @Override
    public boolean isFinished() {
        return alpha <= 0;
    }
}