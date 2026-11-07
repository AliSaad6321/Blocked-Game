package model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class Board {
    public static final int GRID_SIZE = 8;
    private final int[][] grid;

    public Board() {
        this.grid = new int[GRID_SIZE][GRID_SIZE];
        clearBoard();
    }

    public void clearBoard() {
        for (int r = 0; r < GRID_SIZE; r++) {
            for (int c = 0; c < GRID_SIZE; c++) {
                grid[r][c] = 0;
            }
        }
    }

    public int getCell(int r, int c) {
        if (isValidPosition(r, c)) {
            return grid[r][c];
        }
        return -1;
    }

    public boolean isValidPosition(int r, int c) {
        return r >= 0 && r < GRID_SIZE && c >= 0 && c < GRID_SIZE;
    }

    public boolean canPlacePiece(BlockPiece piece, int targetRow, int targetCol) {
        if (piece == null) return false;
        int[][] shape = piece.getShape();

        for (int r = 0; r < shape.length; r++) {
            for (int c = 0; c < shape[r].length; c++) {
                if (shape[r][c] > 0) {
                    int gridR = targetRow + r;
                    int gridC = targetCol + c;

                    if (!isValidPosition(gridR, gridC)) return false;
                    if (grid[gridR][gridC] != 0) return false;
                }
            }
        }
        return true;
    }

    public boolean placePiece(BlockPiece piece, int targetRow, int targetCol) {
        if (!canPlacePiece(piece, targetRow, targetCol)) return false;

        int[][] shape = piece.getShape();
        for (int r = 0; r < shape.length; r++) {
            for (int c = 0; c < shape[r].length; c++) {
                if (shape[r][c] > 0) {
                    grid[targetRow + r][targetCol + c] = piece.getColorId();
                }
            }
        }
        return true;
    }

    public LineClearResult processLineClears() {
        List<Integer> fullRows = new ArrayList<>();
        List<Integer> fullCols = new ArrayList<>();

        for (int r = 0; r < GRID_SIZE; r++) {
            boolean full = true;
            for (int c = 0; c < GRID_SIZE; c++) {
                if (grid[r][c] == 0) { full = false; break; }
            }
            if (full) fullRows.add(r);
        }

        for (int c = 0; c < GRID_SIZE; c++) {
            boolean full = true;
            for (int r = 0; r < GRID_SIZE; r++) {
                if (grid[r][c] == 0) { full = false; break; }
            }
            if (full) fullCols.add(c);
        }

        List<Point> clearedCells = new ArrayList<>();
        for (int r : fullRows) {
            for (int c = 0; c < GRID_SIZE; c++) {
                if (grid[r][c] != 0) {
                    grid[r][c] = 0;
                    clearedCells.add(new Point(c, r));
                }
            }
        }

        for (int c : fullCols) {
            for (int r = 0; r < GRID_SIZE; r++) {
                if (grid[r][c] != 0) {
                    grid[r][c] = 0;
                    clearedCells.add(new Point(c, r));
                }
            }
        }

        return new LineClearResult(fullRows.size() + fullCols.size(), clearedCells, fullRows, fullCols);
    }

    public boolean canAnyPieceFit(BlockPiece[] tray) {
        for (BlockPiece piece : tray) {
            if (piece == null) continue;
            for (int r = 0; r < GRID_SIZE; r++) {
                for (int c = 0; c < GRID_SIZE; c++) {
                    if (canPlacePiece(piece, r, c)) return true;
                }
            }
        }
        return false;
    }

    public static class LineClearResult {
        public final int linesCleared;
        public final List<Point> clearedCells;
        public final List<Integer> clearedRows;
        public final List<Integer> clearedCols;

        public LineClearResult(int linesCleared, List<Point> clearedCells, List<Integer> clearedRows, List<Integer> clearedCols) {
            this.linesCleared = linesCleared;
            this.clearedCells = clearedCells;
            this.clearedRows = clearedRows;
            this.clearedCols = clearedCols;
        }
    }
}
