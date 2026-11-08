package view.anim;

import java.awt.Color;
import java.awt.Graphics2D;

public class PopAnimation implements IDrawable {
    private final int gridRow;
    private final int gridCol;
    private final Color color;
    private float scale = 0.2f;
    private int alpha = 255;

    public PopAnimation(int gridRow, int gridCol, Color color) {
        this.gridRow = gridRow;
        this.gridCol = gridCol;
        this.color = color;
    }

    @Override
    public void update() {
        scale += 0.15f;
        alpha = Math.max(0, alpha - 20);
    }

    @Override
    public void render(Graphics2D g2d, int boardOriginX, int boardOriginY, int tileSize) {
        if (alpha <= 0) return;

        int centerX = boardOriginX + gridCol * tileSize + tileSize / 2;
        int centerY = boardOriginY + gridRow * tileSize + tileSize / 2;
        int size = (int) (tileSize * scale);

        g2d.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha));
        g2d.drawOval(centerX - size / 2, centerY - size / 2, size, size);
    }

    @Override
    public boolean isFinished() {
        return alpha <= 0;
    }
}