package sources;

import java.util.regex.Pattern;

import parser.jsonFiles.DepotInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.net.http.*;
import java.io.IOException;
import java.net.URI;

public class SourceFinder {
    // Oh how I love regex...
    private final String systemSearchRegex ="\"name\":\"([\\w\\s-]*)\",\"mainstar\":\"(?:[\\w\\s\\(\\)-]*)\",\"coords\":\\{\"x\":([\\d.-]*),\"y\":([\\d.-]*),\"z\":([\\d.-]*)";
    //                                  System name grabbed ^                      Ignore Star ^           System (x,y,z) grabbed   x ^              y ^              z ^
    private final String stationInfoRegex = "\"marketId\":(\\d*),\"type\":\"([a-zA-Z\\s]*)\",\"name\":\"([a-zA-Z-\\s]*)\"";
    //                                     market ID grabbed ^    station type ^                Station Name ^
    private final String stationMarketRegex = "\"id\":\"(?:[\\w\\s]*)\",\"name\":\"([\\w\\s\\-\\.]*)\",\"buyPrice\":(?:\\d*),\"stock\":(\\d*)";
    //                                                            Material Name grabbed ^                      Material Stock grabbed ^
    Pattern systemPattern = Pattern.compile(systemSearchRegex);
    Pattern stationPattern = Pattern.compile(stationInfoRegex);
    Pattern marketPattern = Pattern.compile(stationMarketRegex);

    String[] allowedStations = {"Orbis Starport", "Ocellus Starport", "Coriolis Starport", "Dodec Starport", "Asteroid Base", "Planetary Outpost", "Planetary Port"};

    HttpClient client = HttpClient.newHttpClient();

    public ArrayList<SystemInfo> searchForSources(DepotInfo depot, int searchRadius) {
        System.out.println("Searching for source systems...");
        ArrayList<SystemInfo> systems = findAllSystems(depot.getSysPos(), searchRadius);
        System.out.println("Searching for stations in systems...");
        systems = findStationsInSystems(systems);
        System.out.println("Checking markets in stations...");
        systems = findMarkets(systems, depot.getMatList());
        System.out.println("Search Complete!");
        return systems;
    }

    /**
     * Search for all systems in the given radius around 
     * @param coordinates The coordinates where we want to search from
     * @param searchRadius How far (in LY) we want to search
     * @return An array of all the systems found
     */
    public ArrayList<SystemInfo> findAllSystems(double[] coordinates, int searchRadius) {
        ArrayList<SystemInfo> systems = new ArrayList<SystemInfo>();

        String url = "https://edgis.elitedangereuse.fr/neighbors?x="+coordinates[0]+
            "&y="+coordinates[1]+
            "&z="+coordinates[2]+
            "&radius="+searchRadius;

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .GET().build();
        
        try {
            // Send request
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            // System.out.println("Status Code: "+response.statusCode());
            // System.out.println("Body: "+response.body());

            Matcher matcher = systemPattern.matcher(response.body());

            // Find all systems in response
            int counter = 0;
            while (matcher.find()) {
                String sysName = matcher.group(1);
                int id = counter;
                double x = Double.parseDouble(matcher.group(2));
                double y = Double.parseDouble(matcher.group(3));
                double z = Double.parseDouble(matcher.group(4));
                double[] systemCoords = {x, y, z};
                systems.add(new SystemInfo(sysName, id, systemCoords, null));
                counter++;
            }
            
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return systems;
    }

    /**
     * Find all the stations in the given list of systems. Remove any systems that don't have stations
     * @param systems List of systems to search through
     * @return List of systems with the stations added
     */
    public ArrayList<SystemInfo> findStationsInSystems(ArrayList<SystemInfo> systems) {
        for (int i = 0; i<systems.size(); i++) {
            SystemInfo currSys = systems.get(i);
            String sysName = currSys.getName().replace(" ", "%20"); // Prep for use in URL
            String url = "https://www.edsm.net/api-system-v1/stations?systemName="+sysName;

            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET().build();
            
            ArrayList<Station> stations = new ArrayList<Station>();
            
            try {
                TimeUnit.MILLISECONDS.sleep(500); // Wait half a second between requests to not hit the rate limit
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 429) {
                    // Damn, too many...
                    System.err.println("Warning: Too many requests. Waiting 1 Hour...");
                    TimeUnit.HOURS.sleep(1);
                    response = client.send(request, HttpResponse.BodyHandlers.ofString());
                }
                // System.out.println("Status Code: "+response.statusCode());
                // System.out.println("Body: "+response.body());

                Matcher matcher = stationPattern.matcher(response.body());

                // Find all the station info and add it to the station list
                while (matcher.find()) {
                    long marketId = Long.parseLong(matcher.group(1));
                    String stationType = matcher.group(2);
                    String stationName = matcher.group(3);
                    if (isStringInList(stationType, allowedStations)) {
                        stations.add(new Station(stationName, stationType, marketId, null));
                    }
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }

            if (stations.size() == 0) {
                // Remove the current system if no stations found
                systems.remove(i);
                i--;
            } else {
                // Add the stations to the current system
                currSys.setStations(stations);
                systems.set(i, currSys);
            }
        }

        return systems;
    }

    public ArrayList<SystemInfo> findMarkets(ArrayList<SystemInfo> systems, HashMap<String,Integer> reqMats) {
        // Make all material names lowercase in the required material list
        // This is because of liquid oxygen.
        // In the journals it is "Liquid oxygen", but on EDSM it is "Liquid Oxygen"!
        for (Object key : reqMats.keySet().toArray()) {
            String matName = key.toString();
            int tempNum = reqMats.get(matName);
            reqMats.remove(matName);
            reqMats.put(matName.toLowerCase(), tempNum);
        }
        for (int i = 0; i < systems.size(); i++) {
            // Check currSys
            SystemInfo currSys = systems.get(i);
            ArrayList<Station> stations = currSys.getStations();

            for (int j = 0; j<stations.size(); j++) {
                // Check currStat in currSys
                Station currStat = stations.get(j);
                long marketID = currStat.getMarketId();

                String url = "https://www.edsm.net/api-system-v1/stations/market?marketId="+marketID;

                HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET().build();

                HashMap<String,Long> marketStock = new HashMap<>();

                try {
                    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                    // System.out.println("Status Code: "+response.statusCode());
                    // System.out.println("Body: "+response.body());

                    Matcher matcher = marketPattern.matcher(response.body());

                    while (matcher.find()) {
                        String matName = matcher.group(1);
                        long matStock = Long.parseLong(matcher.group(2));
                        if (matStock != 0) {
                            marketStock.put(matName, matStock);
                        }
                    }
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }

                // Find what materials from the list the station has
                String[] materials = doesMarketFulfillRequirement(marketStock, reqMats);

                if (materials[0] == null) {
                    // No materials that are needed were found
                    stations.remove(j);
                    j--;
                } else {
                    // Add market to station and say what to use station for
                    currStat.setMarket(marketStock);
                    currStat.setMaterialsContained(materials);
                    // Update required material list to avoid multiple stations having the same material
                    reqMats = updateMaterialList(materials, reqMats);
                    // Update station list in system
                    stations.set(j, currStat);
                }
                currSys.setStations(stations); // update system's station list

                if (reqMats.size() == 0) {
                    // All materials have been found, end the search
                    break;
                }
            }
            if (currSys.getStations().size() == 0) {
                // System doesn't have any stations that have the required materials, REMOVED
                systems.remove(i);
                i--;
            } else {
                // Update systems list to show only useful stations in this system
                systems.set(i, currSys);
            }

            if (reqMats.size() == 0) {
                break;
            }
        }

        return systems;
    }

    public boolean isStringInList(String check, String[] list) {
        for (int i = 0; i<list.length; i++) {
            if (check.equals(list[i])) {
                return true;
            }
        }
        return false;
    }

    public String[] doesMarketFulfillRequirement(HashMap<String,Long> market, HashMap<String,Integer> reqMats) {
        String[] materialsFulfilled = new String[reqMats.size()];
        int matCounter = 0;
        for (int i = 0; i<market.size(); i++) {
            String currMat = market.keySet().toArray()[i].toString();
            // If the current material is one of the required materials
            if (reqMats.containsKey(currMat.toLowerCase())) {
                long stock = market.get(currMat);
                long reqStock = reqMats.get(currMat.toLowerCase()); // lowercase because EDSM
                if (stock >= reqStock) {
                    // The current material has enough stock
                    materialsFulfilled[matCounter] = currMat;
                    matCounter++;
                }
            }
        }
        return materialsFulfilled;
    }

    public HashMap<String,Integer> updateMaterialList(String[] matsToRemove, HashMap<String,Integer> materialList) {
        // Remove all the materials we found
        for (String material : matsToRemove) {
            // `matsToRemove` may contain null entries if the array was only
            // partially filled in `doesMarketFulfillRequirement`, so skip them.
            if (material == null) {
                continue;
            }
            materialList.remove(material.toLowerCase());
        }
        return materialList;
    }
}
