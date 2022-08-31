import java.util.*;
import java.io.*;

/**
 * Handles the input and output of the network's weights.
 *
 * @author Brandon Park
 * @version 8/28/22
 */
public class WeightsHandler {
    /**
     * Initializes an empty array of weights according to a set of dimensions.
     *
     * @param nodes the number of nodes in each layer of the network.
     * @return returns an empty array of weights with the given dimensions.
     */
    private static double[][][] initWeightsArray(int[] nodes) {
        int layers = nodes.length;

        double[][][] W = new double[layers - 1][][];
        for (int n = 0; n < layers - 1; n++)
            W[n] = new double[nodes[n]][nodes[n + 1]];

        return W;
    }

    /**
     * Initializes an array of weights to random values within a certain range.
     *
     * @param nodes the number of nodes in each layer of the network.
     * @param min   the lower bound of the random generation (inclusive).
     * @param max   the upper bound of the random generation (exclusive).
     * @return returns the randomly initialized array of weights.
     */
    public static double[][][] randomizeWeights(int[] nodes, double min, double max) {
        double[][][] W = initWeightsArray(nodes);

        for (int n = 0; n < W.length; n++) {
            for (int a = 0; a < W[n].length; a++) {
                for (int b = 0; b < W[n][a].length; b++)
                    W[n][a][b] = RandomGenerator.random(min, max);
            }
        }

        return W;
    }

    /**
     * Reads in an array of weights from a text file.
     *
     * @param nodes    the number of nodes in each layer of the network.
     * @param filePath the file path of the text file.
     * @return returns the array of weights.
     */
    public static double[][][] readWeights(int[] nodes, String filePath) {
        int layers = nodes.length;
        double[][][] W = null;

        try {
            FileReader fileReader = new FileReader(filePath);
            Scanner scanner = new Scanner(fileReader);

            W = initWeightsArray(nodes);

            for (int n = 0; n < layers - 1; n++) {
                if (scanner.nextInt() != nodes[n]) {
                    System.out.println("Weights file does not match network structure. Aborting process.");
                    System.exit(1);
                }
            }


            while (scanner.hasNext()) {
                int n = scanner.nextInt();
                int a = scanner.nextInt();
                int b = scanner.nextInt();
                W[n][a][b] = scanner.nextDouble();
            }

            scanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("Weights file not found at " + filePath + ". Aborting process.");
            System.exit(1);
        }

        return W;
    }

    /**
     * Writes an array of weights to a text file.
     *
     * @param W        the array of weights.
     * @param filePath the file path of the text file.
     */
    public static void writeWeights(double[][][] W, String filePath) {
        try {
            FileWriter writer = new FileWriter(filePath);

            String result = "";
            result += W[0].length + " " + W[1].length + " " + W[1][0].length + "\n\n";

            for (int n = 0; n < W.length; n++) {
                for (int a = 0; a < W[n].length; a++) {
                    for (int b = 0; b < W[n][a].length; b++) {
                        result += n + " " + a + " " + b + " ";
                        result += W[n][a][b] + "\n";
                    }
                }
            }

            writer.write(result);
            writer.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.out.println("Exception occurred during write to file. Aborting process.");
            System.exit(1);
        }
    }
}
