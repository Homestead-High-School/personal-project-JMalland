import java.awt.*;
import java.awt.event.ActionEvent;

public class CustomEvent extends ActionEvent {
    private int r = 0;
    private int c = 0;
    private int index = 0;
    private char t = '\0';

    public CustomEvent(Component c, int id) {
        super(c, id, "");
    }
    
    public CustomEvent(Component c, int id, int index) {
        super(c, id, "");
        this.index = index;
    }

    public CustomEvent(Component c, int id, int row, int col) {
        super(c, id, "");
        this.r = row;
        this.c = col;
    }
    public CustomEvent(Component c, int id, char t, int row, int col) {
        super(c, id, "");
        this.r = row;
        this.c = col;
        this.t = t;
    }

    public char getChar() {
        return(t);
    }

    // Returns the index of the component, assuming it is within a single array
    public int getIndex() {
        return(index);
    }

    // Returns the row index of the component, assuming it is within a 2d array
    public int getRow() {
        return(r);
    }

    // Returns the col index of the component, assuming it is within a 2d array
    public int getCol() {
        return(c);
    }
}
