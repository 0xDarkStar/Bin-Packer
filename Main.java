import java.util.HashMap;
import java.util.List;
import java.util.Map;

import calculator.Run;
import calculator.RunCalculator;
import formatter.Formatter;
import parser.jsonFiles.JSONparser;
import parser.txtFiles.txtParser;

public class Main {
    static boolean readLogs = false;
    static boolean useRemaining = false;
    static String[] outputTypes = {};
    static int storage = 0;
    static String fileName = "";

    public static void main(String[] args) {
        List<Map.Entry<String,Integer>> orderedList;

        handleIn(args);

        if (readLogs) { // Read through the journals
            JSONparser organizer = new JSONparser();
            HashMap<String, Integer> list = organizer.findListInJournal(useRemaining);
            orderedList = organizer.sortList(list);
        } else { // Read the file given
            txtParser organizer = new txtParser();
            HashMap<String, Integer> list = organizer.getListFromFile(fileName);
            orderedList = organizer.sortList(list);
            storage = organizer.storageSpace;
        }

        // Calculate the runs required
        RunCalculator runCalc = new RunCalculator();
        List<Run> allRuns = runCalc.calculateRuns(orderedList, storage);
        // Print output to files
        Formatter output = new Formatter();
        if (outputTypes.length == 0) {
            System.out.println("No output type was given!");
            System.out.println("Defaulting to Block...");
            output.writeBlockRuns(allRuns); // Might as well call directly
        } else {
            for (String type : outputTypes) {
                System.out.println("Writing "+type+" file.");
                output.writeRunsToFile(type, allRuns, orderedList);
            }
        }
    }

    public static void handleIn(String[] args) {
        for (int i = 0; i < args.length; i++) {
            // Create var for checking
            String item = args[i].toLowerCase();
            // Look for options
            switch (item) {
                case "--remaining":
                    useRemaining = true;
                    break;
                case "--readlogs":
                    readLogs = true;
                    break;
                case "--storage":
                    if (i+1 < args.length) {
                        storage = Integer.parseInt(args[i+1]);
                        i++; // Storage capacity grabbed, SKIP
                    } else {
                        System.err.println("Error: --storage requires a value");
                    }
                    break;
                case "--output":
                    if (i+1 < args.length) {
                        outputTypes = stringLister(args[i+1].toLowerCase());
                        i++; // Desired output types grabbed, SKIP
                    } else {
                        System.err.println("Error: --output requires a value");
                    }
                    break;
                default:
                    fileName = args[i];
                    break;
            }
        }
    }

    public static String[] stringLister(String inputList) {
        String[] outputList = inputList.split(",");
        return outputList;
    }
}
