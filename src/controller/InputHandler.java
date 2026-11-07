package controller;

import model.BlockPiece;
import view.GamePanel;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class InputHandler implements KeyListener, MouseListener, MouseMotionListener {
    private final GamePanel gamePanel;

    private int selectedTrayIndex = -1;
    private BlockPiece draggedPiece = null;
    private boolean dragging = false;
    private Point mousePosition = new Point(0, 0);

    private boolean kbNavigatingGrid = false;
    private int kbGridRow = 3;
    private int kbGridCol = 3;

    public InputHandler(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
    }

    public void clearSelection() {
        this.selectedTrayIndex = -1;
        this.draggedPiece = null;
        this.dragging = false;
        this.kbNavigatingGrid = false;
    }

    public int getSelectedTrayIndex() { return selectedTrayIndex; }
    public BlockPiece getDraggedPiece() { return draggedPiece; }
    public boolean isDragging() { return dragging; }
    public Point getMousePosition() { return mousePosition; }

    public boolean isKbNavigatingGrid() { return kbNavigatingGrid; }
    public int getKbGridRow() { return kbGridRow; }
    public int getKbGridCol() { return kbGridCol; }

    @Override public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();

        if (code == KeyEvent.VK_P) {
            gamePanel.togglePause();
            return;
        } else if (code == KeyEvent.VK_M) {
            gamePanel.getSoundManager().toggleMute();
            gamePanel.repaint();
            return;
        } else if (code == KeyEvent.VK_R) {
            gamePanel.restartGame();
            return;
        }

        if (!gamePanel.isPlayingState()) return;

        if (code == KeyEvent.VK_1 || code == KeyEvent.VK_NUMPAD1) {
            selectTrayPiece(0);
        } else if (code == KeyEvent.VK_2 || code == KeyEvent.VK_NUMPAD2) {
            selectTrayPiece(1);
        } else if (code == KeyEvent.VK_3 || code == KeyEvent.VK_NUMPAD3) {
            selectTrayPiece(2);
        }

        if (kbNavigatingGrid && draggedPiece != null) {
            if (code == KeyEvent.VK_UP || code == KeyEvent.VK_W) {
                kbGridRow = Math.max(0, kbGridRow - 1);
            } else if (code == KeyEvent.VK_DOWN || code == KeyEvent.VK_S) {
                kbGridRow = Math.min(7, kbGridRow + 1);
            } else if (code == KeyEvent.VK_LEFT || code == KeyEvent.VK_A) {
                kbGridCol = Math.max(0, kbGridCol - 1);
            } else if (code == KeyEvent.VK_RIGHT || code == KeyEvent.VK_D) {
                kbGridCol = Math.min(7, kbGridCol + 1);
            } else if (code == KeyEvent.VK_SPACE || code == KeyEvent.VK_ENTER) {
                boolean placed = gamePanel.placePieceFromTray(selectedTrayIndex, kbGridRow, kbGridCol);
                if (!placed) {
                    gamePanel.getSoundManager().playInvalid();
                }
                clearSelection();
            } else if (code == KeyEvent.VK_ESCAPE) {
                clearSelection();
            }
            gamePanel.repaint();
        }
    }

    @Override public void keyReleased(KeyEvent e) {}

    private void selectTrayPiece(int index) {
        BlockPiece[] tray = gamePanel.getTray();
        if (tray != null && index >= 0 && index < tray.length && tray[index] != null) {
            selectedTrayIndex = index;
            draggedPiece = tray[index];
            kbNavigatingGrid = true;
            kbGridRow = 3;
            kbGridCol = 3;
            gamePanel.getSoundManager().playSelection();
            gamePanel.repaint();
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        mousePosition = e.getPoint();
        if (gamePanel.handleOverlayClick(mousePosition.x, mousePosition.y)) {
            return;
        }

        if (!gamePanel.isPlayingState()) return;

        int startX = 50;
        int startY = 520;
        int slotWidth = 120;
        BlockPiece[] tray = gamePanel.getTray();

        for (int i = 0; i < tray.length; i++) {
            int slotX = startX + i * (slotWidth + 15);
            if (mousePosition.x >= slotX && mousePosition.x <= slotX + slotWidth &&
                mousePosition.y >= startY && mousePosition.y <= startY + 90) {
                if (tray[i] != null) {
                    selectedTrayIndex = i;
                    draggedPiece = tray[i];
                    dragging = true;
                    gamePanel.getSoundManager().playSelection();
                    gamePanel.repaint();
                    break;
                }
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        mousePosition = e.getPoint();
        if (dragging && draggedPiece != null) {
            boolean placed = gamePanel.placePieceAtMouse(draggedPiece, selectedTrayIndex, mousePosition.x, mousePosition.y);
            if (!placed) {
                gamePanel.getSoundManager().playInvalid();
            }
            clearSelection();
            gamePanel.repaint();
        }
    }

    @Override public void mouseClicked(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}

    @Override
    public void mouseDragged(MouseEvent e) {
        mousePosition = e.getPoint();
        gamePanel.repaint();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        mousePosition = e.getPoint();
    }
}