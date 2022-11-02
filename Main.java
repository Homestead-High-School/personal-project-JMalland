import java.util.*;
import java.io.File;
public class Main {
    public static void main(String[] args) {
        TreeMap<Character, TreeMap<Integer, HashSet<Word>>> map = parseAndStore("Words.txt"); // Get the sorted TreeMap
        for (char c : map.keySet()) { // Sorted by Character
            System.out.println(c+": ");
            int total = 0;
            TreeMap<Integer, HashSet<Word>> tempMap = map.get(c);
            for (int n : tempMap.keySet()) { // Sorted again by Integer
                total += tempMap.get(n).size();
                //System.out.println("\t"+n+":");
                //System.out.println("\t\tSize: "+tempMap.get(n).size()); // Stored as a HashSet of Strings
            }
            System.out.println("\tTotal Size: "+total+" Words");
        }
    }

    public static TreeMap<Character, TreeMap<Integer, HashSet<Word>>> parseAndStore(String file) {
        TreeMap<Character, TreeMap<Integer, HashSet<Word>>> map = new TreeMap<>(); // Default TreeMap to be returned
        try {
            Scanner input = new Scanner(new File(file)); // Use Scanner to parse the file
            while (input.hasNextLine()) { // Loop through each line in the file
                Word temp = new Word(input.nextLine()); // Store String for easy access
                if (temp.contains("#")) continue; // Skip if the word is commented.
                map.putIfAbsent(temp.word().charAt(0), new TreeMap<Integer, HashSet<Word>>()); // Add Integer TreeMap
                map.get(temp.word().charAt(0)).putIfAbsent(temp.word().length(), new HashSet<Word>()); // Add String HashSet
                map.get(temp.word().charAt(0)).get(temp.word().length()).add(temp); // Add String to HashSet
            }
        } catch (Exception e) { // Throw an exception, if necessary
            System.out.println("Error: "+e); // Print the stack trace.
        }
        return(map);
    }
}
