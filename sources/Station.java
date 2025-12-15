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
    String getName() {
        return this.name;
    }

    String getType() {
        return this.type;
    }

    long getMarketId() {
        return this.marketID;
    }

    HashMap<String,Long> getMarket() {
        return this.market;
    }

    String getLargestPad() {
        return this.largestPad;
    }

    String[] getMaterialsContained() {
        return this.materialsContained;
    }

    // Setters
    void setName(String name) {
        this.name = name;
    }

    void setType(String type) {
        this.type = type;
    }

    void setMarketId(long marketID) {
        this.marketID = marketID;
    }

    void setMarket(HashMap<String,Long> market) {
        this.market = market;
    }

    void setLargestPad(String largestPad) {
        this.largestPad = largestPad;
    }

    void setMaterialsContained(String[] newMaterials) {
        this.materialsContained = newMaterials;
    }
}
