import java.awt.*;
import javax.swing.*;

public class PaddedPanel extends JPanel {
    public static final float X_AXIS = BoxLayout.X_AXIS;
    public static final float Y_AXIS = BoxLayout.Y_AXIS;
    private int padding = 0;
    private int layout = 0;

    public PaddedPanel(int padding, int layout) {
        this.layout = layout;
        setLayout(new GridBagLayout());
        GridBagLayout g = (GridBagLayout)(getLayout());
    }

    public PaddedPanel(Component[] comps, int padding, int layout) {
        this.padding = padding;
        this.layout = layout;
        setLayout(new GridBagLayout());
        for (int i=0; i<comps.length; i++) {
            addPadding(comps[i]);
        }
    }

    public PaddedPanel(Component comp, int padding, int layout) {
        this.padding = padding;
        this.layout = layout;
        setLayout(new GridBagLayout());
        addPadding(comp);
    }

    public void addPadding(Component comp) {
        // Keeping Components Square: https://stackoverflow.com/questions/48623643/how-to-make-gridbaglayout-specific-size
        // Sizing Components With GridBagLayout: https://stackoverflow.com/questions/4842261/margin-padding-in-gridbaglayout-java
        GridBagConstraints c = new GridBagConstraints(); // Creates constraints for the component
        int LR = layout == PaddedPanel.X_AXIS ? padding : 0; // Figures if the padding is horizontal
        int UD = layout == PaddedPanel.X_AXIS ? 0 : padding; // Figures if the padding is vertical
        c.insets = new Insets(UD, LR, UD, LR); // Insets(T, L, B, R)
        c.weightx = 1; // Sets the weight for the X-Padding. Determines the ratio for H/W Padding
        c.weighty = 1; // Sets the weight for the Y-Padding. Determines the ratio for W/H Padding
        c.fill = GridBagConstraints.BOTH; // Ensures both vertical and horizontal resizing
        GridBagLayout g = (GridBagLayout)(getLayout()); // Gets the LayoutManager
        g.setConstraints(comp, c); // Sets the constraints for the component
        add(comp); // Adds the component
    }
}
