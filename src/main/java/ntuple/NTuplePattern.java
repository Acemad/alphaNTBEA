package ntuple;

import java.util.Arrays;

/**
 * Represents a subset of elements of a SearchSpace point, determined by the dimensions indexed by an NTuple.
 * (Or, a projection of point with respect to a subset of dimensions indexed by an NTuple)
 * @author sml
 * @author Acemad
 */
public class NTuplePattern implements Comparable<NTuplePattern> {

    // Pattern: subset of a point
    private final int[] pattern;

    /**
     * Creates a new NTuplePattern from a full SearchSpace point and an NTuple instance. Using the NTuple instance,
     * the pattern is created by retrieving the values in the given point which the indices match those found in the
     * NTuple
     * @param point The point in question
     * @param nTuple The nTuple used to extract the pattern
     */
    public NTuplePattern(int[] point, NTuple nTuple) {
        pattern = new int[nTuple.length()];
        for (int i = 0; i < nTuple.length(); i++)
            pattern[i] = point[nTuple.indexAt(i)];
    }

    /**
     * An NTuplePattern is 'higher' than another instance if it is the first to encounter a value superior thant the
     * one in the other instance for the same index. It is 'lower' in the opposite situation. Otherwise, they're equal
     * @param nTuplePattern The pattern to compare with
     * @return Comparison result
     */
    @Override
    public int compareTo(NTuplePattern nTuplePattern) {

        for (int i = 0; i < pattern.length; i++) {
            if (pattern[i] > nTuplePattern.getValueAt(i)) return 1;
            if (pattern[i] < nTuplePattern.getValueAt(i)) return -1;
        }
        return 0;
    }

    /**
     * Two patterns are equal if they share the same values in the same indices.
     * @param o The object to compare with
     * @return Result of comparison
     */
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
        return Arrays.toString(pattern);
    }

    /**
     * Returns the value of the pattern element at the given index.
     * @param index Index of the value to return
     * @return Value of the pattern at index
     */
    public int getValueAt(int index) {
        return pattern[index];
    }
}
