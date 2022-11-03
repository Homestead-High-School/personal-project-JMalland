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
    private final int TILE_SIZE = 50;
    private final int HAND_LENGTH = 7;
    private final int MENU_WIDTH = 300;
    private final int MENU_HEIGHT = 75;
    private int FRAME_WIDTH = 1040;
    private int FRAME_HEIGHT = 1040;
    private boolean GameStarted = false;
 
    // default constructor
    Board() {
        frame = new JFrame("Scrabble");
        
        try {
            // set look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
        }

        // Create the main menu
        
        // https://stackoverflow.com/questions/70523527/how-to-stop-components-adapting-to-the-size-of-a-jpanel-that-is-inside-a-jscroll
        gamePanel.setLayout(new BoxLayout(gamePanel, BoxLayout.Y_AXIS)); // Box Layout layers the Board and the Players Hand from top to bottom
        gamePanel.add(createBoard()); // Create and add the board to the application frame
        gamePanel.add(createHand()); // Create and add the hand to the application frame;

        mainPanel.setLayout(new BorderLayout()); // Set the BorderLayout for the main menu
        mainPanel.add(createMenu(), BorderLayout.CENTER); // Create and add the Menu to the JPanel
        
        mainPanel.setVisible(true); // Set the main menu visible, if not
        gamePanel.setVisible(true); // Set the board visible, for when it will be added

        frame.add(mainPanel); // Add the main menu to the JFrame
        
        Toolkit.getDefaultToolkit().setDynamicLayout(false); // Ensures window resize keeps the right ratio: https://stackoverflow.com/questions/20925193/using-componentadapter-to-determine-when-frame-resize-is-finished 
        frame.setPreferredSize(new Dimension(FRAME_WIDTH/2, FRAME_HEIGHT/2)); // Set the Preferred size
        frame.setMaximumSize(new Dimension(FRAME_WIDTH*2, FRAME_HEIGHT*2)); // Sets the Maximum size
        frame.setMinimumSize(new Dimension(FRAME_WIDTH/2, FRAME_HEIGHT/2)); // Sets the Minimum size

        frame.addComponentListener(new ComponentAdapter() { // EventListener for window resizing: https://stackoverflow.com/questions/2303305/window-resize-event
            public void componentResized(ComponentEvent componentEvent) { // Kind of works.
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
 
    // Board Creation
    private JPanel createBoard() {
        GridLayout grid = new GridLayout(ROWS,COLS); // Main Board layout
        JPanel board = new JPanel(grid); // Main Board panel
        
        // https://stackoverflow.com/questions/29379441/java-set-transparency-on-color-color-without-using-rgbs
        // https://stackoverflow.com/questions/6256483/how-to-set-the-button-color-of-a-jbutton-not-background-color
        for (int r=0; r<ROWS; r++) {
            for (int c=0; c<COLS; c++) {
                int tile = Scrabble.getVal(r%ROWS, c%COLS); // Create the tile value to determine the look of each button
                JButton temp = createButton("", new Color(0xFFFFFF), tile); // Blank Tile, represented by a '0'
                if (tile == 1 || tile == 2) { // Tile is a Letter Tile, represented by a '1' or '2'
                    temp = createButton((tile == 1 ? '2' : '3') + "x L", new Color(0x4274FF), tile);
                }
                else if (tile == 3 || tile == 4) { // Tile is a Word Tile, represented by '3' or '4'
                    temp = createButton((tile == 3 ? '2' : '3') + "x W", new Color(0xD7381C), tile);
                }
                // https://stackoverflow.com/questions/33954698/jbutton-change-default-borderhttps://stackoverflow.com/questions/33954698/jbutton-change-default-border
                // Maybe I should make the borders appear curved?
                temp.setBorder(BorderFactory.createLineBorder(Color.black, 1)); // Create each tile with a black border
                temp.setSize(TILE_SIZE, TILE_SIZE); // Set tile size
                temp.setMaximumSize(new Dimension(TILE_SIZE, TILE_SIZE));
                board.add(temp); // Add tile to the grid
            }
        }
        setDefaultSizes(board, COLS*TILE_SIZE, ROWS*TILE_SIZE); // Sets all preferred sizes of the JPanel
        return(board);
    }

    private JPanel createHand() {
        GridLayout grid = new GridLayout(1,7); // Main Hand layout
        int padding = (FRAME_WIDTH - HAND_LENGTH*(int)(TILE_SIZE*1.5))/(int)(TILE_SIZE*1.5)/2;
        JPanel hand = new JPanel(grid); // Main Hand Panel
        for (int i=0; i<padding; i++) { // Loop that runs 'padding' number of times
            JLabel tile = new JLabel(); // Creates an empty JLabel
            tile.setSize((int)(TILE_SIZE*1.5), (int)(TILE_SIZE*1.5)); // Sets the size
            hand.add(tile); // Adds it to the Hand panel, as left-padding
        }
        for (int i=0; i<7; i++) {
            JLabel tile = new JLabel("W", SwingConstants.CENTER) {
                @Override // Paints a custom color
                public void paintComponent(Graphics g) {
                    g.setColor(new Color(0xBA7F40));
                    g.fillRect(0, 0, getSize().width, getSize().height);
                    super.paintComponent(g);
                }
            };
            tile.setSize((int)(TILE_SIZE*1.5), (int)(TILE_SIZE*1.5)); // Sets the size of the JLabel to the determined size
            tile.setBorder(BorderFactory.createLineBorder(Color.black, 1)); // Sets the border of the JLabel to black
            hand.add(tile); // Adds it to the Hand panel, representing a tile held by the player 
        }
        for (int i=0; i<padding; i++) { // Loop that runs 'padding' number of times
            JLabel tile = new JLabel(); // Creates an empty JLabel
            tile.setSize((int)(TILE_SIZE*1.5), (int)(TILE_SIZE*1.5)); // Sets the size
            hand.add(tile); // Adds it to the Hand panel, as right-padding
        }
        setDefaultSizes(hand, FRAME_WIDTH, (int)(TILE_SIZE*1.5)); // Sets all preferred sizes of the JPanel
        return(hand);
    }

    private JPanel createMenu() {
        JPanel menu = new JPanel(); // Reset the Menu JPanel, just cause
        JPanel players = new JPanel(new GridLayout(2, 1)); // Default JPanel to store the Player Slider
        JPanel start = new JPanel(new GridLayout(3, 1)); // Default JPanel to store the Start Button

        // JButton for Options... etc ???
        final JButton startButton = createButton("Start", new Color(0xFFBB00), 2); // Button to confirm starting the game
        final JSlider pSlider = new JSlider(2, 6); // A Slider to select the number of players, max should be four
        final JLabel numPlayers = new JLabel("2", SwingConstants.CENTER); // Create a JLabel to display the number of Players

        menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS)); // Set the LayoutManager for the Start Menu

        setDefaultSizes(numPlayers, MENU_HEIGHT, MENU_HEIGHT); // Set preferred sizes for the Player Counter
        setDefaultSizes(pSlider, MENU_WIDTH, MENU_HEIGHT); // Set the preferred sizes for the Player Slider
        setDefaultSizes(startButton, MENU_WIDTH, MENU_HEIGHT); // Set the preferred sizes for the Start Button

        // Should have Tick-Marks on the slider, if possible
        pSlider.setValue(2); // Set the default value of the Player Slider
        pSlider.addChangeListener(new ChangeListener() { // EventListener to check when Player Slider gets changed
            @Override
            public void stateChanged(ChangeEvent e) {
                numPlayers.setText(pSlider.getValue()+""); // Set the Player Counter to display the updated JSlider value
            }
        });

        // Action Listener: https://stackoverflow.com/questions/22580243/get-position-of-the-button-on-gridlayout
        startButton.setEnabled(true);
        startButton.addActionListener(new ActionListener() { // EventListener to check when Start Button gets clicked
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.remove(mainPanel); // Remove the start menu from the window's view
                frame.add(gamePanel); // Add the game board to the window
                frame.pack(); // Repack the JFrame to keep the graphics up to speed
                int players = pSlider.getValue(); // Number of players in the game
                GameStarted = true; // Mark that the game has started
                System.out.println("Starting the game with "+players+" players"); 
                // Put Code Below For How Game Will Be Running
                // Maybe Make A Recursive Game-Playing Method, Or Have One In Scrabble.Java
                // Could Also Have A ScrabbleMain.Java That Runs?
            }
        }); 

        players.add(numPlayers); // Add the Player Counter to the JPanel, to be stored in a separate Menu Panel
        players.add(pSlider); // Add the Player Slider to the JPanel, to be stored in a separate Menu Panel
        
        start.add(Box.createVerticalStrut(MENU_HEIGHT)); // Add the blank label to the Start Panel, just so the vertical placement is even between components
        start.add(startButton); // Add the Start Button to the JPanel, to be stored in a seperate Menu Panel
        
        menu.add(Box.createVerticalGlue()); // Vertically center using Glue() --> https://stackoverflow.com/questions/60422149/vertically-center-content-with-boxlayout
        menu.add(players, Box.CENTER_ALIGNMENT); // Add the Player JPanel to the Menu
        menu.add(start, Box.CENTER_ALIGNMENT); // Add the Start JPanel to the Menu
        return(menu);
    }

    private void setDefaultSizes(JComponent comp, int width, int height) {
        comp.setPreferredSize(new Dimension(width, height));
        comp.setMaximumSize(new Dimension(width*2, height*2));
        comp.setMinimumSize(new Dimension(width/2, height/2));
    }

    private JButton createButton(final String text, final Color color, final int tile) {
        JButton temp = new JButton(text) {
            @Override
            public void paintComponent(Graphics g) {
                g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), tile%2 == 1 ? 25 : 100)); // 75% Opacity, if tile is 1 or 3
                g.fillRect(0, 0, getSize().width, getSize().height);
                super.paintComponent(g);
            }
        };
        temp.setContentAreaFilled(false); // Change how the JButton paints the borders, so I can paint the border below
        return(temp);
    }

    public JButton[][] getBoard() {
        JPanel board = (JPanel)(gamePanel.getComponent(0));
        JButton[][] list = new JButton[ROWS][COLS]; // Creates a 2D Array of JButtons
        for (int i=0; i<ROWS*COLS; i++) { // Loops through each JButton on the board
            list[i/ROWS][i%COLS] = (JButton)(board.getComponent(i)); // Adds the JButton to the array
        }
        return(list); // Returns the array
    }
    
    public boolean gameStarted() {
        return(GameStarted);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Might be able to have the Board() object with custom ActionListener outside of class?
        if (gameStarted()) {
            System.out.println("The game is running...");
        }
    }
}
