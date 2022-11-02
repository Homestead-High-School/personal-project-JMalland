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
    private TreeMap<Character, HashMap<Integer, HashMap<String, String>>> list = null;
    private ArrayList<Point> coords;
    private ArrayList<Character> chars;

    public Scrabble(TreeMap<Character, HashMap<Integer, HashMap<String, String>>> list) {
        this.list = list;
        coords = new ArrayList<Point>();
    }

    public void placeLetter(char l, int r, int c) {
        if (r > map.length || c > map[0].length) {
            throw new IllegalArgumentException("Index Out Of Bounds For Map Placement");
        }
        chars.add(l); // Append character
        coords.add(new Point(r, c)); // Append character coordinates
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
            if (row == -1 || p.y != row) {
                row = p.y; // Set the current row
                System.out.println(current); // Display Word
                if (!current.equals("")) {
                    if (!list.get(current.charAt(0)).get(current.length()).containsKey(current)) {
                        return(false); // Map doesn't contain the word created by the combination of letters.
                    }
                    current = "";
                }
            }
            current += chars.get(i);
        }
        Collections.sort(coords, new Comparator<Point>() { // Sort By Rows
            @Override
            public int compare(Point o1, Point o2) {
                if (o1.x != o2.x) {
                    return(o1.x - o2.x);
                }
                else { // 2,3 2,5 2,6 2,4
                    return(o1.y - o2.y);
                }
            }
        });
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
