import java.awt.*;
import java.util.ArrayList;

import javax.swing.*;

public class PaddedPanel extends JPanel {
    public static final int X_AXIS = BoxLayout.X_AXIS;
    public static final int Y_AXIS = BoxLayout.Y_AXIS;
    private ArrayList<Component> components = new ArrayList<Component>();
    private int layout = 0;
    private int width = 0;

    // Provides padding for a list of components
    public PaddedPanel(Component[] comps, double each, double side, int width, int layout) {
        this.layout = layout;
        this.width = width;
        setLayout(new GridBagLayout());
        addPadding(side, 0);
        for (int i=1; i<comps.length*2; i++) {
            if (i%2 == 0) {
                addPadding(each, i);
            }
            else {
                addComponent(comps[i/2], i);
            }
        }
        addPadding(side, comps.length*2);
    }

    // Provides padding for a single component
    public PaddedPanel(Component comp, double side, int width, int layout) {
        this.layout = layout;
        this.width = width;
        setLayout(new GridBagLayout());
        addPadding(side, 0);
        addComponent(comp, 1);
        addPadding(side, 2);
    }

    public Component[] getJComponents() {
        return(components.toArray(new Component[0]));
    }

    public Component getJComponent(int i) {
        System.out.println("Requested: "+i+" Returned: "+(2*(i + 1) - 1));
        return(getComponents()[2*(i + 1) - 1]);
    }

    public void addComponent(Component comp) {
        addComponent(comp, GridBagConstraints.RELATIVE);
    }

    private void addComponent(Component comp, int coord) {
        GridBagConstraints c = new GridBagConstraints();
        c.weightx = 1;
        c.weighty = 1;
        c.gridx = layout == X_AXIS ? coord : 0;
        c.gridy = layout == Y_AXIS ? coord : 0;
        c.fill = GridBagConstraints.BOTH;
        GridBagLayout g = (GridBagLayout) getLayout();
        g.setConstraints(comp, c);
        components.add(comp);
        add(comp);
    }

    // Adds padding to a component contained within the panel
    private void addPadding(double padding, int coord) {
        // Keeping Components Square: https://stackoverflow.com/questions/48623643/how-to-make-gridbaglayout-specific-size
        // Sizing Components With GridBagLayout: https://stackoverflow.com/questions/4842261/margin-padding-in-gridbaglayout-java
        GridBagConstraints c = new GridBagConstraints(); // Creates constraints for the component
        c.weightx = layout == PaddedPanel.X_AXIS ? padding / width : 1; // Sets the weight for the X-Padding. Determines the ratio for H/W Padding
        c.weighty = layout == PaddedPanel.Y_AXIS ? padding / width : 1; // Sets the weight for the Y-Padding. Determines the ratio for W/H Padding
        c.gridx = layout == X_AXIS ? coord : 0;
        c.gridy = layout == Y_AXIS ? coord : 0;
        c.fill = GridBagConstraints.BOTH; // Ensures both vertical and horizontal resizing
        GridBagLayout g = (GridBagLayout)(getLayout()); // Gets the LayoutManager
        JLabel pad = new JLabel();
        g.setConstraints(pad, c); // Sets the constraints for the component
        add(pad); // Adds the component
    }
}
