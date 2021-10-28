package ntuple;

import space.SearchSpace;
import utils.SummaryStatisticsPlus;

import java.util.*;

/**
 * Define an NTuple as a tuple consisting of indices, each pointing to a specific dimension in the search space.
 * An NTuple keeps track of all NTuplePatterns that were sampled and that fall within the dimensions tracked by the
 * NTuple.
 * @author sml
 * @author Acemad
 */
public class NTuple implements Comparable<NTuple> {

    // The array of dimension indices
    private final int[] nTuple;
    // A map linking each NTuplePattern, a pattern of values in the dimensions pointed to by this NTuple, to a
    // SummaryStatisticsPlus object. The aim is to keep track of the various statistics of different patterns.
    private final Map<NTuplePattern, SummaryStatisticsPlus> patternStats;
    // The number of samples added to this NTuple.
    private int numSamples = 0;
    // The number of combinations possible for this nTuple. Depends on the search space.
    private final long combinationsCount;

    /**
     * Constructs an NTuple using the given array of dimension indices.
     * @param nTuple The NTuple array, consisting of dimension indices.
     * @param searchSpace The search space to use for calculating the number of possible combinations.
     */
    public NTuple(int[] nTuple, SearchSpace searchSpace) {
        this.nTuple = nTuple;
        this.combinationsCount = nTupleCombinationsCount(searchSpace);
        patternStats = new HashMap<>();
    }

    /**
     * Computes the number of combinations possible for this nTuple, with respect to the given search space.
     * @param searchSpace A SearchSpace instance specifying the number of dimensions and their sizes
     * @return The number of combinations possible.
     */
    private long nTupleCombinationsCount(SearchSpace searchSpace) {
        long size = 1;
        for (int dimension : nTuple)
            size *= searchSpace.dimensionSize(dimension);
        return size;
    }

    /**
     * Adds a sample point and its value to this NTuple. First, it will derive the relevant NTuplePattern from the given
     * point, and retrieve its SummaryStatistics from the patternStats map. If no SummaryStatistics exists, one will
     * be created and added to the map along the NTuplePattern. Next, the value of the point is added to the Stats, and
     * the number of the samples gets increased.
     * @param point The point to add (we are interested in the tuple's projection with respect to the point)
     * @param value Value of the point
     */
    public void addPoint(int[] point, double value) {

        // Derive the pattern (the projection) described by this tuple
        NTuplePattern pattern = new NTuplePattern(point, this);
        // Stats associated with the pattern
        SummaryStatisticsPlus stats = patternStats.get(pattern);

        // Create new stats if the pattern is not yet associated with a stats object
        if (stats == null) {
            stats = new SummaryStatisticsPlus();
            patternStats.put(pattern, stats);
        }

        // Update the stats with the value of the point, and increment the number of samples.
        stats.addValue(value);
        numSamples++;
    }

    /**
     * Retrieve stats associated with a pattern, matching this tuple, from the given point.
     * @param point The point to gets stats for
     * @return A SummaryStatisticsPlus instance representing the stats of the pattern in the point.
     */
    public SummaryStatisticsPlus getStats(int[] point) {
        NTuplePattern pattern = new NTuplePattern(point, this);
        return patternStats.get(pattern);
    }

    /**
     * Resets numSamples and clears patternStats.
     */
    public void reset() {
        numSamples = 0;
        patternStats.clear();
    }

    /**
     * Generates a statistics report for this NTuple consisting of the different statistics of the observed patterns
     * @return A report String containing useful data of the observed patterns
     */
    public String generateReport() {

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(length()).append("-Tuple")
                .append(Arrays.toString(nTuple)).append("\tSamples: ").append(numSamples)
                .append("\tObserved Patterns: ").append(numEntries())
                .append(" / ").append(combinationsCount)
                .append(" (").append(String.format("%.2f",getPercentObserved())).append("%)\n")
                .append("\tPattern / Samples / Mean / STD / Min / Max\n");

        Set<NTuplePattern> orderedKeys = new TreeSet<>(patternStats.keySet());
        for (NTuplePattern key : orderedKeys) {
            SummaryStatisticsPlus stats = patternStats.get(key);
            stringBuilder.append("\t").append(key).append("\t ")
                    .append(String.format("%-4d", stats.getN())).append("\t ")
                    .append(stats.getMean()).append("\t ")
                    .append(stats.getStandardDeviation()).append("\t ")
                    .append(stats.getMin()).append("\t ")
                    .append(stats.getMax()).append("\n");
        }
        stringBuilder.append("\n");

        return stringBuilder.toString();
    }

    /**
     * Implementation of the Comparable interface. Compares two nTuples where the longer tuple comes first, and if
     * the lengths are equal, comparison follows the indices where higher indices come first.
     *
     * @param nTuple The nTuple to compare with
     * @return Results of the comparison
     */
    @Override
    public int compareTo(NTuple nTuple) {

        if (length() > nTuple.length()) return 1;
        if (length() < nTuple.length()) return -1;

        for (int i = 0; i < length(); i++) {
            if (indexAt(i) > nTuple.indexAt(i)) return 1;
            if (indexAt(i) < nTuple.indexAt(i)) return -1;
        }
        return 0;
    }

    /**
     * Returns numSamples
     * @return The number of samples
     */
    public int numSamples() {
        return numSamples;
    }

    /**
     * Returns the number of entries, which is the size of the patternStats map.
     * @return The number of patterns observed for this nTuple
     */
    public int numEntries() {
        return patternStats.size();
    }

    /**
     * Computes the percentage of patterns observed with respect to the total combinations count
     * @return The percentage of observed combinations (the coverage rate)
     */
    public double getPercentObserved() {
        return ((double) numEntries() / combinationsCount) * 100;
    }

    /**
     * The length of an NTuple is the number of dimensions it indexes.
     * @return The number of dimensions indexed by this tuple
     */
    public int length() {
        return nTuple.length;
    }

    /**
     * Retrieve the index of the dimension at the given position.
     * @param position The position in the tuple
     * @return The index of the dimension at the given position
     */
    public int indexAt(int position) {
        return nTuple[position];
    }
}
