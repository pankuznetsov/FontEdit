
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.Serializable;


public class Font implements Serializable {

    public static final transient boolean HORIZONTAL = false, VERTICAL = true;
    public static final transient boolean MSB = false, LSB = true;
    int characters;
    int width, height;
    boolean[][][] data;
    
    private int roundUpModulus(int n, int modulus) {
        int rem = n % modulus;
        if (rem > 0) return n + (modulus - rem);
        return n;
    }
    
    private String byteToHex(byte num) {
        char[] hexDigits = new char[2];
        hexDigits[0] = Character.forDigit((num >> 4) & 0xF, 16);
        hexDigits[1] = Character.forDigit((num & 0xF), 16);
        return new String(hexDigits);
    }
    
    public String exportAsCode(int a, int b, boolean scan) {
        int charLength = (roundUpModulus(scan == HORIZONTAL ? width : height, 8) * (scan == HORIZONTAL ? height : width)) / 8;
        // System.out.println("charLength: " + charLength);
        int arrayLength = charLength * (b - a);
        // System.out.println("arrayLength: " + arrayLength);
        String code = "\n#define OFFSET " + a + "\n";
        code += "#define BYTES_PER_CHAR " + charLength + "\n";
        code += "#define BYTES_PER_LINE " + charLength / height + "\n";
        code += "\nbyte FONT_" + width + "X" + height + "[" + arrayLength + "] = {";
        for (int i = a; i < b; i++) {
            for (int y = 0; y < (scan == HORIZONTAL ? height : width); y++) {
                code += "\n\t\t";
                byte s = 0x00; int bit = 0;
                for (int x = 0; x < (scan == HORIZONTAL ? width : height); x++) {
                    int realX = x, realY = y;
                    if (scan == VERTICAL) { realX = y; realY = x; }
                    if (data[i][realX][realY]) s |= (1 << bit);
                    bit++;
                    if (bit == 8 || x == (scan == HORIZONTAL ? width : height) - 1) {
                        code += "0x" + byteToHex(s).toUpperCase() + ", "; // Write byte
                        s = 0x00; bit = 0;
                    }
                }
            }
        }
        code = code.substring(0, code.length() - 2);
        code += "\n};\n\n";
        if (scan == HORIZONTAL) {
            code += "bool get_pixel(int c, uint8_t x, uint8_t y) {\n" +
                    "\tint si = BYTES_PER_CHAR * c;\n" +
                    "\tuint8_t b = y * BYTES_PER_LINE * 8 + x;\n" +
                    "\treturn (FONT_" + width + "X" + height + "[si + b / 8] >> b % 8) & 1;\n" +
                    "}\n";
        } else {
            code += "bool get_pixel(int c, uint8_t x, uint8_t y) {\n" +
                    "\tint si = BYTES_PER_CHAR * c;\n" +
                    "\tuint8_t b = x * BYTES_PER_LINE * 8 + y;\n" +
                    "\treturn (FONT_" + width + "X" + height + "[si + b / 8] >> b % 8) & 1;\n" +
                    "}\n";
        }
        return code;
    }
    
    public BufferedImage exportAsImage(int a, int b, int columns, int margin, int padding) {
        int w = width * columns + margin * (columns - 1) + padding * 2;
        int d = Math.max(b - a, 1);
        int h = height * (d / columns + (d % columns > 0 ? 1 : 0)) + margin * (d / columns + (d % columns > 0 ? 1 : 0) - 1) + padding * 2;
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.setColor(EditorPanel.BACKGROUND);
        g.fillRect(0, 0, w, h);
        g.setColor(Color.black);
        int i = a;
        for (int y = padding; y < h - height; y += height + margin)
            for (int x = padding; x < w - width; x += width + margin) {
                g.drawImage(getImageOfSymbol(i), x, y, null);
                i++;
                if (i > b) return img;
            }
        return img;
    }
    
    public void clearChar(int index) {
        for (int y = 0; y < height; y++)
            for (int x = 0; x < width; x++)
                data[index][x][y] = false;
    }
    
    public void invertChar(int index) {
        for (int y = 0; y < height; y++)
            for (int x = 0; x < width; x++)
                data[index][x][y] = !data[index][x][y];
    }
    
    public void randomizeChar(int index) {
        for (int y = 0; y < height; y++)
            for (int x = 0; x < width; x++)
                data[index][x][y] = Math.random() > 0.5;
    }
    
    public BufferedImage getImageOfSymbol(int index) {
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        for (int y = 0; y < height; y++)
            for (int x = 0; x < width; x++) {
                g.setColor(data[index][x][y] ? Color.black : Color.white);
                g.fillRect(x, y, 1, 1);
            }
        return img;
    }
    
    public Font(int characters, int width, int height) {
        this.characters = characters;
        this.width = width;
        this.height = height;
        data = new boolean[this.characters][this.width][this.height];
        for (int i = 0; i < this.characters; i++)
            randomizeChar(i);
    }
}