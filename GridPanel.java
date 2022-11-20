import javax.swing.*;
import java.awt.*;
import java.util.*;

public class GridPanel extends JPanel {
    public int X_AXIS = BoxLayout.X_AXIS; // X_Axis Layout
    public int Y_AXIS = BoxLayout.Y_AXIS; // Y-Axis Layout
    private int width = 0; // Sets the maximum width
    private int height = 0; // Sets the maximum height
    private int layout = 0; // Represents either X/Y Axis layout
    private ArrayList<Component> components = new ArrayList<Component>();
    
    public GridPanel(int width, int height, int layout) {
        this.width = width;
        this.height = height;
        this.layout = layout;
        setLayout(new GridBagLayout());
    }

    public void add(Component c, int row, int col, int w, int h, int fill) {
        if (c instanceof Tile) { // Stores all Tile objects seperately, for easy access
            components.add(c);
        }
        GridBagLayout l = (GridBagLayout) getLayout(); // Store the Layout Manager for quick acess
        double dimension = layout == X_AXIS ? c.getSize().width : c.getSize().height; // Determine the dimension being weighed
        GridBagConstraints g = createConstraints(dimension/width, dimension/height, col, row, w, h, fill); // Create the constraints to the Component
        l.setConstraints(c, g); // Set the constraints of the component
        add(c); // Add the component to the GridPanel
    }

    public Component[] getTileComponents() {
        return(components.toArray(new Component[0])); // Return all Tile components specifically
    }

    private GridBagConstraints createConstraints(double xLbs, double yLbs, int x, int y, int w, int h, int fill) {
        GridBagConstraints g = new GridBagConstraints();
        g.weightx = xLbs;
        g.weighty = yLbs;
        g.gridx = x;
        g.gridy = y;
        g.gridwidth = w;
        g.gridheight = h;
        g.fill = fill;
        return(g);
    }
}
