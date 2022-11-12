import java.awt.*;

public class Tile extends CurvedButton {
    Tile pointsTo = null;
    int letterBonus = 1;
    int wordBonus = 1;
    int tileValue = 0;
    String originalText = "";
    Point point = null;

    public Tile() {
        super();
    }
    
    public Tile(String s, int r, int l, int w) {
        super(s, r);
        originalText = s;
        letterBonus = l;
        wordBonus = w;
    }

    public Tile(String s, int r, Color c, int o, int l, int w) {
        super(s, r, c, o);
        originalText = s;
        letterBonus = l;
        wordBonus = w;
    }

    // Method to swap the properties of two tiles, while keeping their location the same
    public static void swapTiles(Tile a, Tile b) {
        String aText = a.findText(); // Temp to hold A's text
        String aOrigin = a.originalText; // Temp to hold A's original text
        Tile aPointing = a.pointsTo; // Temp to hold the tile A points towards
        Tile bPointing = b.pointsTo; // Temp to hold the tile B points towards
        int aLBonus = a.letterBonus; // Temp to hold A's word bonus
        int aWBonus = a.wordBonus; // Temp to hold A's letter bonus
        int aTileVal = a.tileValue; // Temp to hold A's tile value
        Point aPoint = a.point; // Temp to hold the Point A is at
        // Swaps all of Tile A's properties with B's
        a.setText(b.findText());
        a.setOriginal(b.getOriginal());
        a.setPointingTo(bPointing);
        a.letterBonus = b.letterBonus;
        a.wordBonus = b.wordBonus;
        a.tileValue = b.tileValue;
        a.setPoint(b.getPoint());
        // Swaps all of Tile B's properties with A's original properties
        b.setText(aText);
        b.setOriginal(aOrigin);
        b.setPointingTo(aPointing);
        b.letterBonus = aLBonus;
        b.wordBonus = aWBonus;
        b.tileValue = aTileVal;
        b.setPoint(aPoint);
    }

    public void setValue(int v) {
        tileValue = v;
    }

    public void setPoint(Point p) {
        point = p;
    }

    public void setOriginal(String s) {
        originalText = s;
    }

    public Point getPoint() {
        return(point);
    }

    public int getWordBonus() {
        return(wordBonus);    
    }

    public int getLetterBonus() {
        return(letterBonus);
    }

    // Sets the text of the Tile while saving the original text
    public void swapText(String s) {
        originalText = super.findText(); // Store the original text from the parent class
        super.setText(s); // Set the text from the parent class
    }

    // Returns the original text of the Tile
    public String getOriginal() {
        return(originalText);
    }
    
    public void setPointingTo(Tile p) {
        pointsTo = p; // Sets the current tile pointing towards Tile 'p'
        if (p != null) { // Checks if Tile 'p' isn't null
            p.pointsTo = this; // Sets Tile 'p' pointing towards the current tile
        }
    }

    public Tile getPointingTo() {
        return(pointsTo);
    }
}
