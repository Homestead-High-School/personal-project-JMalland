import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;

// Create BoxPanel class, determinant being BoxLayout.X_AXIS and BoxLayout.Y_AXIS

public class Menu extends JPanel {
    public static final int Y_AXIS = BoxLayout.Y_AXIS;
    public static final int X_AXIS = BoxLayout.X_AXIS;
    public static final float CENTER = Box.CENTER_ALIGNMENT;
    private final float defaultPos;
    private final int axis;
    private int width = 0;
    private int height = 0;

    public Menu(int w, int h, float pos, int layout) {
        width = w; // Set the width
        height = h; // Set the height
        defaultPos = pos; // Set the default relative position
        axis = layout; // Set the layout style

        add(Box.createVerticalGlue(), Box.CENTER_ALIGNMENT); // Add the glue to begin with, keeping future components aligned
    }

    public Component add(Component comp) {
        // BoxLayout Styling: https://stackoverflow.com/questions/60422149/vertically-center-content-with-boxlayout
        comp.setPreferredSize(new Dimension(width, height));
        comp.setMaximumSize(comp.getPreferredSize());
        super.add(comp, defaultPos);
        super.add((axis == Menu.Y_AXIS ? Box.createVerticalGlue() : Box.createHorizontalGlue()), Box.CENTER_ALIGNMENT);
        return(comp); // Needs to return something, otherwise method throws an error
    }

    public Component getMenuComponent(int n) {
        ArrayList<Component> list = new ArrayList<Component>();
        for (Component c : getComponents()) { // Loop through each JPanel component
            if (!(c instanceof Box.Filler)) { // Check to see if the component is a Filler
                list.add(c); // Add it to the list if it isn't a Filler
            }
        }
        if (n >= list.size()) { // Throw an exception if I'm stupid enough to hit it
            throw new IllegalArgumentException("Index Out Of Bounds For Component List: "+n);
        }
        return(list.get(n)); // Return the component from the JPanel
    }
}
