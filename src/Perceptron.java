/**
 * Models a two hidden-layer perceptron algorithm with any number of nodes using the backpropagation algorithm.
 *
 * @author Brandon Park
 * @version 8/28/22
 */
public class Perceptron {
    public double startTime;

    double Theta_i, Theta_j, Theta_k, Omega_j, Omega_k, Psi_j, Psi_k;

    public double[][][] W;
    public double[][] a = new double[4][];
    public double[][] Theta = new double[4][];

    public int[] nodes;

    public double totalError;

    public double[] psi;

    boolean printDetailed;

    /**
     * Constructs a new Perceptron object and initializes the architecture.
     *
     * @param nodes         the number of nodes in each layer of the network, where nodes[i] corresponds to the i'th hidden
     *                      layer.
     * @param printDetailed whether the network should have detailed output or not.
     */
    public Perceptron(int[] nodes, boolean printDetailed) {
        this.nodes = nodes;

        for (int alpha = 0; alpha < 4; alpha++)
            a[alpha] = new double[nodes[alpha]];

        this.printDetailed = printDetailed;
    }

    /**
     * Calculates the sigmoid of an input (i.e. 1/(1+e^-input))
     *
     * @param input the input value.
     * @return returns the sigmoid of input as a double.
     */
    private double sigmoid(double input) {
        return 1.0 / (1.0 + Math.exp(-input));
    }

    /**
     * Calculates f' of an input, which for sigmoid is f(input)(1-f(input)).
     *
     * @param input the input value;
     * @return returns f' of input as a double.
     */
    private double fPrime(double input) {
        double f = sigmoid(input);
        return f * (1.0 - f);
    }

    /**
     * Runs the perceptron, propagating each activation result forward into the next layer.
     *
     * @param inputSet the input values of the testing set.
     */
    public void run(double[] inputSet) {
        a[0] = inputSet;

        for (int i = 0; i < nodes[3]; i++) {
            Theta_i = 0.0;

            for (int j = 0; j < nodes[2]; j++) {
                Theta_j = 0.0;

                for (int k = 0; k < nodes[1]; k++) {
                    Theta_k = 0.0;

                    for (int m = 0; m < nodes[0]; m++)
                        Theta_k += a[0][m] * W[0][m][k];

                    a[1][k] = sigmoid(Theta_k);
                    Theta_j += a[1][k] * W[1][k][j];
                }

                a[2][j] = sigmoid(Theta_j);
                Theta_i += a[2][j] * W[2][j][i];
            }

            a[3][i] = sigmoid(Theta_i);
        }
    }

    /**
     * Runs the perceptron while outputting the results to the console.
     *
     * @param inputSet the input values of the testing set.
     */
    public void runWithOutput(double[] inputSet) {
        run(inputSet);

        System.out.println();
        if (printDetailed) {
            System.out.print("Inputs:");
            for (int m = 0; m < nodes[0]; m++)
                System.out.print(" " + a[0][m]);
            System.out.print(", ");
        }

        System.out.print("F:");
        for (int i = 0; i < nodes[3]; i++)
            System.out.print(" " + a[3][i]);
    }

    /**
     * Runs the perceptron while outputting both the results and expected values to console.
     *
     * @param inputSet  the input values of the testing set.
     * @param outputSet the output values of the testing set.
     */
    public void runWithOutput(double[] inputSet, double[] outputSet) {
        run(inputSet);

        System.out.println();
        if (printDetailed) {
            System.out.print("Inputs:");
            for (int m = 0; m < nodes[0]; m++)
                System.out.print(" " + a[0][m]);
            System.out.print(", ");
        }

        System.out.print("F:");
        for (int i = 0; i < nodes[3]; i++)
            System.out.print(" " + a[3][i]);
        System.out.print(", ");

        System.out.print("T:");
        for (int i = 0; i < nodes[3]; i++)
            System.out.print(" " + outputSet[i]);
    }

    /**
     * Runs the perceptron, propagating each activation result forward and storing necessary values to be used in backpropagation.
     *
     * @param inputSet  the input values of the testing set.
     * @param outputSet the output values of the testing set.
     */
    public void runDetailed(double[] inputSet, double[] outputSet) {
        a[0] = inputSet;

        for (int i = 0; i < nodes[3]; i++) {
            Theta[3][i] = 0.0;

            for (int j = 0; j < nodes[2]; j++) {
                Theta[2][j] = 0.0;

                for (int k = 0; k < nodes[1]; k++) {
                    Theta[1][k] = 0.0;

                    for (int m = 0; m < nodes[0]; m++)
                        Theta[1][k] += a[0][m] * W[0][m][k];

                    a[1][k] = sigmoid(Theta[1][k]);
                    Theta[2][j] += a[1][k] * W[1][k][j];
                }

                a[2][j] = sigmoid(Theta[2][j]);
                Theta[3][i] += a[2][j] * W[2][j][i];
            }

            a[3][i] = sigmoid(Theta[3][i]);
            totalError += 0.5 * (outputSet[i] - a[3][i]) * (outputSet[i] - a[3][i]);
            psi[i] = (outputSet[i] - a[3][i]) * fPrime(Theta[3][i]);
        }
    }

    /**
     * Trains the perceptron, using the gradient descent algorithm with backpropagation to update the weights until one of the following conditions is met:
     * 1. The max number of iterations is reached.
     * 2. The total error of the training sets is below the threshold.
     *
     * @param maxIterations    the max number of training cycles.
     * @param lambda           the learning rate applied to each weight change.
     * @param errorThreshold   the error threshold to be reached.
     * @param trainInput       the input values of the training set.
     * @param trainOutput      the output values of the training set.
     * @param weightsFilePath  the file path where the weights are saved.
     * @param autosaveInterval the number of training cycles before each autosave occurs.
     */
    public void train(int maxIterations, double lambda, double errorThreshold, double[][] trainInput, double[][] trainOutput, String weightsFilePath, int autosaveInterval) {
        startTime = System.currentTimeMillis();
        boolean done = false;
        int iteration = 1;

        for (int alpha = 0; alpha < 4; alpha++)
            Theta[alpha] = new double[nodes[alpha]];

        psi = new double[nodes[3]];

        while (!done) {
            totalError = 0.0;

            for (int t = 0; t < trainInput.length; t++) {
                runDetailed(trainInput[t], trainOutput[t]);

                for (int k = 0; k < nodes[1]; k++) {
                    Omega_k = 0.0;

                    for (int j = 0; j < nodes[2]; j++) {
                        Omega_j = 0.0;

                        for (int i = 0; i < nodes[3]; i++) {
                            Omega_j += psi[i] * W[2][j][i];
                            W[2][j][i] += lambda * a[2][j] * psi[i];
                        }

                        Psi_j = Omega_j * fPrime(Theta[2][j]);
                        Omega_k += Psi_j * W[1][k][j];
                        W[1][k][j] += lambda * a[1][k] * Psi_j;
                    }

                    Psi_k = Omega_k * fPrime(Theta[1][k]);

                    for (int m = 0; m < nodes[0]; m++)
                        W[0][m][k] += lambda * a[0][m] * Psi_k;
                }
            }

            if (printDetailed)
                System.out.println("Iteration " + iteration + "'s total error is " + totalError + ".");

            iteration++;

            if (iteration > maxIterations) {
                System.out.println();
                System.out.println("Max number of iterations reached (" + maxIterations + ").");
                done = true;
            } else if (totalError < errorThreshold) {
                System.out.println();
                System.out.println(iteration + " total iterations.");
                System.out.println("Error threshold met: " + totalError + " total error compared to threshold " + errorThreshold + ".");
                done = true;
            } else if (autosaveInterval > 0 && iteration % autosaveInterval == 0) {
                System.out.println("Autosaving... (at " + iteration + " total iterations with autosave interval of " + autosaveInterval + "). Currently at " + totalError + " total error.");
                WeightsHandler.writeWeights(W, weightsFilePath);
            }
        }

        System.out.println((System.currentTimeMillis() - startTime) + "ms elapsed.");

        for (int t = 0; t < trainInput.length; t++)
            runWithOutput(trainInput[t], trainOutput[t]);
    }
}
