import java.awt.Color;

public class Tile extends CurvedButton {
    private Tile pointsTo = null;
    private String originalText = "";
    private String prevText = "";
    private Point point = null;
    private int index = -1;

    public Tile() {
        super();
    }
    
    public Tile(String s, int r, Color c, int o, int i) {
        super(s, r, c, o);
        originalText = s;
        index = i;
    }

    public Tile(String s, int r, Color c, int o, int row, int col) {
        super(s, r, c, o);
        originalText = s;
        point = new Point(row, col);
    }

    // Method to swap the properties of two tiles, while keeping their location the same
    public static void swapTiles(Tile a, Tile b) {
        // Temp variables to hold all of A's properties to be used in setting B's
        String aText = a.findText(); 
        String aOrigin = a.originalText;
        Tile aPointing = a.pointsTo;
        Tile bPointing = b.pointsTo;
        Point aPoint = a.point;
        // Swaps all of Tile A's properties with B's
        a.setText(b.findText());
        a.setOriginal(b.getOriginal());
        a.setPointingTo(bPointing);
        a.point = b.point;
        // Swaps all of Tile B's properties with A's original properties
        b.setText(aText);
        b.setOriginal(aOrigin);
        b.setPointingTo(aPointing);
        b.point = aPoint;
    }
    
    public void resetProperties(String s, int r, Color c, int o) {
        super.setColor(c);
        super.setOpacity(o);
        super.setText(s);
        originalText = s;
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
