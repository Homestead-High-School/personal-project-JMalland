import java.util.*;
import java.io.File;
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
        Scrabble basic = new Scrabble(map);
        String one = "HAPPY";
        String two = "UNHAPPY";
        String three = "HELP";
        String four = "HUGS";
        String five = "SONG";
        String six = "PIN";
        /* End Result:
            U N H A P P Y   H U G S
                E                 O
                L             P I N
            H A P P Y             G
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
                else if (r == 10 && c > 8 && c < 11) {
                    basic.placeLetter(six.charAt(c-9), r, c);
                    System.out.print(six.charAt(c-9)+" ");
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
        System.out.println("Words Are Connected: "+basic.areConnected(true));
        
        System.out.println("A: "+Scrabble.getLetterValue('A')+" "+basic.getLetterCount('A'));
        System.out.println("B: "+Scrabble.getLetterValue('B')+" "+basic.getLetterCount('B'));
        System.out.println("C: "+Scrabble.getLetterValue('C')+" "+basic.getLetterCount('C'));
        System.out.println("_: "+Scrabble.getLetterValue(' ')+" "+basic.getLetterCount(' '));
        
        final Board b = new Board();
        final Scrabble real = new Scrabble(map);

        b.addCustomListener(new CustomListener() {
            @Override
            public void actionPerformed(CustomEvent e) {
                if (e.getID() == b.GAME_RUNNING) {
                    System.out.println("Game Running.");
                    b.startGame();
                }
                else if (e.getID() == b.DRAW_HAND) {
                    System.out.println("Hand Drawn.");
                    b.setHand(real.drawTiles(b.getBlankAmount()));
                    //b.setHand(new char[] {'L', 'I', 'T', 'I', 'P', 'T', 'M'});
                }
                else if (e.getID() == b.SELECTED_HAND) {
                    System.out.println("Selected Tile.");
                }
                else if (e.getID() == b.SHUFFLED_TILES) {
                    System.out.println("Shuffled Tiles.");
                }
                else if (e.getID() == b.PLACING_LETTER || e.getID() == b.SELECTED_LETTER) {
                    if (real.notYetPlaced(e.getRow(), e.getCol())) {
                        if (e.getID() == b.PLACING_LETTER) {
                            System.out.println("Placing To: "+((Tile)(e.getSource())).findText());
                            b.placeTile((Tile) e.getSource());
                        }
                        else {
                            System.out.println("Selecting: "+((Tile)(e.getSource())).findText());
                            b.selectTile((Tile) e.getSource());
                        }
                    }
                }
                else if (e.getID() == b.PLACED_LETTER) {
                    Tile t = (Tile) e.getSource();
                    System.out.println("Placed Tile @ ["+t.getPoint().r+"]["+t.getPoint().c+"]. Contains Letter: "+t.findText()+".");
                    real.placeLetter(e.getChar(), e.getRow(), e.getCol());
                }
                else if (e.getID() == b.FINALIZED_PLAY) {
                    System.out.println("Submitted Letters: "+real.getPlacedLetters()+".");
                    int score = real.submitWordPlacement();
                    System.out.println("Submitted Play Is Worth: "+score);
                    if (score > 0) {
                        b.tilesWereSubmitted();
                    }
                    else {
                        //b.displayError("Invalid Word");
                    }
                }
                else if (e.getID() == b.RECALLED_TILE) {
                    if (real.notYetPlaced(e.getRow(), e.getCol())) {
                        System.out.println("Recalled Placed Tile: "+e.getRow()+" "+e.getCol());
                        b.recallTile((Tile) e.getSource());
                        real.recallTile(e.getRow(), e.getCol());
                    }
                }
                else if (e.getID() == b.RECALLED_ALL) {
                    System.out.println("Recalled All Tiles.");
                    real.clearWordPlacement();
                }
            }
        });
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
