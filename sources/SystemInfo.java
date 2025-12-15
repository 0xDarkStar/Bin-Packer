package sources;

import java.util.ArrayList;

public class SystemInfo {
    private String name;
    private int id;
    private double[] sysPos;
    private ArrayList<Station> stations;

    SystemInfo(String name, int id, double[] systemPosition, ArrayList<Station> stations) {
        this.name = name;
        this.id = id;
        this.sysPos = systemPosition;
        this.stations = stations;
    }

    // Getters
    String getName() {
        return this.name;
    }

    int getId() {
        return this.id;
    }

    double[] getSystemPosition() {
        return this.sysPos;
    }

    ArrayList<Station> getStations() {
        return this.stations;
    }

    // Setters
    void setNewName(String newName) {
        this.name = newName;
    }

    void setNewId(int newId) {
        this.id = newId;
    }

    void setSystemPosition(double[] newSystemPosition) {
        this.sysPos = newSystemPosition;
    }

    void setStations(ArrayList<Station> newStations) {
        this.stations = newStations;
    }
}
