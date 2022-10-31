import java.awt.event.*;
import javax.swing.*;
import java.awt.*;
class Board extends JFrame implements ActionListener {
    // create a frame
    static JFrame frame;
 
    // create a textfield
    static JTextField l;
 
    // store operator and operands
    String s0, s1, s2;
 
    // default constructor
    Board() {
        s0 = s1 = s2 = "";
    }
 
    // main function
    public static void main(String args[]) {
        // create a frame
        frame = new JFrame("Scrabble");
 
        try {
            // set look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
        }

        GridLayout grid = new GridLayout(25,25); // Main Board layout
        JPanel board = new JPanel(grid); // Main Board panel
        int step = 0;
        for (int r=0; r<25; r++) {
            for (int c=0; c<25; c++) {
                JButton temp = new JButton();
                c += step;
                if ((r%2 == 0 && c%5 == 1) || (r%2 == 1 && c%5 == 4)) { // Make sure there are gaps between the different types.
                    temp.setText("W");
                }
                else if ((r%2 == 0 && c%5 == 3) || (r%2 == 1 && c%5 == 0)) {
                    temp.setText("L");
                }
                temp.setSize(50, 50);
                board.add(temp);
                c -= step;
            }
            step ++;
        }
        frame.add(board);
        frame.setSize(1000, 1000);
        frame.setVisible(true);
    }
    public void actionPerformed(ActionEvent e)
    {
        String s = e.getActionCommand();
 
        // if the value is a number
        if ((s.charAt(0) >= '0' && s.charAt(0) <= '9') || s.charAt(0) == '.') {
            // if operand is present then add to second no
            if (!s1.equals(""))
                s2 = s2 + s;
            else
                s0 = s0 + s;
 
            // set the value of text
            l.setText(s0 + s1 + s2);
        }
        else if (s.charAt(0) == 'C') {
            // clear the one letter
            s0 = s1 = s2 = "";
 
            // set the value of text
            l.setText(s0 + s1 + s2);
        }
        else if (s.charAt(0) == '=') {
 
            double te;
 
            // store the value in 1st
            if (s1.equals("+"))
                te = (Double.parseDouble(s0) + Double.parseDouble(s2));
            else if (s1.equals("-"))
                te = (Double.parseDouble(s0) - Double.parseDouble(s2));
            else if (s1.equals("/"))
                te = (Double.parseDouble(s0) / Double.parseDouble(s2));
            else
                te = (Double.parseDouble(s0) * Double.parseDouble(s2));
 
            // set the value of text
            l.setText(s0 + s1 + s2 + "=" + te);
 
            // convert it to string
            s0 = Double.toString(te);
 
            s1 = s2 = "";
        }
        else {
            // if there was no operand
            if (s1.equals("") || s2.equals(""))
                s1 = s;
            // else evaluate
            else {
                double te;
 
                // store the value in 1st
                if (s1.equals("+"))
                    te = (Double.parseDouble(s0) + Double.parseDouble(s2));
                else if (s1.equals("-"))
                    te = (Double.parseDouble(s0) - Double.parseDouble(s2));
                else if (s1.equals("/"))
                    te = (Double.parseDouble(s0) / Double.parseDouble(s2));
                else
                    te = (Double.parseDouble(s0) * Double.parseDouble(s2));
 
                // convert it to string
                s0 = Double.toString(te);
 
                // place the operator
                s1 = s;
 
                // make the operand blank
                s2 = "";
            }
 
            // set the value of text
            l.setText(s0 + s1 + s2);
        }
    }
}
