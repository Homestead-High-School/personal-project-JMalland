import java.util.*;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;


public class LayerPanel extends JPanel {
    HashMap<Integer, HashSet<Component>> map = new HashMap<Integer, HashSet<Component>>();
    
    public LayerPanel() {
        setLayout(new GridBagLayout());
        //CurvedButton a = new CurvedButton("A", 25, Color.RED, 100);
        //CurvedButton b = new CurvedButton("B", 25, Color.GREEN, 100);
        JButton a = new JButton("TEXT A");
        JButton b = new JButton("TEXT B");
        a.setBounds(0, 0, 100, 100);
        b.setBounds(100 + 15, 0, 100, 100);
        a.setEnabled(true);
        b.setEnabled(true);
        add(a);
        add(b);

        a.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("A Clicked!");
            }
        });
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

    @Override
    public Component[] getComponents() {
        HashSet<Component> max = new HashSet<Component>();
        for (HashSet<Component> c : map.values()) {
            max.addAll(c);
        }
        return(max.toArray(new Component[0]));
    }

    @Override
    public Component add(Component c) {
        if (!map.containsKey(0)) {
            map.put(0, new HashSet<Component>());
        }
        map.get(0).add(c);
        return(c);
    }

    @Override
    public Component add(Component c, int i) {
        if (!map.containsKey(i)) {
            map.put(i, new HashSet<Component>());
        }
        map.get(i).add(c);
        return(c);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (HashSet<Component> s : map.values()) {
            for (Component c : s) {
                JButton t = (JButton) c;
                t.paint(g.create(c.getX(), c.getY(), c.getWidth(), c.getHeight()));
                //t.paintBorder(g.create(c.getX(), c.getY(), c.getWidth(), c.getHeight()));
                //c.paint(g.create(c.getX(), c.getY(), c.getWidth(), c.getHeight()));
            }
        }
    }
}
