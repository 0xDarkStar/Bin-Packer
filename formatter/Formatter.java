package formatter;
import java.util.List;
import java.util.Map;

import calculator.Run;
import calculator.RunItem;

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

    public void writeRunsToFile(String outputType, List<Run> runs, List<Map.Entry<String,Integer>> fullList) {
        switch (outputType) {
            case "flow":
                writeFlowRuns(runs, fullList);
                break;
            case "block":
                writeBlockRuns(runs);
                break;
            default:
                writeBlockRuns(runs);
                break;
        }
    }

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
}
