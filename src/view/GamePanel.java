package view;

import controller.InputHandler;
import controller.Logger;
import controller.ScoreManager;
import controller.SoundManager;
import model.Board;
import model.BlockPiece;
import model.GameMode;
import model.GameState;
import model.PieceGenerator;
import view.anim.IDrawable;
import view.anim.PopAnimation;
import view.anim.RowClearFlashAnimation;

import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GamePanel extends JPanel implements ActionListener {
    public static final int BOARD_SIZE = 400;
    public static final int TILE_SIZE = BOARD_SIZE / Board.GRID_SIZE;

    public static final int DEFAULT_TIME_ATTACK_SECONDS = 180;
    public static final int TIME_ATTACK_TARGET_SCORE = 500;

    private final Board board;
    private final ScoreManager scoreManager;
    private final SoundManager soundManager;
    private final UIOverlay uiOverlay;
    private final InputHandler inputHandler;

    private BlockPiece[] trayPieces;
    private final List<IDrawable> animations = new ArrayList<>();

    private GameState currentState = GameState.MAIN_MENU;
    private GameMode currentMode = GameMode.CLASSIC;

    private final Timer gameLoop;
    private final Timer secondTimer;
    private int timeRemaining = DEFAULT_TIME_ATTACK_SECONDS;
    private int elapsedTime = 0;
    private Font customFont;

    public GamePanel() {
        setPreferredSize(new Dimension(500, 650));
        setBackground(new Color(24, 24, 27));
        setFocusable(true);

        loadCustomFont();

        this.board = new Board();
        this.scoreManager = new ScoreManager();
        this.soundManager = new SoundManager();
        this.uiOverlay = new UIOverlay();

        this.inputHandler = new InputHandler(this);
        addKeyListener(inputHandler);
        addMouseListener(inputHandler);
        addMouseMotionListener(inputHandler);

        this.trayPieces = PieceGenerator.generateTray();

        this.gameLoop = new Timer(16, this);
        this.gameLoop.start();

        this.secondTimer = new Timer(1000, e -> {
            if (currentState == GameState.PLAYING) {
                elapsedTime++;
                if (currentMode == GameMode.TIME_ATTACK) {
                    timeRemaining--;
                    if (timeRemaining <= 0) {
                        currentState = GameState.GAME_OVER;
                        soundManager.playGameOver();
                        Logger.log("Time Attack Failed: Time Expired.");
                    }
                }
                repaint();
            }
        });
        this.secondTimer.start();
        Logger.log("Application Started.");
    }

    private void loadCustomFont() {
        try {
            File fontFile = new File("UI/Font/Kenney Future Narrow.ttf");
            if (fontFile.exists()) {
                customFont = Font.createFont(Font.TRUETYPE_FONT, fontFile);
            } else {
                customFont = new Font("SansSerif", Font.BOLD, 14);
            }
        } catch (Exception e) {
            customFont = new Font("SansSerif", Font.BOLD, 14);
        }
    }

    private Font getFont(float size) {
        return customFont.deriveFont(Font.BOLD, size);
    }

    public SoundManager getSoundManager() { return soundManager; }
    public BlockPiece[] getTray() { return trayPieces; }
    public boolean isPlayingState() { return currentState == GameState.PLAYING; }

    public void updateGameTick() {
        actionPerformed(null);
    }

    public void togglePause() {
        if (currentState == GameState.PLAYING) {
            currentState = GameState.PAUSED;
            Logger.log("Game Paused");
        } else if (currentState == GameState.PAUSED) {
            currentState = GameState.PLAYING;
            Logger.log("Game Resumed");
        }
        repaint();
    }

    public void restartGame() {
        startGame(currentMode);
    }

    public void startGame(GameMode mode) {
        this.currentMode = mode;
        this.currentState = GameState.PLAYING;
        this.board.clearBoard();
        this.scoreManager.resetScore();
        this.trayPieces = PieceGenerator.generateTray();
        this.animations.clear();
        this.inputHandler.clearSelection();
        this.elapsedTime = 0;
        this.timeRemaining = DEFAULT_TIME_ATTACK_SECONDS;
        Logger.log("Started new game in mode: " + mode);
        repaint();
    }

    public boolean placePieceFromTray(int trayIndex, int targetRow, int targetCol) {
        if (trayIndex < 0 || trayIndex >= trayPieces.length) return false;
        BlockPiece piece = trayPieces[trayIndex];
        if (piece == null) return false;

        if (board.placePiece(piece, targetRow, targetCol)) {
            scoreManager.addPlacementPoints(piece.getBlockCount());
            soundManager.playPlacement();

            animations.add(new PopAnimation(targetRow, targetCol, piece.getColor()));
            trayPieces[trayIndex] = null;

            Board.LineClearResult result = board.processLineClears();
            if (result.linesCleared > 0) {
                scoreManager.addLineClearPoints(result.linesCleared);
                soundManager.playLineClear();
                animations.add(new RowClearFlashAnimation(result.clearedRows, result.clearedCols));
            }

            if (currentMode == GameMode.TIME_ATTACK && scoreManager.getCurrentScore() >= TIME_ATTACK_TARGET_SCORE) {
                currentState = GameState.VICTORY;
                soundManager.playPlacement();
                Logger.log("Time Attack Victory! Score: " + scoreManager.getCurrentScore());
            } else {
                checkTrayReplenish();
            }

            repaint();
            return true;
        }
        return false;
    }

    public boolean placePieceAtMouse(BlockPiece piece, int trayIndex, int mouseX, int mouseY) {
        int boardOriginX = (getWidth() - BOARD_SIZE) / 2;
        int boardOriginY = 90;

        int targetCol = (mouseX - boardOriginX) / TILE_SIZE;
        int targetRow = (mouseY - boardOriginY) / TILE_SIZE;

        return placePieceFromTray(trayIndex, targetRow, targetCol);
    }

    private void checkTrayReplenish() {
        boolean empty = true;
        for (BlockPiece p : trayPieces) {
            if (p != null) { empty = false; break; }
        }
        if (empty) {
            trayPieces = PieceGenerator.generateTray();
        }

        if (!board.canAnyPieceFit(trayPieces)) {
            currentState = GameState.GAME_OVER;
            soundManager.playGameOver();
            Logger.log("Game Over: No moves left.");
        }
    }

    public boolean handleOverlayClick(int x, int y) {
        if (currentState == GameState.MAIN_MENU) {
            if (UIOverlay.PLAY_CLASSIC_BTN.contains(x, y)) {
                startGame(GameMode.CLASSIC);
                return true;
            } else if (UIOverlay.PLAY_TIME_BTN.contains(x, y)) {
                startGame(GameMode.TIME_ATTACK);
                return true;
            } else if (UIOverlay.INSTRUCTIONS_BTN.contains(x, y)) {
                currentState = GameState.INSTRUCTIONS;
                repaint();
                return true;
            } else if (UIOverlay.EXIT_BTN.contains(x, y)) {
                System.exit(0);
            }
        } else if (currentState == GameState.INSTRUCTIONS) {
            if (UIOverlay.MAIN_MENU_BTN.contains(x, y)) {
                currentState = GameState.MAIN_MENU;
                repaint();
                return true;
            }
        } else if (currentState == GameState.PAUSED) {
            if (UIOverlay.RESUME_BTN.contains(x, y)) {
                togglePause();
                return true;
            } else if (UIOverlay.RESTART_BTN.contains(x, y)) {
                restartGame();
                return true;
            } else if (UIOverlay.MAIN_MENU_BTN.contains(x, y)) {
                currentState = GameState.MAIN_MENU;
                repaint();
                return true;
            }
        } else if (currentState == GameState.GAME_OVER || currentState == GameState.VICTORY) {
            if (UIOverlay.RESTART_BTN.contains(x, y)) {
                restartGame();
                return true;
            } else if (UIOverlay.MAIN_MENU_BTN.contains(x, y)) {
                currentState = GameState.MAIN_MENU;
                repaint();
                return true;
            }
        }
        return false;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Iterator<IDrawable> iter = animations.iterator();
        while (iter.hasNext()) {
            IDrawable anim = iter.next();
            anim.update();
            if (anim.isFinished()) iter.remove();
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int boardOriginX = (getWidth() - BOARD_SIZE) / 2;
        int boardOriginY = 90;

        drawBoard(g2d, boardOriginX, boardOriginY);
        drawTray(g2d);
        drawScoreHeader(g2d);

        // Preview for keyboard navigation
        if (inputHandler.isKbNavigatingGrid() && inputHandler.getDraggedPiece() != null) {
            BlockPiece piece = inputHandler.getDraggedPiece();
            int r = inputHandler.getKbGridRow();
            int c = inputHandler.getKbGridCol();
            boolean valid = board.canPlacePiece(piece, r, c);

            g2d.setColor(valid ? new Color(46, 204, 113, 150) : new Color(231, 76, 60, 150));
            for (int pr = 0; pr < piece.getRows(); pr++) {
                for (int pc = 0; pc < piece.getCols(); pc++) {
                    if (piece.getShape()[pr][pc] > 0) {
                        int x = boardOriginX + (c + pc) * TILE_SIZE;
                        int y = boardOriginY + (r + pr) * TILE_SIZE;
                        g2d.fillRect(x + 1, y + 1, TILE_SIZE - 2, TILE_SIZE - 2);
                    }
                }
            }
        }

        // Preview for mouse dragging
        if (inputHandler.isDragging() && inputHandler.getDraggedPiece() != null) {
            Point p = inputHandler.getMousePosition();
            BlockPiece piece = inputHandler.getDraggedPiece();

            int targetCol = (p.x - boardOriginX) / TILE_SIZE;
            int targetRow = (p.y - boardOriginY) / TILE_SIZE;
            boolean valid = board.canPlacePiece(piece, targetRow, targetCol);

            if (valid) {
                g2d.setColor(new Color(255, 255, 255, 80));
                for (int pr = 0; pr < piece.getRows(); pr++) {
                    for (int pc = 0; pc < piece.getCols(); pc++) {
                        if (piece.getShape()[pr][pc] > 0) {
                            int x = boardOriginX + (targetCol + pc) * TILE_SIZE;
                            int y = boardOriginY + (targetRow + pr) * TILE_SIZE;
                            g2d.fillRect(x + 1, y + 1, TILE_SIZE - 2, TILE_SIZE - 2);
                        }
                    }
                }
            }

            BufferedImage img = PieceGenerator.getBlockImage(piece.getColorId());
            for (int r = 0; r < piece.getRows(); r++) {
                for (int c = 0; c < piece.getCols(); c++) {
                    if (piece.getShape()[r][c] > 0) {
                        int bx = p.x + c * TILE_SIZE - TILE_SIZE / 2;
                        int by = p.y + r * TILE_SIZE - TILE_SIZE / 2;
                        if (img != null) {
                            g2d.drawImage(img, bx, by, TILE_SIZE - 2, TILE_SIZE - 2, null);
                        } else {
                            g2d.setColor(piece.getColor());
                            g2d.fillRect(bx, by, TILE_SIZE - 2, TILE_SIZE - 2);
                        }
                    }
                }
            }
        }

        for (IDrawable anim : animations) {
            anim.render(g2d, boardOriginX, boardOriginY, TILE_SIZE);
        }

        if (currentState == GameState.MAIN_MENU) {
            uiOverlay.renderMainMenu(g2d, getWidth(), getHeight());
        } else if (currentState == GameState.INSTRUCTIONS) {
            uiOverlay.renderInstructions(g2d, getWidth(), getHeight());
        } else if (currentState == GameState.PAUSED) {
            uiOverlay.renderPauseScreen(g2d, getWidth(), getHeight());
        } else if (currentState == GameState.GAME_OVER) {
            uiOverlay.renderGameOverScreen(g2d, getWidth(), getHeight(), scoreManager.getCurrentScore(), scoreManager.getHighScore(), false);
        } else if (currentState == GameState.VICTORY) {
            uiOverlay.renderGameOverScreen(g2d, getWidth(), getHeight(), scoreManager.getCurrentScore(), scoreManager.getHighScore(), true);
        }
    }

    private void drawScoreHeader(Graphics2D g2d) {
        g2d.setColor(new Color(38, 38, 44));
        g2d.fillRoundRect(50, 15, 400, 60, 15, 15);

        g2d.setFont(getFont(13f));

        g2d.setColor(Color.WHITE);
        g2d.drawString("SCORE: " + scoreManager.getCurrentScore(), 65, 40);

        g2d.setColor(new Color(241, 196, 15));
        g2d.drawString("BEST: " + scoreManager.getHighScore(), 200, 40);

        g2d.setColor(soundManager.isMuted() ? new Color(231, 76, 60) : new Color(46, 204, 113));
        g2d.drawString("MUTE: " + (soundManager.isMuted() ? "ON" : "OFF"), 340, 40);

        g2d.setColor(new Color(180, 180, 180));
        g2d.setFont(getFont(11f));
        if (currentMode == GameMode.TIME_ATTACK) {
            g2d.drawString("TIME LEFT: " + timeRemaining + "s  |  TARGET: " + TIME_ATTACK_TARGET_SCORE, 65, 60);
        } else {
            g2d.drawString("ELAPSED TIME: " + elapsedTime + "s", 65, 60);
        }
    }

    private void drawBoard(Graphics2D g2d, int ox, int oy) {
        g2d.setColor(new Color(32, 32, 36));
        g2d.fillRect(ox, oy, BOARD_SIZE, BOARD_SIZE);

        for (int r = 0; r < Board.GRID_SIZE; r++) {
            for (int c = 0; c < Board.GRID_SIZE; c++) {
                int x = ox + c * TILE_SIZE;
                int y = oy + r * TILE_SIZE;
                int colorId = board.getCell(r, c);

                if (colorId > 0) {
                    BufferedImage img = PieceGenerator.getBlockImage(colorId);
                    if (img != null) {
                        g2d.drawImage(img, x + 1, y + 1, TILE_SIZE - 2, TILE_SIZE - 2, null);
                    } else {
                        g2d.setColor(PieceGenerator.PIECE_COLORS[Math.min(colorId, PieceGenerator.PIECE_COLORS.length - 1)]);
                        g2d.fillRect(x + 1, y + 1, TILE_SIZE - 2, TILE_SIZE - 2);
                    }
                } else {
                    g2d.setColor(new Color(45, 45, 52));
                    g2d.drawRect(x, y, TILE_SIZE, TILE_SIZE);
                }
            }
        }
    }

    private void drawTray(Graphics2D g2d) {
        int startX = 50;
        int startY = 515;
        int slotWidth = 120;
        int slotHeight = 110;

        for (int i = 0; i < trayPieces.length; i++) {
            BlockPiece piece = trayPieces[i];
            int slotX = startX + i * (slotWidth + 15);

            g2d.setColor(inputHandler.getSelectedTrayIndex() == i ? new Color(60, 60, 70) : new Color(38, 38, 44));
            g2d.fillRoundRect(slotX, startY, slotWidth, slotHeight, 15, 15);
            g2d.setColor(new Color(55, 55, 65));
            g2d.drawRoundRect(slotX, startY, slotWidth, slotHeight, 15, 15);

            if (piece != null && !(inputHandler.isDragging() && inputHandler.getSelectedTrayIndex() == i)) {
                BufferedImage img = PieceGenerator.getBlockImage(piece.getColorId());
                int miniTile = 22;

                int pieceW = piece.getCols() * miniTile;
                int pieceH = piece.getRows() * miniTile;
                int offsetX = slotX + (slotWidth - pieceW) / 2;
                int offsetY = startY + (slotHeight - pieceH) / 2;

                for (int r = 0; r < piece.getRows(); r++) {
                    for (int c = 0; c < piece.getCols(); c++) {
                        if (piece.getShape()[r][c] > 0) {
                            int bx = offsetX + c * miniTile;
                            int by = offsetY + r * miniTile;
                            if (img != null) {
                                g2d.drawImage(img, bx, by, miniTile - 2, miniTile - 2, null);
                            } else {
                                g2d.setColor(piece.getColor());
                                g2d.fillRect(bx, by, miniTile - 2, miniTile - 2);
                            }
                        }
                    }
                }
            }
        }
    }
}