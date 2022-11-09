import java.util.TreeMap;
import java.util.HashMap;
import java.util.Random;
import java.awt.Point;
import java.util.Comparator;

public class Scrabble {
    private static int[] tiles = new int[] {9, 2, 2, 2, 12, 2, 3, 2, 9, 1, 1, 4, 2, 6, 8, 2, 1, 6, 4, 6, 4, 2, 2, 1, 2, 1, 2}; // List of tile amounts, each corresponding to a letter
    private static final int[] values = new int[] {1, 3, 3, 2, 1, 4, 2, 4, 1, 8, 5, 1, 3, 1, 1, 3, 10, 1, 1, 1, 1, 4, 4, 8, 4, 10, 0}; // List of tile values, each corresponding to a letter
    private static final int[][] board = new int[][] {
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
    private TreeMap<Character, HashMap<String, String>> list;
    private char[][] map = new char[15][15];
    private HashMap<Point, Character> items;
    private int numTiles = 0;

    public Scrabble(TreeMap<Character, HashMap<String, String>> list) {
        this.list = list; // Map of possible words, sorted by first character, A-Z
        items = new HashMap<Point, Character>(); // Map of coordinates, each pointing to a char
        numTiles = 100; // Set the default number of tiles
    }

    public char[] drawTiles() {
        Random rand = new Random();
        char[] hand = new char[7];
        if (numTiles > 7) { // Check to see if enough tiles left to draw
            for (int i=0; i<hand.length; i++) {
                do { // Run the loop while the chosen character is empty
                    hand[i] = (char)(65+rand.nextInt(27)); // Choose a random character, from A-Z or SPACE
                    hand[i] = (hand[i]-65 == 26) ? 32 : hand[i]; // If the character was SPACE, fix the value
                    // NEED TO DECREMENT THE OVERALL TILE COUNT
                    // ENDS UP WITH TOO MANY INCORRECT LETTER AMOUNTS IN THE DEFAULT HANDS
                } while (getLetterCount(hand[i]) == 0);
                tiles[hand[i]-65 < 0 ? 26 : hand[i]-65] -= 1; // Decrements the count
            }
            numTiles -= 7; // Remove 7 tiles from the count, since a hand is 7 large
            return(hand); // Return the chosen hand
        }
        else { // Throw Exception because extra tiles are unknown at the moment
            throw new IllegalArgumentException("Out Of Tiles. Didn't Know What To Do");
        }
    }

    public int getLetterValue(char c) {
        return(c == 32 ? Scrabble.values[26] : Scrabble.values[c-65]);
    }

    public int getLetterCount(char c) {
        return(c == 32 ? Scrabble.tiles[26] : Scrabble.tiles[c-65]);
    }

    public void placeLetter(char l, int r, int c) {
        if (r > map.length || c > map[0].length) { // Throw an exception if I'm stupid enough to make such a horrible mistake
            throw new IllegalArgumentException("Index Out Of Bounds For Map Placement");
        }
        items.put(new Point(c, r), l); // Append the coordinate, pointing to the character
    }

    public boolean validWordPlacement() {
        return(calcRowOrCol(true) && calcRowOrCol(false)); // Check that the words formed by rows and columns are valid
    }

    private boolean calcRowOrCol(final boolean rowOrCol) {
        // Sort rows & cols by both row and col index, to compare words in same column and row.
        // https://stackoverflow.com/questions/2784514/sort-arraylist-of-custom-objects-by-property
        TreeMap<Point, Character> map = new TreeMap<Point, Character>(new Comparator<Point>() { // Sort By Columns
            @Override
            public int compare(Point o1, Point o2) {
                if ((rowOrCol && o1.x != o2.x) || (!rowOrCol && o1.y != o2.y)) { // Each x value is the column of the point
                    return(rowOrCol ? o1.x - o2.x : o1.y - o2.y); // Sort by column/row placement first
                }
                else { // Each y value is the row of the point
                    return(rowOrCol ? o1.y - o2.y : o1.x - o2.x); // Sort by row/column placement second
                }
            }
        });
        map.putAll(items); // Add all placed characters to the map to be sorted
        String current = ""; // String to store the current word, formed by row or column
        int position = -1; // Stores the position of the current word to mark when the loop shifts rows or columns
        for (Point p : map.keySet()) { // Loop through each character, forming each possible word in the list
            int posUsed = rowOrCol ? p.x : p.y; // Use X if 'rowOrCol' is true, otherwise use Y
            if (position == -1 || posUsed != position || map.get(p) == ' ') { // The point doesn't follow the previous one
                if (isLegalWord(current)) { // Check to see if the word is valid
                    position = posUsed; // Set the current position
                    current = ""; // Reset the string to start the word over again
                }
                else {
                    return(false); // Return false if invalid
                }
            }
            if (map.get(p) != ' ') { 
                current += map.get(p); // Only add the character if it isn't a space
            }
        }
        return(isLegalWord(current)); // Returns whether or not the final word is valid
    }
    
    private boolean isLegalWord(String word) {
        if (!word.equals("") && word.length() > 2) { // If the word isn't null
            System.out.println("\t"+word); // Print the word, if it's real.
        }
        return((word.equals("") || word.length() < 2) || list.get(word.charAt(0)).containsKey(word)); // Map does or doesn't contain the word
    }

    // Return the scrabble board
    public static int[][] getBoard() {
        return(Scrabble.board.clone()); // Return a clone of the board, so it isn't potentially edited
    }

    // Return the scrabble board tile
    public static int getVal(int r, int c) {
        if (r > Scrabble.board.length || c > Scrabble.board.length) { // Throw an exception if I'm stupid enough to make such a horrible mistake.
            throw new IllegalArgumentException("Index Out Of Bounds For Scrabble Board");
        }
        return(Scrabble.board[r][c]);
    }
}
