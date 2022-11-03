import java.util.*;
import java.io.File;
public class Main {
    public static void main(String[] args) {
        TreeMap<Character, HashMap<String, String>> map = parseAndStore("Words.txt"); // Get the sorted TreeMap
        int numWords = 0;
        for (char c : map.keySet()) { // Sorted by Character
            numWords += map.get(c).size();
        }
        System.out.println(numWords+" Words!");
        Scrabble basic = new Scrabble(map);
        String one = "HAPPY";
        String two = "UNHAPPY";
        String three = "HELP";
        String four = "HUGS";
        String five = "SOUP";
        String six = "PERP";
        /* End Result:
            U N H A P P Y   H U G S
                E                 O
                L                 U
            H A P P Y       P R E P
        */
        for (int r=0; r<12; r++) {
            for (int c=0; c<12; c++) {
                if (c < one.length() && r == 11) {
                    basic.placeLetter(one.charAt(c), r, c);
                    System.out.print(one.charAt(c)+" ");
                }
                else if (r < 11 && r > 8 && c == 2) {
                    basic.placeLetter(three.charAt(r-8), r, c);
                    System.out.print(three.charAt(r-8)+" ");
                }
                else if (r == 11 && c > 7) {
                    basic.placeLetter(six.charAt(c-8), r, c);
                    System.out.print(six.charAt(c-8)+" ");
                }
                else if (r == 8 && c > 7 && c < 12) {
                    basic.placeLetter(four.charAt(c-8), r, c);
                    System.out.print(four.charAt(c-8)+" ");
                }
                else if (c == 11 && r > 8) {
                    basic.placeLetter(five.charAt(r-8), r, c);
                    System.out.print(five.charAt(r-8)+" ");
                }
                else if (c < two.length() && r == 8) {
                    basic.placeLetter(two.charAt(c), r, c);
                    System.out.print(two.charAt(c)+" ");
                }
                else {
                    basic.placeLetter(' ', r, c);
                    System.out.print(". ");
                }
            }
            System.out.println();
        }
        System.out.println("Words Contained: ");
        boolean temp = basic.validWordPlacement();
        System.out.println("Words Are Valid: "+temp);
    }

    public static TreeMap<Character, HashMap<String, String>> parseAndStore(String file) {
        TreeMap<Character, HashMap<String, String>> map = new TreeMap<Character, HashMap<String, String>>(); // Default TreeMap to be returned
        try {
            Scanner input = new Scanner(new File(file)); // Use Scanner to parse the file
            while (input.hasNextLine()) { // Loop through each line in the file
                String temp = input.nextLine().trim(); // Store String for easy access
                if (temp.contains("#")) continue; // Skip if the word is commented.
                String[] arr = temp.trim().split("\\s", 2);
                String define = arr[1];
                temp = arr[0];
                if (!map.containsKey(temp.charAt(0))) { // Add an empty TreeMap if it doesn't exist
                    map.put(temp.charAt(0), new HashMap<String, String>()); // Add Integer TreeMap
                }
                //if (!map.get(temp.charAt(0)).containsKey(temp)) { // Add an emptuy HashMap if it doesn't exist
                map.get(temp.charAt(0)).put(temp, define);
                //}
                //map.get(temp.charAt(0)).get(temp.length()).put(temp, define); // Add the String and its definition to the HashMap
            }
        } catch (Exception e) { // Throw an exception, if necessary
            System.out.println("Error: "+e); // Print the stack trace.
        }
        return(map);
    }
}
