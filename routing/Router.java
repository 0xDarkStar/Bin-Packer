package routing;

public class Router {
    static int[] beginRouting(double[][] inputSys) {
        double[][] dists = createDistanceTable(inputSys);
        // Create extremely simple route with greedy algorithm
        // TODO: Use a better algorithm to create the route
        int[] route = findRoute(dists);
        return route;
    }

    static double[][] createDistanceTable(double[][] inputSys) {
        int sysCount = inputSys.length;
        double[][] distances = new double[sysCount][sysCount];

        for (int i=0; i<sysCount; i++) {
            for (int j=0; j<sysCount; j++) {
                double xA = inputSys[i][0], yA = inputSys[i][1], zA = inputSys[i][2];
                double xB = inputSys[j][0], yB = inputSys[j][1], zB = inputSys[j][2];
                // d = sqrt((xB-xA)^2 + (yB-yA)^2 + (zB-zA)^2)
                double distFromSys = Math.sqrt(Math.pow(xB-xA, 2) + Math.pow(yB-yA, 2) + Math.pow(zB-zA, 2));
                // Round up to second place
                distFromSys = Math.floor(distFromSys*100)/100;
                distances[i][j] = distFromSys;
            }
        }
        return distances;
    }

    static int[] findRoute(double[][] distances) {
        int sysCount = distances.length;
        int[] route = new int[sysCount+1];
        route[0] = 0;

        for (int i=0; i<sysCount; i++) {
            int currSys = route[i];
            int closest = currSys; // Make sure it always grabs another system.
            for (int j=0; j<sysCount; j++) {
                if (isIntInArray(route, j)) {
                    // If current item in route, skip
                    continue;
                }
                if (closest == currSys) {
                    closest = j;
                } else if (distances[currSys][j] < distances[currSys][closest]) {
                    closest = j; // If this system is closer
                }
            }
            route[i+1] = closest;
        }
        route[sysCount] = 0;

        return route;
    }

    static boolean isIntInArray(int[] route, int curr) {
        for (int i=0; i<route.length; i++) {
            if (route[i] == curr) {
                return true;
            }
        }
        return false;
    }
}
