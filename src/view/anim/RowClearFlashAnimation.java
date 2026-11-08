package view.anim;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.List;

public class RowClearFlashAnimation implements IDrawable {
    private final List<Integer> rows;
    private final List<Integer> cols;
    private int alpha = 200;

    public RowClearFlashAnimation(List<Integer> rows, List<Integer> cols) {
        this.rows = rows;
        this.cols = cols;
    }

    @Override
    public void update() {
        alpha = Math.max(0, alpha - 25);
    }

    @Override
    public void render(Graphics2D g2d, int boardOriginX, int boardOriginY, int tileSize) {
        if (alpha <= 0) return;
        g2d.setColor(new Color(255, 255, 255, alpha));

        for (int r : rows) {
            g2d.fillRect(boardOriginX, boardOriginY + r * tileSize, tileSize * 8, tileSize);
        }
        for (int c : cols) {
            g2d.fillRect(boardOriginX + c * tileSize, boardOriginY, tileSize, tileSize * 8);
        }
    }

    @Override
    public boolean isFinished() {
        return alpha <= 0;
    }
}