package sources;

import java.util.HashMap;

public class Station {
    private String name;
    private String type; // Orbital or Surface?
    private long marketID; // Market ID to search for market
    private HashMap<String,Long> market; // {"Material Name": <Material Stock>}
    private String largestPad; // Medium or Large?
    private String[] materialsContained;

    Station(String name, String type, long marketID, String largestPad) {
        this.name = name;
        this.type = type;
        this.marketID = marketID;
        this.largestPad = largestPad;
    }

    // Getters
    public String getName() {
        return this.name;
    }

    public String getType() {
        return this.type;
    }

    public long getMarketId() {
        return this.marketID;
    }

    public HashMap<String,Long> getMarket() {
        return this.market;
    }

    public String getLargestPad() {
        return this.largestPad;
    }

    public String[] getMaterialsContained() {
        return this.materialsContained;
    }

    // Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setMarketId(long marketID) {
        this.marketID = marketID;
    }

    public void setMarket(HashMap<String,Long> market) {
        this.market = market;
    }

    public void setLargestPad(String largestPad) {
        this.largestPad = largestPad;
    }

    public void setMaterialsContained(String[] newMaterials) {
        this.materialsContained = newMaterials;
    }
}
