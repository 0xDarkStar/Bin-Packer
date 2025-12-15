import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import calculator.Run;
import calculator.RunCalculator;

import formatter.Formatter;

import parser.jsonFiles.DepotInfo;
import parser.jsonFiles.JSONparser;
import parser.txtFiles.TXTparser;

import sources.SourceFinder;
import sources.SystemInfo;

public class Main {
    static boolean readLogs = false;
    static boolean useRemaining = false;
    static boolean createRoutes = false;
    static int searchRadius = 50; // 50 LY search radius default
    static String journalDir = "";
    static String[] outputTypes = {};
    static int storage = 0;
    static String fileName = "";

    public static void main(String[] args) {
        List<Map.Entry<String,Integer>> orderedList;
        ArrayList<SystemInfo> sources = new ArrayList<>(); // Make the Java compiler happy

        handleIn(args);

        if (readLogs) { // Read through the journals
            if (storage == 0) {
                System.err.println("Error: A storage quantity needs to be provided.");
                System.exit(1);
            }
            JSONparser organizer = new JSONparser();
            DepotInfo depot = organizer.findListInJournal(useRemaining, journalDir);
            orderedList = organizer.sortList(depot.getMatList());
            if (createRoutes) {
                SourceFinder searcher = new SourceFinder();
                sources = searcher.searchForSources(depot, searchRadius);
            }
        } else { // Read the file given
            TXTparser organizer = new TXTparser();
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
        if (createRoutes) {
            // TODO: Complete writeRoutes in the formatter
            output.writeRoutes(allRuns, sources);
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
                        System.exit(1);
                    }
                    break;
                case "--output":
                    if (i+1 < args.length) {
                        outputTypes = stringLister(args[i+1].toLowerCase());
                        i++; // Desired output types grabbed, SKIP
                    } else {
                        System.err.println("Error: --output requires a value");
                        System.exit(1);
                    }
                    break;
                case "--createroutes":
                    createRoutes = true;
                    break;
                case "--searchradius":
                    if (i+1 < args.length) {
                        searchRadius = Integer.parseInt(args[i+1]);
                        i++;
                    } else {
                        System.err.println("Error: --searchRadius requires a value");
                        System.exit(1);
                    }
                    break;
                case "--journaldir":
                    if (i+1 < args.length) {
                        journalDir = args[i+1];
                        i++;
                    } else {
                        System.err.println("Error: --journaldir requires a path");
                        System.exit(1);
                    }
                    break;
                case "--help":
                    System.out.println("All available options for Bin Packer:");
                    System.out.println("  --readlogs");
                    System.out.println("      Read the Elite Dangerous journals for the last construction depot visited.");
                    System.out.println("  --journaldir <path>");
                    System.out.println("      Define the path to your journals.");
                    System.out.println("  --storage <capacity>");
                    System.out.println("      Define the storage capacity of the vessel.");
                    System.out.println("  --remaining");
                    System.out.println("      Make runs using ONLY the remaining materials.");
                    System.out.println("  --output <formats>");
                    System.out.println("      Select the format(s) that are to be outputted. Formats must be separated by commas and NO spaces.");
                    System.out.println("      Formats: block, float");
                    System.out.println("  --createroutes");
                    System.out.println("      Search for all nearby sources and create routes to follow");
                    System.out.println("  --searchradius <radius>");
                    System.out.println("      Set the search radius in LY.");
                    System.exit(0);
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
