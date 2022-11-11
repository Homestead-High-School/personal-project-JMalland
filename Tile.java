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
        pointsTo = p;
    }

    public Tile getPointingTo() {
        return(pointsTo);
    }
}
