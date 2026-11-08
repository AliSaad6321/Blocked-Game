package model;

import java.awt.Color;

public class BlockPiece {
    private final int[][] shape;
    private final Color color;
    private final int colorId;
    private final String name;

    public BlockPiece(int[][] shape, Color color, int colorId, String name) {
        this.shape = shape;
        this.color = color;
        this.colorId = colorId;
        this.name = name;
    }

    public int[][] getShape() { return shape; }
    public Color getColor() { return color; }
    public int getColorId() { return colorId; }
    public String getName() { return name; }

    public int getRows() { return shape.length; }
    public int getCols() { return shape[0].length; }

    public int getBlockCount() {
        int count = 0;
        for (int[] row : shape) {
            for (int cell : row) {
                if (cell > 0) count++;
            }
        }
        return count;
    }

    public BlockPiece copy() {
        int[][] newShape = new int[getRows()][getCols()];
        for (int r = 0; r < getRows(); r++) {
            System.arraycopy(shape[r], 0, newShape[r], 0, getCols());
        }
        return new BlockPiece(newShape, color, colorId, name);
    }
}