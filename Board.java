import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.awt.*;
class Board extends JFrame implements ActionListener {

    /*
    *   Should make either a sidemenu, or selections from the player's hand, where, when selected, it highlights the border in yellow or something.
    */
    private JFrame frame;
    private JPanel gamePanel = new JPanel();
    private JPanel mainPanel = new JPanel();
    private CurvedButton startButton = new CurvedButton();
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
    private int player_count = 2;
    private int selected_tile = 1;
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
    
        frame.add(mainPanel); // Add the main menu to the JFrame
        
        Toolkit.getDefaultToolkit().setDynamicLayout(false); // Ensures window resize keeps the right ratio: https://stackoverflow.com/questions/20925193/using-componentadapter-to-determine-when-frame-resize-is-finished 
        setDefaultSizes(frame, FRAME_WIDTH, FRAME_HEIGHT); // Set the default sizes for the JFrame
        
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

    public void setHand(char[] list) {
        JPanel whole = (JPanel)(gamePanel.getComponent(1));
        JPanel hand = (JPanel)(whole.getComponent(1));
        for (int i=0; i<list.length; i++) {
            CurvedButton button = (CurvedButton)(hand.getComponent(i));
            button.setText(list[i]+"");
        }
    }

    public CurvedButton[] getHand() {
        JPanel whole = (JPanel)(gamePanel.getComponent(1));
        JPanel hand = (JPanel)(whole.getComponent(1));
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
                temp.setContentAreaFilled(false); // Make it so it doesn't draw the default background
                temp.setMaximumSize(new Dimension(TILE_SIZE, TILE_SIZE)); // Set the maximum size per each tile
                board.add(temp); // Add tile to the grid
            }
        }
        setDefaultSizes(board, COLS*TILE_SIZE, ROWS*TILE_SIZE); // Sets all preferred sizes of the JPanel
        gamePanel.add(board); // Create and add the board to the application frame
    }

    // Creates the JPanel that features each player's hand of tiles
    private void createHand() {
        int padding = (ORIGINAL_WIDTH - (HAND_LENGTH*(int)(TILE_SIZE*1.5)))/4; // Calculates the padding needed for the Player's Hand
        Hand array = new Hand(7, (int)(TILE_SIZE*1.5), (int)(TILE_SIZE*1.5));
        array.setLayout(new GridLayout(1, 7));
        for (int i=0; i<7; i++) {
            final int index = i; // Is final so the action listener can access it
            CurvedButton tile = new CurvedButton("W", (int)(TILE_RADIUS*1.5), new Color(0xBA7F40), 100);
            tile.setFont(new Font("Serif", Font.PLAIN, (int)(FONT_SIZE*2.5))); // Set the font of the tile
            tile.setContentAreaFilled(false); // Set it so the default background isn't painted
            tile.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.out.println("Tile #"+(index+1)+" Selected!");
                    Component[] list = array.getHand();
                    for (int i=0; i<list.length; i++) {
                        if (!(list[i] instanceof CurvedButton)) {
                            continue;
                        }
                        CurvedButton temp = (CurvedButton)(list[i]); // Create a temporary button to store the tile
                        if (i == index && selected_tile != index) { // Check the tile to see if it's the selected one
                            temp.setBorder(Color.orange, 6); // Set the border of the selected tile orange
                        }
                        else {
                            temp.setBorder(Color.black, 2); // Set the border of the other tiles black
                        }
                    }
                    selected_tile = index == selected_tile ? selected_tile+1 : index;
                }
            });
            //array.add(Box.createHorizontalStrut((int)(TILE_SIZE)/6)); // Should act as a spacer for the letters
            array.add(tile); // Adds it to the hand panel, representing the tiles held by the player. 
        }
        // EmptyBorder: https://stackoverflow.com/questions/13547361/how-to-use-margins-and-paddings-with-java-gridlayout
        // Could probably use EmptyBorder with Tile, and adjust the setDefaultSizes() method for array.
        PaddedPanel hand = new PaddedPanel(array, padding, 0, BoxLayout.X_AXIS);
        setDefaultSizes(hand, ORIGINAL_WIDTH, (int)(TILE_SIZE*1.5)); // Sets all preferred sizes of the JPanel
        gamePanel.add(hand); // Create and add the hand to the application frame;
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

        PaddedPanel start = new PaddedPanel(startButton, MENU_WIDTH/2, MENU_HEIGHT, BoxLayout.X_AXIS); // Creates a panel with padding on the left and right
        PaddedPanel select = new PaddedPanel(selector, MENU_WIDTH/3, MENU_HEIGHT, BoxLayout.X_AXIS); // Creates a panel with padding on the left and right

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
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Might be able to have the Board() object with custom ActionListener outside of class?
        if (gameStarted()) {
            System.out.println("The game is running...");
        }
    }
}