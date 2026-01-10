package sources;

import java.util.ArrayList;
import java.util.HashMap;

public class FilterResult {
    private ArrayList<Station> stations;
    private HashMap<String,Integer> requiredMaterials;

    FilterResult(ArrayList<Station> stations, HashMap<String,Integer> reqMats) {
        this.stations = stations;
        this.requiredMaterials = reqMats;
    }

    // Getters
    public ArrayList<Station> getStations() {
        return stations;
    }

    public HashMap<String, Integer> getRequiredMaterials() {
        return requiredMaterials;
    }

    // Setters
    public void setStations(ArrayList<Station> stations) {
        this.stations = stations;
    }

    public void setRequiredMaterials(HashMap<String, Integer> requiredMaterials) {
        this.requiredMaterials = requiredMaterials;
    }
}
