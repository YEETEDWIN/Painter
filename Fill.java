import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayDeque;
import java.util.Queue;

public final class Fill {
    private static final int COLOR_TOLERANCE = 120;

    private Fill() {
    }

    public static BufferedImage createFillLayer(BufferedImage source, int startX, int startY, Color fillColor) {

        int width = source.getWidth();
        int height = source.getHeight();
        BufferedImage fillLayer = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        if (startX < 0 || startX >= width || startY < 0 || startY >= height) {
            return fillLayer;
        }

        int targetColor = source.getRGB(startX, startY);
        int replacementColor = fillColor.getRGB();

        if (targetColor == replacementColor) {
            return fillLayer;
        }

        boolean[][] visited = new boolean[height][width];
        Queue<Point> pixels = new ArrayDeque<>();
        pixels.add(new Point(startX, startY));

        while (!pixels.isEmpty()) {
            Point point = pixels.remove();
            int x = point.x;
            int y = point.y;

            if (x < 0 || x >= width || y < 0 || y >= height || visited[y][x]) {
                continue;
            }

            visited[y][x] = true;

            if (!colorsMatch(source.getRGB(x, y), targetColor)) {
                continue;
            }

            fillLayer.setRGB(x, y, replacementColor);
            pixels.add(new Point(x + 1, y));
            pixels.add(new Point(x - 1, y));
            pixels.add(new Point(x, y + 1));
            pixels.add(new Point(x, y - 1));
            pixels.add(new Point(x + 1, y + 1));
            pixels.add(new Point(x - 1, y + 1));
            pixels.add(new Point(x + 1, y - 1));
            pixels.add(new Point(x - 1, y - 1));
        }

        // Expanding fill layer here to ensure that the fill layer covers the stroke layer completely
        BufferedImage expandedFill = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        
        for (int y = 0; y < height; y++){
            for (int x = 0; x < width; x++){
                boolean covered = fillLayer.getRGB(x, y) != 0;

                for (int dy = -1; dy <= 1 && !covered; dy++){
                    for (int dx = -1; dx <= 1 && !covered; dx++){
                        int neighborX = x + dx;
                        int neighborY = y + dy;
                        
                        if (neighborX >= 0 && neighborX < width
                            && neighborY >=0 && neighborY < height
                            && fillLayer.getRGB(neighborX, neighborY) != 0) {
                                covered = true;
                            }
                    }
                }

                if (covered) {
                    expandedFill.setRGB(x, y, replacementColor);
                }
            }
        }
        return expandedFill;

    }

    private static boolean colorsMatch(int color, int targetcolor) {
        return Math.abs(((color >> 16) & 0xff) - ((targetcolor >> 16) & 0xff)) <= COLOR_TOLERANCE
                && Math.abs(((color >> 8) & 0xff) - ((targetcolor >> 8) & 0xff)) <= COLOR_TOLERANCE
                && Math.abs((color & 0xff) - (targetcolor & 0xff)) <= COLOR_TOLERANCE
                && Math.abs(((color >>> 24) & 0xff) - ((targetcolor >>> 24) & 0xff)) <= COLOR_TOLERANCE;
    }

}
