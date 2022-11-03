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
                JButton temp = new JButton();
                final int tile = Scrabble.getVal(r%rows, c%cols);
                String text = "";
                if (tile > 0) {
                    text += tile%2 == 1 ? "2x " : "3x ";
                    text += tile < 2 ? 'L' : 'W'; // Letter/Word Bonus
                }
                temp = new JButton(text) {
                    public void paintComponent(Graphics g) {
                        Color color = new Color(0xFFFFFF);
                        if (tile == 1 || tile == 2) { // Letter Bonus
                            color = new Color(0x4274FF); // Blue Tile
                        }
                        else if (tile == 3 || tile == 4) { // Word Bonus
                            color = new Color(0xD7381C); // Red Tile
                        }
                        g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), tile == 1 || tile == 3 ? 25 : 100)); // 75% Opacity, if tile is 1 or 3
                        g.fillRect(0, 0, getSize().width, getSize().height);
                        super.paintComponent(g);
                    }
                };
                temp.setContentAreaFilled(false);
                // https://stackoverflow.com/questions/33954698/jbutton-change-default-borderhttps://stackoverflow.com/questions/33954698/jbutton-change-default-border
                // Maybe I should make the borders appear curved?
                temp.setBorder(BorderFactory.createLineBorder(Color.black, 1));
                // Action Listener: https://stackoverflow.com/questions/22580243/get-position-of-the-button-on-gridlayout
                final int rIndex = r; // Make final so accessable at any time
                final int cIndex = c; // Make final so accessable at any time
                temp.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        // Pass the resultant actions to the Scrabble Game? // Board.getComponent(n)
                        // Or, have a method return a list of all tiles, and add actionListeners inside a seperate class or the Scrabble class?
                        System.out.println("rowIndex "+rIndex+" columnIndex "+cIndex);
                    }
        
                });
                temp.setSize(50, 50);
                board.add(temp);
            }
        }
        frame.add(board);
        frame.setSize(1000, 1000);
        frame.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO Auto-generated method stub
        
    }
}
