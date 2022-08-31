import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.*;
import java.io.*;

/**
 * Configures and executes the perceptron process.
 *
 * @author Brandon Park
 * @version 8/31/22
 */
public class PerceptronRunner {
    static boolean train = false;
    static int[] nodes;
    static boolean printDetailed;

    static final String DEFAULT_CONFIG_PATH = "src/defaults/defaultConfig.json";
    static JSONObject defaultConfig;
    static JSONObject config;

    static Perceptron perceptron;

    static double[][] inputSets;
    static double[][] outputSets;

    static String weightsPath;
    static String setsPath;

    static boolean useTrainingWeights;
    static double lambda;
    static int maxIterations;
    static double errorThreshold;
    static double minRandom;
    static double maxRandom;
    static boolean saveWeights;
    static String savedWeightsPath;
    static int autosaveInterval;


    /**
     * Parses and stores both the default configuration file and provided configuration file as JSONObjects, using the
     * default configuration file if the provided configuration file is missing or malformed.
     *
     * @param filePath the file path of the configuration file.
     */
    private static void parseConfig(String filePath) {
        JSONParser parser = new JSONParser();

        try {
            FileReader fileReader = new FileReader(DEFAULT_CONFIG_PATH);
            defaultConfig = (JSONObject) parser.parse(fileReader);
        } catch (FileNotFoundException e) {
            System.out.println("Default configuration file not found at " + DEFAULT_CONFIG_PATH + ". Aborting process" +
                    ".");
            System.exit(1);
        } catch (ParseException | IOException e) {
            System.out.println(e.getMessage());
            System.out.println("Malformed default configuration file. Aborting process.");
            System.exit(1);
        }

        try {
            FileReader fileReader = new FileReader(filePath);
            System.out.println("Configuration file found. Loading configuration options from " + filePath + ".");
            config = (JSONObject) parser.parse(fileReader);
            System.out.println("Configuration options successfully loaded.");
        } catch (FileNotFoundException e) {
            System.out.println("Configuration file not found. Using default configuration file at " + DEFAULT_CONFIG_PATH + ".");
            config = defaultConfig;
        } catch (ParseException | IOException e) {
            System.out.println(e.getMessage());
            System.out.println("Malformed configuration file. Using default configuration file at " + DEFAULT_CONFIG_PATH + ".");
            config = defaultConfig;
        }
    }

    /**
     * Gets the value from config corresponding to a given key, or the default if not provided.
     *
     * @param key the key corresponding to the desired value.
     * @return returns the value corresponding to key, or the default if config does not provide it.
     */
    private static Object getConfig(String key) {
        Object value = defaultConfig.get(key);
        if (config.containsKey(key)) value = config.get(key);
        return value;
    }

    /**
     * Configures the network according to config.
     */
    private static void configNetwork() {
        train = (boolean) getConfig("train");
        nodes = ((JSONArray) getConfig("nodes")).stream().mapToInt(i -> (int) (long) i).toArray();
        printDetailed = (boolean) getConfig("printDetailed");
        perceptron = new Perceptron(nodes, printDetailed);
    }

    /**
     * Configures the runtime options according to config.
     *
     * @param operation the operation being executed on the network, must be "run" or "train".
     */
    private static void configure(String operation) {
        setsPath = (String) getConfig("setsPath");
        if (operation.equals("run")) {
            weightsPath = (String) getConfig("weightsPath");
        } else if (operation.equals("train")) {
            useTrainingWeights = (boolean) getConfig("useTrainingWeights");
            if (useTrainingWeights)
                weightsPath = (String) getConfig("weightsPath");

            lambda = (double) getConfig("lambda");
            maxIterations = (int) (long) getConfig("maxIterations");
            errorThreshold = (double) getConfig("errorThreshold");
            minRandom = (double) getConfig("minRandom");
            maxRandom = (double) getConfig("maxRandom");

            saveWeights = (boolean) getConfig("saveWeights");
            if (saveWeights) {
                savedWeightsPath = (String) getConfig("savedWeightsPath");
                autosaveInterval = (int) (long) getConfig("autosaveInterval");
            }
        }
    }

    /**
     * Tests the network on an array of testing sets.
     */
    private static void runNetwork() {
        System.out.println("Running network.");
        configure("run");

        perceptron.W = WeightsHandler.readWeights(nodes, weightsPath);

        Object[] testingSets = SetsHandler.readSets(nodes[0], nodes[3], setsPath);

        inputSets = (double[][]) testingSets[0];
        outputSets = (double[][]) testingSets[1];

        for (int t = 0; t < inputSets.length; t++)
            perceptron.runWithOutput(inputSets[t], outputSets[t]);
    }

    /**
     * Trains the network on an array of training sets.
     */
    private static void trainNetwork() {
        System.out.println("Training network.");
        configure("train");

        System.out.println(" - Random weight range: " + minRandom + " to " + maxRandom);
        System.out.println(" - Max iterations: " + maxIterations);
        System.out.println(" - Lambda: " + lambda);

        if (useTrainingWeights)
            perceptron.W = WeightsHandler.readWeights(nodes, weightsPath);
        else
            perceptron.W = WeightsHandler.randomizeWeights(nodes, minRandom, maxRandom);

        Object[] trainingSets = SetsHandler.readSets(nodes[0], nodes[3], setsPath);

        inputSets = (double[][]) trainingSets[0];
        outputSets = (double[][]) trainingSets[1];

        perceptron.train(maxIterations, lambda, errorThreshold, inputSets, outputSets, savedWeightsPath, autosaveInterval);

        if (saveWeights)
            WeightsHandler.writeWeights(perceptron.W, savedWeightsPath);
    }

    /**
     * Executes the perceptron process.
     *
     * @param args options from the command line containing the file path of the configuration file (optional)
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        if (args.length > 0)
            parseConfig(args[0]);
        else
            parseConfig("");
        configNetwork();

        System.out.println();
        System.out.print("Network configuration:");
        for (int alpha = 0; alpha < 4; alpha++) {
            System.out.print(" " + nodes[alpha]);
        }
        System.out.println();

        System.out.println();
        if (train) trainNetwork();
        else runNetwork();
    }
}
