import java.awt.*;
import java.awt.geom.Rectangle2D;
import javax.swing.*;

// Need to be able to paint a different opacity when button is pressed.

public class CurvedButton extends JButton {
    private static JFrame frame;
    private String text = "";
    private int radius = 1;
    private int opacity = 100;
    private int borderSize = 2;
    private double xOffset = 0.5;
    private double yOffset = 1.0/3.0;
    private Color color = Color.white;
    private Color borderColor = Color.black;
    private Color tColor = Color.black;
    private Font font = new Font("Arial", Font.PLAIN, 14);

    public CurvedButton() {

    }

    // Text, Radius
    public CurvedButton(String s, int r) {
        text = s;
        radius = r;
        setContentAreaFilled(false);
    }

    // Text, Radius, Color, Opacity
    public CurvedButton(String s, int r, Color c, int o) {
        text = s;
        radius = r;
        color = c;
        opacity = o;
        setContentAreaFilled(false);
    }

    // Sets the JFrame the button models it's size after
    // Probably not needed
    public static void setFrame(JFrame f) {
        frame = f;
    }

    public void setYOffset(double y) {
        yOffset = y;
    }

    public void setXOffset(double x) {
        xOffset = x;
    }

    // Sets the border color and size
    public void setBorder(Color c, int s) {
        borderColor = c;
        borderSize = s;
        repaint();
    }

    public void setTextColor(Color c) {
        tColor = c;
        repaint();
    }

    public Color getBorderColor() {
        return(borderColor);
    }

    public int getBorderSize() {
        return(borderSize);
    }

    // Sets the color of the button
    public void setColor(Color c) {
        color = c;
        repaint();
    }

    public Color getColor() {
        return(color);
    }

    public void setOpacity(int o) {
        opacity = o;
    }

    public int getOpacity() {
        return(opacity);
    }

    // Sets the font of the button
    public void setFont(Font f) {
        font = f;
        repaint();
    }

    @Override
    public Font getFont() {
        return(font);
    }

    // Sets the text of the button
    public void setText(String s) {
        text = s;
        repaint();
    }

    // Returns only the text contained within this class
    public String findText() {
        return(text);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        paintComponent(g);
        paintBorder(g);
    }
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g); // Prevents duplicates: https://stackoverflow.com/questions/13773315/java-paintcomponent-paints-a-copy-of-the-top-gui-panel-for-no-apparent-reason
        // Draw Button Background:  https://stackoverflow.com/questions/26036002/how-to-make-round-jbuttons-in-java
        // Setting Opacity:         https://stackoverflow.com/questions/29379441/java-set-transparency-on-color-color-without-using-rgbs
        // Setting Button Color:    https://stackoverflow.com/questions/6256483/how-to-set-the-button-color-of-a-jbutton-not-background-color
        if (!getModel().isPressed()) {
            g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), opacity));
        }
        else {
            g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 2*opacity/3));
        }

        g.fillRoundRect(0, 0, getSize().width, getSize().height, (int)(radius * frame.getWidth()/Board.MAX_WIDTH), (int)(radius * frame.getHeight()/Board.MAX_HEIGHT));

        // End the method if text doesn't need to be painted
        if (text.trim().equals("")) {
            return;
        }

        // Draw Button Text: https://stackoverflow.com/questions/5378052/positioning-string-in-graphic-java
        g.setFont(new Font(font.getName(), font.getStyle(), (int)(font.getSize()*frame.getWidth()/Board.MAX_WIDTH)));
        g.setColor(tColor);
        Rectangle2D rect = g.getFontMetrics().getStringBounds(text, g);
        g.drawString(text, (int)(getSize().width/2 - rect.getWidth()*xOffset), (int)(getSize().height/2 + rect.getHeight()*yOffset));
    }

    @Override
    public void paintBorder(Graphics g) {
        //super.paintBorder(g); // Prevents duplicates: https://stackoverflow.com/questions/13773315/java-paintcomponent-paints-a-copy-of-the-top-gui-panel-for-no-apparent-reason
        Graphics2D g2d = (Graphics2D)(g);
        g2d.setColor(borderColor);
        // Setting Stroke Thickness: https://stackoverflow.com/questions/4219511/draw-rectangle-border-thickness
        g2d.setStroke(new BasicStroke((int)(borderSize * (frame.getWidth()/Board.MAX_WIDTH))));
        if ((int)(borderSize * (frame.getWidth()/Board.MAX_WIDTH)) < 2) {
            g2d.setStroke(new BasicStroke(2));
        }
        // Draw Button Border: https://stackoverflow.com/questions/13866252/button-with-round-edges
        g2d.drawRoundRect(0, 0, getSize().width-1, getSize().height-1, (int)(radius * frame.getWidth()/Board.MAX_WIDTH), (int)(radius * frame.getHeight()/Board.MAX_HEIGHT));
    }
}
