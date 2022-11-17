import java.awt.*;
import java.util.ArrayList;

public class Tile extends CurvedButton {
    private static ArrayList<Point> dragList = new ArrayList<Point>();
    private Tile pointsTo = null;
    private int letterBonus = 1;
    private int wordBonus = 1;
    private int tileValue = 0;
    private String originalText = "";
    private String prevText = "";
    private Point point = null;
    private Point dragLocation = null;
    private int index = -1;

    public Tile() {
        super();
    }
    
    public Tile(String s, int r, Color c, int o, int i) {
        super(s, r, c, o);
        originalText = s;
        index = i;
    }

    public Tile(String s, int r, Color c, int o, int l, int w, int row, int col) {
        super(s, r, c, o);
        originalText = s;
        letterBonus = l;
        wordBonus = w;
        point = new Point(row, col);
    }

    public static int getDragIndex(Point p) {
        return(Tile.dragList.indexOf(p));
    }

    public static void clearDragTiles() {
        dragList.clear();
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
        a.point = b.point;
        // Swaps all of Tile B's properties with A's original properties
        b.setText(aText);
        b.setOriginal(aOrigin);
        b.setPointingTo(aPointing);
        b.letterBonus = aLBonus;
        b.wordBonus = aWBonus;
        b.tileValue = aTileVal;
        b.point = aPoint;
    }
    
    public void resetProperties(String s, int r, Color c, int o, int l, int w) {
        super.setColor(c);
        super.setOpacity(o);
        super.setText(s);
        originalText = s;
        letterBonus = l;
        wordBonus = w;
    }

    public void setValue(int v) {
        tileValue = v;
    }

    public void setOriginal(String s) {
        originalText = s;
    }

    public int getIndex() {
        return(index);
    }

    public Point getPoint() {
        return(point);
    }

    public void setDragLocation(Point p) {
        dragLocation = p;
        Tile.dragList.add(dragLocation);
    }

    public Point getDragLocation() {
        return(dragLocation);
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
