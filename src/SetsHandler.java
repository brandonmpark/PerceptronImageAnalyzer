import java.util.*;
import java.io.*;

/**
 * Handles the input of testing and training sets for the network.
 *
 * @author Brandon Park
 * @version 8/31/22
 */
public class SetsHandler {
    /**
     * Reads an array of training/testing sets from a text file.
     *
     * @param inputNodes  the number of input nodes in the network.
     * @param outputNodes the number of output nodes in the network.
     * @param filePath    the file path of the text file.
     * @return returns the array of testing sets.
     */
    public static Object[] readSets(int inputNodes, int outputNodes, String filePath) {
        double[][] inputSets = null;
        double[][] outputSets = null;

        if (filePath.equals("TRAINING_IMAGES")) {
            inputSets = new double[25][inputNodes];
            outputSets = new double[25][outputNodes];
            getTrainingImages(inputSets, outputSets);
        } else if (filePath.equals("TESTING_IMAGES")) {
            inputSets = new double[5][inputNodes];
            outputSets = new double[5][outputNodes];
            getTestingImages(inputSets, outputSets);
        } else {
            try {

                FileReader fileReader = new FileReader(filePath);
                Scanner scanner = new Scanner(fileReader);

                int numSets = scanner.nextInt();
                inputSets = new double[numSets][inputNodes];
                outputSets = new double[numSets][outputNodes];

                for (int t = 0; t < inputSets.length; t++) {
                    for (int k = 0; k < inputNodes; k++)
                        inputSets[t][k] = scanner.nextDouble();
                    for (int i = 0; i < outputNodes; i++)
                        outputSets[t][i] = scanner.nextDouble();
                }
            } catch (FileNotFoundException e) {
                System.out.println("Training/testing file not found at " + filePath + ". Aborting process.");
                System.exit(1);
            }
        }

        return new Object[]{inputSets, outputSets};
    }

    /**
     * Reads in the images for training.
     *
     * @param inputSets  the array of input sets for the network.
     * @param outputSets the array of output sets for the network.
     */
    private static void getTrainingImages(double[][] inputSets, double[][] outputSets) {
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                inputSets[i * 5 + j] = ImageProcessor.getImage("src/hands/" + (int) (i + 1) + "_" + (int) (j + 1) +
                        ".bmp");
                outputSets[i * 5 + j] = new double[]{0.1 * (i + 1)};
            }
        }
    }

    /**
     * Reads in the images for testing.
     *
     * @param inputSets  the array of input sets for the network.
     * @param outputSets the array of output sets for the network.
     */
    private static void getTestingImages(double[][] inputSets, double[][] outputSets) {
        for (int i = 0; i < 5; i++) {
            inputSets[i] = ImageProcessor.getImage("src/hands/" + (int) (i + 1) + "_" + 6 + ".bmp");
            outputSets[i] = new double[]{0.1 * (i + 1)};
        }
    }
}
