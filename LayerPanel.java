import java.util.*;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;


public class LayerPanel extends JPanel {
    HashMap<Integer, HashSet<Component>> map = new HashMap<Integer, HashSet<Component>>();
    
    public LayerPanel() {
        setLayout(null);
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

    public Component[] getComponentsInLayer(int i) {
        return(map.get(i).toArray(new Component[0]));
    }

    @Override
    public Component add(Component c, int zIndex) {
        super.add(c);
        if (!map.containsKey(zIndex)) {
            map.put(zIndex, new HashSet<Component>());
        }
        map.get(zIndex).add(c);
        return(c);
    }
    
    @Override
    public Component add(Component c) {
        return(add(c, 0));
    }

    @Override
    // Showing on hover/interaction: https://stackoverflow.com/questions/7629473/component-layering-java-swing-layers-showing-on-hover
    public void paintComponent(Graphics g) {
        super.paintComponent(g); // Always call. ALWAYS!
        for (int i : map.keySet()) {
            for (Component c : map.get(i)) {
                // I imagine I'm calling the wrong potential paint method, so sometimes things get painted, but not for my custom classes.
                c.paint(g.create(c.getX(), c.getY(), c.getWidth(), c.getHeight()));
            }
        }
    }
}
