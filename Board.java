import java.awt.event.*;
import java.util.HashMap;
import java.util.HashSet;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
class Board extends JFrame {
    /*
    *   Should make either a sidemenu, or selections from the player's hand, where, when selected, it highlights the border in yellow or something.
    */
    public final int SELECTED_HAND = ("HAND").hashCode();
    public final int PLACED_LETTER = ("TILE").hashCode();
    public final int DRAW_HAND = ("DRAW").hashCode();
    public final int GAME_RUNNING = ("ON").hashCode();
    private final JFrame frame;
    private JPanel gamePanel = new JPanel();
    private JPanel mainPanel = new JPanel();
    private CurvedButton startButton = new CurvedButton();
    private HashSet<CustomListener> listeners = new HashSet<CustomListener>();
    private HashSet<Tile> placedTiles = new HashSet<Tile>();
    private final double MULT = 0.5;
    private final int ROWS = Scrabble.getBoard().length;
    private final int COLS = Scrabble.getBoard()[0].length;
    private final int FONT_SIZE = (int)(26*MULT); // 26 Is actually double. Window starts out at half size
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
                FRAME_WIDTH = frame.getWidth(); // Update the Width property so it is current
                FRAME_HEIGHT = frame.getHeight(); // Update the Height property so it is current
                if (gamePanel.getComponents().length > 1) {
                    JPanel hand = (JPanel)(gamePanel.getComponent(1)); // Store the Hand panel to readjust component sizes
                    for (Component tile : hand.getComponents()) { // Loop through each tile and adjust size
                        tile.setPreferredSize(new Dimension(H_TILE_SIZE * FRAME_WIDTH/ORIGINAL_WIDTH, H_TILE_SIZE * FRAME_WIDTH/ORIGINAL_WIDTH)); // Set the size relative to the window size
                    }
                    hand.setPreferredSize(new Dimension(ORIGINAL_WIDTH, hand.getComponent(0).getHeight() + H_Y_OFF)); // Recalculate Hand dimensions based on window size
                }
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
        dispatchEvent(new CustomEvent(frame, DRAW_HAND));
    }

    // Should be remade
    // Returns whether or not the game has been started
    public boolean gameStarted() {
        return(GameStarted);
    }
    
    // Should be remade
    public CurvedButton getStart() {
        return(startButton); // Returns the Start Button
    }

    // Should be remade
    public int getPlayers() {
        return(player_count); // Returns the number of players
    }

    // Sets the letters on each tile within the players hand
    public void setHand(char[] list) {
        JPanel hand = (JPanel)(gamePanel.getComponent(1));
        for (int i=0; i<list.length; i++) {
            Tile button = (Tile)(hand.getComponent(i));
            button.setText(list[i]+"");
            button.setValue(Scrabble.getLetterValue(list[i]));
        }
    }

    // Highlights the selected tile within the players hand
    public void selectTile(Tile c) {
        if (!(c instanceof Tile)) { // Checks to see if the tile is a valid selection
            return; // Return, if tile is not valid
        }
        getTile(selected_tile).setBorder(Color.black, 2);
        Component[] list = getHand().getComponents(); // Stores the list of tiles in the players hand
        for (int i=0; i<list.length; i++) {
            Tile t = (Tile) list[i];
            if (list[i] == (Component) c) {
                if (selected_tile == i || t.findText().equals("")) { // Checks if the selected tile was previously selected
                    selected_tile = -1; // Deselects the selected tile
                }
                else {
                    t.setBorder(Color.orange, 6); // Set tile border to orange
                    selected_tile = i; // Sets the selected tile
                }
            }
        }
    }

    // Places the letter from the selected tile within the players hand
    // May want to dispatch a CustomEvent containing the coords/letter placed to Main.java
    public void placeTile(Tile c) {
        if (selected_tile < 0 || !(c instanceof Tile) || c == getTile(selected_tile)) { // Check to see if the tile can be placed
            return; // Return, if no tile is selected
        }
        Tile select = getTile(selected_tile);
        c.setText(select.findText()); // Add the text to the inputted tile
        select.setText(""); // Clear the text from the selected tile
        // COULD INSTEAD GREY OUT THE TILE
        select.setPointingTo(c); // Set the hand tile pointing to the placed tile
        c.setPointingTo(select); // Set the placed tile pointing to the hand tile
        placedTiles.add(c); // Add the placed tile to the list of tiles
        selectTile(select); // Clear the tile selection
    }

    // Recalls all tiles placed on the board, starting the play over
    public void recallTiles() {
        for (Tile p : placedTiles) { // Loop through each Tile placed on the board
            Tile t = p.getPointingTo(); // Store the hand tile
            p.setText(p.getOriginalText()); // Swap the placed tile text with its original
            t.setText(t.getOriginalText()); // Swap the hand tile text with its original
        }
        placedTiles.clear(); // Wipe the set of all placed tiles, since they were recalled
    }

    // Creates a HashMap of the Point/Character locations of placed tiles on the board
    public HashMap<Point, Character> getTilesPlaced() {
        HashMap<Point, Character> map = new HashMap<Point, Character>(); // Creates empty HashMap to store Point/Character placements
        for (Tile p : placedTiles) { // Loop through each Tile placed
            map.put(p.getPoint(), p.findText().charAt(0)); // Add the position and text of the tile to the map
        }
        return(map); // Return the newly created HashMap
    }

    // Creates the JPanel which holds each JButton that makes up the Scrabble board 
    private void createBoard() {
        gamePanel = new JPanel(); // Clears the gamePanel
        gamePanel.setLayout(new BoxLayout(gamePanel, BoxLayout.Y_AXIS)); // BoxLayout layers the Board and the Player's Hand from top to bottom
        JPanel board = new JPanel(new GridLayout(ROWS,COLS)); // Main Board panel
        for (int r=0; r<ROWS; r++) {
            for (int c=0; c<COLS; c++) {
                int tile = Scrabble.getVal(r%ROWS, c%COLS); // Create the tile value to determine the look of each button
                final Tile temp; // I hate how ugly this line looks
                if (tile == 1 || tile == 2) { // Tile is a Letter Tile, represented by a '1' or '2'
                    temp = new Tile((tile == 1 ? '2' : '3') + "x L", TILE_RADIUS, new Color(0x4274FF), tile%2 == 1 ? 25 : 100, tile, 1);
                }
                else if (tile == 3 || tile == 4) { // Tile is a Word Tile, represented by '3' or '4'
                    temp = new Tile((tile == 3 ? '2' : '3') + "x W", TILE_RADIUS, new Color(0xD7381C), tile%2 == 1 ? 25 : 100, 1, tile);
                }
                else { // Blank Tile, represented by '0'
                    temp = new Tile("", TILE_RADIUS, Color.white, 100, 1, 1);
                }
                temp.setFont(new Font("Serif", Font.BOLD, FONT_SIZE)); // Set the font of the tile
                temp.setPoint(new Point(r, c)); // Sets the [row][col] Point the tile is placed at
                temp.setContentAreaFilled(false); // Make it so it doesn't draw the default background
                temp.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        placeTile(temp);
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
        // FlowLayout Help: https://docs.oracle.com/javase/tutorial/uiswing/layout/flow.html
        JPanel hand = new JPanel(new FlowLayout(FlowLayout.CENTER, H_X_OFF, H_Y_OFF)); // FlowLayout allows spacing and centering, for a single row or column
        frame.dispatchEvent(new FocusEvent(frame, ("HAND").hashCode()));
        for (int i=0; i<HAND_LENGTH; i++) {
            final int index = i;
            Tile tile = new Tile("W", (int)(TILE_RADIUS*1.5), new Color(0xBA7F40), 100, 1, 1); // Create the letter tile
            tile.setFont(new Font("Serif", Font.PLAIN, (int)(FONT_SIZE*1.5))); // Set the font of the tile
            tile.setContentAreaFilled(false); // Set it so the default background isn't painted
            tile.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (index == 6) {
                        recallTiles();
                    }
                    else {
                        selectTile(tile);
                    }
                }
            });
            tile.setPreferredSize(new Dimension(H_TILE_SIZE * FRAME_WIDTH/ORIGINAL_WIDTH, H_TILE_SIZE * FRAME_WIDTH/ORIGINAL_WIDTH)); // Set the size relative to the window size
            hand.add(tile); // Add the tile to the FlowLayout Panel
        }
        setDefaultSizes(hand, ORIGINAL_WIDTH, H_TILE_SIZE); // Set the default sizes
        gamePanel.add(hand); // Create and add the hand to the application frame;
    }

    // Creates the JPanel that contains the components which make up the main menu
    private void createMenu() {
        mainPanel = new JPanel(new BorderLayout()); // Clears and sets the layout manager for the mainPanel
        Menu screen = new Menu(MENU_WIDTH, MENU_HEIGHT, Menu.CENTER, Menu.Y_AXIS); // Generate the Main Menu

        startButton = new CurvedButton("Start", 15, Color.yellow, 100); // Creates a default start button
        startButton.setFont(new Font("Serif", Font.PLAIN, 75)); // Sets the font of the Start Button to size 75
        startButton.setEnabled(true);

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispatchEvent(new CustomEvent(startButton, GAME_RUNNING));
            }
        });

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

    private void dispatchEvent(CustomEvent e) {
        for (CustomListener c : listeners) {
            c.actionPerformed(e);
        }
    }

    // Returns the JPanel for the players hand
    private JPanel getHand() {
        return((JPanel)(gamePanel.getComponent(1)));
    }

    // Returns the a tile from the players hand at the given index
    private Tile getTile(int i) {
        if (i < 0) {
            return(new Tile());
        }
        return((Tile)(getHand().getComponent(i)));
    }

    // Sets the Preferred, Minimum, and Maximum size of a JComponent
    // Should still probably play around with the default starting sizes for FRAME_WIDTH & FRAME_HEIGHT
    private void setDefaultSizes(Component comp, int width, int height) {
        comp.setPreferredSize(new Dimension(width, height));
        comp.setMaximumSize(new Dimension(width*2, height*2));
        comp.setMinimumSize(new Dimension(width/2, height/2));
        comp.setSize(new Dimension(width, height));
    }

    // Adding Listeners To JFrame: https://stackoverflow.com/questions/18165800/how-to-add-actionlistener-to-jframe-without-using-buttons-and-panels
    public void addCustomListener(CustomListener c) {
        listeners.add(c);
    }
}
