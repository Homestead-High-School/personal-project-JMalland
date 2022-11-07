import java.awt.*;
import javax.swing.*;

public class PaddedPanel extends JPanel {
    public static final float X_AXIS = BoxLayout.X_AXIS;
    public static final float Y_AXIS = BoxLayout.Y_AXIS;


    public PaddedPanel(Component comp, int padding, int otherDefault, int layout) {
        setLayout(new BorderLayout((layout == PaddedPanel.X_AXIS ? padding : 0), (layout == PaddedPanel.X_AXIS ? 0 : padding)));
        JLabel padA = new JLabel();
        JLabel padB = new JLabel();
        if (layout == PaddedPanel.X_AXIS) { // Pads the panel on the left/right
            padA.setSize(0, otherDefault);
            padA.setPreferredSize(padA.getSize());
            padB.setSize(0, otherDefault);
            padB.setPreferredSize(padB.getSize());
            add(padA, BorderLayout.WEST);
            add(comp, BorderLayout.CENTER);
            add(padB, BorderLayout.EAST);
        }
        else if (layout == PaddedPanel.Y_AXIS) { // Pads the panel on the top/bottom
            padA.setSize(otherDefault, 0);
            padA.setPreferredSize(padA.getSize());
            padB.setSize(otherDefault, 0);
            padB.setPreferredSize(padB.getSize());
            add(padA, BorderLayout.NORTH);
            add(comp, BorderLayout.CENTER);
            add(padB, BorderLayout.SOUTH);
        }
        else { // Treats it like a normal JPanel
            add(comp, BorderLayout.CENTER);
        }
    }
}
