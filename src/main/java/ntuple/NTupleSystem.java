package ntuple;

import org.apache.commons.math3.util.Combinations;
import org.apache.commons.math3.util.Pair;
import space.SearchSpace;
import utils.BestItemSelector;
import utils.PRNG;
import utils.SummaryStatisticsPlus;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the NTuple bandit fitness landscape model.
 * @author sml
 * @author Acemad
 */
public class NTupleSystem {

    // The search space associated with the fitness landscape model
    private final SearchSpace searchSpace;
    // The list of NTuples to consider for this model
    private final List<NTuple> nTuples;
    // The list of points sampled
    private final List<int[]> sampledPoints;

    /**
     * Constructs an NTupleSystem instance using the given search space and NTuple lengths. Initializes the lists of
     * nTuples and sampled points. Generates the required NTuples based on the given lengths.
     * @param searchSpace The associated SearchSpace
     * @param tupleLengths The tuple lengths to consider
     */
    public NTupleSystem(SearchSpace searchSpace, int... tupleLengths) {
        this.searchSpace = searchSpace;
        nTuples = new ArrayList<>();
        sampledPoints = new ArrayList<>();
        generateNTuples(tupleLengths);
    }

    /**
     * Given a list of tuple lengths, generate the required NTuples for each length depending on the number of
     * dimensions in the search space. The list of nTuples is populated with the required NTuple instances.
     * Note: Uses apache commons math library to generate combinations
     *
     * @param tupleLengths Lengths of the n-tuples to consider
     */
    private void generateNTuples(int... tupleLengths) {
        for (int tupleLength : tupleLengths) {
            Combinations combinations = new Combinations(searchSpace.dimensions(), tupleLength);
            for (int[] combination : combinations)
                nTuples.add(new NTuple(combination, searchSpace));
        }
        nTuples.sort(null);
    }

    /**
     * Adds the given point to the sampled points list, and to each tuple in the tuples list. The NTuples will extract
     * the sub-components of the point in the dimensions indexed by the NTuple, the extracted pattern will be added
     * along the given value to the internal patterns map.
     *
     * @param point The point in question; a point from the search space.
     * @param value The value of the point with respect to an evaluation function
     */
    public void addPoint(int[] point, double value) {
        sampledPoints.add(point);
        for (NTuple nTuple : nTuples)
            nTuple.addPoint(point, value);
    }

    /**
     * Computes the mean value estimate of a given point using the data present in the model. From each tuple get the
     * stats object associated with the pattern extracted from the given point. If the stats object exists (the pattern
     * was previously sampled), then the mean value is saved for each tuple. The mean of saved means is returned as
     * the mean value estimate of the given point.
     *
     * @param point The point in question
     * @return The mean value estimate of the point using the underlying model
     */
    public double getMeanValueEstimate(int[] point) {

        // Holds mean values from the nTuples
        SummaryStatisticsPlus meanValueStats = new SummaryStatisticsPlus();

        // Iterates over tuples (bandits) and compute the mean value of the arm (pattern from point).
        for (NTuple nTuple : nTuples) {
            SummaryStatisticsPlus pointStats = nTuple.getStats(point);
            if (pointStats != null) { // The arm (point) was previously chosen
                double meanValue = pointStats.getMean();
                if (!Double.isNaN(meanValue))
                    meanValueStats.addValue(pointStats.getMean());
            }
        }

        // Compute and return the mean value estimate from the nTuple means.
        double meanValueEstimate = meanValueStats.getMean();
        return (Double.isNaN(meanValueEstimate) ? 0.0 : meanValueEstimate);
    }

    /**
     * Given a point from the search space, compute its exploration estimate. First, compute the exploration vector,
     * which has the same size as the number of NTuples, and contains in each position the value of the exploration
     * term of the UCB formula related to one NTuple (bandit) and the arm played on that bandit (point). Next, compute
     * and return the mean value of the values in the exploration vector.
     *
     * @param point The point in question, used to extract the arm played in each bandit.
     * @param epsilon Parameter used to calculate an exploration term even for unexplored arms.
     * @return The exploration estimate related to the point.
     */
    public double getExplorationEstimate(int[] point, double epsilon) {

        double[] explorationVector = new double[nTuples.size()];

        // Iterate over tuples (bandits) and compute the exploration term of the arm played (pattern from the point)
        for (int i = 0; i < nTuples.size(); i++) {
            NTuple nTuple = nTuples.get(i);
            SummaryStatisticsPlus stats = nTuple.getStats(point);
            if (stats != null) // Arm already sampled
                explorationVector[i] = Math.sqrt(Math.log(nTuple.numSamples() + 1) / (stats.getN() + epsilon));
            else // Arm was not sampled
                explorationVector[i] = Math.sqrt(Math.log(nTuple.numSamples() + 1) / epsilon);
        }

        // Compute the mean of the exploration vector
        double mean = 0;
        for (double explorationValue : explorationVector)
            mean += explorationValue / nTuples.size();

        return mean;
    }

    /**
     * Given a point in the search space, compute its UCB value using the mean value estimate, and exploration estimate.
     * A tie-breaking noise is added to the result.
     *
     * @param point The point in question
     * @param epsilon Exploration estimate parameter
     * @param kExplore Exploration Coefficient
     * @return UCB value of the point
     */
    public double getUCBValue(int[] point, double epsilon, double kExplore) {
        return getMeanValueEstimate(point) + kExplore * getExplorationEstimate(point, epsilon) +
                PRNG.nextDouble() * 1e-6;
    }

    /**
     * From the list of sampled points, find the point that has the highest mean value estimate and return it along
     * its value in a Pair object.
     *
     * @return A Pair instance consisting of the best point, and its value
     */
    public Pair<int[], Double> getBestOfSampled() {
        BestItemSelector<int[]> selector = new BestItemSelector<>(BestItemSelector.HIGHER_IS_BETTER);
        for (int[] sampledPoint : sampledPoints)
            selector.addItem(sampledPoint, getMeanValueEstimate(sampledPoint));
        return new Pair<>(selector.getBestItem(), selector.getBestItemScore());
    }

    /**
     * Iterates through all the points in the search space, and finds the one with the highest mean value estimate.
     * This will execute slowly for larger search spaces.
     *
     * @return A Pair instance consisting of the best point, and its value
     */
    public Pair<int[], Double> getBestSolution() {
        BestItemSelector<int[]> selector = new BestItemSelector<>(BestItemSelector.HIGHER_IS_BETTER);
        for (long i = 0; i < searchSpace.size(); i++) {
            int[] point = searchSpace.getPoint(i);
            selector.addItem(point, getMeanValueEstimate(point));
        }
        return new Pair<>(selector.getBestItem(), selector.getBestItemScore());
    }

    /**
     * Resets the landscape model by clearing the sampled points list, and resetting the underlying NTuples.
     */
    public void reset() {
        sampledPoints.clear();
        for (NTuple nTuple : nTuples)
            nTuple.reset();
    }

    /**
     * Generate a detailed report containing the statistics of each NTuple (bandits) and their patterns (arms)
     *
     * @return A String containing the textual representation of the report.
     */
    public String generateReport() {
        StringBuilder stringBuilder = new StringBuilder();
        for (NTuple nTuple : nTuples)
            stringBuilder.append(nTuple.generateReport());
        return stringBuilder.toString();
    }

    /**
     * Returns a random point from the underlying search space
     * @return A random point.
     */
    public int[] getRandomPoint() {
        return searchSpace.randomPoint();
    }

    public SearchSpace getSearchSpace() {
        return searchSpace;
    }

    public List<NTuple> getNTuples() {
        return nTuples;
    }

    public List<int[]> getSampledPoints() {
        return sampledPoints;
    }

}
