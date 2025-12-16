package parser.jsonFiles;

import java.util.HashMap;

public class DepotInfo {
    private HashMap<String,Integer> matList; // {"Material Name": <Material Quantity>}
    private double[] sysPos; // {x, y, z}

    DepotInfo(HashMap<String,Integer> matList, double[] sysPos) {
        this.matList = matList;
        this.sysPos = sysPos;
    }

    public HashMap<String,Integer> getMatList() {
        return this.matList;
    }

    public double[] getSysPos() {
        return this.sysPos;
    }

    public void setMatList(HashMap<String,Integer> newMatList) {
        this.matList = newMatList;
    }

    public void setSysPos(double[] newSysPos) {
        this.sysPos = newSysPos;
    }
}
