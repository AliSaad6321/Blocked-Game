package model;

/**
 * Stores the occupied cells of the 8 by 8 game board.
 */
public class Board {
    public static final int SIZE = 8;

    private final int[][] cells;

    public Board() {
        cells = new int[SIZE][SIZE];
    }

    public int getCell(int row, int column) {
        validateBoardPosition(row, column);
        return cells[row][column];
    }

    public boolean isWithinBounds(int row, int column) {
        return row >= 0 && row < SIZE && column >= 0 && column < SIZE;
    }

    /**
     * Checks whether every occupied shape cell fits on an empty board cell.
     */
    public boolean canPlace(int[][] shape, int startRow, int startColumn) {
        validateShape(shape);

        for (int row = 0; row < shape.length; row++) {
            for (int column = 0; column < shape[row].length; column++) {
                if (shape[row][column] == 0) {
                    continue;
                }

                int boardRow = startRow + row;
                int boardColumn = startColumn + column;

                if (!isWithinBounds(boardRow, boardColumn)
                        || cells[boardRow][boardColumn] != 0) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Places a shape when it fits, returning false when the placement is invalid.
     */
    public boolean place(int[][] shape, int startRow, int startColumn, int colorId) {
        if (colorId <= 0) {
            throw new IllegalArgumentException("Color ID must be positive.");
        }

        if (!canPlace(shape, startRow, startColumn)) {
            return false;
        }

        for (int row = 0; row < shape.length; row++) {
            for (int column = 0; column < shape[row].length; column++) {
                if (shape[row][column] != 0) {
                    cells[startRow + row][startColumn + column] = colorId;
                }
            }
        }

        return true;
    }

    /**
     * Clears all completed rows and columns from the same board state.
     */
    public int clearCompletedLines() {
        boolean[] completedRows = new boolean[SIZE];
        boolean[] completedColumns = new boolean[SIZE];
        int completedLineCount = 0;

        for (int row = 0; row < SIZE; row++) {
            completedRows[row] = isRowComplete(row);
            if (completedRows[row]) {
                completedLineCount++;
            }
        }

        for (int column = 0; column < SIZE; column++) {
            completedColumns[column] = isColumnComplete(column);
            if (completedColumns[column]) {
                completedLineCount++;
            }
        }

        for (int row = 0; row < SIZE; row++) {
            for (int column = 0; column < SIZE; column++) {
                if (completedRows[row] || completedColumns[column]) {
                    cells[row][column] = 0;
                }
            }
        }

        return completedLineCount;
    }

    public void clear() {
        for (int row = 0; row < SIZE; row++) {
            for (int column = 0; column < SIZE; column++) {
                cells[row][column] = 0;
            }
        }
    }

    private boolean isRowComplete(int row) {
        for (int column = 0; column < SIZE; column++) {
            if (cells[row][column] == 0) {
                return false;
            }
        }
        return true;
    }

    private boolean isColumnComplete(int column) {
        for (int row = 0; row < SIZE; row++) {
            if (cells[row][column] == 0) {
                return false;
            }
        }
        return true;
    }

    private void validateBoardPosition(int row, int column) {
        if (!isWithinBounds(row, column)) {
            throw new IndexOutOfBoundsException("Board position is outside the 8 by 8 grid.");
        }
    }

    private void validateShape(int[][] shape) {
        if (shape == null || shape.length == 0) {
            throw new IllegalArgumentException("Shape must contain at least one row.");
        }

        int columnCount = -1;
        boolean hasOccupiedCell = false;

        for (int[] row : shape) {
            if (row == null || row.length == 0) {
                throw new IllegalArgumentException("Shape rows must contain at least one cell.");
            }

            if (columnCount == -1) {
                columnCount = row.length;
            } else if (row.length != columnCount) {
                throw new IllegalArgumentException("Shape rows must have equal lengths.");
            }

            for (int cell : row) {
                if (cell != 0) {
                    hasOccupiedCell = true;
                }
            }
        }

        if (!hasOccupiedCell) {
            throw new IllegalArgumentException("Shape must contain an occupied cell.");
        }
    }
}
