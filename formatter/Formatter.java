package formatter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import calculator.Run;
import calculator.RunItem;
import parser.jsonFiles.DepotInfo;
import sources.SystemInfo;
import sources.Station;
import routing.Router;

import java.io.FileWriter;
import java.io.IOException;

public class Formatter {
    private FileWriter writer;
    public void printRuns(List<Run> runs) {
        for (Run run : runs) {
            System.out.println(
                "Run " + run.getRunStartNumber() +
                "-" + run.getRunEndNumber() +
                " (" + run.getTotalUsed() +
                ") (Remainder: " + run.getRunRemainder() + ")"
            );
        }
    }

    /**
     * Calls the correct writer
     * @param outputType Style wanted [block, flow]
     * @param runs All the runs required
     * @param fullList All the materials ordered from least to greatest
     */
    public void writeRunsToFile(String outputType, List<Run> runs, List<Map.Entry<String,Integer>> fullList) {
        switch (outputType) {
            case "flow":
                writeFlowRuns(runs, fullList);
                break;
            case "block":
                writeBlockRuns(runs);
                break;
            // TODO: Create a JSON output for easier integration with others
            default:
                writeBlockRuns(runs);
                break;
        }
    }

    /**
     * Writes all the runs into their own blocks to give as much information to
     * the user as possible
     * 
     * @param runs List of all the runs required
     */
    public void writeBlockRuns(List<Run> runs) {
        try {
            writer = new FileWriter("BlockRuns.txt");

            for (Run run : runs) {
                String currBlock = "";
                if (run.isFull()) {
                    // A FULL run
                    if (run.getRunRemainder() > 0) {
                        currBlock += String.format("\nRun %d-%d (%d):    (Remainder: %d)\n",
                            run.getRunStartNumber(),
                            run.getRunEndNumber(),
                            run.getTotalUsed(),
                            run.getRunRemainder());
                    } else {
                        currBlock += String.format("\nRun %d-%d (%d):\n",
                            run.getRunStartNumber(),
                            run.getRunEndNumber(),
                            run.getTotalUsed());
                    }
                } else {
                    // A non-FULL run (possibly remainder run?)
                    if (run.getRunRemainder() > 0) {
                        currBlock += String.format("\nRun %d (%d):    (Remainder: %d)\n",
                            run.getRunStartNumber(),
                            run.getTotalUsed(),
                            run.getRunRemainder());
                    } else {
                        currBlock += String.format("\nRun %d (%d):\n",
                            run.getRunStartNumber(),
                            run.getTotalUsed());
                    }
                }
                int maxNameSize = run.getLargestNameSize();
                for (RunItem item : run.getItems()) {
                    currBlock += String.format("  - %-"+maxNameSize+"s %d\n",
                        item.getName(),
                        item.getQuantity());
                }
                writer.write(currBlock);
            }
            writer.close();
        } catch (IOException e) {
            System.err.println("We recieved an error while trying to write to the file.");
            System.err.println(e);
            System.exit(0);
        }
    }

    /**
     * Writes all the runs on top of the ordered list of all the materials
     * @param runs List of all the runs required
     * @param fullList All the materials ordered from least to greatest
     */
    public void writeFlowRuns(List<Run> runs, List<Map.Entry<String,Integer>> fullList) {
        int itemIndex = 0;
        boolean dontRepeat = false;
        int nextRun = 1;
        int spacesAdded = 0;
        int maxNameSize = 0;
        int maxNumSize = 0;
        // Determine the max sizes for names and numbers
        for (Run run : runs) {
            if (run.getLargestNameSize() > maxNameSize) {
                maxNameSize = run.getLargestNameSize();
            }
            if (run.getNumSize() > maxNumSize) {
                maxNumSize = run.getNumSize();
            }
        }

        String printedList = "";
        for (Run currRun : runs) {
            int currRunSize = currRun.getItems().size();
            int halfSize = currRunSize/2;
            for (int i = 0; i < currRunSize; i++) {
                RunItem item = currRun.getItems().get(i);
                int matchIndex = 0;
                while (matchIndex < fullList.size()) {
                    if (fullList.get(matchIndex).getKey().equals(item.getName())) {
                        break;
                    }
                    matchIndex++;
                }
                if (matchIndex > itemIndex) {
                    for (int j = 0; j < matchIndex - itemIndex; j++) {                        
                        // Add the item and its info
                        printedList += String.format(" - %-"+maxNameSize+"s %"+maxNumSize+"d ", fullList.get(itemIndex).getKey(), fullList.get(itemIndex).getValue());
                        // Add the indentation needed
                        printedList += " ".repeat(spacesAdded);
                        printedList += "\\\n"; // Assume remainder flows
                        itemIndex++;
                        spacesAdded++;
                    }
                }
                if (matchIndex == itemIndex && dontRepeat) {
                    // This is the final item, again
                    continue;
                }
                if (matchIndex < itemIndex) {
                    // This is an item we already covered, SKIP!
                    continue;
                }

                // Add the item and its info
                printedList += String.format(" - %-"+maxNameSize+"s %"+maxNumSize+"d ", item.getName(), fullList.get(itemIndex).getValue());
                // Add the indentation needed
                printedList += " ".repeat(spacesAdded);

                // Is it a pyramid or not?
                if (currRunSize > 2) { // Yes
                    // Figure out where on the pyramid we are
                    if (i < halfSize) { // We're near the start
                        printedList += "\\";
                        spacesAdded++;
                    } else if (i == halfSize) { // We're where we see the run data
                        printedList += "> Run "+currRun.getRunStartNumber()+" ("+currRun.getTotalUsed()+")";
                        spacesAdded--;
                    } else if (i == currRunSize-1 && currRun.getRunRemainder() > 0) {
                        // This only runs if we're at the end of the pyramid and we have a remainder
                        printedList += "/ \\ (remainder: "+currRun.getRunRemainder()+")";
                        spacesAdded += 3;
                    } else if (i == currRunSize-1) { // We're at the end of the pyramid. We don't have any remainders either!
                        printedList += "/";
                        spacesAdded = 0;
                    } else { // We're going down still
                        printedList += "/";
                        spacesAdded--;
                    }
                } else if (currRunSize == 2) { // Not really
                    // Always make the second item the one with the run info
                    if (i == 1 && currRun.getRunRemainder() > 0) { // We've got a remainder, let it flow to the next one
                        printedList += "\\ Run "+currRun.getRunStartNumber()+" ("+currRun.getTotalUsed()+") (remainder: "+currRun.getRunRemainder()+")";
                        spacesAdded++;
                    } else if (i == 1) { // No remainder, we can cut the slope here
                        printedList += "> Run "+currRun.getRunStartNumber()+" ("+currRun.getTotalUsed()+")";
                        spacesAdded = 0;
                    } else { // We're the first item.
                        printedList += "\\";
                        spacesAdded++;
                    }
                } else { // No
                    if (nextRun == runs.size()) {nextRun--;} // Prevent errors from there being no FULL runs
                    // What type of FULL run are we?
                    if (runs.get(nextRun).getRunRemainder() > 0 && currRun.isFull() && !runs.get(nextRun).isFull() && itemIndex == fullList.size()-1) {
                        // Final FULL run with REMAINDER run that also leaves a remainder
                        printedList += "> Run "+currRun.getRunStartNumber()+"-"+(currRun.getRunEndNumber()+2)+" ("+currRun.getTotalUsed()+") ("+runs.get(nextRun).getTotalUsed()+") (remainder: "+runs.get(nextRun).getRunRemainder()+")";
                        spacesAdded++;
                    } else if (runs.get(nextRun).getRunRemainder() > 0 && currRun.isFull() && !runs.get(nextRun).isFull()) {
                        // FULL run with REMAINDER run that also leaves a remainder
                        printedList += "\\ Run "+currRun.getRunStartNumber()+"-"+(currRun.getRunEndNumber()+1)+" ("+currRun.getTotalUsed()+") ("+runs.get(nextRun).getTotalUsed()+") (remainder: "+runs.get(nextRun).getRunRemainder()+")";
                        spacesAdded++;
                    } else if (runs.get(nextRun).getRunRemainder() == 0 && currRun.isFull() && !runs.get(nextRun).isFull()) {
                        // FULL run with REMAINDER run and NO remainder
                        printedList += "> Run "+currRun.getRunStartNumber()+"-"+(currRun.getRunEndNumber()+1)+" ("+currRun.getTotalUsed()+") ("+runs.get(nextRun).getTotalUsed()+")";
                        spacesAdded = 0;
                    } else if (currRun.getRunRemainder() > 0 && currRun.isFull()) {
                        // FULL run with a remainder
                        printedList += "\\ Run "+currRun.getRunStartNumber()+"-"+currRun.getRunEndNumber()+" ("+currRun.getTotalUsed()+") (remainder: "+currRun.getRunRemainder()+")";
                        spacesAdded++;
                    } else if (currRun.getRunRemainder() == 0 && currRun.isFull()) {
                        // FULL run with NO remainder
                        printedList += "> Run "+currRun.getRunStartNumber()+"-"+currRun.getRunEndNumber()+" ("+currRun.getTotalUsed()+")";
                        spacesAdded = 0;
                    } else {
                        // Not a FULL run
                        printedList += "> Run "+currRun.getRunStartNumber()+" ("+currRun.getTotalUsed()+")";
                        spacesAdded = 0;
                    }
                }
                // Moving to next item
                printedList += "\n"; 
                itemIndex++;
                // Prevent IndexOutOfBounds errors:
                if (itemIndex == fullList.size()) {
                    itemIndex--;
                    dontRepeat = true;
                }
                if (spacesAdded < 0) {
                    spacesAdded = 0;
                }
            }
            nextRun++;
        }
        try {
            writer = new FileWriter("FlowRuns.txt");
            writer.write(printedList);
            writer.close();
        } catch (IOException e) {
            System.err.println("We recieved an error while trying to write to the file.");
            System.err.println(e);
            System.exit(0);
        }
    }

    public void writeRoutes(List<Run> runs, ArrayList<SystemInfo> systems, DepotInfo depot) {
        // Find stations required for run
        // Create route for run (use the router)
        // Print route
        // How...
        // Hashmaps used to link materials to their locations
        HashMap<String,String> matLocation = new HashMap<>(); // {<Material Name>, <System Name, Station Name>} (Printing)
        HashMap<String,SystemInfo> matSystem = new HashMap<>(); // {<Material Name>, <System>} (Organization of materials)
        try {
            writer = new FileWriter("Route.txt");

            for (SystemInfo currSys : systems) {
                for (Station currStat : currSys.getStations()) {
                    String[] contained = currStat.getMaterialsContained();
                    // Some stations may not have any materials assigned
                    if (contained == null) {
                        continue;
                    }
                    String location = currSys.getName()+", "+currStat.getName();
                    for (String material : contained) {
                        if (material == null) continue;
                        // Link the materials to systems
                        matLocation.put(material.toLowerCase(), location);
                        matSystem.put(material.toLowerCase(), currSys);
                    }
                }
            }
            
            for (Run trip : runs) {
                double[][] systemCoords = new double[trip.getItems().size()+1][3]; // Used to create distance tables
                systemCoords[0] = depot.getSysPos();
                int counter = 1;
                for (RunItem material : trip.getItems()) {
                    SystemInfo system = matSystem.get(material.getName().toLowerCase());
                    if (system == null) {
                        // Skip items that do not have a mapped system; prevents NPEs
                        continue;
                    }
                    double[] individCoords = system.getSystemPosition();
                    systemCoords[counter][0] = individCoords[0]; // X
                    systemCoords[counter][1] = individCoords[1]; // Y
                    systemCoords[counter][2] = individCoords[2]; // Z
                    counter++;
                }
        
                int[] route = Router.beginRouting(systemCoords);

                RunItem[] materialRoute = new RunItem[route.length]; // The materials we pick up in order
                materialRoute[0] = null; // Depot Start
                materialRoute[route.length-1] = null; // Depot End
                for (int i = 0; i<trip.getItems().size(); i++) {
                    int indexSearch = route[i+1]-1;
                    RunItem material = trip.getItems().get(indexSearch);
                    materialRoute[i+1] = material;
                }

                String currRoute = "";
                boolean depotStart = false;
                boolean multipleTrips = (trip.getRunEndNumber()-trip.getRunStartNumber() == 0) ? false : true;
                boolean remainderFromTrip = (trip.getRunRemainder() == 0) ? true : false;
                int tripCount = trip.getRunEndNumber()-trip.getRunStartNumber()+1;
                String prevLocation = "";
                for (RunItem material : materialRoute) {
                    if (!depotStart) { // Run Info
                        if (multipleTrips) {
                            if (remainderFromTrip) {
                                currRoute += "Runs "+trip.getRunStartNumber()+"-"+trip.getRunEndNumber()+" ("+trip.getTotalUsed()+"):\n";
                            } else {
                                currRoute += "Runs "+trip.getRunStartNumber()+"-"+trip.getRunEndNumber()+" ("+trip.getTotalUsed()+"): (Remainder: "+trip.getRunRemainder()+")\n";
                            }
                        } else {
                            if (remainderFromTrip) {
                                currRoute += "Run "+trip.getRunStartNumber()+" ("+trip.getTotalUsed()+"):\n";
                            } else {
                                currRoute += "Run "+trip.getRunStartNumber()+" ("+trip.getTotalUsed()+"): (Remainder: "+trip.getRunRemainder()+")\n";
                            }
                        }
                        depotStart = true;
                        continue; // This is the depot, don't process further
                    }
                    try {
                        // No Error? We have a material
                        String matName = material.getName();
                        if (multipleTrips) {
                            currRoute += "  Go to "+matLocation.get(matName.toLowerCase())+" and buy:\n";
                            currRoute += "    - A full cargo hold of "+matName+" "+tripCount+" times\n";
                        } else {
                            if (prevLocation.equals(matLocation.get(matName.toLowerCase()))) {
                                currRoute += "    - "+material.getQuantity()+" units of "+matName+"\n";
                            } else {
                                currRoute += "  Go to "+matLocation.get(matName.toLowerCase())+" and buy:\n";
                                currRoute += "    - "+material.getQuantity()+" units of "+matName+"\n";
                                prevLocation = matLocation.get(matName.toLowerCase());
                            }
                        }
                    } catch (NullPointerException e) { // Error? We have the depot
                        currRoute += "  Return to the depot and deposit all materials.\n\n";
                    }
                }
                writer.write(currRoute);
            }
            writer.close();
        } catch (IOException e) {
            System.err.println("Error when writing route:");
            e.printStackTrace();
        }
    }

}
