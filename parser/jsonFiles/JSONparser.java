package parser.jsonFiles;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.*;

public class JSONparser {

    final String regex = "\"Name_Localised\":\"([\\w\\s.]+)+\", \"RequiredAmount\":(\\d+)+, \"ProvidedAmount\":(\\d+)+";
    final Pattern pattern = Pattern.compile(regex);

    public HashMap<String, Integer> findListInJournal(boolean useRemaining) {
        HashMap<String, Integer> itemList = new HashMap<>();
        boolean notFound = true;
        List<String> ignored = new ArrayList<>();
        System.out.println("Searching...");
        while (notFound) {
            String check = "";
            try {
                File latestFile = JournalFinder.findLatestJournal(ignored);
                check = findNewestDepot(latestFile);
                if (check == null) {
                    ignored.add(latestFile.getName());
                    continue;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            Matcher matcher = pattern.matcher(check);

            while(matcher.find()) {
                String name = matcher.group(1);
                int required = Integer.parseInt(matcher.group(2)); // Use if provided is ignored
                int provided = Integer.parseInt(matcher.group(3));
                int remaining = required-provided;                 // Use if provided is counted
                if (useRemaining) itemList.put(name, remaining);
                else itemList.put(name, required);
            }
            notFound = false;
        }
        return itemList;
    }

    public String findNewestDepot(File currFile) throws IOException {
        try (RandomAccessFile file = new RandomAccessFile(currFile, "r")) {
            long fileLength = file.length();
            StringBuilder line = new StringBuilder();

            long pointer = fileLength-1;

            while (pointer >= 0) {
                file.seek(pointer);
                char c = (char) file.read();

                if (c == '\n' || pointer == 0) {
                    String currLine = line.reverse().toString().trim();

                    if (currLine.contains("\"event\":\"ColonisationConstructionDepot\"")) {
                        return currLine;
                    }
                    line = new StringBuilder();
                } else {
                    line.append(c);
                }

                pointer--;
            }
        }
        return null;
    }

    public List<Map.Entry<String,Integer>> sortList(HashMap<String,Integer> list) {
        List<Map.Entry<String,Integer>> sortedList = new ArrayList<>(list.entrySet());
        sortedList.sort(Map.Entry.comparingByValue());
        return sortedList;
    }
}
