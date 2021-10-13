import java.util.Arrays;

public class NTuplePattern implements Comparable<NTuplePattern> {

    private final int[] pattern;

    public NTuplePattern(int[] point, NTuple nTuple) {
        pattern = new int[nTuple.length()];
        for (int i = 0; i < nTuple.length(); i++)
            pattern[i] = point[nTuple.indexAt(i)];
    }

    @Override
    public int compareTo(NTuplePattern nTuplePattern) {

        for (int i = 0; i < pattern.length; i++) {
            if (pattern[i] > nTuplePattern.getValueAt(i)) return 1;
            if (pattern[i] < nTuplePattern.getValueAt(i)) return -1;
        }
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NTuplePattern that = (NTuplePattern) o;
        return Arrays.equals(pattern, that.pattern);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(pattern);
    }

    @Override
    public String toString() {
        return "NTuplePattern{" +
                "pattern=" + Arrays.toString(pattern) +
                '}';
    }

    public int getValueAt(int i) {
        return pattern[i];
    }
}
