package model;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class PieceGenerator {
    private static final Random random = new Random();
    private static final Map<Integer, BufferedImage> BLOCK_IMAGES = new HashMap<>();

    public static final Color[] PIECE_COLORS = {
        new Color(0, 0, 0, 0),
        new Color(231, 76, 60),   // 1: Red
        new Color(46, 204, 113),  // 2: Green
        new Color(52, 152, 219),  // 3: Blue
        new Color(241, 196, 15),  // 4: Yellow
        new Color(155, 89, 182),  // 5: Purple
        new Color(230, 126, 34),  // 6: Orange
        new Color(255, 105, 180)  // 7: Pink
    };

    private static final String[] IMAGE_NAMES = {
        "", "red.png", "green.png", "blue.png", "yellow.png", "purple.png", "orange.png", "pink.png"
    };

    static {
        for (int i = 1; i < IMAGE_NAMES.length; i++) {
            try {
                File file = new File("UI/blocks/" + IMAGE_NAMES[i]);
                if (file.exists()) {
                    BLOCK_IMAGES.put(i, ImageIO.read(file));
                }
            } catch (Exception ignored) {}
        }
    }

    public static BufferedImage getBlockImage(int colorId) {
        return BLOCK_IMAGES.get(colorId);
    }

    private static final List<BlockPiece> TEMPLATES = new ArrayList<>();

    static {
        // Single Block (1)
        TEMPLATES.add(new BlockPiece(new int[][]{{1}}, PIECE_COLORS[1], 1, "Single Block 1x1"));

        // Line Shapes (6)
        TEMPLATES.add(new BlockPiece(new int[][]{{1, 1}}, PIECE_COLORS[3], 3, "Line 1x2 H"));
        TEMPLATES.add(new BlockPiece(new int[][]{{1}, {1}}, PIECE_COLORS[2], 2, "Line 2x1 V"));
        TEMPLATES.add(new BlockPiece(new int[][]{{1, 1, 1}}, PIECE_COLORS[4], 4, "Line 1x3 H"));
        TEMPLATES.add(new BlockPiece(new int[][]{{1}, {1}, {1}}, PIECE_COLORS[5], 5, "Line 3x1 V"));
        TEMPLATES.add(new BlockPiece(new int[][]{{1, 1, 1, 1}}, PIECE_COLORS[6], 6, "Line 1x4 H"));
        TEMPLATES.add(new BlockPiece(new int[][]{{1}, {1}, {1}, {1}}, PIECE_COLORS[7], 7, "Line 4x1 V"));

        // Square Shape (1)
        TEMPLATES.add(new BlockPiece(new int[][]{{1, 1}, {1, 1}}, PIECE_COLORS[1], 1, "Square 2x2"));

        // Rectangle Shapes (2)
        TEMPLATES.add(new BlockPiece(new int[][]{{1, 1, 1}, {1, 1, 1}}, PIECE_COLORS[7], 7, "Rectangle 3x2"));
        TEMPLATES.add(new BlockPiece(new int[][]{{1, 1}, {1, 1}, {1, 1}}, PIECE_COLORS[5], 5, "Rectangle 2x3"));

        // L Shapes (4)
        TEMPLATES.add(new BlockPiece(new int[][]{{1, 1}, {1, 0}}, PIECE_COLORS[4], 4, "L top-right"));
        TEMPLATES.add(new BlockPiece(new int[][]{{1, 1}, {0, 1}}, PIECE_COLORS[3], 3, "L top-left"));
        TEMPLATES.add(new BlockPiece(new int[][]{{0, 1}, {1, 1}}, PIECE_COLORS[2], 2, "L bottom-right"));
        TEMPLATES.add(new BlockPiece(new int[][]{{1, 0}, {1, 1}}, PIECE_COLORS[1], 1, "L bottom-left"));

        // T Shapes (4)
        TEMPLATES.add(new BlockPiece(new int[][]{{1, 1, 1}, {0, 1, 0}}, PIECE_COLORS[7], 7, "T top"));
        TEMPLATES.add(new BlockPiece(new int[][]{{0, 1, 0}, {1, 1, 1}}, PIECE_COLORS[6], 6, "T down"));
        TEMPLATES.add(new BlockPiece(new int[][]{{1, 0}, {1, 1}, {1, 0}}, PIECE_COLORS[5], 5, "T left"));
        TEMPLATES.add(new BlockPiece(new int[][]{{0, 1}, {1, 1}, {0, 1}}, PIECE_COLORS[4], 4, "T right"));

        // S Shapes (2)
        TEMPLATES.add(new BlockPiece(new int[][]{{0, 1, 1}, {1, 1, 0}}, PIECE_COLORS[2], 2, "S horizontal"));
        TEMPLATES.add(new BlockPiece(new int[][]{{1, 0}, {1, 1}, {0, 1}}, PIECE_COLORS[6], 6, "S vertical"));
    }

    public static BlockPiece getRandomPiece() {
        return TEMPLATES.get(random.nextInt(TEMPLATES.size())).copy();
    }

    public static BlockPiece[] generateTray() {
        BlockPiece[] tray = new BlockPiece[3];
        for (int i = 0; i < 3; i++) {
            tray[i] = getRandomPiece();
        }
        return tray;
    }
}
