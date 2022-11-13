import java.util.TreeMap;
import java.util.HashMap;
import java.util.Random;
import java.util.Comparator;

// Will eventually need to create a method to find all directly connected points to a valid placed word

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
    private HashMap<Point, Integer> counted;
    private int numTiles = 0;

    public Scrabble(TreeMap<Character, HashMap<String, String>> list) {
        this.list = list; // Map of possible words, sorted by first character, A-Z
        items = new HashMap<Point, Character>(); // Map of coordinates, each pointing to a char
        counted = new HashMap<Point, Integer>();
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

    public static int getLetterValue(char c) {
        return(c == 32 ? Scrabble.values[26] : Scrabble.values[c-65]);
    }

    public int getLetterCount(char c) {
        return(c == 32 ? Scrabble.tiles[26] : Scrabble.tiles[c-65]);
    }

    public void recallTile(int row, int col) {
        items.remove(new Point(row, col));
    }

    public void placeLetter(char l, int r, int c) {
        if (r > map.length || c > map[0].length) { // Throw an exception if I'm stupid enough to make such a horrible mistake
            throw new IllegalArgumentException("Index Out Of Bounds For Map Placement");
        }
        items.put(new Point(r, c), l); // Append the coordinate, pointing to the character
    }

    public int calculatePlacementValue() {
        calcRowOrCol(true); // Calculate the row values
        calcRowOrCol(false); // Calculate the col values
        int score = 0;
        for (Point p : counted.keySet()) {
            //System.out.print("["+p.getRow()+"]["+p.getCol()+"] --> "+counted.get(p)+"    ");
            score += counted.get(p);
        }
        counted.clear();
        //System.out.println();
        return(score);
    }

    public boolean validWordPlacement() {
        return(areConnected(true) && calcRowOrCol(true) && calcRowOrCol(false)); // Check that the words formed by rows and columns are valid
    }

    public void clearWordPlacement() {
        counted.clear(); // Clear the map of scored tiles
        items.clear(); // Clear the map of placed tiles
    }

    public boolean areConnected(boolean rowOrCol) {
        TreeMap<Point, Character> map = makeSortedMap(rowOrCol);
        boolean isConnected = true; // Stores the status of whether or not there's gaps between tiles in the row or column
        int altPosition = -1; // Stores the current tile position
        int position = -1;
        int length = 0;
        for (Point p : map.keySet()) { // Loops through each tile
            int posUsed = rowOrCol ? p.getRow() : p.getCol();
            int altPos = rowOrCol ? p.getCol() : p.getRow(); // Determines the current position
            if (altPosition == -1) { // Checks if the tile position is at its default
                altPosition = altPos; // Update the position to match the current
                position = posUsed;
                length ++;
            }
            else if (posUsed != position) { // Check if the position is no longer current
                if (length < 2) { // Check to see if the length is valid
                    isConnected = false; // Set the connectedness to false, since the length was invalid
                    break; // Quit the loop
                }
                position = posUsed; // Reset the position, since on a new row/col
                altPosition = altPos; // Reset the alternate position, since on a new row/col
                length = 0; // Reset the length, since on a new row/col
            }
            else if (altPos - altPosition > 1) { // Check if there's a gap greater than one between the tiles
                isConnected = false; // Set the connectedness to false
                break; // Quit the loop
            }
            else {
                altPosition = altPos; // Update the tile position to be current
                length ++;
            }
        }
        isConnected = !isConnected ? isConnected : length > 1;
        if (rowOrCol) {
            return(isConnected || areConnected(!rowOrCol)); // Return whether or not there are no gaps between tiles in either the row or col 
        }
        else {
            return(isConnected); // Return whether or not there are no gaps between tiles
        }
    }

    private boolean calcRowOrCol(final boolean rowOrCol) {
        // Eventually need to remove the " map.get(p) == ' ' " and implement a blank tile chooser.
        int score = 0;
        TreeMap<Point, Character> map = makeSortedMap(rowOrCol);
        HashMap<Point, Integer> letterMap = new HashMap<Point, Integer>();
        HashMap<Point, Integer> wordMap = new HashMap<Point, Integer>();
        String current = ""; // String to store the current word, formed by row or column
        int position = -1; // Stores the position of the current word to mark when the loop shifts rows or columns
        int wordMultiplier = 1;
        for (Point p : map.keySet()) { // Loop through each character, forming each possible word in the list
            int posUsed = rowOrCol ? p.getRow() : p.getCol(); // Use X if 'rowOrCol' is true, otherwise use Y
            int posVal = Scrabble.getVal(p.getRow(), p.getCol());
            if (position == -1 || posUsed != position || map.get(p) == ' ') { // The point doesn't follow the previous one
                if (isLegalWord(current) && score >= 0 && current.length() > 1) { // Check to see if the word is valid
                    for (Point x : letterMap.keySet()) { // Loop through each point in the letter map
                        wordMap.put(x, letterMap.get(x) * wordMultiplier); // Add the total point value to the word map
                    }
                }
                else if (current.length() > 1 && map.get(p) != ' ') {
                    score = -1; // Sets the score to negative because its invalid
                }
                wordMultiplier = 1; // Resets the word multiplier
                position = posUsed; // Set the current position
                current = ""; // Reset the string to start the word over again
            }
            if (map.get(p) != ' ') {
                current += map.get(p); // Only add the character if it isn't a space
            }
            int letterMultiplier = 1;
            if (posVal == 4 || posVal == 3) { // Checks if the tile is a word multiplier
                wordMultiplier *= posVal - 1; // Adds the value to the total multiplier
            }
            else if (posVal == 2 || posVal == 1) { // Checks if the tile is a letter multiplier
                letterMultiplier *= posVal + 1; // Adds the value to the letter multiplier
            }
            int charValue = Scrabble.getLetterValue(map.get(p)) * letterMultiplier; // Calculate the letter value
            letterMap.put(p, charValue); // Add the point to the letter map, per each word
        }
        
        if (isLegalWord(current) && score >= 0 && current.length() > 1) { // Check to see if the word is valid
            for (Point x : letterMap.keySet()) { // Loop through each point in the letter map
                wordMap.put(x, letterMap.get(x) * wordMultiplier); // Add the total point value to the word map
            }
        }
        else if (current.length() > 1) {
            score = -1; // Sets the score to negative, if invalid
        }
        System.out.println();
        for (Point x : wordMap.keySet()) {
            if (!counted.containsKey(x) || counted.get(x) < wordMap.get(x)) { // Check that the point hasn't been counted, or wasn't counted at the right value
                counted.put(x, wordMap.get(x)); // Update the counted tiles map to hold the tiles of highest value
            }
        }

        return(score == 0); // Returns whether or not the final word is valid
    }

    // Returns a TreeMap of sorted points, sorted based on column or row. Used to compare word placement
    private TreeMap<Point, Character> makeSortedMap(boolean rowOrCol) {
        // https://stackoverflow.com/questions/2784514/sort-arraylist-of-custom-objects-by-property
        TreeMap<Point, Character> map = new TreeMap<Point, Character>(new Comparator<Point>() { // Sort By Columns
            @Override
            public int compare(Point a, Point b) {
                if (rowOrCol ? a.getRow() != b.getRow() : a.getCol() != b.getCol()) {
                    return(rowOrCol ? a.getRow() - b.getRow() : a.getCol() - b.getCol());
                }
                else {
                    return(rowOrCol ? a.getCol() - b.getCol() : a.getRow() - b.getRow());
                }
            }
        });
        map.putAll(items); // Add all placed characters to the map to be sorted
        return(map);
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
