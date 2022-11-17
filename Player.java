public class Player {
    private int score = 0;
    private char[] hand = new char[0];
    
    public Player(char[] hand) {
        this.hand = hand;
    }

    public void addToScore(int p) {
        score += p;
    }

    public int getScore() {
        return(score);
    }

    public void setTile(int i, char c) {
        hand[i] = c;
    }

    public char[] getHand() {
        return(hand.clone());
    }

    public char getTile(int i) {
        if (i < 0 || i >= hand.length) { // Throw an exception if I'm stupid enough to hit it
            throw new IllegalArgumentException("Index Out Of Bounds For Hand Retrieval");
        }
        return(hand[i]);
    }
}
