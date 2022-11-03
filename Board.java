import java.awt.event.*;
import javax.swing.*;
import java.awt.*;
class Board extends JFrame implements ActionListener {
    // create a frame
    static JFrame frame;
    
 
    // default constructor
    Board() {
        
    }
 
    // main function
    public static void main(String args[]) {
        // create a frame
        frame = new JFrame("Scrabble");
        int rows, cols = rows = 15;
 
        try {
            // set look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
        }

        GridLayout grid = new GridLayout(rows,cols); // Main Board layout
        JPanel board = new JPanel(grid); // Main Board panel
        
        // https://stackoverflow.com/questions/29379441/java-set-transparency-on-color-color-without-using-rgbs
        // https://stackoverflow.com/questions/6256483/how-to-set-the-button-color-of-a-jbutton-not-background-color
        for (int r=0; r<rows; r++) {
            for (int c=0; c<cols; c++) {
                final int rIndex = r; // Make final so accessible at any time
                final int cIndex = c; // Make final so accessible at any time
                final int tile = Scrabble.getVal(r%rows, c%cols); // Make final so accessible in @Overridden methods
                if (tile == 1 || tile == 2) { // Tile is a Letter Tile, represented by a '1' or '2'
                    final String text = (tile == 1 ? '2' : '3') + "x L"; // 2 or 3 * Letter Bonus
                    final Color color = new Color(0x4274FF); // Blue Tile
                }
                else if (tile == 3 || tile == 4) { // Tile is a Word Tile, represented by '3' or '4'
                    final String text = (tile == 3 ? '2' : '3') + "x W"; // 2 or 3 * Word Bonus
                    final Color color = new Color(0xD7381C); // Red Tile
                }
                else { // Tile is a Blank Tile, represented by '0'
                    final String text = ""; // Blank Text
                    final Color color = new Color(0xFFFFFF); // White Tile
                }
                temp = new JButton(text) {
                    @Override
                    public void paintComponent(Graphics g) {
                        g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), tile%2 == 1 ? 25 : 100)); // 75% Opacity, if tile is 1 or 3
                        g.fillRect(0, 0, getSize().width, getSize().height);
                        super.paintComponent(g);
                    }
                };
                temp.setContentAreaFilled(false); // Change how the JButton paints the borders, so I can paint the border below
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
        frame.add(board); // Add the grid to the application frame
        frame.setSize(1000, 1000); // Set the application frame to 1000 x 1000
        frame.setVisible(true); // Set the application frame visible
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO Auto-generated method stub
        
    }
}
