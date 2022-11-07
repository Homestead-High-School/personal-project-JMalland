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
    private final Font font = new Font("Serif", Font.PLAIN, 75);

    public Menu(int w, int h, float pos, int layout) {
        width = w; // Set the width
        height = h; // Set the height
        defaultPos = pos; // Set the default relative position
        axis = layout; // Set the layout style

        add(Box.createVerticalGlue(), Box.CENTER_ALIGNMENT); // Add the glue to begin with, keeping future components aligned

        CurvedButton start = new CurvedButton("Start", 15, Color.yellow); // Create a rounded button to start the game when pressed
        start.setFont(font); // Set the default font
        start.setPreferredSize(new Dimension(w, h)); // Set the Preferred size
        start.setMaximumSize(start.getPreferredSize()); // Set the Maximum size
        JPanel paddedStart = createPaddedRow(start, w/2); // Make the Start Button appear smaller and padded

        JSlider select = new JSlider(2, 6); // Set the Player Selector, max and min values
        select.setValue(2); // Set the default number of players
        select.setMajorTickSpacing(1); // Make it so each interval is 1
        select.setPaintTicks(true); // Make it so tick marks are painted at each interval
        select.setPreferredSize(new Dimension(w, h)); // Set the Preferred size
        select.setMaximumSize(select.getPreferredSize()); // Set the Maximum size
        JPanel paddedSelector = createPaddedRow(select, w/3); // Make the selector appear smaller and padded
        
        CurvedLabel players = new CurvedLabel("Players:    2"); // Set the default number of players
        players.setColor(Color.black); // Set the text color black
        players.setFont(font); // Set the default font
        players.setPreferredSize(new Dimension(h, h)); // Set the Preferred size
        players.setMaximumSize(players.getPreferredSize()); // Set the Maximum size
        
        this.add(players); // Add the Player Counter to the JPanel
        this.add(paddedSelector); // Add the Player Selector to the JPanel
        this.add(paddedStart); // Add the Start Button to the JPanel

        setLayout(new GridLayout(getComponents().length, 1));
        setPreferredSize(new Dimension(width*3 - width/3, height*3)); // Sets the Preferred size
        setMaximumSize(getPreferredSize()); // Sets the Maximum Size
    }

    private JPanel createPaddedRow(Component comp, int padding) {
        JPanel temp = new JPanel(new BorderLayout(padding, 0));//new GridLayout(1, 3));
        JLabel paddedA = new JLabel(); // Empty JLabel to act as left padding 
        paddedA.setSize(new Dimension(0, height)); // Set default size of JLabel
        JLabel paddedB = new JLabel(); // Empty JLabel to act as right padding
        paddedB.setSize(new Dimension(0, height)); // Set default size of JLabel
        temp.add(paddedA, BorderLayout.WEST); // Put the left padding in the JPanel
        temp.add(comp, BorderLayout.CENTER); // Put the component in the center
        temp.add(paddedB, BorderLayout.EAST); // Put the right padding in the JPanel
        return(temp); // Return the newly padded component as a JPanel
    }

    public Component add(Component comp) {
        // BoxLayout Styling: https://stackoverflow.com/questions/60422149/vertically-center-content-with-boxlayout
        super.add(comp, defaultPos);
        super.add((axis == Menu.Y_AXIS ? Box.createVerticalGlue() : Box.createHorizontalGlue()), Box.CENTER_ALIGNMENT);
        return(comp);
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
