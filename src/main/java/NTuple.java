import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

/**
 * Define an NTuple as a tuple consisting of indices, each pointing to a specific dimension in the search space.
 * An NTuple keeps track of all NTuplePatterns that were sampled, and that fall in the dimensions tracked by the NTuple
 */
public class NTuple {

    // The array of dimension indices
    private final int[] nTuple;
    // A map linking each NTuplePattern, a pattern of values in the dimensions pointed to by this NTuple, to a
    // SummaryStatisticsPlus object. The aim is to keep track of the various statistics of different patterns.
    private final Map<NTuplePattern, SummaryStatisticsPlus> patternStats;
    // The number of samples added to this NTuple.
    private int numSamples = 0;

    /**
     * Constructs an NTuple using the given array of dimension indices.
     * @param nTuple
     */
    public NTuple(int... nTuple) {
        this.nTuple = nTuple;
        patternStats = new HashMap<>();
    }

    /**
     * Adds a sample point and its value to this NTuple. First, it will derive the relevant NTuplePattern from the given
     * point, and retrieve its SummaryStatistics from the patternStats map. If no SummaryStatistics exists, one will
     * be created and added to the map along the NTuplePattern. Next, the value of the point is added to the Stats, and
     * the number of the samples gets increased.
     * @param point
     * @param value
     */
    public void addSample(int[] point, double value) {

        NTuplePattern pattern = new NTuplePattern(point, this);
        SummaryStatisticsPlus stats = patternStats.get(pattern);

        if (stats == null) {
            stats = new SummaryStatisticsPlus();
            patternStats.put(pattern, stats);
        }

        stats.addValue(value);
        numSamples++;
    }

    /**
     * Resets numSamples and clears patternStats.
     */
    public void reset() {
        numSamples = 0;
        patternStats.clear();
    }

    /**
     * Returns numSamples
     * @return
     */
    public int numSamples() {
        return numSamples;
    }

    /**
     * Returns the number of entries, which is the size of the patternStats map.
     * @return
     */
    public int numEntries() {
        return patternStats.size();
    }

    public Map<NTuplePattern, SummaryStatisticsPlus> getPatternStats() {
        return patternStats;
    }

    /**
     * The length of an NTuple is the number of dimensions it indexes.
     * @return
     */
    public int length() {
        return nTuple.length;
    }

    /**
     * Retrieve the index of the dimension at the given position.
     * @param i
     * @return
     */
    public int indexAt(int i) {
        return nTuple[i];
    }

    /**
     * Constructs a String representation for this NTuple.
     * @return
     */
    public String toString() {

        StringBuilder stringBuilder = new StringBuilder();

        TreeSet<NTuplePattern> orderedKeys = new TreeSet<>(patternStats.keySet());
        for (NTuplePattern key : orderedKeys) {
            SummaryStatisticsPlus stats = patternStats.get(key);
            stringBuilder.append(key).append("\t ")
                         .append(stats.getN()).append("\t ")
                         .append(stats.getMean()).append("\t ")
                         .append(stats.getStandardDeviation()).append("\n");
        }

        return stringBuilder.toString();
    }

}
