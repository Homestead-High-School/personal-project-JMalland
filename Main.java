import java.util.*;
import java.io.File;
import java.awt.Color;
import java.awt.event.*;
import javax.swing.*;
public class Main {
    public static void main(String[] args) {
        final TreeMap<Character, HashMap<String, String>> map = parseAndStore("Words.txt"); // Get the sorted TreeMap
        int numWords = 0;
        int maxWords = 0;
        for (char c : map.keySet()) { // Sorted by Character
            numWords += map.get(c).size();
            for (String s : map.get(c).keySet()) {
                if (s.length() > maxWords) maxWords = s.length();
            }
        }
        System.out.println(numWords+" Words!");
        System.out.println("Longest Word: "+maxWords);
        final Scrabble basic = new Scrabble(map);
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
        
        final Board b = new Board();

        final JPanel hand = b.getHandPanel();
        hand.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == b.SELECTED_HAND) {
                    System.out.println("Selected Tile: "+b.getSelectedTile());
                }
                else if (e.getClickCount() == b.PLACED_LETTER) {
                    System.out.println("Placed Tile!");
                    CurvedButton temp = (CurvedButton) b.getSelectedTile();
                    basic.placeLetter(temp.getText().charAt(0), e.getX(), e.getY());
                    JFrame frame = (JFrame) e.getSource();
                    JPanel game = (JPanel) frame.getComponent(0);
                    JPanel board = (JPanel) game.getComponent(0);
                    CurvedButton tile = (CurvedButton) board.getComponent((e.getX()+1)*(e.getY()+1) - 1);
                    tile.setText(temp.getText());
                }
            }
        });


        CurvedButton start = b.getStart();
        start.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Game Running...");
                b.startGame();
                Scrabble game = new Scrabble(map);
                b.setHand(game.drawTiles());
            }
        });

        final JButton[][] tiles = b.getTiles();
        for (int r=0; r<tiles.length; r++) {
            for (int c=0; c<tiles[r].length; c++) {
                final int rIndex = r;
                final int cIndex = c;
                tiles[r][c].addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        System.out.println("Tile clicked @ ("+rIndex+", "+cIndex+").");
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
