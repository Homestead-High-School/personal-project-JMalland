import java.util.*;

public class Player {
    private String name;
    private TreeMap<Character, Integer> tiles;
    private int score;

    public Player(String name, char[] hand) {
        score = 0;
        this.name = name;
        tiles = new TreeMap<Character, Integer>();
        for (char c : hand) {
            if (!tiles.containsKey(c)) {
                tiles.put(c, 0);
            }
            tiles.put(c, tiles.get(c)+1);
        }
    }

    public Character[] getHand() {
        return(tiles.keySet().toArray(new Character[0])); // Return a clone, so it cannot be edited.
    }

    public void playedWord(int total) {
        score += total; // Increase score by total worth.
    }
}
