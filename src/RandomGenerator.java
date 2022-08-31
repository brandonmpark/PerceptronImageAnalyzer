/**
 * Generates random numbers.
 *
 * @author Brandon Park
 * @version 8/28/22
 */
public class RandomGenerator {
    /**
     * Generates a random double between min (inclusive) and max (exclusive).
     *
     * @param min the lower bound of the random generation (inclusive).
     * @param max the upper bound of the random generation (exclusive).
     * @return returns a random double between min and max.
     */
    public static double random(double min, double max) {
        double difference = max - min;
        return difference * Math.random() + min;
    }
}
