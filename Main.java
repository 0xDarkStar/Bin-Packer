import java.util.HashMap;
import java.util.List;
import java.util.Map;

import calculator.Run;
import calculator.RunCalculator;
import formatter.Formatter;
import parser.jsonFiles.JSONparser;
import parser.txtFiles.txtParser;

public class Main {
    public static void main(String[] args) {
        String fileName = args[0];
        
        if (fileName.toLowerCase().equals("--readlogs")) {
            boolean useRemaining = false;
            if (args.length == 3) {
                useRemaining = Boolean.parseBoolean(args[2]);
            }
            JSONparser organizer = new JSONparser();
            HashMap<String, Integer> list = organizer.findListInJournal(useRemaining);
            List<Map.Entry<String,Integer>> orderedList = organizer.sortList(list);
            // Figure out what are the optimized runs.
            RunCalculator runCalc = new RunCalculator();
            List<Run> allRuns = runCalc.calculateRuns(orderedList, Integer.parseInt(args[1]));
            if (allRuns == null) {
                System.err.println("Previously visited construction site is complete.");
                System.err.println("Please visit a new site.");
                System.exit(0);
            }
            // Print it out for the user
            Formatter textFormat = new Formatter();
            textFormat.writeRunsToFile("flow", allRuns, orderedList);
            textFormat.writeRunsToFile("block", allRuns, null);
        } else {
            txtParser organizer = new txtParser();
            // Get the list from the file
            HashMap<String, Integer> list = organizer.getListFromFile(fileName);
            List<Map.Entry<String,Integer>> orderedList = organizer.sortList(list);
            // Figure out what are the optimized runs.
            RunCalculator runCalc = new RunCalculator();
            List<Run> allRuns = runCalc.calculateRuns(orderedList, organizer.storageSpace);
            // Print it out for the user
            Formatter textFormat = new Formatter();
            textFormat.writeRunsToFile("flow", allRuns, orderedList);
            textFormat.writeRunsToFile("block", allRuns, null);
        }
    }
}
