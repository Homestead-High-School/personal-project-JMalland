import java.util.*;
import java.io.File;
import java.awt.event.*;
import javax.swing.*;

import java.awt.*;
public class Main {
    public static void main(String[] args) {
        TreeMap<Character, HashMap<String, String>> map = parseAndStore("Words.txt"); // Get the sorted TreeMap
        int numWords = 0;
        for (char c : map.keySet()) { // Sorted by Character
            numWords += map.get(c).size();
        }
        System.out.println(numWords+" Words!");
        Scrabble basic = new Scrabble(map);
        String one = "HAPPY";
        String two = "UNHAPPY";
        String three = "HELP";
        String four = "HUGS";
        String five = "SOUP";
        String six = "PERP";
        /* End Result:
            U N H A P P Y   H U G S
                E                 O
                L                 U
            H A P P Y       P R E P
        */
        for (int r=0; r<12; r++) {
            for (int c=0; c<12; c++) {
                if (c < one.length() && r == 11) {
                    basic.placeLetter(one.charAt(c), r, c);
                    System.out.print(one.charAt(c)+" ");
                }
                else if (r < 11 && r > 8 && c == 2) {
                    basic.placeLetter(three.charAt(r-8), r, c);
                    System.out.print(three.charAt(r-8)+" ");
                }
                else if (r == 11 && c > 7) {
                    basic.placeLetter(six.charAt(c-8), r, c);
                    System.out.print(six.charAt(c-8)+" ");
                }
                else if (r == 8 && c > 7 && c < 12) {
                    basic.placeLetter(four.charAt(c-8), r, c);
                    System.out.print(four.charAt(c-8)+" ");
                }
                else if (c == 11 && r > 8) {
                    basic.placeLetter(five.charAt(r-8), r, c);
                    System.out.print(five.charAt(r-8)+" ");
                }
                else if (c < two.length() && r == 8) {
                    basic.placeLetter(two.charAt(c), r, c);
                    System.out.print(two.charAt(c)+" ");
                }
                else {
                    basic.placeLetter(' ', r, c);
                    System.out.print(". ");
                }
            }
            System.out.println();
        }
        System.out.println("Words Contained: ");
        boolean temp = basic.validWordPlacement();
        System.out.println("Words Are Valid: "+temp);
        
        System.out.println("A: "+basic.getLetterValue('A')+" "+basic.getLetterCount('A'));
        System.out.println("B: "+basic.getLetterValue('B')+" "+basic.getLetterCount('B'));
        System.out.println("C: "+basic.getLetterValue('C')+" "+basic.getLetterCount('C'));
        System.out.println("_: "+basic.getLetterValue(' ')+" "+basic.getLetterCount(' '));
        
        Board b = new Board();
        final JButton[][] tiles = b.getTiles();
        for (int r=0; r<tiles.length; r++) {
            for (int c=0; c<tiles[r].length; c++) {
                final int rIndex = r;
                final int cIndex = c;
                tiles[r][c].addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        System.out.println("Tile clicked @ ("+rIndex+", "+cIndex+").");
                        JButton b = tiles[rIndex][cIndex];
                        b.setLayout(new BorderLayout());
                        // The code below provides an example of how to change the specific x and y coordinates of a JComponent
                        //board.setBounds(board.getBounds().y+(rIndex*10), board.getBounds().x+(cIndex*10), board.getWidth(), board.getHeight());
                        System.out.println(b.getBounds().x+" "+b.getBounds().y);
                        b.setBounds(b.getBounds().x+1, b.getBounds().y+1, b.getWidth(), b.getHeight());
                    }
                });

                tiles[r][c].addComponentListener(new ComponentAdapter() {
                    @Override
                    public void componentResized(ComponentEvent e) {
                        // TODO Auto-generated method stub
                        //System.out.println("Tile: ("+rIndex+", "+cIndex+") Resized!");
                    }
                });
            }
        }
    }

    public static TreeMap<Character, HashMap<String, String>> parseAndStore(String file) {
        TreeMap<Character, HashMap<String, String>> map = new TreeMap<Character, HashMap<String, String>>(); // Default TreeMap to be returned
        try {
            Scanner input = new Scanner(new File(file)); // Use Scanner to parse the file
            while (input.hasNextLine()) { // Loop through each line in the file
                String[] arr = input.nextLine().trim().split("\\s", 2); // Store word for easy access
                char c = arr[0].charAt(0); // Frontmost character in the string
                if (arr[0].contains("#")) continue; // Skip if the word is commented.
                if (!map.containsKey(c)) { // Add an empty TreeMap if it doesn't exist
                    map.put(c, new HashMap<String, String>()); // Add the Character, corresponding to a TreeMap of Strings
                }
                map.get(c).put(arr[0], arr[1]); // Add the word and definition to the map
            }
        } catch (Exception e) { // Throw an exception, if necessary
            System.out.println("Error: "+e); // Print the stack trace.
        }
        return(map);
    }
}
