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
        GridBagConstraints c = new GridBagConstraints();
        // https://stackoverflow.com/questions/4842261/margin-padding-in-gridbaglayout-java
        int LR = layout == PaddedPanel.X_AXIS ? padding : 0;
        int UD = layout == PaddedPanel.X_AXIS ? 0 : padding;
        c.insets = new Insets(UD, LR, UD, LR); // Insets(T, L, B, R)
        c.weightx = 1;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        GridBagLayout g = (GridBagLayout)(getLayout());
        g.setConstraints(comp, c);
        add(comp);
    }
}
