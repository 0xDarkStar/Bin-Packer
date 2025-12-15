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
    public String getName() {
        return this.name;
    }

    public int getId() {
        return this.id;
    }

    public double[] getSystemPosition() {
        return this.sysPos;
    }

    public ArrayList<Station> getStations() {
        return this.stations;
    }

    // Setters
    public void setNewName(String newName) {
        this.name = newName;
    }

    public void setNewId(int newId) {
        this.id = newId;
    }

    public void setSystemPosition(double[] newSystemPosition) {
        this.sysPos = newSystemPosition;
    }

    public void setStations(ArrayList<Station> newStations) {
        this.stations = newStations;
    }
}
