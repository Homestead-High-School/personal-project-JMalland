import java.awt.*;

public class Tile extends CurvedButton {
    Tile pointsTo = null;
    String originalText = "";
    Point point = null;
    
    public Tile(String s, int r) {
        super(s, r);
        originalText = s;
    }

    public Tile(String s, int r, Color c) {
        super(s, r, c);
        originalText = s;
    }

    public Tile(String s, int r, Color c, int o) {
        super(s, r, c, o);
        originalText = s;
    }

    public void setPoint(Point p) {
        point = p;
    }

    public Point getPoint() {
        return(point);
    }

    // Sets the text of the Tile while saving the original text
    @Override
    public void setText(String s) {
        originalText = super.findText(); // Store the original text from the parent class
        super.setText(s); // Set the text from the parent class
    }

    // Returns the original text of the Tile
    public String getOriginalText() {
        return(originalText);
    }
    
    public void setPointingTo(Tile p) {
        pointsTo = p;
    }

    public Tile getPointingTo() {
        return(pointsTo);
    }
}
