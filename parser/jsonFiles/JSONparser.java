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

    final String materialRegex = "\"Name_Localised\":\"([\\w\\s.]+)+\", \"RequiredAmount\":(\\d+)+, \"ProvidedAmount\":(\\d+)+";
    final String posRegex = "\"StarPos\":\\[([\\d|.|\\-]+),([\\d|.|\\-]+),([\\d|.|\\-]+)\\]";
    final Pattern materialPattern = Pattern.compile(materialRegex);
    final Pattern posPattern = Pattern.compile(posRegex);

    public DepotInfo findListInJournal(boolean useRemaining) {
        HashMap<String, Integer> itemList = new HashMap<>();
        DepotInfo depot = new DepotInfo(itemList, null);
        boolean notFound = true;
        List<String> ignored = new ArrayList<>();
        System.out.println("Searching...");
        while (notFound) {
            String check[] = {"",""};
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

            Matcher matcher = materialPattern.matcher(check[0]);
            while(matcher.find()) {
                String name = matcher.group(1);
                int required = Integer.parseInt(matcher.group(2)); // Use if provided is ignored
                int provided = Integer.parseInt(matcher.group(3));
                int remaining = required-provided;                 // Use if provided is counted
                if (useRemaining) itemList.put(name, remaining);
                else itemList.put(name, required);
            }
            depot.setMatList(itemList);

            matcher = posPattern.matcher(check[1]);
            while(matcher.find()) {
                double x = Double.parseDouble(matcher.group(1));
                double y = Double.parseDouble(matcher.group(2));
                double z = Double.parseDouble(matcher.group(3));
                depot.setSysPos(new double[] {x,y,z});
            }

            notFound = false;
        }
        return depot;
    }

    public String[] findNewestDepot(File currFile) throws IOException {
        try (RandomAccessFile file = new RandomAccessFile(currFile, "r")) {
            boolean depotFound = false;
            String[] outLines = {"",""};
            long fileLength = file.length();
            StringBuilder line = new StringBuilder();

            long pointer = fileLength-1;

            while (pointer >= 0) {
                file.seek(pointer);
                char c = (char) file.read();

                if ((c == '\n' || pointer == 0) && !depotFound) {
                    String currLine = line.reverse().toString().trim();

                    if (currLine.contains("\"event\":\"ColonisationConstructionDepot\"")) {
                        outLines[0] = currLine;
                    }
                    line = new StringBuilder();
                } else if ((c == '\n' || pointer == 0) && depotFound) {
                    String currLine = line.reverse().toString().trim();

                    if (currLine.contains("\"StarPos\":[")) {
                        outLines[1] = currLine;
                        return outLines;
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
