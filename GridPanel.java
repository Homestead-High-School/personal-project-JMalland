import javax.swing.*;
import java.awt.*;
import java.util.*;

public class GridPanel extends JPanel {
    public int X_AXIS = BoxLayout.X_AXIS;
    public int Y_AXIS = BoxLayout.Y_AXIS;
    private int width = 0;
    private int height = 0;
    private int layout = 0;
    private ArrayList<Component> components = new ArrayList<Component>();
    
    public GridPanel(int width, int height, int layout) {
        this.width = width;
        this.height = height;
        this.layout = layout;
        setLayout(new GridBagLayout());
    }

    public void add(Component c, int row, int col, int w, int h, int fill) {
        if (c instanceof Tile) {
            components.add(c);
        }
        GridBagLayout l = (GridBagLayout) getLayout();
        int dimension = layout == X_AXIS ? c.getSize().width : c.getSize().height;
        GridBagConstraints g = createConstraints(dimension/1.0/width, dimension/1.0/height, col, row, w, h, fill);
        l.setConstraints(c, g);
        add(c);
    }

    public Component[] getTileComponents() {
        return(components.toArray(new Component[0]));
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
