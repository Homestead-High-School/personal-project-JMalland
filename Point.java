public class Point {
    int r, c;

    public Point(int r, int c) {
        this.r = r;
        this.c = c;
    }

    public int getRow() {
        return(r);
    }

    public int getCol() {
        return(c);
    }

    @Override
    public int hashCode() {
        int primeA = 31; // Prime number to represent the Row
        int primeB = 17; // Prime number to represent the Col
        return(r * primeA + c * primeB); // Return the sum of the two primes, multiplied by the row and col
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Point) {
            Point obj = (Point) o;
            return(obj.getRow() == r && obj.getCol() == c);
        }
        return(false);
    }
}
