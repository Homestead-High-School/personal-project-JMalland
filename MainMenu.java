import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import javax.swing.*;

public class MainMenu extends JPanel {
    private int width = 0;
    private int height = 0;
    private JPanel menu = new JPanel();
    private ArrayList<Component> realComponents = new ArrayList<Component>();
    private LinkedHashMap<Component, Float> list = new LinkedHashMap<Component, Float>();
    private final Font font = new Font("Serif", Font.PLAIN, 75);

    public MainMenu(int w, int h) {
        width = w; // Set the width
        height = h; // Set the height

        list.put(Box.createVerticalGlue(), Box.CENTER_ALIGNMENT); // Add one to begin with, to keep the future components in line
        
        CurvedButton start = new CurvedButton("Start", 15, Color.yellow); // Create a rounded button to start the game when pressed
        start.setFont(font); // Set the default font
        start.setPreferredSize(new Dimension(w, h)); // Set the Preferred size
        start.setMaximumSize(start.getPreferredSize()); // Set the Maximum size
        JPanel paddedStart = createPaddedRow(start, w/2); // Make the Start Button appear smaller and padded

        JSlider select = new JSlider(2, 6); // Set the Player Selector, max and min values
        select.setValue(2); // Set the default number of players
        select.setMajorTickSpacing(1); // Make it so each interval is 1
        select.setPaintTicks(true); // Make it so tick marks are painted at each interval
        select.setPreferredSize(new Dimension(w/3 * 2, h)); // Set the Preferred size
        select.setMaximumSize(select.getPreferredSize()); // Set the Maximum size
        JPanel paddedSelector = createPaddedRow(select, w/3); // Make the selector appear smaller and padded
        
        CurvedLabel players = new CurvedLabel("Players:    2"); // Set the default number of players
        players.setColor(Color.black); // Set the text color black
        players.setFont(font); // Set the default font
        players.setPreferredSize(new Dimension(h, h)); // Set the Preferred size
        players.setMaximumSize(players.getPreferredSize()); // Set the Maximum size
        
        addComponent(players, Box.CENTER_ALIGNMENT); // Add the Player Counter to the JPanel
        addComponent(paddedSelector, Box.CENTER_ALIGNMENT); // Add the Player Selector to the JPanel
        addComponent(paddedStart, Box.CENTER_ALIGNMENT); // Add the Start Button to the JPanel

        menu = new JPanel(new GridLayout(list.size(), 1));
        for (Component c : list.keySet()) { // Loops through each component in the map
            menu.add(c, list.get(c)); // Adds the component to the JPanel
        }
        menu.setPreferredSize(new Dimension(width*3 - width/3, height*3)); // Sets the Preferred size
        menu.setMaximumSize(menu.getPreferredSize()); // Sets the Maximum Size

        realComponents.addAll(list.keySet()); // Create a list of the components to be removed of all Fillers
        for (int i=realComponents.size()-1; i>=0; i--) { // Loop through each item of the Menu JPanel
            if (realComponents.get(i) instanceof Box.Filler) { // If the Component is a Filler
                realComponents.remove(i); // Remove the Filler from the list of components
            }
        }
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

    private void addComponent(Component comp, float position) {
        // BoxLayout Styling: https://stackoverflow.com/questions/60422149/vertically-center-content-with-boxlayout
        list.put(comp, position);
        list.put(Box.createVerticalGlue(), Box.CENTER_ALIGNMENT);
    }

    @Override 
    public Component getComponent(int n) {
        if (n > realComponents.size()) { // Throw an exception if I'm stupid enough to hit it
            throw new IllegalArgumentException("Index Out Of Bounds For Component List");
        }

        return(realComponents.get(n)); // Return the component from the JPanel
    }
    
    public JPanel getAsJPanel() {
        return(menu); // Returns the created JPanel
    }
}
