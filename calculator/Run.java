package calculator;
import java.util.List;
import java.util.ArrayList;

public class Run {
    private int runStartNumber;
    private int runEndNumber;
    private List<RunItem> items;
    private int totalUsed;
    private boolean isFull;
    private int runRemainder;

    Run(int runStartNum, int runEndNum, List<RunItem> items, int totalSpaceUsed, boolean isFullRun, int leftOverItem) {
        this.runStartNumber = runStartNum;
        this.runEndNumber = runEndNum;
        this.items = new ArrayList<>(items); // Create a copy to avoid reference issues
        this.totalUsed = totalSpaceUsed;
        this.isFull = isFullRun;
        this.runRemainder = leftOverItem;
    }

    public int getRunStartNumber() {
        return runStartNumber;
    }

    public void setRunStartNumber(int runStartNumber) {
        this.runStartNumber = runStartNumber;
    }

    public int getRunEndNumber() {
        return runEndNumber;
    }

    public void setRunEndNumber(int runEndNumber) {
        this.runEndNumber = runEndNumber;
    }

    public List<RunItem> getItems() {
        return items;
    }

    public void setItems(List<RunItem> items) {
        this.items = items;
    }

    public int getTotalUsed() {
        return totalUsed;
    }

    public void setTotalUsed(int totalUsed) {
        this.totalUsed = totalUsed;
    }

    public boolean isFull() {
        return isFull;
    }

    public void setFull(boolean isFull) {
        this.isFull = isFull;
    }

    public int getRunRemainder() {
        return runRemainder;
    }

    public void setRunRemainder(int leftOverItem) {
        this.runRemainder = leftOverItem;
    }

    public int getLargestNameSize() {
        int currBest = 0;
        for (RunItem item : items) {
            if (item.getName().length() > currBest) {
                currBest = item.getName().length();
            }
        }
        return currBest;
    }

    public int getNumSize() {
        int numSize = 2;
        int curr = this.totalUsed;
        while (curr >= 10) {
            if (curr/10 >= 1) {
                numSize += 1;
                curr/=10;
            } else {
                break;
            }
        }
        return numSize;
    }
}