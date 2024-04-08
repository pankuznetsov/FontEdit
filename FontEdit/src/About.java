
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JPanel;
import javax.swing.Timer;


public class About extends JPanel {
    
    public static final String[] MIT_LICENSE = { "Copyright (c) 2021 Kuznetsov S .A. \n",
            "\n",
            "Permission is hereby granted, free of charge, to any person obtaining a copy\n",
            "of this software and associated documentation files (the \"Software\"), to deal\n",
            "in the Software without restriction, including without limitation the rights\n",
            "to use, copy, modify, merge, publish, distribute, sublicense, and/or sell\n",
            "copies of the Software, and to permit persons to whom the Software is\n",
            "furnished to do so, subject to the following conditions:\n",
            "\n",
            "The above copyright notice and this permission notice shall be included in all\n",
            "copies or substantial portions of the Software.\n",
            "\n",
            "THE SOFTWARE IS PROVIDED \"AS IS\", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR\n",
            "IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,\n",
            "FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE\n",
            "AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER\n",
            "LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,\n",
            "OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE\n",
            "SOFTWARE." };
    
    Timer timer;
    int t = 0;
    
    @Override
    public void paintComponent(Graphics g) {
        if (timer == null) {
            timer = new Timer(30, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    repaint();
                }
            });
            timer.start();
        }
        g.setColor(Color.black);
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(Color.white);
        g.setFont(new java.awt.Font("Verdana", java.awt.Font.BOLD, 60));
        g.drawString("FontEdit", 50, 90);
        g.setFont(new java.awt.Font("Verdana", java.awt.Font.PLAIN, 10));
        for (int i = 0; i < MIT_LICENSE.length; i++)
            g.drawString(MIT_LICENSE[i], 20, 135 + i * g.getFontMetrics().getHeight());
        g.setFont(new java.awt.Font("Verdana", java.awt.Font.BOLD, 10));
        g.drawString("Kuznetsov S. A., 2021", 410, 493);
        for (int y = 460; y < 480; y++)
            for (int x = 0; x < getWidth(); x++) {
                float red = (float) (Math.sin(((float) (x + t)) / 50.0f) + 1.0f) / 2.0f;
                float green = (float) (Math.sin(((float) (y + t)) / 50.0f) + 1.0f) / 2.0f;
                float blue = (float) (Math.sin(((float) (x + y + t)) / 50.0f) + 1.0f) / 2.0f;
                // Color.getHSBColor(hue, saturation, value);
                g.setColor(new Color((int) (red * 255), (int) (green * 255), (int) (blue * 255)));
                g.fillRect(x, y, 1, 1);
            }
        t += 1;
    }
}
