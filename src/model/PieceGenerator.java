package model;

import java.util.Random;

/**
 * Creates random trays of block pieces and checks remaining moves.
 */
public class PieceGenerator {
    public static final int TRAY_SIZE = 3;

    private static final int COLOR_COUNT = 6;

    private static final int[][][] SHAPES = {
            {{1}},
            {{1, 1}},
            {{1}, {1}},
            {{1, 1, 1}},
            {{1}, {1}, {1}},
            {{1, 1, 1, 1}},
            {{1}, {1}, {1}, {1}},
            {{1, 1}, {1, 1}},
            {{1, 1, 1}, {1, 1, 1}},
            {{1, 1}, {1, 1}, {1, 1}},
            {{1, 0}, {1, 0}, {1, 1}},
            {{1, 1, 1}, {1, 0, 0}},
            {{1, 1}, {0, 1}, {0, 1}},
            {{0, 0, 1}, {1, 1, 1}},
            {{1, 1, 1}, {0, 1, 0}},
            {{0, 1}, {1, 1}, {0, 1}},
            {{0, 1, 0}, {1, 1, 1}},
            {{1, 0}, {1, 1}, {1, 0}},
            {{0, 1, 1}, {1, 1, 0}},
            {{1, 0}, {1, 1}, {0, 1}}
    };

    private final Random random;

    public PieceGenerator() {
        this(new Random());
    }

    public PieceGenerator(Random random) {
        if (random == null) {
            throw new IllegalArgumentException("Random source is required.");
        }
        this.random = random;
    }

    /**
     * Generates the three pieces shown in a new tray.
     */
    public BlockPiece[] generateTray() {
        BlockPiece[] tray = new BlockPiece[TRAY_SIZE];

        for (int index = 0; index < TRAY_SIZE; index++) {
            int[][] shape = SHAPES[random.nextInt(SHAPES.length)];
            int colorId = random.nextInt(COLOR_COUNT) + 1;
            tray[index] = new BlockPiece(shape, colorId);
        }

        return tray;
    }

    /**
     * Returns true when at least one remaining piece can be placed.
     */
    public boolean canAnyPieceFit(Board board, BlockPiece[] pieces) {
        if (board == null || pieces == null) {
            throw new IllegalArgumentException("Board and pieces are required.");
        }

        for (BlockPiece piece : pieces) {
            if (piece != null && canPieceFit(board, piece)) {
                return true;
            }
        }

        return false;
    }

    private boolean canPieceFit(Board board, BlockPiece piece) {
        int[][] shape = piece.getShape();

        for (int row = 0; row < Board.SIZE; row++) {
            for (int column = 0; column < Board.SIZE; column++) {
                if (board.canPlace(shape, row, column)) {
                    return true;
                }
            }
        }

        return false;
    }
}
