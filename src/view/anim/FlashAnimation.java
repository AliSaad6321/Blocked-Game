package view.anim;

import java.awt.Color;
import java.awt.Graphics2D;

public class FlashAnimation implements IDrawable {
    private final int index;
    private final boolean isRow;
    private int alpha = 255;
    private static final int FADE_SPEED = 25;

    public FlashAnimation(int index, boolean isRow) {
        this.index = index;
        this.isRow = isRow;
    }

    @Override
    public void update() {
        alpha = Math.max(0, alpha - FADE_SPEED);
    }

    @Override
    public void render(Graphics2D g2d, int boardOriginX, int boardOriginY, int tileSize) {
        if (alpha <= 0) return;

        g2d.setColor(new Color(255, 255, 255, alpha));
        if (isRow) {
            int y = boardOriginY + index * tileSize;
            g2d.fillRect(boardOriginX, y, tileSize * 8, tileSize);
        } else {
            int x = boardOriginX + index * tileSize;
            g2d.fillRect(x, boardOriginY, tileSize, tileSize * 8);
        }
    }

    @Override
    public boolean isFinished() {
        return alpha <= 0;
    }
}