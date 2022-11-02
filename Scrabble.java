import java.util.TreeMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.ArrayList;
import java.awt.Point;
import java.util.Collections;
import java.util.Comparator;

public class Scrabble {
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
    private char[][] map = new char[15][15];
    private TreeMap<Character, TreeMap<Integer, HashMap<String, String>>> list = null;
    private ArrayList<Point> coords;
    private ArrayList<Character> chars;

    public Scrabble(TreeMap<Character, TreeMap<Integer, HashMap<String, String>>> list) {
        this.list = list;
        coords = new ArrayList<Point>();
        chars = new ArrayList<Character>();
    }

    public void placeLetter(char l, int r, int c) {
        if (r > map.length || c > map[0].length) {
            throw new IllegalArgumentException("Index Out Of Bounds For Map Placement");
        }
        chars.add(l); // Append character
        coords.add(new Point(c, r)); // Append character coordinates
    }

    public boolean validWordPlacement() {
        // Sort rows & cols by both row and col index, to compare words in same column and row.
        // https://stackoverflow.com/questions/2784514/sort-arraylist-of-custom-objects-by-property
        Collections.sort(coords, new Comparator<Point>() { // Sort By Rows
            @Override
            public int compare(Point o1, Point o2) {
                if (o1.y != o2.y) {
                    return(o1.y - o2.y);
                }
                else { // 2,3 2,5 2,6 2,4
                    return(o1.x - o2.x);
                }
            }
        });
        String current = "";
        int row = -1;
        for (int i=0; i<coords.size(); i++) { // Loop through each character, forming each possible word in the list
            Point p = coords.get(i);
            System.out.print(p.y+","+p.x+"\t");
            if (row == -1 || p.y != row || chars.get(i) == ' ') {
                row = p.y; // Set the current row
                if (!current.equals("") && current.length() > 1) {
                    System.out.println("\n"+current); // Display Word
                    if (!isLegalWord(current)) { // Word may be flipped
                        return(false);
                    }
                }
                else if (!current.equals("")) {
                    System.out.println();
                }
                current = "";
            }
            current += chars.get(i);
        }
        System.out.println("\n"+current);
        if (!current.equals("")) {
            if (!isLegalWord(current)) {
                return(false);
            }
        }
        System.out.println("Columns:");
        Collections.sort(coords, new Comparator<Point>() { // Sort By Columns
            @Override
            public int compare(Point o1, Point o2) {
                if (o1.x != o2.x) {
                    return(o1.x - o2.x);
                }
                else {
                    return(o1.y - o2.y);
                }
            }
        });
        current = "";
        int col = -1;
        for (int i=0; i<coords.size(); i++) { // Loop through each character, forming each possible word in the list
            Point p = coords.get(i);
            System.out.print(p.y+","+p.x+"\t");
            if (col == -1 || p.x != col || chars.get(i) == ' ') {
                col = p.x; // Set the current column
                if (!current.equals("") && current.length() > 1) {
                    System.out.println("\n"+current); // Display Word
                    if (!isLegalWord(current)) { // Word may be upside down
                        return(false);
                    }
                }
                else if (!current.equals("")) {
                    System.out.println();
                }
                current = "";
            }
            current += chars.get(i);
        }
        //coords.clear(); // Wipe the words of the placement. Probably don't want to do
        //chars.clear(); // Wipe the words of the placement.
        return(true);
    }
    
    private boolean isLegalWord(String word) {
        if (word.length() < 2 || !list.get(word.charAt(0)).containsKey(word.length())) {
            return(false); // Word doesn't exist at that character or length
        }
        else {
            return(list.get(word.charAt(0)).get(word.length()).containsKey(word)); // Map does or doesn't contain the word
        }
    }

    // Return the scrabble board
    public static int[][] getBoard() {
        return(Scrabble.board.clone()); // Return a clone of the board, so it isn't potentially edited
    }

    // Return the scrabble board tile
    public static int getVal(int r, int c) {
        if (r > Scrabble.board.length || c > Scrabble.board.length) {
            throw new IllegalArgumentException("Index Out Of Bounds For Scrabble Board");
        }
        return(Scrabble.board[r][c]);
    }
}
