package model;

/**
 * Represents a block shape and its board color.
 */
public class BlockPiece {
    private final int[][] shape;
    private final int colorId;

    public BlockPiece(int[][] shape, int colorId) {
        if (colorId <= 0) {
            throw new IllegalArgumentException("Color ID must be positive.");
        }

        this.shape = copyAndValidateShape(shape);
        this.colorId = colorId;
    }

    public int getColorId() {
        return colorId;
    }

    public int getHeight() {
        return shape.length;
    }

    public int getWidth() {
        return shape[0].length;
    }

    public boolean isOccupied(int row, int column) {
        if (row < 0 || row >= getHeight() || column < 0 || column >= getWidth()) {
            throw new IndexOutOfBoundsException("Shape position is outside the piece.");
        }
        return shape[row][column] != 0;
    }

    public int[][] getShape() {
        int[][] shapeCopy = new int[shape.length][shape[0].length];

        for (int row = 0; row < shape.length; row++) {
            System.arraycopy(shape[row], 0, shapeCopy[row], 0, shape[row].length);
        }

        return shapeCopy;
    }

    private int[][] copyAndValidateShape(int[][] source) {
        if (source == null || source.length == 0) {
            throw new IllegalArgumentException("Shape must contain at least one row.");
        }

        int width = -1;
        boolean hasOccupiedCell = false;
        int[][] copy = new int[source.length][];

        for (int row = 0; row < source.length; row++) {
            if (source[row] == null || source[row].length == 0) {
                throw new IllegalArgumentException("Shape rows must contain at least one cell.");
            }

            if (width == -1) {
                width = source[row].length;
            } else if (source[row].length != width) {
                throw new IllegalArgumentException("Shape rows must have equal lengths.");
            }

            copy[row] = new int[width];
            for (int column = 0; column < width; column++) {
                copy[row][column] = source[row][column] == 0 ? 0 : 1;
                hasOccupiedCell |= copy[row][column] != 0;
            }
        }

        if (!hasOccupiedCell) {
            throw new IllegalArgumentException("Shape must contain an occupied cell.");
        }

        return copy;
    }
}
