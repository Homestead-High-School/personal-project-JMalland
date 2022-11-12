import java.awt.*;

public class Tile extends CurvedButton {
    Tile pointsTo = null;
    int letterBonus = 1;
    int wordBonus = 1;
    int tileValue = 0;
    String originalText = "";
    String prevText = "";
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
        // Temp variables to hold all of A's properties to be used in setting B's
        String aText = a.findText(); 
        String aOrigin = a.originalText;
        Tile aPointing = a.pointsTo;
        Tile bPointing = b.pointsTo;
        int aLBonus = a.letterBonus;
        int aWBonus = a.wordBonus;
        int aTileVal = a.tileValue;
        Point aPoint = a.point;
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

    public static void swapData(Tile a, Tile b) {
        // Temp variables to hold all of A's settings to be used in changing B's
        Color aColor = a.getColor();
        int aOpacity = a.getOpacity();
        Color aBorderC = a.getBorderColor();
        int aBorderS = a.getBorderSize();
        Font aFont = a.getFont();
        // Swaps all of Tile A's settings with B's
        a.setColor(b.getColor());
        a.setOpacity(b.getOpacity());
        a.setBorder(b.getBorderColor(), b.getBorderSize());
        a.setFont(b.getFont());
        // Swaps all of Tile B's settings with A's original settings
        b.setColor(aColor);
        b.setOpacity(aOpacity);
        b.setBorder(aBorderC, aBorderS);
        b.setFont(aFont);
    }

    public void resetProperties(String s, int r, Color c, int o, int l, int w) {
        Tile current = new Tile(s, r, c, o, l, w);
        Tile.swapTiles(this, current);
        Tile.swapData(this, current);
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
        prevText = super.findText(); // Stores the previous text
        super.setText(s); // Resets the current text to match 's'
    }

    // Returns the original text of the Tile
    public String getOriginal() {
        return(originalText);
    }

    // Returns the previous text of the Tile
    public String getPrev() {
        return(prevText);
    }
    
    // Determines which tile the current one corresponds to.
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
