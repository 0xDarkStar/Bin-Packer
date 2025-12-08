package calculator;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RunCalculator {
    private double RunThreshold = 0.90;

    public List<Run> calculateRuns(List<Map.Entry<String,Integer>> itemsList, int maxCargo) {
        int currRunIndex = 1; // An ever-increasing number keeping track of what run we're currently calculating
        int currItemCount = 0; // A running total of how much space is being used
        int prevRemainder = 0; // A hold for the items' total space usage to wait until REMAINDER runs
        int currRemainder = 0; // Either causes a REMAINDER run or is added to the hold
        List<RunItem> remainderRunItems = new ArrayList<>(); // A hold for items to wait until the REMAINDER runs
        List<RunItem> currRunItems = new ArrayList<>(); // A list holding the items in the current run
        List<Run> allRuns = new ArrayList<>(); // A list with all the runs. Will be used for formatting
        int fullRuns = 0;
        // Go through every single item
        for (Map.Entry<String,Integer> entry : itemsList) {
            int currEntrySpace = entry.getValue();
            String currEntryName = entry.getKey();
            if (currEntrySpace >= maxCargo) {
                // This item alone can fill its own run(s)
                // Check if the items before take up enough space to warrant their own run.
                if (currItemCount >= maxCargo*RunThreshold) { // They do! :D
                    allRuns.add(new Run(currRunIndex, currRunIndex, currRunItems, currItemCount, false, 0));
                    currRunIndex++;
                    currRunItems.clear();
                    currItemCount = 0;
                } else { // They don't, move them to the hold.
                    prevRemainder = currItemCount;
                    remainderRunItems = new ArrayList<>(currRunItems);
                    currItemCount = 0;
                    currRunItems.clear();
                }
                // How many FULL runs can this item do?
                fullRuns = currEntrySpace/maxCargo;
                currRemainder = currEntrySpace%maxCargo;
                currItemCount = fullRuns*maxCargo;
                currRunItems.add(new RunItem(currEntryName, currItemCount));
                allRuns.add(new Run(currRunIndex, currRunIndex + fullRuns-1, currRunItems, currItemCount, true, currRemainder));
                // Update the variables
                currRunIndex += fullRuns;
                currItemCount = 0;
                fullRuns = 0;
                currRunItems.clear();
                // Now check if we should do a REMAINDER run.
                if (currRemainder + prevRemainder >= maxCargo*RunThreshold) {
                    // It is large enough, let's do a REMAINDER run.
                    // But first, save the remainder
                    prevRemainder += currRemainder;
                    int tempCurrItemStorage = currRemainder;
                    currRemainder = 0;
                    if (prevRemainder > maxCargo) {
                        currRemainder = prevRemainder - maxCargo; // How much was left behind
                        prevRemainder -= currRemainder; // How much space we took up for the whole run
                        tempCurrItemStorage -= currRemainder; // How much we actually used
                    }
                    remainderRunItems.add(new RunItem(currEntryName, tempCurrItemStorage));
                    allRuns.add(new Run(currRunIndex, currRunIndex, remainderRunItems, prevRemainder, false, currRemainder));
                    // Update all the variables
                    currRunIndex++;
                    remainderRunItems.clear();
                    prevRemainder = 0;
                    if (currRemainder > 0) {
                        // We have some left over, add it to the next run
                        currRunItems.add(new RunItem(currEntryName, currRemainder));
                        currItemCount = currRemainder;
                        currRemainder = 0;
                    }
                    // My head hurts :(
                } else {
                    // It isn't large enough, let's just add this to the remainder run and continue
                    currItemCount = currRemainder + prevRemainder;
                    remainderRunItems.add(new RunItem(currEntryName, currRemainder));
                    currRunItems.addAll(remainderRunItems);
                    currRemainder = 0;
                    prevRemainder = 0;
                }
            } else if (currEntrySpace + currItemCount >= maxCargo*RunThreshold) {
                // This item and all the previous ones meet the threshold for a run.
                // First, find out how much of our current item we use.
                currItemCount += currEntrySpace;
                int tempCurrItemStorage = currEntrySpace;
                currRemainder = currItemCount - maxCargo; // How much was left behind
                if (currRemainder > 0) {
                    currItemCount -= currRemainder; // How much space we took up for the whole run
                    tempCurrItemStorage -= currRemainder; // How much we actually used
                } else {
                    currRemainder = 0;
                }

                // Add our current item to the run and end it
                currRunItems.add(new RunItem(currEntryName, tempCurrItemStorage));
                allRuns.add(new Run(currRunIndex, currRunIndex, currRunItems, currItemCount, false, currRemainder));

                // Update all the variables
                currRunIndex++;
                currRunItems.clear();
                currItemCount = currRemainder;
                if (currRemainder > 0) {
                    currRunItems.add(new RunItem(currEntryName, currRemainder));
                }
                currRemainder = 0;
            } else {
                // It wasn't able to reach the threshold even with all the other items...
                // Just add it to our current run and move to the next item.
                currRunItems.add(new RunItem(currEntryName, currEntrySpace));
                currItemCount += currEntrySpace;
            }
        }
        // Clean up any remainders we may have
        if (currItemCount > 0 || prevRemainder > 0) {
            currItemCount += prevRemainder;
            currRunItems.addAll(remainderRunItems);
            int fullRunsNeeded = (currItemCount/maxCargo)-1;
            if (fullRunsNeeded < 0) {
                fullRunsNeeded = 0;
            }
            if (fullRunsNeeded > 0) {
                allRuns.add(new Run(currRunIndex, currRunIndex+fullRunsNeeded, currRunItems, currItemCount, true, 0));
            } else {
                allRuns.add(new Run(currRunIndex, currRunIndex, currRunItems, currItemCount, false, 0));
            }
        }
        if (allRuns.size() > 0) {
            return allRuns;
        } else {
            return null;
        }
    }
}
