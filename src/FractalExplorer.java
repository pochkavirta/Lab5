import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public class FractalExplorer {
    private int sizeDisplay;
    private JImageDisplay jImageDisplay;
    private FractalGenerator fractalGenerator;
    private Rectangle2D.Double rectangle2D;

    public FractalExplorer(int sizeDisplay) {
        this.sizeDisplay = sizeDisplay;
        this.fractalGenerator = new Mandelbrot();
        this.rectangle2D = new Rectangle2D.Double();
        this.fractalGenerator.getInitialRange(rectangle2D);
        this.jImageDisplay = new JImageDisplay(sizeDisplay, sizeDisplay);
    }

    public static void main(String[] args) {
        FractalExplorer displayExplorer = new FractalExplorer(800);
        displayExplorer.createAndShowGUI();
        displayExplorer.drawFractal();
    }

    private void createAndShowGUI() {
        jImageDisplay.setLayout(new BorderLayout());
        JFrame myFrame = new JFrame("Запуск фрактала");
        myFrame.add(jImageDisplay, BorderLayout.CENTER);
        JButton resetButton = new JButton("Сброс отображения");
        ActionListenerImpl resetHandler = new ActionListenerImpl();
        resetButton.addActionListener(resetHandler);
        MouseListener click = new MouseListener();
        jImageDisplay.addMouseListener(click);
        myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JComboBox myComboBox = new JComboBox();

        FractalGenerator mandelbrotFractal = new Mandelbrot();
        myComboBox.addItem(mandelbrotFractal);
        FractalGenerator tricornFractal = new Tricorn();
        myComboBox.addItem(tricornFractal);
        FractalGenerator burningShipFractal = new BurningShip();
        myComboBox.addItem(burningShipFractal);

        ActionListenerImpl fractalChooser = new ActionListenerImpl();
        myComboBox.addActionListener(fractalChooser);

        JPanel myPanel = new JPanel();
        JLabel myLabel = new JLabel("Фрактал:");
        myPanel.add(myLabel);
        myPanel.add(myComboBox);
        myFrame.add(myPanel, BorderLayout.NORTH);

        JButton saveButton = new JButton("Сохранить");
        JPanel myBottomPanel = new JPanel();
        myBottomPanel.add(saveButton);
        myBottomPanel.add(resetButton);
        myFrame.add(myBottomPanel, BorderLayout.SOUTH);

        ActionListenerImpl saveHandler = new ActionListenerImpl();
        saveButton.addActionListener(saveHandler);

        myFrame.pack();
        myFrame.setVisible(true);
        myFrame.setResizable(false);
    }

    private void drawFractal() {
        for (int x = 0; x < sizeDisplay; x++) {
            for (int y = 0; y < sizeDisplay; y++) {
                double xCoord = FractalGenerator.getCoord(rectangle2D.x,
                        rectangle2D.x + rectangle2D.width, sizeDisplay, x);
                double yCoord = FractalGenerator.getCoord(rectangle2D.y,
                        rectangle2D.y + rectangle2D.height, sizeDisplay, y);
                int iteration = fractalGenerator.numIterations(xCoord, yCoord);
                if (iteration == -1) {
                    jImageDisplay.drawPixel(x, y, 0);
                } else {
                    float hue = 0.7f + (float) iteration / 200f;
                    int rgbColor = Color.HSBtoRGB(hue, 1f, 1f);
                    jImageDisplay.drawPixel(x, y, rgbColor);
                }
            }
        }
        jImageDisplay.repaint();
    }

    public class ActionListenerImpl implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();
            if (e.getSource() instanceof JComboBox) {
                JComboBox mySource = (JComboBox) e.getSource();
                fractalGenerator = (FractalGenerator) mySource.getSelectedItem();
                fractalGenerator.getInitialRange(rectangle2D);
                drawFractal();

            } else if (command.equals("Сброс отображения")) {
                fractalGenerator.getInitialRange(rectangle2D);
                drawFractal();
            } else if (command.equals("Сохранить")) {
                JFileChooser myFileChooser = new JFileChooser();
                FileFilter extensionFilter =
                        new FileNameExtensionFilter("PNG Images", "png");
                myFileChooser.setFileFilter(extensionFilter);
                myFileChooser.setAcceptAllFileFilterUsed(false);
                int userSelection = myFileChooser.showSaveDialog(jImageDisplay);
                if (userSelection == JFileChooser.APPROVE_OPTION) {
                    java.io.File file = myFileChooser.getSelectedFile();
                    try {
                        BufferedImage displayImage = jImageDisplay.getImage();
                        javax.imageio.ImageIO.write(displayImage, "png", file);
                    } catch (Exception exception) {
                        JOptionPane.showMessageDialog(jImageDisplay,
                                exception.getMessage(), "Не удалось сохранить изображение",
                                JOptionPane.ERROR_MESSAGE);
                    }
                } else return;
            }
        }
    }

    public class MouseListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            int x = e.getX();
            double xCoord = FractalGenerator.getCoord(rectangle2D.x, rectangle2D.x + rectangle2D.width, sizeDisplay, x);
            int y = e.getY();
            double yCoord = FractalGenerator.getCoord(rectangle2D.y, rectangle2D.y + rectangle2D.height, sizeDisplay, y);
            fractalGenerator.recenterAndZoomRange(rectangle2D, xCoord, yCoord, 0.5);
            drawFractal();
        }
    }
}
