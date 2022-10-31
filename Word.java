public class Word {
    String word = "";
    String definition = "";

    public Word(String s) {
        String[] split = s.trim().split("\\s", 2);
        this.word = split[0].trim();
        this.definition = split[1].trim();
    }

    public boolean contains(String s) {
        return(word.contains(s) || definition.contains(s));
    }

    public String word() {
        return(word);
    }

    public String definition() {
        return(definition);
    }
}
