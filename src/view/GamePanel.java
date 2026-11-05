package view;

import controller.InputHandler;
import controller.ScoreManager;
import controller.SoundManager;
import model.BlockPiece;
import model.Board;
import model.GameMode;
import model.GameState;
import model.PieceGenerator;
import view.anim.FlashAnimation;
import view.anim.IDrawable;
import view.anim.PopAnimation;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class GamePanel extends JPanel {
    public static final int BOARD_ORIGIN_X = 140;
    public static final int BOARD_ORIGIN_Y = 110;
    public static final int TILE_SIZE = 45;

    private final Board board;
    private final ScoreManager scoreManager;
    private final SoundManager soundManager;
    private final InputHandler inputHandler;
    private final UIOverlay uiOverlay;

    private final BlockPiece[] trayPieces = new BlockPiece[3];
    private final Rectangle[] traySlotBounds = new Rectangle[3];
    private final List<IDrawable> animations = new ArrayList<>();

    private GameState currentState = GameState.MAIN_MENU;
    private GameMode currentMode = GameMode.CLASSIC;

    private int timeRemainingSeconds = 180;
    private int elapsedTimeSeconds = 0;

    public GamePanel() {
        setPreferredSize(new Dimension(640, 680));
        setBackground(new Color(24, 26, 33));
        setFocusable(true);

        this.board = new Board();
        this.scoreManager = new ScoreManager();
        this.soundManager = new SoundManager();
        this.uiOverlay = new UIOverlay();
        this.inputHandler = new InputHandler(this);

        addMouseListener(inputHandler);
        addMouseMotionListener(inputHandler);
        addKeyListener(inputHandler);

        setupTraySlotBounds();
        setupOverlayMouseListener();
    }

    private void setupTraySlotBounds() {
        int trayY = 510;
        int slotWidth = 140;
        int slotHeight = 130;
        int startX = 60;
        int gap = 40;

        for (int i = 0; i < 3; i++) {
            traySlotBounds[i] = new Rectangle(startX + i * (slotWidth + gap), trayY, slotWidth, slotHeight);
        }
    }

    private void setupOverlayMouseListener() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (currentState == GameState.MAIN_MENU) {
                    GameMode selectedMode = uiOverlay.getClickedMode(e.getX(), e.getY());
                    if (selectedMode != null) {
                        startGame(selectedMode);
                    }
                } else if (currentState == GameState.PAUSED || currentState == GameState.GAME_OVER) {
                    UIOverlay.OverlayAction action = uiOverlay.getClickedAction(currentState, e.getX(), e.getY());
                    switch (action) {
                        case RESUME:
                            currentState = GameState.PLAYING;
                            repaint();
                            break;
                        case RESTART:
                            startGame(currentMode);
                            break;
                        case MAIN_MENU:
                            currentState = GameState.MAIN_MENU;
                            repaint();
                            break;
                        default:
                            break;
                    }
                }
            }
        });
    }

    public void startGame(GameMode mode) {
        this.currentMode = mode;
        this.currentState = GameState.PLAYING;
        this.board.clearBoard();
        this.scoreManager.resetScore();
        this.timeRemainingSeconds = 180;
        this.elapsedTimeSeconds = 0;
        this.animations.clear();
        this.inputHandler.clearSelection();
        refreshTray();
        repaint();
    }

    public void refreshTray() {
        BlockPiece[] newTray = PieceGenerator.generateTray();
        System.arraycopy(newTray, 0, trayPieces, 0, 3);
        checkGameOverCondition();
    }

    public void checkGameOverCondition() {
        if (!board.canAnyPieceFit(trayPieces)) {
            currentState = GameState.GAME_OVER;
            soundManager.playGameOver();
        }
    }

    public void updateGameTick() {
        if (currentState != GameState.PLAYING) return;

        if (currentMode == GameMode.TIME_ATTACK) {
            timeRemainingSeconds--;
            if (timeRemainingSeconds <= 0) {
                currentState = GameState.GAME_OVER;
                soundManager.playGameOver();
            }
        } else {
            elapsedTimeSeconds++;
        }

        // Update active animations
        Iterator<IDrawable> iterator = animations.iterator();
        while (iterator.hasNext()) {
            IDrawable anim = iterator.next();
            anim.update();
            if (anim.isFinished()) {
                iterator.remove();
            }
        }
        repaint();
    }

    public void executePlacement(BlockPiece piece, int trayIdx, int targetRow, int targetCol) {
        if (board.placePiece(piece, targetRow, targetCol)) {
            scoreManager.addPlacementPoints(piece.getBlockCount());
            soundManager.playPlacement();

            // Spawn piece placement pop effect
            animations.add(new PopAnimation(targetRow, targetCol, piece.getColor()));

            trayPieces[trayIdx] = null;

            // Process line clearing
            Board.LineClearResult result = board.processLineClears();
            if (result.linesCleared > 0) {
                scoreManager.addLineClearPoints(result.linesCleared);
                soundManager.playLineClear();

                for (int r : result.clearedRows) {
                    animations.add(new FlashAnimation(r, true));
                }
                for (int c : result.clearedCols) {
                    animations.add(new FlashAnimation(c, false));
                }
            }

            // Check if tray is empty to generate new pieces
            boolean trayEmpty = true;
            for (BlockPiece p : trayPieces) {
                if (p != null) {
                    trayEmpty = false;
                    break;
                }
            }
            if (trayEmpty) {
                refreshTray();
            } else {
                checkGameOverCondition();
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        drawHUD(g2d);
        drawBoardGrid(g2d);
        drawTray(g2d);
        renderAnimations(g2d);
        drawPlacementPreview(g2d);

        if (currentState == GameState.MAIN_MENU) {
            uiOverlay.renderMainMenu(g2d, getWidth(), getHeight());
        } else if (currentState == GameState.PAUSED) {
            uiOverlay.renderPauseScreen(g2d, getWidth(), getHeight());
        } else if (currentState == GameState.GAME_OVER) {
            uiOverlay.renderGameOverScreen(g2d, getWidth(), getHeight(), scoreManager.getCurrentScore(), scoreManager.getHighScore(), currentMode);
        }
    }

    private void drawHUD(Graphics2D g2d) {
        g2d.setFont(new Font("Segoe UI", Font.BOLD, 18));
        g2d.setColor(Color.WHITE);
        g2d.drawString("SCORE: " + scoreManager.getCurrentScore(), 40, 50);

        g2d.setColor(new Color(241, 196, 15));
        g2d.drawString("BEST: " + scoreManager.getHighScore(), 250, 50);

        g2d.setColor(Color.CYAN);
        if (currentMode == GameMode.TIME_ATTACK) {
            g2d.drawString("TIME: " + timeRemainingSeconds + "s", 470, 50);
        } else {
            g2d.drawString("TIME: " + elapsedTimeSeconds + "s", 470, 50);
        }

        g2d.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        g2d.setColor(new Color(140, 145, 160));
        g2d.drawString("Keys: 1-3 (Select) | Arrows (Move) | Space/Enter (Place) | P (Pause) | M (Mute: " + (soundManager.isMuted() ? "ON" : "OFF") + ")", 40, 80);
    }

    private void drawBoardGrid(Graphics2D g2d) {
        g2d.setColor(new Color(35, 39, 50));
        g2d.fillRoundRect(BOARD_ORIGIN_X - 10, BOARD_ORIGIN_Y - 10, TILE_SIZE * 8 + 20, TILE_SIZE * 8 + 20, 16, 16);

        for (int r = 0; r < Board.GRID_SIZE; r++) {
            for (int c = 0; c < Board.GRID_SIZE; c++) {
                int x = BOARD_ORIGIN_X + c * TILE_SIZE;
                int y = BOARD_ORIGIN_Y + r * TILE_SIZE;

                int colorId = board.getCell(r, c);
                if (colorId > 0) {
                    g2d.setColor(PieceGenerator.PIECE_COLORS[colorId]);
                    g2d.fillRoundRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4, 8, 8);
                } else {
                    g2d.setColor(new Color(45, 50, 65));
                    g2d.fillRoundRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4, 8, 8);
                }
            }
        }
    }

    private void drawTray(Graphics2D g2d) {
        for (int i = 0; i < 3; i++) {
            Rectangle slot = traySlotBounds[i];
            g2d.setColor(new Color(35, 39, 50));
            g2d.fillRoundRect(slot.x, slot.y, slot.width, slot.height, 12, 12);

            if (inputHandler.getSelectedTrayIndex() == i && inputHandler.isKbNavigatingGrid()) {
                g2d.setColor(Color.YELLOW);
                g2d.drawRoundRect(slot.x, slot.y, slot.width, slot.height, 12, 12);
            }

            BlockPiece piece = trayPieces[i];
            if (piece != null && !(inputHandler.isDragging() && inputHandler.getSelectedTrayIndex() == i)) {
                drawPieceInBounds(g2d, piece, slot);
            }
        }
    }

    private void drawPieceInBounds(Graphics2D g2d, BlockPiece piece, Rectangle bounds) {
        int miniTile = 22;
        int[][] shape = piece.getShape();
        int pieceW = piece.getCols() * miniTile;
        int pieceH = piece.getRows() * miniTile;

        int startX = bounds.x + (bounds.width - pieceW) / 2;
        int startY = bounds.y + (bounds.height - pieceH) / 2;

        g2d.setColor(piece.getColor());
        for (int r = 0; r < piece.getRows(); r++) {
            for (int c = 0; c < piece.getCols(); c++) {
                if (shape[r][c] > 0) {
                    g2d.fillRoundRect(startX + c * miniTile, startY + r * miniTile, miniTile - 2, miniTile - 2, 4, 4);
                }
            }
        }
    }

    private void drawPlacementPreview(Graphics2D g2d) {
        BlockPiece piece = inputHandler.getDraggedPiece();
        if (piece == null || currentState != GameState.PLAYING) return;

        if (inputHandler.isDragging()) {
            Point mousePt = inputHandler.getMousePosition();
            int previewCol = (int) Math.round((double) (mousePt.x - BOARD_ORIGIN_X) / TILE_SIZE);
            int previewRow = (int) Math.round((double) (mousePt.y - BOARD_ORIGIN_Y) / TILE_SIZE);

            boolean valid = board.canPlacePiece(piece, previewRow, previewCol);
            drawGhostPiece(g2d, piece, previewRow, previewCol, valid);

            // Floating drag piece under cursor
            int[][] shape = piece.getShape();
            g2d.setColor(piece.getColor());
            for (int r = 0; r < piece.getRows(); r++) {
                for (int c = 0; c < piece.getCols(); c++) {
                    if (shape[r][c] > 0) {
                        g2d.fillRoundRect(mousePt.x + c * TILE_SIZE - TILE_SIZE / 2, mousePt.y + r * TILE_SIZE - TILE_SIZE / 2, TILE_SIZE - 4, TILE_SIZE - 4, 8, 8);
                    }
                }
            }
        } else if (inputHandler.isKbNavigatingGrid()) {
            int row = inputHandler.getKbGridRow();
            int col = inputHandler.getKbGridCol();
            boolean valid = board.canPlacePiece(piece, row, col);
            drawGhostPiece(g2d, piece, row, col, valid);
        }
    }

    private void drawGhostPiece(Graphics2D g2d, BlockPiece piece, int row, int col, boolean isValid) {
        int[][] shape = piece.getShape();
        Color previewColor = isValid ? new Color(46, 204, 113, 130) : new Color(231, 76, 60, 130);
        g2d.setColor(previewColor);

        for (int r = 0; r < piece.getRows(); r++) {
            for (int c = 0; c < piece.getCols(); c++) {
                if (shape[r][c] > 0) {
                    int gridR = row + r;
                    int gridC = col + c;
                    if (board.isValidPosition(gridR, gridC)) {
                        int x = BOARD_ORIGIN_X + gridC * TILE_SIZE;
                        int y = BOARD_ORIGIN_Y + gridR * TILE_SIZE;
                        g2d.fillRoundRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4, 8, 8);
                    }
                }
            }
        }
    }

    private void renderAnimations(Graphics2D g2d) {
        for (IDrawable anim : new ArrayList<>(animations)) {
            anim.render(g2d, BOARD_ORIGIN_X, BOARD_ORIGIN_Y, TILE_SIZE);
        }
    }

    // Getters & Utility Methods
    public Board getBoard() { return board; }
    public SoundManager getSoundManager() { return soundManager; }
    public BlockPiece getTrayPiece(int idx) { return (idx >= 0 && idx < 3) ? trayPieces[idx] : null; }
    public Rectangle getTraySlotBounds(int idx) { return traySlotBounds[idx]; }
    public Point getBoardOrigin() { return new Point(BOARD_ORIGIN_X, BOARD_ORIGIN_Y); }
    public int getTileSize() { return TILE_SIZE; }
    public boolean isPlayingState() { return currentState == GameState.PLAYING; }

    public void togglePause() {
        if (currentState == GameState.PLAYING) {
            currentState = GameState.PAUSED;
        } else if (currentState == GameState.PAUSED) {
            currentState = GameState.PLAYING;
        }
        repaint();
    }

    public void restartGame() {
        startGame(currentMode);
    }
}