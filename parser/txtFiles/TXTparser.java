package parser.txtFiles;
import java.io.File;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This will take the given file and read the list in the file
 * It will read the list as such:
 *  - <Item Name> <Item Count (Integer)>
 */

public class TXTparser {
    public int storageSpace;
    public HashMap<String, Integer> getListFromFile(String fileName) {
        HashMap<String, Integer> itemList = new HashMap<>();
        storageSpace = 0;

        File file = new File(fileName);

        try (Scanner reader = new Scanner(file)) {
            int tempStorage;
            while (reader.hasNextLine()) {
                String line = reader.nextLine();
                ParsedResult item = isLineAnItem(line);
                if (item != null) {
                    itemList.put(item.getName(), item.getCount());
                    continue;
                }
                tempStorage = isLineStorageSpace(line);
                if (tempStorage > 0) {
                    storageSpace = tempStorage;
                }
            }
        } catch (Exception e) {
            System.err.println("Wuh oh, something went wrong while attempting to read the file!");
            System.err.println(e);
            System.exit(0);
        }

        if (storageSpace == 0) {
            System.err.println("Error: Missing required line. Please ensure the file contains \'Cargo Space: X\' where X is a positive integer.");
        } else if (itemList.size() == 0) {
            System.err.println("Error: Missing required line. Please ensure the file contains a list item.\nFormat required: - <Item Name> <Item Count (integer)>");
        }

        return itemList;
    }

    private ParsedResult isLineAnItem(String line) {
        // Split the line into their parts
        String[] sections = line.split(":"); // ["- Name", "Count"]
        if (!sections[0].trim().substring(0,1).equals("-") || sections.length != 2) {
            return null; // If it isn't a list item, I don't care about it!
        }
        String nameSect = sections[0].trim().substring(2);
        int countSect = Integer.parseInt(sections[1].trim());
        return new ParsedResult(nameSect, countSect);
    }

    private int isLineStorageSpace(String line) {
        String[] sections = line.split(":"); // ["Cargo Space", "X"]
        if (!sections[0].trim().toLowerCase().equals("cargo space") || sections.length != 2) {
            return 0;
        }
        int storageSpace = Integer.parseInt(sections[1].trim());
        return storageSpace;
    }

    public List<Map.Entry<String,Integer>> sortList(HashMap<String,Integer> list) {
        List<Map.Entry<String,Integer>> sortedList = new ArrayList<>(list.entrySet());
        sortedList.sort(Map.Entry.comparingByValue());
        return sortedList;
    }
}
