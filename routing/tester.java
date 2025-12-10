package routing;

public class tester {
    public static void main(String[] args) {
        double[][] systems = {
            {0.0, 0.0, 0.0},
            {3.03, -0.09, 3.16},
            {-3.03, 1.38, 4.94},
            {6.25, -1.28, -5.75}
        };
        int sysCount = systems.length;

        double[][] distances = Router.createDistanceTable(systems);
        int[] route = Router.beginRouting(systems);

        // Print distances table
        String out = "";
        for (int i=0; i<sysCount; i++) {
            out += "[";
            for (int j=0; j<sysCount; j++) {
                if (j == sysCount-1) {
                    out += ""+distances[i][j];
                } else {
                    out += ""+distances[i][j]+", ";
                }
            }
            out += "]\n";
        }
        System.out.println(out);

        // Print route
        for (int i : route) {
            System.out.print(i+" ");
        }
        System.out.println();
    }
}
