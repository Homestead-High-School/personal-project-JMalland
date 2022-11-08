import java.awt.*;
import javax.swing.*;

public class Hand extends JPanel {
    Component[] list;
    int index = 0;
    int width = 0;
    int height = 0;

    public Hand(int amount, int w, int h) {
        list = new Component[amount];
        width = w;
        height = h;
    }

    public Component[] getHand() {
        return(list);
    }

    public Component add(Component comp) {
        comp.setSize(width, height);
        super.add(comp);
        list[index] = comp;
        index ++;
        return(comp);
    }
}
