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
        int[][] scrabble = new int[][] {
            {4, 0, 0, 1, 0, 0, 0, 4, 0, 0, 0, 1, 0, 0, 4},
            {0, 3, 0, 0, 0, 2, 0, 0, 0, 2, 0, 0, 0, 3, 0},
            {0, 0, 3, 0, 0, 0, 1, 0, 1, 0, 0, 0, 3, 0, 0},
            {1, 0, 0, 3, 0, 0, 0, 1, 0, 0, 0, 3, 0, 0, 1},
            {0, 0, 0, 0, 3, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0},
            {0, 2, 0, 0, 0, 2, 0, 0, 0, 2, 0, 0, 0, 2, 0},
            {0, 0, 1, 0, 0, 0, 1, 0, 1, 0, 0, 0, 1, 0, 0},
            {4, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 4},
            {0, 0, 1, 0, 0, 0, 1, 0, 1, 0, 0, 0, 1, 0, 0},
            {0, 2, 0, 0, 0, 2, 0, 0, 0, 2, 0, 0, 0, 2, 0},
            {0, 0, 0, 0, 3, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0},
            {1, 0, 0, 3, 0, 0, 0, 1, 0, 0, 0, 3, 0, 0, 1},
            {0, 0, 3, 0, 0, 0, 1, 0, 1, 0, 0, 0, 3, 0, 0},
            {0, 3, 0, 0, 0, 2, 0, 0, 0, 2, 0, 0, 0, 3, 0},
            {4, 0, 0, 1, 0, 0, 0, 4, 0, 0, 0, 1, 0, 0, 4}
        };
        // https://stackoverflow.com/questions/29379441/java-set-transparency-on-color-color-without-using-rgbs
        // https://stackoverflow.com/questions/6256483/how-to-set-the-button-color-of-a-jbutton-not-background-color
        for (int r=0; r<rows; r++) {
            for (int c=0; c<cols; c++) {
                JButton temp = new JButton();
                switch(scrabble[r%rows][c%cols]) {
                    case(1):
                        temp = new JButton("2x L") {
                            public void paintComponent(Graphics g) {
                                Color temp = new Color(0x4274FF);
                                g.setColor(new Color(temp.getRed(), temp.getGreen(), temp.getBlue(), 75)); // 75% Opacity
                                g.fillRect(0, 0, getSize().width, getSize().height);
                                super.paintComponent(g);
                            }
                        };
                        break;
                    case(2):
                        temp = new JButton("3x L") {
                            public void paintComponent(Graphics g) {
                                g.setColor(new Color(0x4274FF));
                                g.fillRect(0, 0, getSize().width, getSize().height);
                                super.paintComponent(g);
                            }
                        };
                        break;
                    case(3):
                        temp = new JButton("2x W") {
                            public void paintComponent(Graphics g) {
                                Color temp = new Color(0xD7381C);
                                g.setColor(new Color(temp.getRed(), temp.getGreen(), temp.getBlue(), 75)); // 75% Opacity
                                g.fillRect(0, 0, getSize().width, getSize().height);
                                super.paintComponent(g);
                            }
                        };
                        break;
                    case(4):
                        temp = new JButton("3x W") {
                            public void paintComponent(Graphics g) {
                                g.setColor(new Color(0xD7381C));
                                g.fillRect(0, 0, getSize().width, getSize().height);
                                super.paintComponent(g);
                            }
                        };
                        break;
                }
                temp.setBorderPainted(true);
                temp.setContentAreaFilled(false);
                //temp.setOpaque(true);
                //temp.setForeground(new Color(0xCC1010));
                //temp.setOpaque(true);
                temp.setSize(50, 50);
                board.add(temp);
            }
        }
        frame.add(board);
        frame.setSize(1000, 1000);
        frame.setVisible(true);
    }
    public void actionPerformed(ActionEvent e) {
    }
}
