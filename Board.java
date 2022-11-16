import java.awt.event.*;
import java.util.ArrayList;
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
    // Actual ratios are more towards 19/15;
    private final double widthRatio = 15.0; // Width has a ratio of 3
    private final double heightRatio = 20.0; // Height has a ratio of 4
    public final int SELECTED_HAND = ("HAND").hashCode();
    public final int SELECTED_LETTER = ("SELECT").hashCode();
    public final int PLACING_LETTER = ("PLACING").hashCode();
    public final int PLACED_LETTER = ("TILE").hashCode();
    public final int RECALLED_TILE = ("RECALL").hashCode();
    public final int RECALLED_ALL = ("ALL").hashCode();
    public final int SHUFFLED_TILES = ("SHUFFLE").hashCode();
    public final int FINALIZED_PLAY = ("SUBMIT").hashCode();
    public final int DRAW_HAND = ("DRAW").hashCode();
    public final int GAME_RUNNING = ("ON").hashCode();
    private final JFrame frame;
    private JPanel gamePanel = new JPanel();
    private JPanel mainPanel = new JPanel();
    private GridPanel board = new GridPanel(1, 1, BoxLayout.X_AXIS);
    private HashSet<CustomListener> listeners = new HashSet<CustomListener>();
    private HashSet<Tile> placedTiles = new HashSet<Tile>();
    private final double MULT = 0.8; // At Mult of 0.8, Aspect Ratio appears odd at <= 630 X 840
    private final int ROWS = Scrabble.getBoard().length;
    private final int COLS = Scrabble.getBoard()[0].length;
    private final int FONT_SIZE = (int)(26*MULT); // 26 Is actually double. Window starts out at half size
    private final int TILE_SIZE = (int)(45*MULT); // 50
    private final int TILE_RADIUS = (int)(18*MULT); // 26 - Is actually double. Window starts out at half size
    private final int SB_HEIGHT = (int)(TILE_SIZE*2.5);
    private final int HAND_LENGTH = 7;
    private final int H_TILE_SIZE = (int)(65 * MULT); // 675 - (8*65 + 8*(65/8) + 50)) --> 20px padding
    private final int H_Y_OFF = 3;
    private final int H_X_OFF = H_TILE_SIZE/8;
    private final int MENU_WIDTH = (int)(300*MULT); // 300
    private final int MENU_HEIGHT = (int)(75*MULT); // 75
    private final int MIN_WIDTH = (int)(MULT*675); // (75*8 + 50) = 650
    private final int MIN_HEIGHT = (int)(MULT*900); // (750 + 75 | 125) = 825 ~ 950
    private final int MAX_WIDTH = (int)(900*MULT); // If exceeds 843, duplicate tiles appear on bottom row
    private final int MAX_HEIGHT = (int)(MULT*1200); // If exceeds 1012, duplicate tiles appear on bottom row
    private int FRAME_WIDTH = MIN_WIDTH; // 528
    private int FRAME_HEIGHT = MIN_HEIGHT; // 528
    private int player_count = 2;
    private int selected_tile = -1;
    private ArrayList<Integer> widths = new ArrayList<Integer>();
    private ArrayList<Integer> heights = new ArrayList<Integer>();
 
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
        createScoreboard(); // Generate the Scoreboard displayed in-game
        createBoard(); // Create the Scrabble Board
        createError();
        createHand(); // Create the Player's Hand

        mainPanel.setVisible(true); // Set the main menu visible, if not
    
        frame.add(mainPanel);//mainPanel); // Add the main menu to the JFrame

        for (int i=MIN_WIDTH; i<=MAX_WIDTH; i+=(int) widthRatio) {
            widths.add(i);
            heights.add((int)(i * (heightRatio/widthRatio)));
        }

        /*for (int i=MIN_HEIGHT; i<=MAX_HEIGHT; i+=(int) heightRatio) {
            heights.add(i);
        }*/

        Toolkit.getDefaultToolkit().setDynamicLayout(false); // Ensures window resize keeps the right ratio: https://stackoverflow.com/questions/20925193/using-componentadapter-to-determine-when-frame-resize-is-finished 
        frame.setSize(new Dimension(MIN_WIDTH, MIN_HEIGHT)); // Sets the default dimensions
        frame.addComponentListener(new ComponentAdapter() {
            // EventListener for window resizing: https://stackoverflow.com/questions/2303305/window-resize-eventff
            public void componentResized(ComponentEvent componentEvent) { // Method to run every time window is resized
                int width = frame.getWidth();
                int height = frame.getHeight();
                if ((int) (width%widthRatio) == 0 && (int) (height%heightRatio) == 0) {
                    
                }
                else if (Math.abs(FRAME_WIDTH - width) > Math.abs(FRAME_HEIGHT - height)) {
                    int w = getClosestIndex(width, widths);
                    int h = getClosestIndex((int) (FRAME_HEIGHT * (width / 1.0 / FRAME_WIDTH)), heights);
                    if (width < MIN_WIDTH || width > MAX_WIDTH) {
                        w = width > MAX_WIDTH ? widths.size()-1 : 0;
                        h = width > MAX_WIDTH ? heights.size()-1 : 0;
                    }
                    frame.setPreferredSize(new Dimension(widths.get(w), heights.get(h)));
                }
                else if (Math.abs(FRAME_WIDTH - width) < Math.abs(FRAME_HEIGHT - height)) {
                    int h = getClosestIndex(height, heights);
                    int w = getClosestIndex((int) (FRAME_WIDTH * (height / 1.0 / FRAME_HEIGHT)), widths);
                    if (height < MIN_HEIGHT || height > MAX_HEIGHT) {
                        h = width > MAX_HEIGHT ? heights.size()-1 : 0;
                        w = height > MAX_HEIGHT ? widths.size()-1 : 0;
                    }
                    frame.setPreferredSize(new Dimension(widths.get(w), heights.get(h)));
                }
                GridPanel error = getError();
                error.setBounds(error.getX(), error.getY(), (int)(error.getSize().width * (frame.getWidth() / 1.0 / FRAME_WIDTH)), (int)(error.getSize().height * (frame.getHeight() / 1.0 / FRAME_HEIGHT)));
                CurvedButton text = (CurvedButton) error.getComponent(0);
                if (frame.getWidth() < 570) {
                    text.setYOffset(-0.375);
                }
                else {
                    text.setYOffset(0.375);
                }
                
                FRAME_WIDTH = frame.getWidth(); // Update the Width property so it is current
                FRAME_HEIGHT = frame.getHeight(); // Update the Height property so it is current
                frame.pack(); // Pack once more, in case the Hand was adjusted
                System.out.println("Window Resized: "+frame.getWidth()+" x "+frame.getHeight());
            }
        });

        frame.setVisible(true); // Set the application frame visible
    }

    private int getClosestIndex(int n, ArrayList<Integer> list) {
        int diff = Math.abs(list.get(0) - n);
        int index = 0;
        for (int i=0; i<list.size(); i++) {
            if (Math.abs(list.get(i) - n) < diff) {
                diff = Math.abs(list.get(i) - n);
                index = i;
            }
            else if (Math.abs(list.get(i) - n) > diff) {
                return(index);
            }
        }
        return(index);
    }
    
    // Starts the game, and switches from mainPanel to gamePanel
    public void startGame() {
        frame.remove(mainPanel); // Remove all current panels to begin the gameplay
        createBoard(); // Recreate the gamePanel
        createHand(); // Recreate the gamePanel
        frame.add(gamePanel); // Add the game panel to display the Scrabble board
        frame.pack(); // Repaint the JFrame
        System.out.println("Game Started: "+player_count+" Players"); // Display the beginning of the game
        dispatchEvent(new CustomEvent(frame, DRAW_HAND));
    }

    // Should be remade
    public int getPlayers() {
        return(player_count); // Returns the number of players
    }

    public int getBlankAmount() {
        return(getEmptyTiles().length);
    }

    // Sets the letters on each tile within the players hand
    public void setHand(char[] list) {
        Tile[] empty = getEmptyTiles(); // Gets the list of blank tiles to be drawn
        if (empty.length < list.length) { // Checks there are enough tiles to hold the list
            return; // Quits the function
        }
        for (int i=0; i<empty.length; i++) { // Loops through each blank tile
            empty[i].setText(list[i]+""); // Sets the text to the drawn tile
            empty[i].setOriginal(list[i]+""); // Sets the original text to its default
            empty[i].setValue(Scrabble.getLetterValue(list[i])); // Sets the value of the tile
        }
    }
    // Highlights the selected tile within the players hand
    public void selectTile(Tile c) {
        if (!(c instanceof Tile)) { // Checks to see if the tile is a valid selection
            return; // Return, if tile is not valid
        }
        int previous_tile = selected_tile; // Stores the index of the previously selected tile
        c.setBorder(Color.orange, 6); // Sets the chosen tile's border to orange
        Point point = c.getPoint(); // Stores the location of the chosen tile, if it's a Board tile
        int index = c.getIndex(); // Stores the index of the chosen tile, if it's a Hand tile
        int calculated_tile = point == null ? -1 : calculateTile(point.getRow(), point.getCol()); // Calculates the index of the chosen tile, if it's a Board tile
        selected_tile = index >= 0 ? index : calculated_tile; // Stores the index of the selected tile, relative to whether or not it is a Board or Hand tile
        getTile(previous_tile).setBorder(Color.black, 2); // Sets the border of the previously selected tile to default

        if (previous_tile == selected_tile) { // Checks if the selected tile was previously selected
            selected_tile = -1; // Deselects the selected tile if it was previously selected
            c.setBorder(Color.black, 2);
        }
        else if (c.findText().equals("")) { // Checks if the selected tile is empty
            c.setBorder(Color.black, 2); // Set the current tile back to its default
            selected_tile = previous_tile; // Reselects the previous tile
        }
    }
    // Places the letter from the selected tile within the players hand
    public void placeTile(Tile c) {
        if (selected_tile < 0 || !(c instanceof Tile) || c == getTile(selected_tile)) { // Check to see if the tile can be placed
            return; // Return, if no tile is selected
        }
        Tile select = getTile(selected_tile); // Store the selected tile
        Tile old = c.getPointingTo(); // Store the previously placed tile, if there is one
        boolean isBoardTile = selected_tile >= 7;
        c.swapText(select.findText()); // Swaps the text from the selected tile to the one it was placed on
        if (!isBoardTile) {
            select.swapText(""); // Sets the text of the selected tile to be blank
            c.setPointingTo(select); // Sets the placed tile corresponding to the selected tile
            select.setPointingTo(c); // Sets the selected tile corresponding to the placed tile
            recallTile(old); // Recalls the tile that was placed previous to the current one
        }
        else{
            select.swapText(c.getPrev()); // Swaps the text of the selected tile with that of the previous text from the placed tile
            c.setPointingTo(select.getPointingTo()); // Sets the placed tile corresponding to the tile the selected one points to
            select.setPointingTo(old); // Sets the selected tile corresponding to the tile the placed one pointed to, previously.
            if (old == null) { // Checks that there was no previous tile at the location of the placed tile
                placedTiles.remove(select); // Removes the selected tile from the list of placed tiles
                dispatchEvent(new CustomEvent(select, RECALLED_TILE, select.getPoint().r, select.getPoint().c)); // Announce the recall of the selected tile
            }
            else {
                dispatchEvent(new CustomEvent(select, PLACED_LETTER, select.findText().charAt(0), select.getPoint().r, select.getPoint().c)); // Announce the re-placement of the selected tile
            }
        }
        selectTile(select); // Deselects the selected tile
        placedTiles.add(c); // Adds the placed tile to the list of placed tiles
        dispatchEvent(new CustomEvent(c, PLACED_LETTER, c.findText().charAt(0), c.getPoint().r, c.getPoint().c)); // Announce the placement of the placed tile
    }

    // Recalls all tiles placed on the board, starting the play over
    public void recallTiles() {
        for (Tile p : placedTiles) { // Loop through each Tile placed on the board
            if (p.getPoint() != null) {
                dispatchEvent(new CustomEvent(p, RECALLED_TILE, p.getPoint().r, p.getPoint().c)); // Trigger an ActionEvent to allow the client to determine whether or not the tile can be recalled
            }
        }
        placedTiles.clear(); // Wipe the set of all placed tiles, since they were recalled
    }
    // Recalls a tile back to its original position
    public void recallTile(Tile p) {
        if (p == null) { // Checks if Tile 'p' is null
            return; // End the function if there's nothing to do
        }
        Tile pointingAt = p.getPointingTo(); // Stores the tile Tile 'p' is pointing to for easy access
        p.setText(p.getOriginal()); // Swap the placed tile text with its original
        p.setPointingTo(null); // Set it so the placed tile no longer points to anything
        recallTile(pointingAt); // Recalls the tile Tile 'p' was pointing to
    }
    public void shuffleTiles() {
        // Shuffling: https://stackoverflow.com/questions/1519736/random-shuffling-of-an-array
        Random rand = new Random(); // Creates a random object for shuffling
        for (int i=0; i<HAND_LENGTH-1; i++) { // Loops through each tile in the Players Hand
            Tile.swapTiles(getTile(i), getTile(rand.nextInt(HAND_LENGTH-i) + i)); // Uses the Tile class to swap the properties of the current tile with that of a random tile, past the current one
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
    public void tilesWereSubmitted() {
        dispatchEvent(new CustomEvent(frame, DRAW_HAND));
    }

    // Creates the JPanel that displays the current player and their score
    // Still under development
    private void createScoreboard() {
        gamePanel = new JPanel(new GridBagLayout()); // Clears the gamePanel
        GridBagLayout l = (GridBagLayout) gamePanel.getLayout();
        GridPanel scoreboard = new GridPanel(MIN_WIDTH, SB_HEIGHT, BoxLayout.X_AXIS); // 50 * 2.5 = 125

        CurvedButton left = new CurvedButton("<", (int)(TILE_RADIUS*1.5), new Color(0x000000), 100);
        left.setSize(TILE_SIZE, TILE_SIZE);
        left.setFont(new Font("Serif", Font.BOLD, FONT_SIZE*2));
        left.setYOffset(1.0/4.0);
        scoreboard.add(left, 1, 0, 1, 1, GridBagConstraints.BOTH);

        scoreboard.add(makePadding((SB_HEIGHT-TILE_SIZE)/2, (SB_HEIGHT-TILE_SIZE)/2), 0, 0, 1, 1, GridBagConstraints.BOTH);
        scoreboard.add(makePadding((SB_HEIGHT-TILE_SIZE)/2, (SB_HEIGHT-TILE_SIZE)/2), 2, 0, 1, 1, GridBagConstraints.BOTH);

        CurvedButton right = new CurvedButton(">", (int)(TILE_RADIUS*1.5), new Color(0x000000), 100);
        right.setSize(TILE_SIZE, TILE_SIZE);
        right.setFont(new Font("Serif", Font.BOLD, FONT_SIZE*2));
        right.setYOffset(1.0/4.0);
        scoreboard.add(right, 1, 3, 1, 1, GridBagConstraints.BOTH);

        scoreboard.add(makePadding((SB_HEIGHT-TILE_SIZE)/2, (SB_HEIGHT-TILE_SIZE)/2), 0, 3, 1, 1, GridBagConstraints.BOTH);
        scoreboard.add(makePadding((SB_HEIGHT-TILE_SIZE)/2, (SB_HEIGHT-TILE_SIZE)/2), 2, 3, 1, 1, GridBagConstraints.BOTH);

        CurvedLabel player = new CurvedLabel("Player:    1"); // Might have usernames, doesn't matter rn
        player.setSize(MIN_WIDTH, SB_HEIGHT);
        player.setFont(new Font("Serif", Font.BOLD, FONT_SIZE*2));
        scoreboard.add(player, 0, 1, 1, 3, GridBagConstraints.BOTH);

        CurvedLabel score = new CurvedLabel("Score:    0");
        score.setSize(MIN_WIDTH, SB_HEIGHT);
        score.setFont(new Font("Serif", Font.BOLD, FONT_SIZE*2));
        scoreboard.add(score, 0, 2, 1, 3, GridBagConstraints.BOTH);

        l.setConstraints(scoreboard, createConstraints(1, 2*SB_HEIGHT/1.0/MIN_HEIGHT, 0, 0, 1, 1, GridBagConstraints.BOTH));
        scoreboard.setPreferredSize(new Dimension(MIN_WIDTH, SB_HEIGHT));
        gamePanel.add(scoreboard);
    }

    // Creates the JPanel which holds each JButton that makes up the Scrabble board 
    // Creates the JPanel which holds each JButton that makes up the Scrabble board 
    private void createBoard() {
        board = new GridPanel(FRAME_WIDTH, FRAME_HEIGHT, BoxLayout.Y_AXIS); // Creates the main Board panel
        for (int r=0; r<ROWS; r++) { // Loops through each row on the board
            for (int c=0; c<COLS; c++) { // Loops through each col on the board
                int tile = Scrabble.getVal(r%ROWS, c%COLS); // Create the tile value to determine the look of each button
                final Tile temp = new Tile("", TILE_RADIUS, new Color(0xBA7F40), 37, 1, 1, r, c); // Blank Tile, represented by '0'
                if (tile == 1 || tile == 2) { // Tile is a Letter Tile, represented by a '1' or '2'
                    temp.resetProperties((tile == 1 ? '2' : '3') + "x L", TILE_RADIUS, new Color(0x4274FF), tile%2 == 1 ? 25 : 100, tile, 1);
                }
                else if (tile == 3 || tile == 4) { // Tile is a Word Tile, represented by '3' or '4'
                    temp.resetProperties((tile == 3 ? '2' : '3') + "x W", TILE_RADIUS, new Color(0xD7381C), tile%2 == 1 ? 25 : 100, 1, tile);
                }
                temp.setFont(new Font("Serif", Font.BOLD, FONT_SIZE)); // Set the font of the tile
                temp.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        if (selected_tile != -1 && calculateTile(temp.getPoint().r, temp.getPoint().c) != selected_tile) { // Checks if the selected tile is valid and is not the same tile
                            dispatchEvent(new CustomEvent(temp, PLACING_LETTER, temp.getPoint().r, temp.getPoint().c)); // Trigger an ActionEvent to allow the client to determine whether or not the tile can be placed
                        }
                        else if (temp.findText().length() == 1) { // Checks that the chosen tile holds a valid letter
                            dispatchEvent(new CustomEvent(temp, SELECTED_LETTER, temp.getPoint().r, temp.getPoint().c)); // Trigger an ActionEvent to allow the client to determine whether or not the tile can be selected
                        }
                    }
                });
                temp.setSize(TILE_SIZE, TILE_SIZE); // Sets the size of the tile. Used in determining weights for the GridPanel
                board.add(temp, r, c, 1, 1, GridBagConstraints.BOTH); // Adds the tile to the Board
            }
        }
        GridBagLayout l = (GridBagLayout) gamePanel.getLayout();
        l.setConstraints(board, createConstraints(1, 1, 0, 1, 1, 1, GridBagConstraints.BOTH)); // Set the constraints on the board
        board.setPreferredSize(new Dimension(COLS*TILE_SIZE, ROWS*TILE_SIZE));
        gamePanel.add(board); // Create and add the board to the application frame
    }

    // Creates the JPanel that features each player's hand of tiles
    private void createHand() {
        GridBagLayout l = (GridBagLayout) gamePanel.getLayout();
        GridPanel hand = new GridPanel(MIN_WIDTH, MIN_HEIGHT, BoxLayout.X_AXIS);
    
        JPanel left = new JPanel(); // Left padding
        left.setSize((MAX_WIDTH - (7*(H_TILE_SIZE + H_X_OFF) + H_TILE_SIZE/2))/2, H_TILE_SIZE); // Set the size of the left padding
        JPanel right = new JPanel(); // Right padding
        right.setSize(left.getSize()); // Set the size of the right padding

        //hand.add(left, 0, 0, 1, 2, GridBagConstraints.BOTH); // Add Left Padding at [0][0]
        //hand.add(right, 0, (HAND_LENGTH)*2 + 4, 1, 2, GridBagConstraints.BOTH); // Add Right Padding at [0][-1]
        
        hand.add(makePadding(4*(MIN_WIDTH - (8*(H_TILE_SIZE + H_X_OFF) + TILE_SIZE))/2 + 4, H_TILE_SIZE), 0, 0, 1, 2, GridBagConstraints.BOTH);
        hand.add(makePadding(4*(MIN_WIDTH - (8*(H_TILE_SIZE + H_X_OFF) + TILE_SIZE))/2 + 4, H_TILE_SIZE), 0, (HAND_LENGTH * 2) + 4, 1, 2, GridBagConstraints.BOTH);

        System.out.println("Padding: "+(MIN_WIDTH - (8*(H_TILE_SIZE + H_X_OFF) + TILE_SIZE)));

        // Would add the Recall and Shuffle buttons up here, if adding directly to JPanel.
        for (int i=0; i<=HAND_LENGTH*2; i++) {
            final Tile tile = new Tile("", (int)(TILE_RADIUS*1.5), new Color(0xBA7F40), 100, i/2); // Create the letter tile
            tile.setFont(new Font("Serif", Font.PLAIN, (int)(FONT_SIZE*1.5))); // Set the font of the tile
            tile.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    selectTile(tile);
                }
            });
            if (i%2 == 1) {
                tile.setSize(H_TILE_SIZE, H_TILE_SIZE); // Gridwidth = 1, gridheight = 2;
                hand.add(tile, 0, i+2, 1, 2, GridBagConstraints.BOTH);
            }
            else {
                JLabel temp = new JLabel();
                temp.setSize(H_X_OFF, H_TILE_SIZE);
                hand.add(temp, 0, i+2, 1, 2, GridBagConstraints.BOTH);
            }
        }
        l.setConstraints(hand, createConstraints(1, (H_TILE_SIZE)/1.0/MAX_WIDTH, 0, 2, 1, 1, GridBagConstraints.BOTH)); // Set the constraints on the hand
        createHandOptions(hand); // Create the action buttons, located on the Player's Hand
        hand.setPreferredSize(new Dimension(MIN_WIDTH, H_TILE_SIZE));
        gamePanel.add(hand); // Create and add the hand to the application frame;
    }

    private void createHandOptions(GridPanel hand) {
        final CurvedButton recall = new CurvedButton("Recall", TILE_RADIUS, new Color(0x036FFC), 100); // Creates the Recall button
        recall.setSize(TILE_SIZE, TILE_SIZE); // Sets the size of the Recall button
        recall.setFont(new Font("Serif", Font.BOLD, 7*FONT_SIZE/8)); // Sets the font size

        final CurvedButton shuffle = new CurvedButton("Shuffle", TILE_RADIUS, new Color(0xFC6603), 100); // Creates the Shuffler button
        shuffle.setSize(TILE_SIZE, TILE_SIZE); // Sets the size of the Shuffle button
        shuffle.setFont(new Font("Serif", Font.BOLD, 7*FONT_SIZE/8)); // Sets the font size

        final CurvedButton submit = new CurvedButton("Submit", TILE_RADIUS, new Color(0x2BAB49), 100); // Creates the Submit button
        submit.setSize(H_TILE_SIZE, H_TILE_SIZE);
        submit.setFont(new Font("Serif", Font.BOLD, FONT_SIZE));

        recall.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (selected_tile >= 7) { // Checks if the selected tile is a Board tile
                    recallTile(getTile(selected_tile)); // Recalls the selected tile
                    selectTile(getTile(selected_tile)); // Clear the selection
                }
                else { // If there is no selected tile, or it isn't a Board tile
                    recallTiles(); // Recalls all placed tiles
                    dispatchEvent(new CustomEvent(frame, RECALLED_ALL)); // Trigger ActionEvent to signal all the tiles were recalled
                }
            }
        });
        shuffle.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispatchEvent(new CustomEvent(shuffle, SHUFFLED_TILES)); // Dispatch Shuffle
                shuffleTiles(); // Shuffles all tiles in the hand
            }
        });
        submit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispatchEvent(new CustomEvent(submit, FINALIZED_PLAY)); // Dispatch Submission
            }
        });
        
        hand.add(recall, 0, 1, 1, 1, GridBagConstraints.BOTH); // Add Recall Button at [0][1]
        hand.add(shuffle, 1, 1, 1, 1, GridBagConstraints.BOTH); // Add Shuffle Button at [1][1]
        hand.add(submit, 0, (HAND_LENGTH)*2 + 3, 1, 2, GridBagConstraints.BOTH); // Add Submit Button at [0][-2];
    }

    // Creates the JPanel that contains the components which make up the main menu
    // Need to redesign this at some point. PaddedPanel is just too non-redundant
    private void createMenu() {
        mainPanel = new JPanel(new GridBagLayout()); // Initializes the mainPanel
        GridBagLayout l = (GridBagLayout) mainPanel.getLayout(); // Stores the GridBagLayout of the mainPanel

        GridPanel menu = new GridPanel(MIN_WIDTH, MIN_HEIGHT, BoxLayout.Y_AXIS); // Creates the Main Menu panel
    
        final CurvedButton startButton = new CurvedButton("Start", 15, Color.yellow, 100); // Creates a default start button
        startButton.setFont(new Font("Serif", Font.PLAIN, 75)); // Sets the font of the Start Button to size 75
        startButton.setSize(MENU_WIDTH/3, MENU_HEIGHT); // Sets the default size of the Start Button

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispatchEvent(new CustomEvent(startButton, GAME_RUNNING)); // Triggers an ActionEvent to signal the game was started
            }
        });

        final JSlider selector = new JSlider(2, 6); // Creates a JSlider between 2 and 6
        selector.setValue(2); // Sets the default player count to 2
        selector.setMajorTickSpacing(1); // Sets the tick spacing to each number
        selector.setPaintTicks(true); // Paints the ticks on the slider
        selector.setSize(MENU_WIDTH/3, MENU_HEIGHT); // Sets the default size of the Selector

        GridPanel start = new GridPanel(MIN_WIDTH, MIN_HEIGHT, BoxLayout.X_AXIS); // Creates a GridPanel to hold the Start Button
        start.add(makePadding(MENU_WIDTH/3, MENU_HEIGHT), 0, 0, 1, 1, GridBagConstraints.BOTH); // Adds Left Padding
        start.add(startButton, 0, 1, 1, 1, GridBagConstraints.BOTH); // Adds the Start Button to the Start Panel
        start.add(makePadding(MENU_WIDTH/3, MENU_HEIGHT), 0, 2, 1, 1, GridBagConstraints.BOTH); // Adds Right Padding
        start.setSize(MIN_WIDTH, MENU_HEIGHT); // Sets the default size of the Start Button
        
        GridPanel select = new GridPanel(MIN_WIDTH, MIN_HEIGHT, BoxLayout.X_AXIS); // Creates a GridPanel to hold the Selector
        select.add(makePadding(MENU_WIDTH/6, MENU_HEIGHT), 0, 0, 1, 1, GridBagConstraints.BOTH); // Adds Left Padding
        select.add(selector, 0, 1, 1, 1, GridBagConstraints.BOTH); // Adds the Selector to the Selector Panel
        select.add(makePadding(MENU_WIDTH/6, MENU_HEIGHT), 0, 2, 1, 1, GridBagConstraints.BOTH); // Adds Right Padding
        select.setSize(MIN_WIDTH, MENU_HEIGHT); // Sets the default size of the Selector

        final CurvedLabel counter = new CurvedLabel("Players:    2"); // Creates the Player Counter display
        counter.setFont(new Font("Serif", Font.PLAIN, 75)); // Sets the font of the Counter to size 75
        counter.setSize(MIN_WIDTH, MENU_HEIGHT); // Sets the default size of the Player Display

        menu.add(makePadding(MIN_WIDTH, MIN_HEIGHT/3 - MENU_HEIGHT), 0, 0, 1, 1, GridBagConstraints.BOTH); // Adds Top Padding
        menu.add(counter, 1, 0, 1, 1, GridBagConstraints.BOTH); // Adds the Player Display to the Main Menu
        menu.add(makePadding(MIN_WIDTH, MIN_HEIGHT/3 - MENU_HEIGHT), 2, 0, 1, 1, GridBagConstraints.BOTH); // Adds Middle Padding
        menu.add(select, 3, 0, 1, 1, GridBagConstraints.BOTH); // Adds the Selector to the Main Menu
        menu.add(makePadding(MIN_WIDTH, MIN_HEIGHT/3 - MENU_HEIGHT), 4, 0, 1, 1, GridBagConstraints.BOTH); // Adds Middle Padding
        menu.add(start, 5, 0, 1, 1, GridBagConstraints.BOTH); // Adds the Start Button to the Main menu
        menu.add(makePadding(MIN_WIDTH, MIN_HEIGHT/3 - MENU_HEIGHT), 6, 0, 1, 1, GridBagConstraints.BOTH); // Adds Bottom Padding

        selector.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                player_count = selector.getValue();
                counter.setText("Players:    "+player_count); // Adjust the counter to match the slider's value
            }
        });

        l.setConstraints(menu, createConstraints(1, 1, 0, 0, 1, 1, GridBagConstraints.BOTH)); // Sets the constraints for the Main menu
        mainPanel.add(menu); // Create and add the Menu to the JPanel
    }

    // An attempt at layering panels
    public void createError() {
        // PaintComponent problems: https://stackoverflow.com/questions/20833913/flickering-when-updating-overlapping-jpanels-inside-a-jlayeredpane-using-timeste
        CurvedButton text = new CurvedButton("Invalid Word", TILE_RADIUS, Color.GRAY, 0);
        text.setEnabled(false);
        text.setXOffset(0.55);
        text.setYOffset(0.375);
        text.setFont(new Font("Serif", Font.BOLD, 37));
        text.setTextColor(Color.RED);
        text.setBorder(new Color(0, 0, 0, 0), 0);
        text.setSize(10, 10);

        GridPanel error = new GridPanel(MIN_WIDTH, MIN_HEIGHT, BoxLayout.X_AXIS);
        error.add(text, 0, 0, 1, 1, GridBagConstraints.BOTH);
        error.setBounds(0, 0, COLS*TILE_SIZE, ROWS*TILE_SIZE);
        error.setPreferredSize(new Dimension(COLS*TILE_SIZE, ROWS*TILE_SIZE));
        error.setOpaque(false);
        error.setVisible(false); // Start with the error screen invisible.
        text.setVisible(false);

        JLayeredPane t = new JLayeredPane();
        
        gamePanel.remove(board);
        board.setBounds(0, 0, board.getSize().width, board.getSize().height);
        
        t.add(error, JLayeredPane.POPUP_LAYER);
        t.add(board, JLayeredPane.DEFAULT_LAYER);
        
        GridBagLayout l = (GridBagLayout) gamePanel.getLayout();
        l.setConstraints(t, createConstraints(1, 1, 0, 1, 1, 1, GridBagConstraints.BOTH));
        //t.setPreferredSize(new Dimension(COLS*TILE_SIZE, ROWS*TILE_SIZE));
        gamePanel.add(t);
    }

 public void displayError(String error) {
        final CurvedButton text = (CurvedButton) getError().getComponent(0);
        text.setText(error);
        text.setOpacity(0);
        // How to use Java Swing Timer: https://stackoverflow.com/questions/28521173/how-to-use-swing-timer-actionlistener
        final Timer inc = new Timer(100, new ActionListener() { // Real timer executes every 100 Miliseconds
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.print(". ");
                text.setOpacity(text.getOpacity()+1);
            }
        });
        final Timer dec = new Timer(100, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.print(". ");
                text.setOpacity(text.getOpacity()-1);
            }
        });
        final Timer original = new Timer(5000, new ActionListener() { // Original timer executes after 5 Seconds
            @Override
            public void actionPerformed(ActionEvent e) {
                if (inc.isRunning()) {
                    inc.stop(); // Stops the Real timer from running
                    //original.start();
                    System.out.println("Stopped.\nDecreasing ");
                }
                else {
                    dec.stop();
                    System.out.println("Stopped.");
                }
            }
        });
        getError().setVisible(true);
        original.setRepeats(false); // Stops the Original timer from repeating
        inc.start(); // starts the Real timer
        System.out.print("Increasing ");
        original.start(); // Starts the Original timer
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

    private Component makePadding(int width, int height) {
        JPanel padding = new JPanel();
        padding.setSize(width, height);
        padding.setMinimumSize(new Dimension(width, height/2));
        padding.setMaximumSize(new Dimension((int) (width/1.0/MIN_WIDTH) * MAX_WIDTH, (int) (height/2.0/MIN_HEIGHT) * MAX_HEIGHT));
        return(padding);
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

    private GridPanel getBoard() {
        return(board);//new GridPanel(1, 1, BoxLayout.X_AXIS));//return((GridPanel)(gamePanel.getComponent(1)));
    }

    private GridPanel getError() {
        JLayeredPane temp = (JLayeredPane) gamePanel.getComponent(1);
        return((GridPanel) temp.getComponent(0));
    }

    private int calculateTile(int r, int c) {
        return(6 + (r + 1) * (c + 1) + (COLS - (c + 1)) * r);
    }

    private Tile[] getEmptyTiles() {
        ArrayList<Tile> temp = new ArrayList<Tile>();
        for (Component c : getHand().getTileComponents()) {
            Tile t = (Tile) c;
            if (t.findText().equals("")) {
                temp.add(t);
            }
        }
        return(temp.toArray(new Tile[0]));
    }

    // Returns the a tile from the players hand at the given index
    private Tile getTile(int i) {
        if (i < 0) {
            return(new Tile());
        }
        else if (i < 7) {
            return((Tile)(getHand().getTileComponents()[i]));
        }
        else {
            return((Tile)(getBoard().getComponent(i-7)));
        }
    }

    // Adding Listeners To JFrame: https://stackoverflow.com/questions/18165800/how-to-add-actionlistener-to-jframe-without-using-buttons-and-panels
    public void addCustomListener(CustomListener c) {
        listeners.add(c);
    }
}