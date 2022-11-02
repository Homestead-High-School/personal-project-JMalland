import java.util.*;
import java.io.File;
public class Main {
    public static void main(String[] args) {
        TreeMap<Character, TreeMap<Integer, HashMap<String, String>>> map = parseAndStore("Words.txt"); // Get the sorted TreeMap
        for (char c : map.keySet()) { // Sorted by Character
            System.out.println(c+": ");
            int total = 0;
            TreeMap<Integer, HashMap<String, String>> tempMap = map.get(c);
            for (int n : tempMap.keySet()) { // Sorted again by Integer
                total += tempMap.get(n).size();
                //System.out.println("\t"+n+":");
                //System.out.println("\t\tSize: "+tempMap.get(n).size()); // Stored as a HashSet of Strings
            }
            System.out.println("\tTotal Size: "+total+" Words");
        }
    }

    public static TreeMap<Character, TreeMap<Integer, HashMap<String, String>>> parseAndStore(String file) {
        TreeMap<Character, TreeMap<Integer, HashMap<String, String>>> map = new TreeMap<Character, TreeMap<Integer, HashMap<String, String>>>(); // Default TreeMap to be returned
        try {
            Scanner input = new Scanner(new File(file)); // Use Scanner to parse the file
            while (input.hasNextLine()) { // Loop through each line in the file
                String temp = input.nextLine().trim(); // Store String for easy access
                if (temp.contains("#")) continue; // Skip if the word is commented.
                String[] arr = temp.trim().split("\\s", 2);
                String define = arr[1];
                temp = arr[0];
                if (!map.containsKey(temp.charAt(0))) { // Add an empty TreeMap if it doesn't exist
                    map.put(temp.charAt(0), new TreeMap<Integer, HashMap<String, String>>()); // Add Integer TreeMap
                }
                if (!map.get(temp.charAt(0)).containsKey(temp.length())) { // Add an emptuy HashMap if it doesn't exist
                    map.get(temp.charAt(0)).put(temp.length(), new HashMap<String, String>());
                }
                map.get(temp.charAt(0)).get(temp.length()).put(temp, define); // Add the String and its definition to the HashMap
            }
        } catch (Exception e) { // Throw an exception, if necessary
            System.out.println("Error: "+e); // Print the stack trace.
        }
        return(map);
    }
}
