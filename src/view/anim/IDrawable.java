package view.anim;

import java.awt.Graphics2D;

public interface IDrawable {

    void update();

    void render(Graphics2D g2d, int boardOriginX, int boardOriginY, int tileSize);

    boolean isFinished();
}