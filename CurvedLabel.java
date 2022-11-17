import java.awt.*;
import java.awt.geom.Rectangle2D;
import javax.swing.*;

public class CurvedLabel extends JLabel {
    private static JFrame frame;
    private String text = "";
    private Color color = Color.black;
    private Color background = null;
    private int radius = 1;
    private int opacity = 100;
    private double xOffset = 1.0/2.0;
    private double yOffset = 1.0/4.0;
    private Font font = new Font("Arial", Font.PLAIN, 14);

    public CurvedLabel() {
        
    }

    public CurvedLabel(String s) {
        text = s;
    }

    public CurvedLabel(String s, int r, Color c) {
        text = s;
        color = c;
    }

    public static void setFrame(JFrame f) {
        frame = f;
    }

    public void setColor(Color c) {
        color = c;
        repaint();
    }
    
    public void setOpacity(int o) {
        opacity = o;
        repaint();
    }

    public int getOpacity() {
        return(opacity);
    }

    public void setBackground(Color c) {
        background = c;
        repaint();
    }

    public void setFont(Font f) {
        font = f;
        repaint();
    }

    public void setText(String s) {
        text = s;
        repaint();
    }

    public void setYOffset(double y) {
        yOffset = y;
    }

    public void setXOffset(double x) {
        xOffset = x;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g); // Prevents duplicates: https://stackoverflow.com/questions/13773315/java-paintcomponent-paints-a-copy-of-the-top-gui-panel-for-no-apparent-reason

        // Draw the background, if necessary
        if (background != null) { // If background isn't null
            g.setColor(new Color(background.getRed(), background.getBlue(), background.getGreen(), opacity)); // Set the color to the background color
            g.fillRoundRect(0, 0, getSize().width, getSize().height, (int)(radius * frame.getWidth()/Board.MAX_WIDTH), (int)(radius * frame.getHeight()/Board.MAX_HEIGHT)); // Draw the background
        }

        // Draw Label Text: https://stackoverflow.com/questions/5378052/positioning-string-in-graphic-java
        g.setFont(new Font(font.getName(), font.getStyle(), (int)(font.getSize()*frame.getWidth()/Board.MAX_WIDTH)));
        g.setColor(new Color(color.getRed(), color.getBlue(), color.getGreen(), 100)); // Should make a text-specific opacity
        Rectangle2D rect = g.getFontMetrics().getStringBounds(text, g);
        g.drawString(text, (int)(getSize().width/2 - rect.getWidth()*xOffset), (int)(getSize().height/2 + rect.getHeight()/2 - rect.getHeight()*yOffset));
    }
}
