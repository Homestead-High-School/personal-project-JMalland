import java.awt.*;
import java.awt.geom.Rectangle2D;
import javax.swing.*;

public class CurvedButton extends JButton {
    private static JFrame frame;
    private String text = "";
    private int radius = 1;
    private int opacity = 100;
    private Color color = new Color(0xFFFFFF);
    private Font font = new Font("Arial", Font.PLAIN, 14);

    public CurvedButton() {
        super();
    }

    public CurvedButton(String s, int r) {
        text = s;
        radius = r;
    }

    public CurvedButton(String s, int r, Color c) {
        text = s;
        radius = r;
        color = c;
    }

    public CurvedButton(String s, int r, Color c, int o) {
        text = s;
        radius = r;
        color = c;
        opacity = o;
    }

    public static void setFrame(JFrame f) {
        frame = f;
    }

    public void setOpacity(int o) {
        opacity = o;
        repaint();
    }

    public void setColor(Color c) {
        color = c;
        repaint();
    }

    public void setFont(Font f) {
        font = f;
    }

    public void setText(String s) {
        text = s;
        repaint();
    }
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g); // Prevents duplicates: https://stackoverflow.com/questions/13773315/java-paintcomponent-paints-a-copy-of-the-top-gui-panel-for-no-apparent-reason
        // Draw Button Background: https://stackoverflow.com/questions/26036002/how-to-make-round-jbuttons-in-java
        g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), opacity));
        g.fillRoundRect(0, 0, getSize().width, getSize().height, (int)(radius * frame.getWidth()/1056.0), (int)(radius * frame.getHeight()/1056.0));

        // End the method if text doesn't need to be painted
        if (text.equals("")) {
            return;
        }

        // Draw Button Text: https://stackoverflow.com/questions/5378052/positioning-string-in-graphic-java
        g.setFont(new Font(font.getName(), font.getStyle(), (int)(font.getSize()*frame.getWidth()/1056.0)));
        g.setColor(Color.black);
        Rectangle2D rect = g.getFontMetrics().getStringBounds(text, g);
        g.drawString(text, (int)(getSize().width/2 - rect.getWidth()/2), (int)(getSize().height/2 + rect.getHeight()/2 - rect.getHeight()/4));
    }

    @Override
    public void paintBorder(Graphics g) {
        //super.paintBorder(g); // Prevents duplicates: https://stackoverflow.com/questions/13773315/java-paintcomponent-paints-a-copy-of-the-top-gui-panel-for-no-apparent-reason
        // Draw Button Border: https://stackoverflow.com/questions/13866252/button-with-round-edges
        g.setColor(Color.black);
        g.drawRoundRect(0, 0, getSize().width-1, getSize().height-1, (int)(radius * frame.getWidth()/1056.0), (int)(radius * frame.getHeight()/1056.0));
    }

    
}
