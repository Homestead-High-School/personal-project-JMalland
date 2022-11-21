import java.awt.event.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.util.Random;


// R I G
//     I     --> Results in 17, 12 points for GIN, then NOW
//     N O W
class Board extends JFrame {
    /*
    *   Should make either a sidemenu, or selections from the player's hand, where, when selected, it highlights the border in yellow or something.
    */
    // Actual ratios are more towards 19/15;
    private static final double MULT = 1; // At Mult of 0.8, Aspect Ratio appears odd at <= 630 X 840
    public static final int MIN_WIDTH = (int)(MULT*675); // (75*8 + 50) = 650
    public static final int MIN_HEIGHT = (int)(MULT*900); // (750 + 75 | 125) = 825 ~ 950
    public static final int MAX_WIDTH = (int)(810*MULT); // If exceeds 843, duplicate tiles appear on bottom row
    public static final int MAX_HEIGHT = (int)(MULT*1080); // If exceeds 1012, duplicate tiles appear on bottom row
    private final double widthRatio = 3.0; // Width has a ratio of 3
    private final double heightRatio = 4.0; // Height has a ratio of 4
    public final int CREATE_PLAYER = ("PLAYER").hashCode();
    public final int SELECTED_HAND = ("HAND").hashCode(); 
    public final int SELECTED_LETTER = ("SELECT").hashCode();
    public final int SKIPPED_TURN = ("SKIP").hashCode();
    public final int SWAPPING_TILES = ("SWAPPING").hashCode();
    public final int TILES_SWAPPED = ("SWITCH").hashCode();
    public final int PLACING_LETTER = ("PLACING").hashCode();
    public final int PLACED_LETTER = ("TILE").hashCode();
    public final int RECALLED_TILE = ("RECALL").hashCode();
    public final int RECALLED_ALL = ("ALL").hashCode();
    public final int SHUFFLED_TILES = ("SHUFFLE").hashCode();
    public final int FINALIZED_PLAY = ("SUBMIT").hashCode();
    public final int GAME_RUNNING = ("ON").hashCode();
    public final int QUIT_GAME = ("QUIT").hashCode();
    private final JFrame frame;
    private JPanel gamePanel = new JPanel();
    private JPanel mainPanel = new JPanel();
    private GridPanel board = new GridPanel(1, 1, BoxLayout.X_AXIS);
    private HashSet<CustomListener> listeners = new HashSet<CustomListener>();
    private HashSet<Tile> placedTiles = new HashSet<Tile>();
    private final int ROWS = Scrabble.getBoard().length;
    private final int COLS = Scrabble.getBoard()[0].length;
    private final int FONT_SIZE = (int)(24*MULT); // 26 Is actually double. Window starts out at half size
    private final int TILE_SIZE = (int)(45*MULT); // 50
    private final int TILE_RADIUS = (int)(25*MULT); // 26 - Is actually double. Window starts out at half size
    private final int MIN_OPACITY = 100;
    private final int MAX_OPACITY = 200;
    private final int MIN_TEXT_OPACITY = 175;
    private final int MAX_TEXT_OPACITY = 200;
    private final int SB_HEIGHT = (int)(TILE_SIZE*1.75);
    private final int HAND_LENGTH = 7;
    private final int HAND_OPACITY = 150;
    private final int H_TILE_SIZE = (int)(65 * MULT); // 675 - (8*65 + 8*(65/8) + 50)) --> 20px padding
    private final int H_X_OFF = 5;
    private final int OPTION_HEIGHT = H_TILE_SIZE/2;
    private final int MENU_WIDTH = (int)(300*MULT); // 300
    private final int MENU_HEIGHT = (int)(75*MULT); // 75
    private final int ERROR_LENGTH = 2000; // Sets the length of time that errors are displayed
    private final int ERROR_OPACITY = 125; // Sets the opacity the error starts at
    private final Timer check; // Uninitialized Timer to be the 'wait' feature of certain toggles
    private int FRAME_WIDTH = MIN_WIDTH; // 528
    private int FRAME_HEIGHT = MIN_HEIGHT; // 528
    private int player_count = 2;
    private int current_player = 0;
    private int displayed_player = 0;
    private int selected_tile = -1;
    private ArrayList<Integer> widths = new ArrayList<Integer>();
    private ArrayList<Integer> heights = new ArrayList<Integer>();
    private HashSet<Integer> swapping = new HashSet<Integer>();
    private Player[] players = null;
 
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

        check = new Timer(3000, null);
        check.setRepeats(false); // Make it so the Timer doesn't repeat
        
        // Using Layouts With Auto-Adapting Components: https://stackoverflow.com/questions/70523527/how-to-stop-components-adapting-to-the-size-of-a-jpanel-that-is-inside-a-jscroll
        createMenu(); // Generate the Main Menu and add to mainPanel
        createScoreboard(); // Generate the Scoreboard displayed in-game
        createBoard(); // Create the Scrabble Board
        createError(); // Create the Error Display
        createHand(); // Create the Player's Hand
        createHandOptions(); // Create the Options below the Hand

        mainPanel.setVisible(true); // Set the main menu visible, if not
    
        frame.add(mainPanel); // Add the main menu to the JFrame

        for (int i=(int)(MIN_WIDTH * (693.0/675)); i<=MAX_HEIGHT; i+=(int) widthRatio) {
            widths.add(i);
            heights.add((int)(i * (heightRatio/widthRatio)));
        }

        // How to detect window minimized/maximized: https://stackoverflow.com/questions/11148950/how-to-detect-jframe-window-minimize-and-maximize-events
        Toolkit.getDefaultToolkit().setDynamicLayout(false); // Ensures window resize keeps the right ratio: https://stackoverflow.com/questions/20925193/using-componentadapter-to-determine-when-frame-resize-is-finished 
        frame.setSize(new Dimension((int)(MIN_WIDTH * 693.0/675), (int)(MIN_HEIGHT * 908.0/900))); // Sets the default dimensions
        frame.addComponentListener(new ComponentAdapter() {
            // EventListener for window resizing: https://stackoverflow.com/questions/2303305/window-resize-eventff
            public void componentResized(ComponentEvent componentEvent) { // Method to run every time window is resized
                int width = frame.getWidth();
                int height = frame.getHeight();
                if ((int) (width%widthRatio) == 0 && (int) (height%heightRatio) == 0) {
                    
                }
                else if (Math.abs(FRAME_WIDTH - width) > Math.abs(FRAME_HEIGHT - height)) { // Check if the width was changed more than the height
                    int w = getClosestIndex(width, widths); // Get the closest acceptable width relative to the chosen width
                    frame.setPreferredSize(new Dimension(widths.get(w), heights.get(w))); // Set the new dimensions
                }
                else if (Math.abs(FRAME_WIDTH - width) < Math.abs(FRAME_HEIGHT - height)) { // Check if the height was changed more than the width
                    int h = getClosestIndex(height, heights); // Get the closest acceptable height relative to the chosen height
                    frame.setPreferredSize(new Dimension(widths.get(h), heights.get(h))); // Set the new dimensions
                }

                if (frame.getWidth() < MIN_WIDTH * 693.0/675 || frame.getWidth() > MAX_WIDTH || frame.getHeight() < MIN_HEIGHT * 908.0/900 || frame.getHeight() > MAX_HEIGHT) { // Check if the new dimensions are beyond the limits
                    int w = frame.getWidth() > MAX_WIDTH || frame.getHeight() > MAX_HEIGHT ? MAX_WIDTH : (int)(MIN_WIDTH * 693.0/675); // Set the Max or Min width
                    int h = frame.getWidth() > MAX_WIDTH || frame.getHeight() > MAX_HEIGHT ? MAX_HEIGHT : (int)(MIN_HEIGHT * 908.0/900); // Set the Max or Min height
                    frame.setPreferredSize(new Dimension(w , h)); // Set the new dimensions
                }

                GridPanel error = getError();
                error.setBounds(error.getX(), error.getY(), (int)(error.getSize().width * (frame.getWidth() / 1.0 / FRAME_WIDTH)), (int)(error.getSize().height * (frame.getHeight() / 1.0 / FRAME_HEIGHT)));
                GridPanel board = getBoard();
                board.setBounds(board.getX(), board.getY(), (int)(board.getSize().width * (frame.getWidth() / 1.0 / FRAME_WIDTH)), (int)(board.getSize().height * (frame.getHeight() / 1.0 / FRAME_HEIGHT)));
                FRAME_WIDTH = frame.getWidth(); // Update the Width property so it is current
                FRAME_HEIGHT = frame.getHeight(); // Update the Height property so it is current
                frame.pack(); // Pack once more, in case the Hand was adjusted
                System.out.println("Window Resized: "+frame.getWidth()+" x "+frame.getHeight());
            }
        });

        frame.setVisible(true); // Set the application frame visible
        frame.setResizable(false);
    }

    // Returns the index of the closest integer to another integer within a list
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
        for (int i=0; i<HAND_LENGTH; i++) {
            getTile(i).setText("");
            getTile(i).setOriginal("");
        }
        players = new Player[0];
        createPlayers(); // Start the process of player creation.
        frame.remove(mainPanel); // Remove all current panels to begin the gameplay
        board.removeAll();
        placedTiles.clear();
        generateBoard();
        frame.add(gamePanel); // Add the game panel to display the Scrabble board
        frame.pack(); // Repack the JFrame
        frame.repaint(); // Repaint the JFrame
        System.out.println("Game Started: "+player_count+" Players"); // Display the beginning of the game
        setHand(players[current_player].getHand()); // Set the hand for Player 1
        getSkipButton().setPushed(false); // De-Toggle the Skip button
        getSwapButton().setPushed(false); // De-Toggle the Swap button
        getQuitButton().setPushed(false); // De-Toggle the Quit button
        selectTile(getTile(selected_tile)); // Deselect the current tile, if there is one
    }

    // Quits the game and returns to the main menu.
    public void endGame() {
        getError().setVisible(false); // Stop the display, to allow text to catch up
        getQuitButton().setPushed(true); // Re-push the Quit button
        getQuitButton().setToggleColor(getQuitButton().getColor()); // Change the toggle color
        final Timer loop = new Timer(0, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                getError().setVisible(true); // Repeatedly refresh the display
            }
        });
        check.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!getQuitButton().isPushed()) { // Check that the button toggled is the quit button
                    return; // Quit the function
                }
                getQuitButton().setPushed(false); // De-Toggle the Quit button
                getQuitButton().setToggleColor(new Color(0x339933)); // Reset the toggle color
                loop.stop(); // Stop the display
                getError().setVisible(false); // Make the display invisible
                frame.remove(gamePanel); // Remove the game board
                frame.add(mainPanel); // Go back to the main screen
                frame.pack(); // Repack the JFrame
                frame.repaint(); // Repaint the JFrame
                System.out.println("Game Quit.");
            }
        });
        check.setRepeats(false); // Make it so the timer can't repeat
        int highest = 0; // Store the highest scoring player
        boolean tied = players.length > 1; // Store whether or not there's a tie
        for (int i=1; i<players.length; i++) { // Loop through each player to find the highest scoring
            tied = players[i-1].getScore() == players[i].getScore() && tied; // Check if there is or isn't a tie
            highest = players[i].getScore() > players[highest].getScore() ? i : highest; // Check for the highest scoring player
        }
        displayCondition(tied ? "The Game Tied!\n\nScore: "+players[0].getScore() : "Winner: Player "+(highest+1)+"\n\nScore: "+players[highest].getScore(), Color.green);
        loop.start(); // Start the loop
        check.start(); // Start the timer
    }

    // Returns the number of players in the game
    public int getPlayers() {
        return(player_count); // Returns the number of players
    }

    // Returns the number of blank tiles that were played
    public int getBlankAmount() {
        return(getEmptyTiles().length);
    }

    // Creates the player at the beginning of a game
    public void createPlayer(char[] hand, int i) {
        if (i >= player_count) { // Throw an exception if I'm stupid enough to hit it
            throw new IllegalArgumentException("Index Out Of Bounds For Player Creation");
        }
        players[i] = new Player(hand);
    }

    // Updates the display of the current player's score
    public void updateScore(int points) {
        players[current_player].addToScore(points);
        changeDisplay(0);
    }

    // Sets the letters on each tile within the players hand
    public void setHand(char[] list) {
        Tile[] empty = getEmptyTiles(); // Gets the list of blank tiles to be drawn
        if (empty.length < list.length) { // Checks there are enough tiles to hold the list
            return; // Quits the function
        }
        for (int i=0; i<empty.length; i++) { // Loops through each blank tile
            players[current_player].setTile(getTileIndex(empty[i]), list[i]);
            empty[i].setText(players[current_player].getTile(i)+"");
            empty[i].setOriginal(players[current_player].getTile(i)+"");
        }
    }
    
    // Highlights the selected tile within the players hand
    public void selectTile(Tile c) {
        if (!(c instanceof Tile)) { // Checks to see if the tile is a valid selection
            return; // Return, if tile is not valid
        }
        int previous_tile = selected_tile; // Stores the index of the previously selected tile
        c.setBorder(getSwapButton().isPushed() ? Color.blue : Color.orange, 6); // Sets the chosen tile's border to orange
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
        else if (getSwapButton().isPushed()) { // Checks if tiles are currently being swapped out
            selected_tile = previous_tile; // Switch the selection back to what it was
            if (!swapping.contains(index)) { // Check if the tile is being selected
                swapping.add(index); // Add it to the list of swapped out tiles
            }
            else { // The tile is being deselected
                swapping.remove(index); // Remove it from the list of swapped tiles
                c.setBorder(Color.black, 2); // Set its border to normal, once more
            }
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
        if (!isBoardTile) {
            recallTile(old); // Recalls the tile that was placed previous to the current one
            c.swapText(select.findText()); // Swaps the text from the selected tile to the one it was placed on
            select.swapText(""); // Sets the text of the selected tile to be blank
            c.setPointingTo(select); // Sets the placed tile corresponding to the selected tile
            select.setPointingTo(c); // Sets the selected tile corresponding to the placed tile
        }
        else{
            c.swapText(select.findText()); // Swaps the text from the selected tile to the one it was placed on
            select.swapText(c.getPrev()); // Swaps the text of the selected tile with that of the previous text from the placed tile
            c.setPointingTo(select.getPointingTo()); // Sets the placed tile corresponding to the tile the selected one points to
            select.setPointingTo(old); // Sets the selected tile corresponding to the tile the placed one pointed to, previously.
            if (old == null) { // Checks that there was no previous tile at the location of the placed tile
                placedTiles.remove(select); // Removes the selected tile from the list of placed tiles
                dispatchEvent(new CustomEvent(select, RECALLED_TILE, select.getPoint().r, select.getPoint().c)); // Announce the recall of the selected tile
            }
            else {
                dispatchEvent(new CustomEvent(select, PLACED_LETTER, select.findText().charAt(0), select.getPoint().r, select.getPoint().c)); // Announce the re-placement of the selected tile
                select.setOpacity(MAX_OPACITY); // Sets the tile to display as clear as possible
                select.setTextOpacity(MAX_TEXT_OPACITY); // Sets the tile to display text much clearer
            }
        }
        selectTile(select); // Deselects the selected tile
        placedTiles.add(c); // Adds the placed tile to the list of placed tiles
        c.setOpacity(MAX_OPACITY); // Sets the tile to display as clear as possible
        c.setTextOpacity(MAX_TEXT_OPACITY); // Sets the tile to display text much clearer
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
        if (p.getPoint() != null) { // Check if it is a Board tile
            p.setOpacity(MIN_OPACITY); // Reset the opacity of the tile to default
            p.setTextOpacity(MIN_TEXT_OPACITY); // Reset the text opacity of the tile to default
        }
        else {
            players[current_player].setTile(getTileIndex(p), p.findText().charAt(0));
        }
        recallTile(pointingAt); // Recalls the tile Tile 'p' was pointing to
    }
    
    // Shuffles the tiles in the Hand and the players Hand
    public void shuffleTiles() {
        // Shuffling: https://stackoverflow.com/questions/1519736/random-shuffling-of-an-array
        Random rand = new Random(); // Creates a random object for shuffling
        for (int i=0; i<HAND_LENGTH-1; i++) { // Loops through each tile in the Players Hand
            int rIdx = rand.nextInt(HAND_LENGTH-i) + i;
            Tile.swapTiles(getTile(i), getTile(rIdx)); // Uses the Tile class to swap the properties of the current tile with that of a random tile, past the current one
            String a = getTile(i).findText();
            String b = getTile(rIdx).findText();
            players[current_player].setTile(i, a.length() > 0 ? a.charAt(0) : '\0');
            players[current_player].setTile(rIdx, b.length() > 0 ? b.charAt(0) : '\0');
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
    
    // Switches to the next turn after a play has been finished
    public void tilesWereSubmitted() {
        recallTiles(); // Recall all currently placed tiles, if any are left
        selectTile(getTile(-1)); // De select the already selected tile, if there is one.
        current_player = (current_player + 1)%player_count; // Calculate the new player
        displayed_player = current_player; // Update the display to match the current player
        setHand(players[current_player].getHand()); // Update the hand to match the current player
        changeDisplay(0); // Update the display to show the current player
    }

    public void clearTileStorage() {
        placedTiles.clear(); // Clear the placed tiles, so they aren't recalled
    }

    // Displays an error to the screen for a specific duration
    public void displayError(String error, Color tColor, int duration) {
        displayError(error, tColor);
        final Timer check = new Timer(0, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                getError().repaint();
            }
        });
        Timer stop = new Timer(duration, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                check.stop();
                getError().setVisible(false);
            }
        });
        stop.setRepeats(false);
        check.start();
        stop.start();
    }

    // Creates the JPanel that displays the current player and their score
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

        left.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changeDisplay(-1);
            }
        });
        right.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changeDisplay(1);
            }
        });

        l.setConstraints(scoreboard, createConstraints(1, 2*SB_HEIGHT/1.0/MIN_HEIGHT, 0, 0, 1, 1, GridBagConstraints.BOTH));
        scoreboard.setPreferredSize(new Dimension(MIN_WIDTH, SB_HEIGHT));
        gamePanel.add(scoreboard);
    }

    // Creates the JPanel which holds each JButton that makes up the Scrabble board
    private void createBoard() {
        for (Component c : board.getComponents()) {
            board.remove(c);
        }
        board = new GridPanel(FRAME_WIDTH, FRAME_HEIGHT, BoxLayout.Y_AXIS); // Creates the main Board panel
        generateBoard();
        GridBagLayout l = (GridBagLayout) gamePanel.getLayout();
        l.setConstraints(board, createConstraints(1, 1, 0, 1, 1, 1, GridBagConstraints.BOTH)); // Set the constraints on the board
        board.setPreferredSize(new Dimension(COLS*TILE_SIZE, ROWS*TILE_SIZE));
        gamePanel.add(board); // Create and add the board to the application frame
    }

    private void generateBoard() {
        for (int r=0; r<ROWS; r++) { // Loops through each row on the board
            for (int c=0; c<COLS; c++) { // Loops through each col on the board
                int tile = Scrabble.getBoard()[r%ROWS][c%COLS]; // Create the tile value to determine the look of each button
                // Tile Colors From: https://htmlcolorcodes.com/
                final Tile temp = new Tile("", TILE_RADIUS, new Color(0xD8A772), MIN_OPACITY, r, c); // Blank Tile, represented by '0'
                if (tile == 1 || tile == 2) { // Tile is a Letter Tile, represented by a '1' or '2'            // 2 / 3 | B / P
                    temp.resetProperties((tile == 1 ? '2' : '3') + "x L", TILE_RADIUS, new Color(tile%2 == 1 ? 0x3498DB : 0x8E44AD), MIN_OPACITY);
                }
                else if (tile == 3 || tile == 4) { // Tile is a Word Tile, represented by '3' or '4'             // 2 / 3 | O / R
                    temp.resetProperties((tile == 3 ? '2' : '3') + "x W", TILE_RADIUS, new Color(tile%2 == 1 ? 0xE67E22 : 0xE74C3C), MIN_OPACITY); // Still testing colors
                }
                temp.setFont(new Font("Serif", Font.BOLD, FONT_SIZE)); // Set the font of the tile
                temp.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        if (!getError().isVisible() && selected_tile != -1 && calculateTile(temp.getPoint().r, temp.getPoint().c) != selected_tile) { // Checks if the selected tile is valid and is not the same tile
                            dispatchEvent(new CustomEvent(temp, PLACING_LETTER, temp.getPoint().r, temp.getPoint().c)); // Trigger an ActionEvent to allow the client to determine whether or not the tile can be placed
                        }
                        else if (!getError().isVisible() && temp.findText().length() == 1) { // Checks that the chosen tile holds a valid letter
                            dispatchEvent(new CustomEvent(temp, SELECTED_LETTER, temp.getPoint().r, temp.getPoint().c)); // Trigger an ActionEvent to allow the client to determine whether or not the tile can be selected
                        }
                    }
                });
                temp.setTextOpacity(175); // Set the opacity of the text displayed in the tile.
                temp.setSize(TILE_SIZE, TILE_SIZE); // Sets the size of the tile. Used in determining weights for the GridPanel
                board.add(temp, r, c, 1, 1, GridBagConstraints.BOTH); // Adds the tile to the Board
            }
        }
    }

    // Creates the JPanel that features each player's hand of tiles
    private void createHand() {
        GridBagLayout l = (GridBagLayout) gamePanel.getLayout();
        GridPanel hand = new GridPanel(MIN_WIDTH, MIN_HEIGHT, BoxLayout.X_AXIS);
        
        hand.add(makePadding((MIN_WIDTH - (9*H_TILE_SIZE + 8*H_X_OFF)), H_TILE_SIZE), 0, 0, 1, 1, GridBagConstraints.BOTH);
        hand.add(makePadding((MIN_WIDTH - (9*H_TILE_SIZE + 8*H_X_OFF)), H_TILE_SIZE), 0, (HAND_LENGTH * 2) + 4, 1, 1, GridBagConstraints.BOTH);

        final CurvedButton submit = new CurvedButton("Submit", TILE_RADIUS, new Color(0x2BAB49), HAND_OPACITY); // Creates the Skip button
        submit.setSize(H_TILE_SIZE, H_TILE_SIZE);
        submit.setFont(new Font("Serif", Font.PLAIN, FONT_SIZE));

        final CurvedButton quit = new CurvedButton("Quit", TILE_RADIUS, new Color(0x2196F3), HAND_OPACITY); // Creates the Quit button
        quit.setSize(H_TILE_SIZE, H_TILE_SIZE);
        quit.setFont(new Font("Serif", Font.PLAIN, FONT_SIZE));
        quit.setToggleColor(new Color(0x339933));
        
        check.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (quit.isPushed()) { // Check the button is still toggled
                    quit.setPushed(false); // De-Toggle the button, as the user didn't confirm
                }
            }
        });
        
        submit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (getError().isVisible()) { // Check that no other buttons are pressed
                    return; // Quit the function, if something has been pressed
                }
                dispatchEvent(new CustomEvent(submit, FINALIZED_PLAY, current_player)); // Dispatch Submission
                Timer auto = new Timer(ERROR_LENGTH, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        getError().setVisible(false);
                    }
                });
                auto.setRepeats(false);
                auto.start();
            }
        });
        quit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (getError().isVisible() && !quit.isPushed()) { // Checks there are no buttons toggled
                    return; // Quits the function if other buttons are toggled.
                }
                if (!quit.isPushed()) { // Checks that the button hasn't been pushed
                    quit.setPushed(true);
                    displayCondition("Are you sure you want to quit?\n\nPress 'Quit' to confirm, or wait to cancel.", Color.ORANGE);
                    check.start();
                }
                else { // The button has been pushed
                    quit.setPushed(false);
                    check.stop();
                    dispatchEvent(new CustomEvent(quit, QUIT_GAME)); // Let the client know the user requested to quit the game
                }
            }
        });

        hand.add(quit, 0, 1, 1, 1, GridBagConstraints.BOTH); // Quit button at [0][1] --> [1][1]
        hand.add(submit, 0, (HAND_LENGTH)*2 + 3, 1, 1, GridBagConstraints.BOTH); // Skip button at [0][17] --> [1][17]
        
        for (int i=0; i<=HAND_LENGTH*2; i++) {
            final Tile tile = new Tile("", (int)(TILE_RADIUS*1.5), new Color(0xD8A772), 200, i/2); // Create the letter tile
            tile.setFont(new Font("Serif", Font.PLAIN, (int)(FONT_SIZE*1.5))); // Set the font of the tile
            tile.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    selectTile(tile);
                }
            });
            tile.setSize(H_TILE_SIZE, H_TILE_SIZE);
            hand.add(i%2 == 1 ? tile : makePadding(H_X_OFF, H_TILE_SIZE), 0, i+2, 1, 1, GridBagConstraints.BOTH); // Add the Tile or padding
        }
        l.setConstraints(hand, createConstraints(1, H_TILE_SIZE/1.0/MIN_HEIGHT, 0, 2, 1, 1, GridBagConstraints.BOTH)); // Set the constraints on the hand
        hand.setPreferredSize(new Dimension(MIN_WIDTH, H_TILE_SIZE));
        gamePanel.add(hand); // Create and add the hand to the application frame;
    }

    // Creates the buttons featured beneath the Hand tiles
    private void createHandOptions() {
        GridBagLayout l = (GridBagLayout) gamePanel.getLayout();
        
        JPanel temp = (JPanel) makePadding(MIN_WIDTH, H_X_OFF); // A temp JPanel to offset the options by a few pixels
        temp.setPreferredSize(new Dimension(MIN_WIDTH, H_X_OFF)); // Sets the size of the JPanel
        l.setConstraints(temp, createConstraints(1, H_X_OFF/1.0/MIN_HEIGHT, 0, 3, 1, 1, GridBagConstraints.BOTH)); // Sets the constraints of the JPanel
        gamePanel.add(temp); // Adds the JPanel to the game panel

        GridPanel options = new GridPanel(MIN_WIDTH, MIN_HEIGHT, BoxLayout.X_AXIS);

        final CurvedButton recall = new CurvedButton("Recall", TILE_RADIUS, new Color(0xD0A600), HAND_OPACITY); // Creates the Recall button
        recall.setSize(2*H_TILE_SIZE - 4*H_X_OFF, OPTION_HEIGHT); // Sets the size of the Recall button
        recall.setFont(new Font("Serif", Font.BOLD, FONT_SIZE)); // Sets the font size

        final CurvedButton shuffle = new CurvedButton("Shuffle", TILE_RADIUS, new Color(0xD0A600), HAND_OPACITY); // Creates the Shuffler button
        shuffle.setSize(2*H_TILE_SIZE, OPTION_HEIGHT); // Sets the size of the Shuffle button
        shuffle.setFont(new Font("Serif", Font.BOLD, FONT_SIZE)); // Sets the font size
        
        final CurvedButton swap = new CurvedButton("Swap", TILE_RADIUS, new Color(0xD0A600), HAND_OPACITY); // Creates the Swap button
        swap.setSize(2*H_TILE_SIZE, OPTION_HEIGHT); // Sets the size of the Swap button
        swap.setFont(new Font("Serif", Font.BOLD, FONT_SIZE)); // Sets the font size
        swap.setToggleColor(new Color(0xE74C3C));

        final CurvedButton skip = new CurvedButton("Skip", TILE_RADIUS, new Color(0xD0A600), HAND_OPACITY); // Creates the Submit button
        skip.setSize(2*H_TILE_SIZE, OPTION_HEIGHT); // Sets the size of the Skip button
        skip.setFont(new Font("Serif", Font.BOLD, FONT_SIZE)); // Sets the font size
        skip.setToggleColor(new Color(0x339933)); // Set the color of the button when toggled.

        check.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (skip.isPushed()) { // Check the button is still toggled
                    skip.setPushed(false); // De-Toggle the button, as the user didn't confirm
                }
            }
        });

        recall.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (selected_tile >= 7) { // Checks if the selected tile is a Board tile
                    Tile t = getTile(selected_tile);
                    dispatchEvent(new CustomEvent(t, RECALLED_TILE, t.getPoint().r, t.getPoint().c)); // Let the client decide whether or not to recall the tile
                    dispatchEvent(new CustomEvent(t, SELECTED_LETTER)); // Let the client decide whether or not to deselect the tile
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
        skip.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (getError().isVisible() && !skip.isPushed()) { // Check that the Swap button isn't toggled
                    return; // Quit the function, if there's another toggled button
                }

                if (!skip.isPushed()) { // If the button isn't pushed
                    skip.setPushed(true); // Sets the toggled status of the Skip button to true
                    displayCondition("Do you really want to skip your turn?\n\nClick 'Skip' to confirm, or wait to cancel.", Color.YELLOW);
                    check.start(); // Start the 3 second timer.
                }
                else { // The button has been pushed
                    skip.setPushed(false); // Sets the toggled status of the Skip button to false
                    check.stop(); // Stop the timer from running, if it has been running. Prevents Timer overlap
                    dispatchEvent(new CustomEvent(skip, SKIPPED_TURN)); // Dispatch Skip
                }
            }
        });
        swap.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (getError().isVisible() && !swap.isPushed()) { // Check that the Skip button isn't toggled
                    return; // Quit the function, if there's another toggled button
                }

                if (!swap.isPushed()) { // If the button is not pushed
                    swap.setPushed(true); // Sets the swapping status to true
                    selectTile(getTile(selected_tile)); // Reselect the selected tile, just in case.
                    dispatchEvent(new CustomEvent(swap, SWAPPING_TILES)); // Lets the client determine how to react to the action of trying to swap tiles
                    displayCondition("Select the tiles you want to swap.\n\nClick 'Swap' to confirm or cancel.", Color.CYAN);
                }
                else { // The button has been pushed
                    swap.setPushed(false); // Sets the swapping status to false
                    if (swapping.size() > 0) { // Checks to see that there are tiles being swapped
                        String chars = "";
                        for (int i : swapping) { // Loops through each selected tile to be swapped
                            chars += getTile(i).findText(); // Count the tiles being put back into the pool
                            getTile(i).setText(""); // Resets the tile
                            getTile(i).setOriginal(""); // Resets the tile
                            players[current_player].setTile(i, '\0'); // Sets the player's tile to null
                            selectTile(getTile(i)); // Deselects the swapped tile
                        }
                        swapping.clear(); // Erases the list of swapped tiles
                        dispatchEvent(new CustomEvent(swap, TILES_SWAPPED, chars)); // Lets the client determine how to swap the tiles
                    }
                }
            }
        });
        
        // Padding on the left and right of the Option buttons
        options.add(makePadding((MIN_WIDTH - (9*H_TILE_SIZE + 8*H_X_OFF))/2, H_TILE_SIZE), 0, 0, 1, 1, GridBagConstraints.BOTH); // Left side padding
        options.add(makePadding((MIN_WIDTH - (9*H_TILE_SIZE + 8*H_X_OFF))/2, H_TILE_SIZE), 0, 9, 1, 1, GridBagConstraints.BOTH); // Right side padding
        //options.add(makePadding(H_TILE_SIZE/2, H_TILE_SIZE), 0, 1, 1, 1, GridBagConstraints.BOTH); // Extra left-padding to pad the area of the Quit Button

        // Padding in between each of the Option buttons
        options.add(makePadding(H_X_OFF, OPTION_HEIGHT), 0, 3, 1, 1, GridBagConstraints.BOTH);
        options.add(makePadding(H_X_OFF, OPTION_HEIGHT), 0, 5, 1, 1, GridBagConstraints.BOTH);
        options.add(makePadding(H_X_OFF, OPTION_HEIGHT), 0, 7, 1, 1, GridBagConstraints.BOTH);

        // Option Buttons that are placed below the Hand, each as long as 2 tiles
        options.add(swap, 0, 8, 1, 1, GridBagConstraints.BOTH); // Swap button at [0][8]
        options.add(recall,0, 2, 1, 1, GridBagConstraints.BOTH); // Recall button at [0][2]
        options.add(shuffle, 0, 4, 1, 1, GridBagConstraints.BOTH); // Shuffle button at [0][4]
        options.add(skip, 0, 6, 1, 1, GridBagConstraints.BOTH); // Skip button at [0][6]

        l.setConstraints(options, createConstraints(1, OPTION_HEIGHT/1.0/MIN_HEIGHT, 0, 4, 1, 1, GridBagConstraints.BOTH));
        options.setPreferredSize(new Dimension(MIN_WIDTH, OPTION_HEIGHT)); // Sets the default size of the Option Buttons
        gamePanel.add(options); // Adds the option buttons to the game panel
    }

    // Creates the JPanel that contains the components which make up the main menu
    private void createMenu() {
        mainPanel = new JPanel(new GridBagLayout()); // Initializes the mainPanel
        GridBagLayout l = (GridBagLayout) mainPanel.getLayout(); // Stores the GridBagLayout of the mainPanel

        GridPanel menu = new GridPanel(MIN_WIDTH, MIN_HEIGHT, BoxLayout.Y_AXIS); // Creates the Main Menu panel
    
        final CurvedButton startButton = new CurvedButton("Start", 15, Color.yellow, 100); // Creates a default start button
        startButton.setFont(new Font("Serif", Font.PLAIN, (int)(FONT_SIZE*2.5))); // 75 font size originally. Sets the font of the Start Button to size 75
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
        counter.setFont(new Font("Serif", Font.PLAIN, (int)(FONT_SIZE*2.5))); // 75 font size originalyl. Sets the font of the Counter to size 75
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

    // Creates the JPanel for layered text that displays most errors
    private void createError() {
        // PaintComponent problems: https://stackoverflow.com/questions/20833913/flickering-when-updating-overlapping-jpanels-inside-a-jlayeredpane-using-timeste
        CurvedLabel text = new CurvedLabel("Invalid Word", TILE_RADIUS, Color.RED);
        text.setEnabled(false);
        text.setFont(new Font("Serif", Font.BOLD, (int)(FONT_SIZE*1.5))); // 37 Font size originally.
        text.setBackground(Color.DARK_GRAY);
        text.setSize(COLS*TILE_SIZE, (int)((ROWS+0.5)*TILE_SIZE));
        text.setOpacity(ERROR_OPACITY);

        GridPanel error = new GridPanel(MIN_WIDTH, MIN_HEIGHT, BoxLayout.X_AXIS); // Creates the error panel
        error.add(text, 0, 0, 1, 1, GridBagConstraints.BOTH); // Adds the text to the panel
        error.setBounds(0, 3, COLS*TILE_SIZE, (int)((ROWS+0.5)*TILE_SIZE)); // The y-value of 3 is enough to generally offset the bottom gap of the panel and the board
        error.setOpaque(false); // Sets the panel as see-through
        error.setVisible(false); // Start with the error screen invisible.
        text.setVisible(false); // Start with the text as invisible

        JLayeredPane t = new JLayeredPane(); // Create the LayeredPanel, to layer components
        
        gamePanel.remove(board); // Remove the board from the JFrame
        board.setBounds(3, 3, (int)((COLS-0.125)*TILE_SIZE), (int)((ROWS+0.5)*TILE_SIZE)); // Set the general dimensions of the board
        
        t.add(error, JLayeredPane.POPUP_LAYER); // Add the error screen to the panel
        t.add(board, JLayeredPane.DEFAULT_LAYER); // Add the board to the panel
        
        GridBagLayout l = (GridBagLayout) gamePanel.getLayout(); // Get the LayoutManager for the GamePanel
        l.setConstraints(t, createConstraints(1, 1, 0, 1, 1, 1, GridBagConstraints.BOTH)); // Set the constraints
        t.setPreferredSize(new Dimension(COLS*TILE_SIZE, (int)((ROWS+0.5)*TILE_SIZE)));
        gamePanel.add(t); // Add the layered panel to the JFrame
    }

    // Creates the players used in the game
    private void createPlayers() {
        players = new Player[player_count];
        for (int i=0; i<player_count; i++) {
            dispatchEvent(new CustomEvent(frame, CREATE_PLAYER, i));
        }
    }

    // Updates the scoreboard display
    private void changeDisplay(int num) {
        displayed_player = Math.abs((displayed_player + num)%player_count);
        getPlayerDisplay().setText("Player:    "+(displayed_player + 1));
        getScoreDisplay().setText("Score:    "+players[displayed_player].getScore());
    }

    // Changes the displayed error
    private void displayError(String error, Color tColor) {
        if (getError().isVisible()) { // Checks if the error is currently being displayed
            return; // Quit the program
        }
        final CurvedLabel text = (CurvedLabel) getError().getComponent(0); // Stores the Text message
        text.setText(error); // Sets the text to be displayed
        getError().setVisible(true); // Sets the error visible
        text.setVisible(true); // Sets the error visible
        text.setOpacity(ERROR_OPACITY); // Sets the opacity the error starts at
        text.setColor(tColor);
    }

    // Displays specific errors based on conditions
    private void displayCondition(String text, Color c) {
        final boolean skipPushed = getSkipButton().isPushed();
        final boolean swapPushed = getSwapButton().isPushed();
        final boolean quitPushed = getQuitButton().isPushed();

        displayError(text, c); // Sets the display for the text

        // How to use Java Swing Timer: https://stackoverflow.com/questions/28521173/how-to-use-swing-timer-actionlistener
        Timer repeat = new Timer(0, new ActionListener() { // Needs to be zero, so it runs without potential overlap
            @Override
            public void actionPerformed(ActionEvent e) {
                if ((quitPushed && getQuitButton().isPushed()) || (swapPushed && getSwapButton().isPushed()) || (skipPushed && getSkipButton().isPushed())) { // Checks that the button is still pushed
                    getSwapButton().setToggleColor(new Color(swapping.size() > 0 ? 0x339933 : 0xE74C3C));
                    getError().repaint();
                }
                else { // The button has been de-pressed
                    getError().setVisible(false);
                    ((Timer) e.getSource()).stop(); // Stop this Timer from running
                }
            }
        });
        repeat.start(); // Start the timer running
    }

    // Creates GridBagConstraints
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

    // Creates an empty JPanel to act as padding
    private Component makePadding(int width, int height) {
        JPanel padding = new JPanel();
        padding.setSize(width, height);
        padding.setMinimumSize(new Dimension(width, height/2));
        padding.setMaximumSize(new Dimension((int) (width/1.0/MIN_WIDTH) * MAX_WIDTH, (int) (height/2.0/MIN_HEIGHT) * MAX_HEIGHT));
        return(padding);
    }

    // Dispatches an AWT Event to the client
    private void dispatchEvent(CustomEvent e) {
        for (CustomListener c : listeners) {
            c.actionPerformed(e);
        }
    }

    // Returns the JPanel for the players hand
    private GridPanel getHand() {
        return((GridPanel)(gamePanel.getComponent(2)));
    }

    // Returns the JPanel for the game board
    private GridPanel getBoard() {
        return(board);//new GridPanel(1, 1, BoxLayout.X_AXIS));//return((GridPanel)(gamePanel.getComponent(1)));
    }

    // Returns the JPanel for the options
    private GridPanel getOptions() {
        return((GridPanel) gamePanel.getComponent(4));
    }

    // Returns the Swap button
    private CurvedButton getSwapButton() {
        return((CurvedButton) getOptions().getComponent(5));
    }

    // Returns the Skip button
    private CurvedButton getSkipButton() {
        return((CurvedButton) getOptions().getComponent(8));
    }

    // Returns the Quit button
    private CurvedButton getQuitButton() {
        return((CurvedButton) getHand().getComponent(2));
    }

    // Returns the JPanel for the scoreboard
    private GridPanel getScoreboard() {
        return((GridPanel) gamePanel.getComponent(0));
    }

    // Returns the JLabel for the currently displayed player
    private CurvedLabel getPlayerDisplay() {
        return((CurvedLabel) getScoreboard().getComponent(6));
    }

    // Returns the JLabel for the currently displayed score
    private CurvedLabel getScoreDisplay() {
        return((CurvedLabel) getScoreboard().getComponent(7));
    }

    // Returns the JPanel for the error display
    private GridPanel getError() {
        JLayeredPane temp = (JLayeredPane) gamePanel.getComponent(1);
        return((GridPanel) temp.getComponent(0));
    }

    // Returns the calculated index of a Board Tile
    private int calculateTile(int r, int c) {
        return(6 + (r + 1) * (c + 1) + (COLS - (c + 1)) * r);
    }

    // Returns a list of all blank tiles that were played
    private Tile[] getEmptyTiles() {
        ArrayList<Tile> temp = new ArrayList<Tile>();
        for (Component c : getHand().getTileComponents()) {
            Tile t = (Tile) c;
            if (t.findText().equals("")) {
                temp.add(t);
            }
        }
        return(temp.size() == 0 ? Arrays.copyOf(getHand().getTileComponents(), getHand().getTileComponents().length, Tile[].class) : temp.toArray(new Tile[0]));
    }

    // Returns the index of a Tile within the Hand
    private int getTileIndex(Tile t) {
        Component[] list = getHand().getTileComponents();
        for (int i=0; i<list.length; i++) {
            if ((Tile) list[i] == t) {
                return(i);
            }
        }
        return(0);
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
