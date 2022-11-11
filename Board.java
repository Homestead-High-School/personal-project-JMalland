import java.awt.event.*;
import java.util.HashMap;
import java.util.HashSet;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.util.Random;

class Board extends JFrame {
    /*
    *   Should make either a sidemenu, or selections from the player's hand, where, when selected, it highlights the border in yellow or something.
    */
    public final int SELECTED_HAND = ("HAND").hashCode();
    public final int PLACED_LETTER = ("TILE").hashCode();
    public final int RECALLED_TILES = ("RECALL").hashCode();
    public final int SHUFFLED_TILES = ("SHUFFLE").hashCode();
    public final int DRAW_HAND = ("DRAW").hashCode();
    public final int GAME_RUNNING = ("ON").hashCode();
    private final JFrame frame;
    private JPanel gamePanel = new JPanel();
    private JPanel mainPanel = new JPanel();
    private CurvedButton startButton = new CurvedButton();
    private HashSet<CustomListener> listeners = new HashSet<CustomListener>();
    private HashSet<Tile> placedTiles = new HashSet<Tile>();
    private final double MULT = 1;
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
        for (int i=0; i<list.length; i++) {
            Tile button = getTile(i);
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
        Component[] list = getHand().getTileComponents(); // Stores the list of tiles in the players hand
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
        c.swapText(select.findText()); // Add the text to the inputted tile
        select.swapText(""); // Clear the text from the selected tile
        // COULD INSTEAD GREY OUT THE TILE
        select.setPointingTo(c); // Set the hand tile pointing to the placed tile and vice versa
        placedTiles.add(c); // Add the placed tile to the list of tiles
        selectTile(select); // Clear the tile selection
        dispatchEvent(new CustomEvent(c, PLACED_LETTER));
    }

    // Recalls all tiles placed on the board, starting the play over
    public void recallTiles() {
        for (Tile p : placedTiles) { // Loop through each Tile placed on the board
            Tile t = p.getPointingTo(); // Store the hand tile
            System.out.println("A: "+t.findText()+", "+t.getOriginal()+", "+t.getPoint());
            System.out.println("B: "+p.findText()+", "+p.getOriginal()+", "+p.getPoint());
            p.setText(p.getOriginal()); // Swap the placed tile text with its original
            t.setText(t.getOriginal()); // Swap the hand tile text with its original
        }
        placedTiles.clear(); // Wipe the set of all placed tiles, since they were recalled
        dispatchEvent(new CustomEvent(frame, RECALLED_TILES));
    }

    public void shuffleTiles() {
        // Shuffling: https://stackoverflow.com/questions/1519736/random-shuffling-of-an-array
        Component[] list = getHand().getTileComponents();
        Random rand = new Random();
        for (int i=0; i<list.length-1; i++) {
            Tile a = (Tile) list[i];
            Tile b = (Tile) list[rand.nextInt(list.length-i) + i];
            Tile.swapTiles(a, b);
        }
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
        gamePanel = new JPanel(new GridBagLayout()); // Clears the gamePanel
        //gamePanel.setLayout(new BoxLayout(gamePanel, BoxLayout.Y_AXIS)); // BoxLayout layers the Board and the Player's Hand from top to bottom
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
                temp.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        placeTile(temp);
                    }
                });
                board.add(temp); // Add tile to the grid
            }
        }
        GridBagLayout l = (GridBagLayout) gamePanel.getLayout();
        GridBagConstraints g = createConstraints(1, 1, 0, 0, 1, 1, GridBagConstraints.BOTH);
        l.setConstraints(board, g); // Set the constraints on the board
        gamePanel.add(board); // Create and add the board to the application frame
    }

    // Creates the JPanel that features each player's hand of tiles
    private void createHand() {
        GridBagLayout l = (GridBagLayout) gamePanel.getLayout();

        GridPanel temp = new GridPanel(FRAME_WIDTH, FRAME_HEIGHT, BoxLayout.X_AXIS);
        
        JPanel a = new JPanel(); // Empty JPanel to act as top spacing
        l.setConstraints(a, createConstraints(1, H_Y_OFF/1.0/FRAME_HEIGHT, 0, 1, 1, 1, GridBagConstraints.VERTICAL));
        JPanel b = new JPanel(); // Empty JPanel to act as bottom spacing.
        l.setConstraints(b, createConstraints(1, H_Y_OFF/1.0/FRAME_HEIGHT, 0, 3, 1, 1, GridBagConstraints.VERTICAL));
        
        // JButtons for Shuffle/Recall
        final CurvedButton recall = new CurvedButton("Recall", TILE_RADIUS, new Color(0x036FFC), 100);
        recall.setSize(H_TILE_SIZE/2, H_TILE_SIZE/2); // Sets the size of the Recall button
        recall.setFont(new Font("Serif", Font.PLAIN, FONT_SIZE)); // Sets the font size
        final CurvedButton shuffle = new CurvedButton("Shuffle", TILE_RADIUS, new Color(0xFC6603), 100);
        shuffle.setSize(H_TILE_SIZE/2, H_TILE_SIZE/2); // Sets the size of the Shuffle button
        shuffle.setFont(new Font("Serif", Font.PLAIN, FONT_SIZE)); // Sets the font size

        recall.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispatchEvent(new CustomEvent(recall, RECALLED_TILES)); // Dispatch Recall
                recallTiles(); // Recalls all placed tiles
            }
        });
        shuffle.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispatchEvent(new CustomEvent(shuffle, SHUFFLED_TILES)); // Dispatch Shuffle
                shuffleTiles(); // Shuffles all tiles in the hand
            }
        });
        
        temp.add(recall, 0, 1, 1, 1, GridBagConstraints.BOTH); // Add Recall Button at [0][1]
        temp.add(shuffle, 1, 1, 1, 1, GridBagConstraints.BOTH); // Add Shuffle Button at [1][1]
        
        // JPanels for side padding
        JPanel left = new JPanel(); // Left padding
        left.setSize((ORIGINAL_WIDTH - (7*(H_TILE_SIZE + H_X_OFF) + H_TILE_SIZE/2))/2, H_TILE_SIZE); // Set the size of the left padding
        JPanel right = new JPanel(); // Right padding
        right.setSize(left.getSize()); // Set the size of the right padding

        temp.add(left, 0, 0, 1, 2, GridBagConstraints.BOTH); // Add Left Padding at [0][0]
        temp.add(right, 0, (HAND_LENGTH+1)*2, 1, 2, GridBagConstraints.BOTH); // Add Right Padding at [0][-1]
        
        // Would add the Recall and Shuffle buttons up here, if adding directly to JPanel.
        for (int i=0; i<HAND_LENGTH*2; i++) {
            final Tile tile = new Tile("W", (int)(TILE_RADIUS*1.5), new Color(0xBA7F40), 100, 1, 1); // Create the letter tile
            tile.setFont(new Font("Serif", Font.PLAIN, (int)(FONT_SIZE*1.5))); // Set the font of the tile
            tile.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    selectTile(tile);
                }
            });
            if (i%2 == 1) {
                tile.setSize(H_TILE_SIZE, H_TILE_SIZE); // Gridwidth = 1, gridheight = 2;
                temp.add(tile, 0, i+2, 1, 2, GridBagConstraints.BOTH);
            }
            else {
                JLabel x = new JLabel();
                x.setSize(H_X_OFF, H_TILE_SIZE);
                temp.add(x, 0, i+2, 1, 2, GridBagConstraints.BOTH);
            }
            // Should directly add tiles to the GridBagLayout. PaddedPanel not needed necessarily
        }
        // NEED TO ADD SIDE PADDING!!!
        GridBagConstraints g = createConstraints(1, 0.125, 0, 2, 1, 0, GridBagConstraints.BOTH); // 0.147727 or 0.116666 seem to be the right ratios
        l.setConstraints(temp, g); // Set the constraints on the hand
        gamePanel.add(a); // Add the top layer
        gamePanel.add(temp); // Create and add the hand to the application frame;
        gamePanel.add(b); // Add the bottom layer
    }

    // Creates the JPanel that contains the components which make up the main menu
    private void createMenu() {
        mainPanel = new JPanel(new BorderLayout()); // Clears and sets the layout manager for the mainPanel
        Menu screen = new Menu(MENU_WIDTH, MENU_HEIGHT, Menu.CENTER, Menu.Y_AXIS); // Generate the Main Menu

        startButton = new CurvedButton("Start", 15, Color.yellow, 100); // Creates a default start button
        startButton.setFont(new Font("Serif", Font.PLAIN, 75)); // Sets the font of the Start Button to size 75
        startButton.setEnabled(true);
        startButton.setSize(MENU_WIDTH, MENU_HEIGHT);

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
        selector.setSize(2*MENU_WIDTH/3, MENU_HEIGHT);

        PaddedPanel start = new PaddedPanel(startButton, MENU_WIDTH, FRAME_WIDTH, FRAME_HEIGHT, BoxLayout.X_AXIS); // Creates a panel with padding on the left and right
        PaddedPanel select = new PaddedPanel(selector, MENU_WIDTH/2, FRAME_WIDTH, FRAME_HEIGHT, BoxLayout.X_AXIS); // Creates a panel with padding on the left and right

        System.out.println("Start Padding: "+((double)(MENU_WIDTH) / FRAME_WIDTH));
        System.out.println("Select Padding: "+(MENU_WIDTH / 2.0 / FRAME_WIDTH));

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

    private void dispatchEvent(CustomEvent e) {
        for (CustomListener c : listeners) {
            c.actionPerformed(e);
        }
    }

    // Returns the JPanel for the players hand
    private GridPanel getHand() {
        return((GridPanel)(gamePanel.getComponent(2)));
    }

    // Returns the a tile from the players hand at the given index
    private Tile getTile(int i) {
        if (i < 0) {
            return(new Tile());
        }
        return((Tile)(getHand().getTileComponents()[i]));
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
