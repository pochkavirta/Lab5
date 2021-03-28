import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class JImageDisplay extends JComponent {
    private BufferedImage image;

    public JImageDisplay(int width, int height) {
        this.image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Dimension preferredSize = new Dimension(width, height);
        super.setPreferredSize(preferredSize);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null);
    }

    public void clearImage() {
        this.image.setRGB(getWidth(), getHeight(), 0);
    }

    public void drawPixel(int x, int y, int rgbColor) {
        this.image.setRGB(x, y, rgbColor);
    }

    public BufferedImage getImage() {
        return image;
    }
}
