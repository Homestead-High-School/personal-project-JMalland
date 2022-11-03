import java.awt.event.*;
import javax.swing.*;
import java.awt.*;
class Board extends JFrame implements ActionListener {
    
    private JFrame frame;
    private JPanel board = new JPanel();
    private JPanel hand = new JPanel();
    private Player[] players;
    private int index;
    private int rows, cols;
 
    // default constructor
    Board(int p) {
        players = new Player[p];
        frame = new JFrame("Scrabble");
        rows = Scrabble.getBoard().length;
        cols = Scrabble.getBoard()[0].length;
        
        try {
            // set look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
        }
        
        for (int i=0; i<players.length; i++) {
            players[i] = new Player("Player "+(i+1), new char[0]);
        }

        index = 0; // Player Index
        // https://stackoverflow.com/questions/70523527/how-to-stop-components-adapting-to-the-size-of-a-jpanel-that-is-inside-a-jscroll
        createBoard(); // Create the game board
        createHand(); // Create the player's hand
        JPanel panel = new JPanel(new GridLayout(2, 1));
        panel.add(board); // Add the grid to the application frame
        panel.add(hand); // Add the hand to the application frame;
        frame.add(panel);
        frame.setSize(1000, 1000); // Set the application frame to 1000 x 1000
        frame.setVisible(true); // Set the application frame visible
    }
 
    // Board Creation
    private void createBoard() {
        GridLayout grid = new GridLayout(rows,cols); // Main Board layout
        board = new JPanel(grid); // Main Board panel
        
        // https://stackoverflow.com/questions/29379441/java-set-transparency-on-color-color-without-using-rgbs
        // https://stackoverflow.com/questions/6256483/how-to-set-the-button-color-of-a-jbutton-not-background-color
        for (int r=0; r<rows; r++) {
            for (int c=0; c<cols; c++) {
                final int rIndex = r; // Make final so accessible at any time
                final int cIndex = c; // Make final so accessible at any time
                int tile = Scrabble.getVal(r%rows, c%cols); // Create the tile value to determine the look of each button
                JButton temp = createButton("", new Color(0xFFFFFF), tile); // Blank Tile, represented by a '0'
                if (tile == 1 || tile == 2) { // Tile is a Letter Tile, represented by a '1' or '2'
                    temp = createButton((tile == 1 ? '2' : '3') + "x L", new Color(0x4274FF), tile);
                }
<<<<<<< HEAD
                temp = new JButton(text) {
                    public void paintComponent(Graphics g) {
                        Color color = new Color(0xFFFFFF);
                        if (tile == 1 || tile == 2) { // Letter Bonus
                            color = new Color(0x4274FF); // Blue Tile
                        }
                        else if (tile == 3 || tile == 4) { // Word Bonus
                            color = new Color(0xD7381C); // Red Tile
                        }
                        g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), tile == 1 || tile == 3 ? 37 : 100)); // 75% Opacity, if tile is 1 or 3
                        g.fillRect(0, 0, getSize().width, getSize().height);
                        super.paintComponent(g);
                    }
                };
                temp.setContentAreaFilled(false);
=======
                else if (tile == 3 || tile == 4) { // Tile is a Word Tile, represented by '3' or '4'
                    temp = createButton((tile == 3 ? '2' : '3') + "x W", new Color(0xD7381C), tile);
                }
                temp.setContentAreaFilled(false); // Change how the JButton paints the borders, so I can paint the border below
>>>>>>> 5285d3e42663ce2597ddda3c523dd754e5edaf8c
                // https://stackoverflow.com/questions/33954698/jbutton-change-default-borderhttps://stackoverflow.com/questions/33954698/jbutton-change-default-border
                // Maybe I should make the borders appear curved?
                temp.setBorder(BorderFactory.createLineBorder(Color.black, 1)); // Create each tile with a black border
                // Action Listener: https://stackoverflow.com/questions/22580243/get-position-of-the-button-on-gridlayout
                temp.addActionListener(new ActionListener() { // Create an actionListener to react when the button is interacted with
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        // Pass the resultant actions to the Scrabble Game? // Board.getComponent(n)
                        // Or, have a method return a list of all tiles, and add actionListeners inside a seperate class or the Scrabble class?
                        System.out.println("rowIndex "+rIndex+" columnIndex "+cIndex);
                    }
                });
                temp.setSize(50, 50); // Set tile size
                board.add(temp); // Add tile to the grid
            }
        }
        board.setMaximumSize(new Dimension(rows*50, cols*50));
        board.setPreferredSize(new Dimension(rows*50, cols*50));
    }

    private void createHand() {
        GridLayout grid = new GridLayout(1,7); // Main Hand layout
        hand = new JPanel(grid); // Main Hand Panel
        for (int i=0; i<7; i++) {
            JLabel tile = new JLabel("W") {
                @Override
                public void paintComponent(Graphics g) {
                    g.setColor(new Color(0xBA7F40)); // 75% Opacity, if tile is 1 or 3
                    g.fillRect(0, 0, getSize().width, getSize().height);
                    super.paintComponent(g);
                }
            };
            tile.setSize(50, 50);
            tile.setBorder(BorderFactory.createLineBorder(Color.black, 1));
            hand.add(tile);
        }
        hand.setMaximumSize(new Dimension(rows*50, cols*50));
        hand.setPreferredSize(new Dimension(rows*50, cols*50));
    }

    private static JButton createButton(final String text, final Color color, final int tile) {
        JButton temp = new JButton(text) {
            @Override
            public void paintComponent(Graphics g) {
                g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), tile%2 == 1 ? 25 : 100)); // 75% Opacity, if tile is 1 or 3
                g.fillRect(0, 0, getSize().width, getSize().height);
                super.paintComponent(g);
            }
        };
        return(temp);
    }

    public JButton[][] getBoard() {
        JButton[][] list = new JButton[rows][cols]; // Creates a 2D Array of JButtons
        for (int i=0; i<rows*cols; i++) { // Loops through each JButton on the board
            list[i/rows][i%cols] = (JButton)(board.getComponent(i)); // Adds the JButton to the array
        }
        return(list); // Returns the array
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO Auto-generated method stub
    }
}
