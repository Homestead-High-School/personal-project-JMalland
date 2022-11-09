import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.awt.*;
class Board extends JFrame {

    /*
    *   Should make either a sidemenu, or selections from the player's hand, where, when selected, it highlights the border in yellow or something.
    */
    private final JFrame frame;
    private JPanel gamePanel = new JPanel();
    private JPanel mainPanel = new JPanel();
    private CurvedButton startButton = new CurvedButton();
    private JPanel handPanel = new JPanel();
    private final double MULT = 1;
    public final int SELECTED_HAND = ("HAND").hashCode();
    public final int PLACED_LETTER = ("TILE").hashCode();
    private final int ROWS = Scrabble.getBoard().length;
    private final int COLS = Scrabble.getBoard()[0].length;
    private final int FONT_SIZE = (int)(26); // Is actually double. Window starts out at half size
    private final int TILE_SIZE = (int)(50*MULT); // 50
    private final int TILE_RADIUS = (int)(26*MULT); // 26 - Is actually double. Window starts out at half size
    private final int HAND_LENGTH = 7;
    private final int H_TILE_SIZE = (int)(TILE_SIZE*1.5);
    private final int H_Y_OFF = 3;
    private final int H_X_OFF = H_TILE_SIZE/8;
    private final int MENU_WIDTH = (int)(300*MULT); // 300
    private final int MENU_HEIGHT = (int)(75*MULT); // 75
    private final int ORIGINAL_WIDTH = (int)(1056*MULT); // 1056
    private final int ORIGINAL_HEIGHT = (int)(1056*MULT); // 1056
    private int FRAME_WIDTH = (int)(528*MULT); // 528
    private int FRAME_HEIGHT = (int)(528*MULT); // 528
    private int player_count = 2;
    private int selected_tile = -1;
    private boolean GameStarted = false;
 
    // The Board() constructor runs its private methods to generate the panels that are contained in the application 
    Board() {
        frame = new JFrame("Scrabble");
        CurvedButton.setFrame(frame); // Set the contentPane of the CurvedButton class
        CurvedLabel.setFrame(frame); // Set the contentPane of the CurvedLabel class
        
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
        }
        
        // Using Layouts With Auto-Adapting Components: https://stackoverflow.com/questions/70523527/how-to-stop-components-adapting-to-the-size-of-a-jpanel-that-is-inside-a-jscroll
        createMenu(); // Generate the Main Menu and add to mainPanel

        createBoard(); // Create the Scrabble Board
        createHand(); // Create the Player's Hand

        mainPanel.setVisible(true); // Set the main menu visible, if not
    
        frame.add(mainPanel);//mainPanel); // Add the main menu to the JFrame
        
        Toolkit.getDefaultToolkit().setDynamicLayout(false); // Ensures window resize keeps the right ratio: https://stackoverflow.com/questions/20925193/using-componentadapter-to-determine-when-frame-resize-is-finished 
        frame.setPreferredSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT));
        frame.setMaximumSize(new Dimension(FRAME_WIDTH*2, FRAME_HEIGHT*2));
        frame.setMinimumSize(frame.getPreferredSize());
        //setDefaultSizes(frame, FRAME_WIDTH, FRAME_HEIGHT); // Set the default sizes for the JFrame
        
        frame.addComponentListener(new ComponentAdapter() { // EventListener for window resizing: https://stackoverflow.com/questions/2303305/window-resize-eventff
            public void componentResized(ComponentEvent componentEvent) { // Method to run every time window is resized
                int width = frame.getWidth(); // Create a temporary width variable, just for simplicity
                int height = frame.getHeight(); // Create a temporary height variable, just for simplicity
                double newSize = (width > FRAME_WIDTH || height > FRAME_HEIGHT) ? Math.min(ORIGINAL_WIDTH, Math.max(width, height)) : Math.min(ORIGINAL_HEIGHT, Math.min(width, height));
                if (width != height) {
                    System.out.println("Resized Window: "+width+" x "+height);
                    frame.setPreferredSize(new Dimension((int) newSize, (int) newSize)); // Update the window size to be smaller or larger
                }
                if (gamePanel.getComponents().length > 1) {
                    JPanel hand = (JPanel)(gamePanel.getComponent(1)); // Store the Hand panel to readjust component sizes
                    for (Component c : hand.getComponents()) { // Loop through each tile and adjust size
                        c.setPreferredSize(new Dimension((int)(H_TILE_SIZE * newSize/ORIGINAL_WIDTH), (int)(H_TILE_SIZE * newSize/ORIGINAL_WIDTH))); // Set the size relative to the window size
                    }
                    hand.setPreferredSize(new Dimension(ORIGINAL_WIDTH, hand.getComponent(0).getHeight() + H_Y_OFF)); // Recalculate Hand dimensions based on window size
                }
                FRAME_WIDTH = frame.getWidth(); // Update the Width property so it is current
                FRAME_HEIGHT = frame.getHeight(); // Update the Height property so it is current
                frame.pack(); // Pack once more, in case the Hand was adjusted
            }
        });

        frame.setVisible(true); // Set the application frame visible
    }
    
    public void startGame() {
        frame.remove(mainPanel); // Remove all current panels to begin the gameplay
        createBoard(); // Recreate the gamePanel
        createHand(); // Recreate the gamePanel
        frame.add(gamePanel); // Add the game panel to display the Scrabble board
        frame.pack(); // Repaint the JFrame
        GameStarted = true; // Set GameStarted status to true
        System.out.println("Game Started: "+player_count+" Players"); // Display the beginning of the game
    }
    
    // Returns the JPanel for the Scrabble board
    public JPanel getBoard() {
        return((JPanel)(gamePanel.getComponent(0)));
    }

    public CurvedButton getStart() {
        return(startButton); // Returns the Start Button
    }

    public int getPlayers() {
        return(player_count); // Returns the number of players
    }

    public JPanel getHandPanel() {
        return(handPanel);
    }

    // Returns whether or not the player has selected a tile
    public boolean tileIsSelected() {
        return(selected_tile > 0);
    }

    // Returns the tile selected by the player
    public JButton getSelectedTile() {
        if (selected_tile < 0) { // Throw an exception if I'm stupid enough to hit it
            throw new IllegalArgumentException("Tile Index Out Of Bounds For Selected Tile");
        }
        JPanel hand = (JPanel)(gamePanel.getComponent(1)); // Store the Hand for access to child components
        return((CurvedButton)(hand.getComponent(selected_tile))); // Convert the tile to CurvedButton and return
    }

    public void setHand(char[] list) {
        JPanel hand = (JPanel)(gamePanel.getComponent(1));
        for (int i=0; i<list.length; i++) {
            CurvedButton button = (CurvedButton)(hand.getComponent(i));
            button.setText(list[i]+"");
        }
    }

    public CurvedButton[] getHand() {
        JPanel whole = (JPanel)(gamePanel.getComponent(1));
        JPanel hand = (JPanel)(whole.getComponent(1));
        System.out.println("Hand: "+hand.getWidth());
        CurvedButton[] list = new CurvedButton[7];
        for (int i=0; i<7; i++) {
            list[i] = (CurvedButton)(hand.getComponent(i));
        }
        return(list);
    }

    // Returns all JButton tiles contained in the Scrabble board panel
    public JButton[][] getTiles() {
        JPanel board = getBoard(); // Gets the board from the Game JPanel
        JButton[][] list = new JButton[ROWS][COLS]; // Creates a 2D Array of JButtons
        for (int i=0; i<ROWS*COLS; i++) { // Loops through each JButton on the board
            list[i/ROWS][i%COLS] = (JButton)(board.getComponent(i)); // Adds the JButton to the array
        }
        return(list); // Returns the array
    }
    
    // Returns whether or not the game has been started
    public boolean gameStarted() {
        return(GameStarted);
    }
 
    // Creates the JPanel which holds each JButton that makes up the Scrabble board 
    private void createBoard() {
        gamePanel = new JPanel(); // Clears the gamePanel
        gamePanel.setLayout(new BoxLayout(gamePanel, BoxLayout.Y_AXIS)); // BoxLayout layers the Board and the Player's Hand from top to bottom
        JPanel board = new JPanel(new GridLayout(ROWS,COLS)); // Main Board panel
        for (int r=0; r<ROWS; r++) {
            for (int c=0; c<COLS; c++) {
                final int rIdx = r;
                final int cIdx = c;
                int tile = Scrabble.getVal(r%ROWS, c%COLS); // Create the tile value to determine the look of each button
                final CurvedButton temp;
                if (tile == 1 || tile == 2) { // Tile is a Letter Tile, represented by a '1' or '2'
                    temp = new CurvedButton((tile == 1 ? '2' : '3') + "x L", TILE_RADIUS, new Color(0x4274FF));
                }
                else if (tile == 3 || tile == 4) { // Tile is a Word Tile, represented by '3' or '4'
                    temp = new CurvedButton((tile == 3 ? '2' : '3') + "x W", TILE_RADIUS, new Color(0xD7381C));
                }
                else {
                    temp = new CurvedButton("", TILE_RADIUS, Color.white); // Blank Tile, represented by '0'
                }
                temp.setFont(new Font("Serif", Font.BOLD, FONT_SIZE)); // Set the font of the tile
                temp.setOpacity(tile%2 == 1 ? 25 : 100); // Set tile opacity
                temp.setSize(TILE_SIZE, TILE_SIZE); // Set tile size
                temp.setYOffset(1.0/3.0);
                temp.setContentAreaFilled(false); // Make it so it doesn't draw the default background
                temp.setMaximumSize(new Dimension(TILE_SIZE, TILE_SIZE)); // Set the maximum size per each tile
                temp.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        if (tileIsSelected()) {
                            frame.dispatchEvent(new MouseEvent(frame, MouseEvent.MOUSE_CLICKED, 0, 0, rIdx, cIdx, PLACED_LETTER, false));
                        }
                    }
                });
                board.add(temp); // Add tile to the grid
            }
        }
        setDefaultSizes(board, COLS*TILE_SIZE, ROWS*TILE_SIZE); // Sets all preferred sizes of the JPanel
        gamePanel.add(board); // Create and add the board to the application frame
    }

    // Creates the JPanel that features each player's hand of tiles
    private void createHand() {
        // Still need to fix Font Sizing
        // FlowLayout Help: https://docs.oracle.com/javase/tutorial/uiswing/layout/flow.html
        final CurvedButton[] list = new CurvedButton[7]; // List of tiles which make up the hand
        handPanel.removeAll();
        handPanel.setLayout(new FlowLayout(FlowLayout.CENTER, H_X_OFF, H_Y_OFF)); // FlowLayout allows spacing and centering, for a single row or column
        frame.dispatchEvent(new FocusEvent(frame, ("HAND").hashCode()));
        for (int i=0; i<7; i++) {
            CurvedButton tile = new CurvedButton("W", (int)(TILE_RADIUS*1.5), new Color(0xBA7F40), 100); // Create the letter tile
            tile.setFont(new Font("Serif", Font.PLAIN, (int)(FONT_SIZE*1.5))); // Set the font of the tile
            tile.setContentAreaFilled(false); // Set it so the default background isn't painted
            final CurvedButton copy = tile; // A Final clone of the tile, so it can be accessed by the EventListener
            tile.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    for (int i=0; i<list.length; i++) { // Loop through each tile to check them individually
                        if (list[i] == copy && i != selected_tile) { // Check the tile to see if it's the selected one
                            list[i].setBorder(Color.orange, 6); // Set the border of the selected tile orange
                            selected_tile = i; // Set the selected tile
                            handPanel.dispatchEvent(new MouseEvent(frame, MouseEvent.MOUSE_CLICKED, 20, 0, 0, 0, SELECTED_HAND, false));
                        }
                        else {
                            list[i].setBorder(Color.black, 2); // Set the border of the other tiles black
                            if (selected_tile == i) { // Checks if the tile was previously selected
                                selected_tile = -1; // Delete the selection
                            }
                        }
                    }
                }
            });
            tile.setYOffset(1.0/3.0); // Reset the height offset of the text, for appearance purposes.
            tile.setPreferredSize(new Dimension(H_TILE_SIZE * FRAME_WIDTH/ORIGINAL_WIDTH, H_TILE_SIZE * FRAME_WIDTH/ORIGINAL_WIDTH)); // Set the size relative to the window size
            handPanel.add(tile); // Add the tile to the FlowLayout Panel
            list[i] = tile; // Add the tile to the list of CurvedButtons
        }
        //frame.setPreferredSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT - (next.getComponent(0).getHeight() - next.getHeight()) + 2*H_Y_OFF)); // Recalculate the frame due to Hand sizing
        //FRAME_HEIGHT = frame.getPreferredSize().height; // Recalculate the height of the frame based on Hand sizing
        setDefaultSizes(handPanel, ORIGINAL_WIDTH, H_TILE_SIZE);
        
        gamePanel.add(handPanel); // Create and add the hand to the application frame;
    }

    // Creates the JPanel that contains the components which make up the main menu
    private void createMenu() {
        mainPanel = new JPanel(new BorderLayout()); // Clears and sets the layout manager for the mainPanel
        Menu screen = new Menu(MENU_WIDTH, MENU_HEIGHT, Menu.CENTER, Menu.Y_AXIS); // Generate the Main Menu

        startButton = new CurvedButton("Start", 15, Color.yellow); // Creates a default start button
        startButton.setFont(new Font("Serif", Font.PLAIN, 75)); // Sets the font of the Start Button to size 75
        startButton.setEnabled(true);

        final JSlider selector = new JSlider(2, 6); // Creates a JSlider between 2 and 6
        selector.setValue(2); // Sets the default player count to 2
        selector.setMajorTickSpacing(1); // Sets the tick spacing to each number
        selector.setPaintTicks(true); // Paints the ticks on the slider

        PaddedPanel start = new PaddedPanel(startButton, MENU_WIDTH/2, BoxLayout.X_AXIS); // Creates a panel with padding on the left and right
        PaddedPanel select = new PaddedPanel(selector, MENU_WIDTH/3, BoxLayout.X_AXIS); // Creates a panel with padding on the left and right

        final CurvedLabel counter = new CurvedLabel("Players:    2"); // Creates the Player Counter display
        counter.setFont(new Font("Serif", Font.PLAIN, 75)); // Sets the font of the Counter to size 75

        screen.add(counter); // Adds the counter to the Menu
        screen.add(select); // Adds the Selector to the Menu
        screen.add(start); // Adds the Start Button to the Menu

        setDefaultSizes(screen, MENU_WIDTH*3 - MENU_WIDTH/2, MENU_HEIGHT*3); // Sets the default sizes of the Menu

        selector.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                player_count = selector.getValue();
                counter.setText("Players:    "+player_count); // Adjust the counter to match the slider's value
            }
        });

        mainPanel.add(screen, BorderLayout.CENTER); // Create and add the Menu to the JPanel
    }

    // Sets the Preferred, Minimum, and Maximum size of a JComponent
    // Should still probably play around with the default starting sizes for FRAME_WIDTH & FRAME_HEIGHT
    private void setDefaultSizes(Component comp, int width, int height) {
        comp.setPreferredSize(new Dimension(width, height));
        comp.setMaximumSize(new Dimension(width*2, height*2));
        comp.setMinimumSize(new Dimension(width/2, height/2));
        comp.setSize(new Dimension(width, height));
    }

    private void testKeyEvent(KeyEvent e) {
        System.out.println("Event: "+e);
        frame.dispatchEvent(e);
    }
}