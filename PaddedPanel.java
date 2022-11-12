import java.awt.*;
import java.util.ArrayList;

import javax.swing.*;

public class PaddedPanel extends JPanel {
    public static final int X_AXIS = BoxLayout.X_AXIS;
    public static final int Y_AXIS = BoxLayout.Y_AXIS;
    private ArrayList<Component> components = new ArrayList<Component>();
    private int layout = 0;
    private int width = 0;
    private int height = 0;

    // Provides padding for a single component
    public PaddedPanel(Component comp, double side, int width, int height, int layout) {
        this.layout = layout;
        this.width = width;
        this.height = height;
        setLayout(new GridBagLayout());
        addPadding(side, 0, 0);
        addComponent(comp, layout == X_AXIS ? 0 : 1, layout == X_AXIS ? 1 : 0);
        addPadding(side, layout == X_AXIS ? 0 : 2, layout == X_AXIS ? 2 : 0);
    }

    public Component[] getJComponents() {
        return(components.toArray(new Component[0]));
    }

    public Component getJComponent(int i) {
        System.out.println("Requested: "+i+" Returned: "+(2*(i + 1) - 1));
        return(getJComponents()[i]);
    }

    private void addComponent(Component comp, int row, int col) {
        GridBagConstraints c = new GridBagConstraints();
        c.weightx = (double)(layout == X_AXIS ? comp.getSize().width : comp.getSize().height) / width;
        c.weighty = (double)(layout == Y_AXIS ? comp.getSize().height : comp.getSize().width) / height;
        c.gridx = col;
        c.gridy = row;
        c.fill = GridBagConstraints.BOTH;
        GridBagLayout g = (GridBagLayout) getLayout();
        g.setConstraints(comp, c);
        components.add(comp);
        add(comp);
    }

    // Adds padding to a component contained within the panel
    private void addPadding(double padding, int row, int col) {
        // Keeping Components Square: https://stackoverflow.com/questions/48623643/how-to-make-gridbaglayout-specific-size
        // Sizing Components With GridBagLayout: https://stackoverflow.com/questions/4842261/margin-padding-in-gridbaglayout-java
        GridBagConstraints c = new GridBagConstraints(); // Creates constraints for the component
        c.weightx = layout == PaddedPanel.X_AXIS ? padding / width : 1; // Sets the weight for the X-Padding. Determines the ratio for H/W Padding
        c.weighty = layout == PaddedPanel.Y_AXIS ? padding / height : 1; // Sets the weight for the Y-Padding. Determines the ratio for W/H Padding
        c.gridx = col;
        c.gridy = row;
        c.fill = GridBagConstraints.BOTH; // Ensures both vertical and horizontal resizing
        GridBagLayout g = (GridBagLayout)(getLayout()); // Gets the LayoutManager
        JLabel pad = new JLabel();
        g.setConstraints(pad, c); // Sets the constraints for the component
        add(pad); // Adds the component
    }
}
