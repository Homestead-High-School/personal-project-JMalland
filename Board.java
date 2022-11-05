import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.awt.*;
class Board extends JFrame implements ActionListener {

    private JFrame frame;
    private JPanel gamePanel = new JPanel();
    private JPanel mainPanel = new JPanel();
    private final int ROWS = Scrabble.getBoard().length;
    private final int COLS = Scrabble.getBoard()[0].length;
    private final int FONT_SIZE = 26; // Is actually double. Window starts out at half size
    private final int TILE_SIZE = 50;
    private final int TILE_RADIUS = 26; // Is actually double. Window starts out at half size
    private final int HAND_LENGTH = 7;
    private final int MENU_WIDTH = 300;
    private final int MENU_HEIGHT = 75;
    private final int ORIGINAL_WIDTH = 1056;
    private final int ORIGINAL_HEIGHT = 1056;
    private int FRAME_WIDTH = 528;
    private int FRAME_HEIGHT = 528;
    private boolean GameStarted = false;
 
    // The Board() constructor runs its private methods to generate the panels that are contained in the application 
    Board() {
        frame = new JFrame("Scrabble");
        CurvedButton.setFrame(frame);
        CurvedLabel.setFrame(frame);
        
        try {
            // set look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
        }

        // Create the main menu
        
        // https://stackoverflow.com/questions/70523527/how-to-stop-components-adapting-to-the-size-of-a-jpanel-that-is-inside-a-jscroll

        mainPanel.setLayout(new BorderLayout()); // Set the BorderLayout for the main menu
        createMenu(); // Generate the Main Menu and add to mainPanel

        gamePanel.setLayout(new BoxLayout(gamePanel, BoxLayout.Y_AXIS)); // Box Layout layers the Board and the Players Hand from top to bottom
        createBoard(); // Generate the Scrabble Board and add to gamePanel
        createHand(); // Generate the Player's Hand and add to gamePanel
        
        mainPanel.setVisible(true); // Set the main menu visible, if not
        gamePanel.setVisible(true); // Set the board visible, for when it will be added

        frame.add(mainPanel); // Add the main menu to the JFrame
        
        Toolkit.getDefaultToolkit().setDynamicLayout(false); // Ensures window resize keeps the right ratio: https://stackoverflow.com/questions/20925193/using-componentadapter-to-determine-when-frame-resize-is-finished 
        frame.setPreferredSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT)); // Set the Preferred size
        frame.setMaximumSize(new Dimension(ORIGINAL_WIDTH*2, ORIGINAL_HEIGHT*2)); // Sets the Maximum size
        frame.setMinimumSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT)); // Sets the Minimum size

        frame.addComponentListener(new ComponentAdapter() { // EventListener for window resizing: https://stackoverflow.com/questions/2303305/window-resize-eventff
            public void componentResized(ComponentEvent componentEvent) { // Method to run every time window is resized
                int width = frame.getWidth(); // Create a temporary width variable, just for simplicity
                int height = frame.getHeight(); // Create a temporary height variable, just for simplicity
                if (width != height) {
                    System.out.println("Resized Window: "+width+" x "+height);
                    if (width > FRAME_WIDTH || height > FRAME_HEIGHT) { // Frame was resized to larger than previously
                        frame.setPreferredSize(new Dimension(Math.max(width, height), Math.max(width, height))); // Set size to the largest dimension
                    }
                    else { // Frame was resized to smaller than previously
                        frame.setPreferredSize(new Dimension(Math.min(width, height), Math.min(width, height))); // Set size to the smallest dimension
                    }
                }
                FRAME_WIDTH = frame.getWidth(); // Update the Width property so it is current
                FRAME_HEIGHT = frame.getHeight(); // Update the Height property so it is current
                frame.pack(); // Repack the frame to adjust the aspect ratios of each component in it.
            }
        });

        frame.setVisible(true); // Set the application frame visible
    }
    
    // Returns the JPanel for the Scrabble board
    public JPanel getBoard() {
        return((JPanel)(gamePanel.getComponent(0)));
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
        GridLayout grid = new GridLayout(ROWS,COLS); // Main Board layout
        JPanel board = new JPanel(grid); // Main Board panel
        
        // https://stackoverflow.com/questions/29379441/java-set-transparency-on-color-color-without-using-rgbs
        // https://stackoverflow.com/questions/6256483/how-to-set-the-button-color-of-a-jbutton-not-background-color
        for (int r=0; r<ROWS; r++) {
            for (int c=0; c<COLS; c++) {
                int tile = Scrabble.getVal(r%ROWS, c%COLS); // Create the tile value to determine the look of each button
                CurvedButton temp = new CurvedButton("", TILE_RADIUS, Color.white); // Blank Tile, represented by '0'
                if (tile == 1 || tile == 2) { // Tile is a Letter Tile, represented by a '1' or '2'
                    temp = new CurvedButton((tile == 1 ? '2' : '3') + "x L", TILE_RADIUS, new Color(0x4274FF));
                }
                else if (tile == 3 || tile == 4) { // Tile is a Word Tile, represented by '3' or '4'
                    temp = new CurvedButton((tile == 3 ? '2' : '3') + "x W", TILE_RADIUS, new Color(0xD7381C));
                }
                temp.setFont(new Font("Serif", Font.BOLD, FONT_SIZE)); // Set the font of the tile
                temp.setOpacity(tile%2 == 1 ? 25 : 100); // Set tile opacity
                temp.setSize(TILE_SIZE, TILE_SIZE); // Set tile size
                temp.setMaximumSize(new Dimension(TILE_SIZE, TILE_SIZE)); // Set the maximum size per each tile
                board.add(temp); // Add tile to the grid
            }
        }
        setDefaultSizes(board, COLS*TILE_SIZE, ROWS*TILE_SIZE); // Sets all preferred sizes of the JPanel
        gamePanel.add(board); // Create and add the board to the application frame
    }

    // Creates the JPanel that features each player's hand of tiles
    private void createHand() {
        GridLayout grid = new GridLayout(1, 7);
        int padding = (ORIGINAL_WIDTH - HAND_LENGTH*(int)(TILE_SIZE*1.5))/(int)(TILE_SIZE*1.5)/2;
        JPanel hand = new JPanel(grid); // Main Hand Panel
        // Could use BoxLayout & createHorizontalStruts() to pad the Hand Panel
        for (int i=0; i<padding; i++) { // Loop that runs 'padding' number of times
            JLabel tile = new JLabel(); // Creates an empty JLabel
            tile.setSize((int)(TILE_SIZE*1.5), (int)(TILE_SIZE*1.5)); // Sets the size
            hand.add(tile); // Adds it to the Hand panel, as left-padding
        }
        for (int i=0; i<7; i++) {
            CurvedLabel tile = new CurvedLabel("W", TILE_RADIUS, new Color(0xBA7F40));
            tile.setSize((int)(TILE_SIZE*1.5), (int)(TILE_SIZE*1.5)); // Sets the size of the JLabel to the determined size
            tile.setFont(new Font("Serif", Font.PLAIN, (int)(FONT_SIZE*2.5)));
            tile.setBorder(BorderFactory.createLineBorder(Color.black, 1)); // Sets the border of the JLabel to black
            hand.add(tile); // Adds it to the Hand panel, representing a tile held by the player 
        }
        for (int i=0; i<padding; i++) { // Loop that runs 'padding' number of times
            JLabel tile = new JLabel(); // Creates an empty JLabel
            tile.setSize((int)(TILE_SIZE*1.5), (int)(TILE_SIZE*1.5)); // Sets the size
            hand.add(tile); // Adds it to the Hand panel, as right-padding
        }
        setDefaultSizes(hand, ORIGINAL_WIDTH, (int)(TILE_SIZE*1.5)); // Sets all preferred sizes of the JPanel
        gamePanel.add(hand); // Create and add the hand to the application frame;
    }

    // Creates the JPanel that contains the components which make up the main menu
    private void createMenu() {
        MainMenu screen = new MainMenu(MENU_WIDTH, MENU_HEIGHT); // Generate the Main Menu

        CurvedLabel counter = (CurvedLabel)(screen.getComponent(0)); // The Counter that displays the number of players
        JSlider selector = (JSlider)(((JPanel)(screen.getComponent(1))).getComponent(1)); // The JSlider that determines the number of players
        CurvedButton startButton = (CurvedButton)(((JPanel)(screen.getComponent(2))).getComponent(1)); // The button that starts the game when pressed

        selector.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                counter.setText("Players:    "+selector.getValue()); // Adjust the counter to match the slider's value
            }
        });

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.remove(mainPanel); // Remove all current panels to begin the gameplay
                frame.add(gamePanel); // Add the game panel to display the Scrabble board
                frame.pack(); // Repaint the JFrame
                GameStarted = true; // Set GameStarted status to true
                System.out.println("Game Started: "+selector.getValue()+" Players"); // Display the beginning of the game
            }
        });

        mainPanel.add(screen.getAsJPanel(), BorderLayout.CENTER); // Create and add the Menu to the JPanel
    }

    // Sets the Preferred, Minimum, and Maximum size of a JComponent
    private void setDefaultSizes(JComponent comp, int width, int height) {
        comp.setPreferredSize(new Dimension(width, height));
        comp.setMaximumSize(new Dimension(width*2, height*2));
        comp.setMinimumSize(new Dimension(width/2, height/2));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Might be able to have the Board() object with custom ActionListener outside of class?
        if (gameStarted()) {
            System.out.println("The game is running...");
        }
    }
}
