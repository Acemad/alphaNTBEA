import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

public class NTuple {

    private final int[] nTuple;
    private final Map<NTuplePattern, SummaryStatisticsPlus> patternStats;
    private int nSamples = 0;

    public NTuple(int... nTuple) {
        this.nTuple = nTuple;
        patternStats = new HashMap<>();
    }

    public void addSample(int[] point, double fitness) {

        NTuplePattern pattern = new NTuplePattern(point, this);
        SummaryStatisticsPlus stats = patternStats.get(pattern);

        if (stats == null) {
            stats = new SummaryStatisticsPlus();
            patternStats.put(pattern, stats);
        }

        stats.addValue(fitness);
        nSamples++;
    }

    public void reset() {
        nSamples = 0;
        patternStats.clear();
    }

    public int numSamples() {
        return nSamples;
    }

    public int numEntries() {
        return patternStats.size();
    }

    public Map<NTuplePattern, SummaryStatisticsPlus> getPatternStats() {
        return patternStats;
    }

    public int length() {
        return nTuple.length;
    }

    public int indexAt(int i) {
        return nTuple[i];
    }

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
