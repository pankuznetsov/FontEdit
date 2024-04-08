
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import javax.swing.JPanel;


public class EditorPanel extends JPanel {
    
    static final Color BACKGROUND = new Color(200, 200, 255);
    CharTile[] tiles;
    public static final int PENCIL = 1, LINE = 2, RECT = 3;
    int tool = 0;
    boolean backgroundToFront = false, drawBackground = true;
    Font font = new Font(256, 10, 12);
    int selectedSymbol = 0;
    int symbolCode = 0, backgroundSymbolCode = 0;
    double zoom = 13;
    boolean lineStartHasBeenSet = false;
    int startX, startY, endX, endY;
    int mouseX, mouseY;
    
    public void scale(double clicks) {
        zoom -= clicks;
        zoom = Math.min(Math.max(zoom, 10), 35);
        repaint();
    }
    
    private float getXD(int pointingX, int pointingY) {
        float k = (((float) (pointingX - startX)) / ((float) (pointingY - startY)));
        return k;
    }
    
    private float getYD(int pointingX, int pointingY) {
        float k = (((float) (pointingY - startY)) / ((float) (pointingX - startX)));
        return k;
    }
    
    @Override
    public void paintComponent(Graphics g) {
        int pointingX = (int) ((mouseX - getWidth() / 2 + (int) (font.width * zoom / 2)) / zoom);
        int pointingY = (int) ((mouseY - getHeight() / 2 + (int) (font.height * zoom / 2)) / zoom);
        g.setColor(Color.white);
        g.fillRect(2, 2, getWidth() - 4, getHeight() - 4);
        if (!backgroundToFront) {
            drawBackground(g);
            drawSymbol(g);
        } else {
            drawSymbol(g);
            drawBackground(g);
        }
        int shiftX = getWidth() / 2 - (int) (((double) font.width) * zoom / 2);
        int shiftY = getHeight() / 2 - (int) (((double) font.height) * zoom / 2);
        g.setColor(Color.black);
        for (int y = 0; y < font.height; y++)
            for (int x = 0; x < font.width; x++)
                g.drawRect(shiftX + (int) (x * zoom), shiftY + (int) (y * zoom), (int) zoom, (int) zoom);
        g.setColor(Color.green);
        if (tool == LINE && lineStartHasBeenSet) {
            g.fillOval(shiftX + (int) (startX * zoom + zoom / 2) - 4, shiftY + (int) (startY * zoom + zoom / 2) - 4, 8, 8);
            float x = getXD(pointingX, pointingY);
            float y = getYD(pointingX, pointingY);
            System.out.println(x + ", " + y);
        }
        if (tool == RECT && lineStartHasBeenSet) {
            absRect(g, shiftX + (int) (startX * zoom + zoom / 2), shiftY + (int) (startY * zoom + zoom / 2),
                    shiftX + (int) (zoom * pointingX + zoom / 2), shiftY + (int) (zoom * pointingY + zoom / 2));
        }
        if (tiles != null) {
            tiles[symbolCode].repaint();
            tiles[selectedSymbol].repaint();
        }
    }
    
    private void absRect(Graphics g, int x1, int y1, int x2, int y2) {
        g.drawLine(x1, y1, x2, y1);
        g.drawLine(x1, y1, x1, y2);
        g.drawLine(x2, y2, x2, y1);
        g.drawLine(x2, y2, x1, y2);
    }
    
    private void drawBackground(Graphics g) {
        if (!drawBackground) return;
        int shiftX = getWidth() / 2 - (int) (font.width * zoom / 2);
        int shiftY = getHeight() / 2 - (int) (font.height * zoom / 2);
        g.setColor(new Color(150, 250, 150));
        for (int y = 0; y < font.height; y++)
            for (int x = 0; x < font.width; x++)
                if (font.data[backgroundSymbolCode][x][y])
                    g.fillRect(shiftX + (int) (x * zoom) + 3, shiftY + (int) (y * zoom) + 3, (int) zoom - 5, (int) zoom - 5);
    }
    
    private void drawSymbol(Graphics g) {
        int shiftX = getWidth() / 2 - (int) (font.width * zoom / 2);
        int shiftY = getHeight() / 2 - (int) (font.height * zoom / 2);
        g.setColor(new Color(0, 0, 0, 220));
        for (int y = 0; y < font.height; y++)
            for (int x = 0; x < font.width; x++)
                if (font.data[symbolCode][x][y])
                    g.fillRect(shiftX + (int) (x * zoom), shiftY + (int) (y * zoom), (int) zoom, (int) zoom);
    }
    
    public void mouse(int screenX, int screenY, int key) {
        int shiftX = getWidth() / 2 - (int) (font.width * zoom / 2);
        int shiftY = getHeight() / 2 - (int) (font.height * zoom / 2);
        if (screenX < shiftX || screenY < shiftY || screenX >= shiftX + (font.width * zoom) || screenY >= shiftY + (font.height * zoom))
            return;
        int x = (int) ((screenX - getWidth() / 2 + (int) (font.width * zoom / 2)) / zoom);
        int y = (int) ((screenY - getHeight() / 2 + (int) (font.height * zoom / 2)) / zoom);
        switch (tool) {
            case PENCIL:
                if (key == MouseEvent.BUTTON1)
                    font.data[symbolCode][x][y] = !font.data[symbolCode][x][y];
                if (key == MouseEvent.BUTTON3)
                    font.data[symbolCode][x][y] = false;
                break;
            case LINE:
            case RECT:
                if (key == MouseEvent.BUTTON1 || key == MouseEvent.BUTTON3)
                    if (!lineStartHasBeenSet) {
                        startX = x;
                        startY = y;
                        lineStartHasBeenSet = true;
                    } else {
                        endX = x;
                        endY = y;
                        if (tool == LINE) {
                            if (endX < startX) {
                                int temp = endX;
                                endX = startX;
                                startX = temp;
                            }
                            if (endY < startY) {
                                int temp = endY;
                                endY = startY;
                                startY = temp;
                            }
                        }
                        if (tool == RECT) {
                            for (int fy = Math.min(startY, endY); fy <= Math.max(startY, endY); fy++)
                                for (int fx = Math.min(startX, endX); fx <= Math.max(startX, endX); fx++) {
                                    if (key == MouseEvent.BUTTON1) font.data[symbolCode][fx][fy] = true;
                                    if (key == MouseEvent.BUTTON3) font.data[symbolCode][fx][fy] = false;
                                }
                        }
                        lineStartHasBeenSet = false;
                    }
                break;
        }
        repaint();
    }
}
