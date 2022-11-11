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
    private double side = 0;
    private double each = 0;
    private int insert_index = -1;
    private int w_offset = 0;
    private int h_offset = 0;

    // Provides padding for a list of components
    public PaddedPanel(Component[] comps, double each, double side, int width, int height, int layout) {
        this.layout = layout;
        this.height = height;
        this.width = width;
        this.each = each;
        this.side = side;
        placeComponents(comps, width, height);
    }

    // Provides padding for a single component
    public PaddedPanel(Component comp, double side, int width, int height, int layout) {
        this.layout = layout;
        this.width = width;
        this.height = height;
        setLayout(new GridBagLayout());
        addPadding(side, 0, 0);
        addComponent(comp, layout == X_AXIS ? 0 : 1, layout == X_AXIS ? 1 : 0, 1, 1);
        addPadding(side, layout == X_AXIS ? 0 : 2, layout == X_AXIS ? 2 : 0);
    }

    public Component[] getJComponents() {
        return(components.toArray(new Component[0]));
    }

    public Component getJComponent(int i) {
        System.out.println("Requested: "+i+" Returned: "+(2*(i + 1) - 1));
        return(getJComponents()[i]);
    }

    public void insertComponent(Component comp, int row, int col, int w, int h) {
        insert_index = layout == X_AXIS ? col : row;
        components.add(insert_index, comp);
        placeComponents(getJComponents(), w, h);
        insert_index = -1;
    }

    private void placeComponents(Component[] list, int w, int h) {
        setLayout(new GridBagLayout());

        int endRow = layout == X_AXIS ? 0 : list.length*2;
        int endCol = layout == X_AXIS ? list.length*2 : 0;

        addPadding(side, 0, 0); // Add side padding
        for (int i=1; i<list.length*2; i++) { // Loop through each component
            int row = layout == X_AXIS ? 0 : i;// + h_offset; // Set the Row location of the component
            int col = layout == Y_AXIS ? 0 : i;// + w_offset; // Set the Col location of the component
            if (i%2 == 0) { // Check if padding needs to be added
                addPadding((layout == X_AXIS ? each : each), row, col); // Add padding to the PaddedPanel
            }
            else { // Add the component when not padding
                if (i/2 == insert_index) {
                    addComponent(list[i/2], row, col, w, h);
                }
                else {
                    addComponent(list[i/2], row, col, 1, 1); // Add the component to the PaddedPanel
                }
            }
        }
        addPadding(side, endRow, endCol); // Add side padding
    }

    private void addComponent(Component comp, int row, int col, int w, int h) {
        GridBagConstraints c = new GridBagConstraints();
        c.weightx = (double)(layout == X_AXIS ? comp.getSize().width : comp.getSize().height) / width;
        c.weighty = (double)(layout == Y_AXIS ? comp.getSize().height : comp.getSize().width) / height;
        c.gridx = col + w_offset;
        c.gridy = row + h_offset;
        c.gridwidth = w;
        c.gridheight = h;
        if (w/2 > 0) {
            w_offset = w/2;
        }
        if (h/2 > 0) {
            System.out.println("Increased H: "+h/2);
            h_offset = h/2;
        }
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
