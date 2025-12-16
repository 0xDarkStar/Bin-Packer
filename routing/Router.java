package routing;

public class Router {
    /**
     * Search for the best route
     * @param inputSys Systems to route through
     * @return Optimized route through all systems
     */
    public static int[] beginRouting(double[][] inputSys) {
        double[][] dists = createDistanceTable(inputSys);
        // Create extremely simple route with greedy algorithm
        int[] route = findRoute(dists);
        // Optimize the route
        route = optimizeRoute(route, dists);
        return route;
    }

    public static double[][] createDistanceTable(double[][] inputSys) {
        int sysCount = inputSys.length;
        double[][] distances = new double[sysCount][sysCount];

        for (int i=0; i<sysCount; i++) {
            for (int j=0; j<sysCount; j++) {
                distances[i][j] = pointDelta(inputSys[i], inputSys[j]);
            }
        }
        return distances;
    }

    public static double pointDelta(double[] pointA, double[] pointB) {
        double xA = pointA[0], yA = pointA[1], zA = pointA[2];
        double xB = pointB[0], yB = pointB[1], zB = pointB[2];
        // d = sqrt((xB-xA)^2 + (yB-yA)^2 + (zB-zA)^2)
        double distFromSys = Math.sqrt(Math.pow(xB-xA, 2) + Math.pow(yB-yA, 2) + Math.pow(zB-zA, 2));
        // Round up to second place
        distFromSys = Math.floor(distFromSys*100)/100;
        return distFromSys;
    }

    public static int[] findRoute(double[][] distances) {
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

    public static boolean isIntInArray(int[] route, int curr) {
        for (int i=0; i<route.length; i++) {
            if (route[i] == curr) {
                return true;
            }
        }
        return false;
    }

    public static int[] optimizeRoute(int[] route, double[][] distances) {
        int prevCost = determineCost(route, distances);
        int currCost;
        int[] improvedRoute;
        boolean improved = true;
        while(improved) {
            improved = false; // If we don't improve it, it stops
            for (int i = 1; i<route.length-3; i++) {
                for (int j = i+1; j <= route.length-2; j++) {
                    improvedRoute = reverseSection(route, i, j);
                    currCost = determineCost(improvedRoute, distances);
                    if (currCost < prevCost) {
                        route = improvedRoute.clone();
                        prevCost = currCost;
                        improved = true; // AGAIN
                    }
                }
            }
        }
        return route;
    }

    public static int determineCost(int[] route, double[][] distances) {
        int cost = 0;
        for (int i = 0; i<route.length-1; i++) {
            cost += distances[route[i]][route[i+1]];
        }
        return cost;
    }

    public static int[] reverseSection(int[] route, int start, int end) {
        int[] newRoute = route.clone();
        while (start < end) {
            int valueHold = newRoute[start];
            newRoute[start] = newRoute[end];
            newRoute[end] = valueHold;
            start++;
            end--;
        }
        return newRoute;
    }

    public static void printRoute(int[] route) {
        for (int i : route) {
            System.out.print(i+" ");
        }
        System.out.println();
    }
}
