package space;

import utils.PRNG;

/**
 * A class representing a discrete search space. The search space is defined using a search space specification, which
 * is a one dimensional int array that represents the number of dimensions of the search space with its own length.
 * The value of each component of the specification determines the size of a dimension in the search space.
 * @author Acemad
 */
public class SearchSpace {

    // The search space specification: length = number of dimensions, elements' value = size of the dimension
    private final int[] specification;

    /**
     * Create a SearchSpace using the given specification.
     * @param specification An int array representing the search space
     */
    public SearchSpace(int... specification) {

        for (int dimensionSize : specification)
            if (dimensionSize == 0) throw new RuntimeException("Search space specification contains a null dimension");

        this.specification = specification;
    }

    /**
     * Generates a random point from this SearchSpace.
     * @return A random point belonging to this search space
     */
    public int[] randomPoint() {
        int[] randomPoint = new int[specification.length];
        for (int i = 0; i < randomPoint.length; i++)
            randomPoint[i] = PRNG.nextInt(specification[i]);

        return randomPoint;
    }

    /**
     * Retrieve a point from this SearchSpace by its index. Each point has an index value from 0 to size - 1, where size
     * is the SearchSpace's size.
     * Note: Taken from sml's NTBEA implementation, extended to long values
     *
     * @param pointIndex The index of the point to retrieve
     * @return A point from the search space with the given index
     */
    public int[] getPoint(long pointIndex) {

        long index = pointIndex;
        int[] point = new int[specification.length];

        for (int i = point.length - 1; i >= 0; i--) {
            point[i] = (int) (index % specification[i]);
            index /= specification[i];
        }

        return point;
    }

    /**
     * Given a point in this SearchSpace calculate its index. Each point has an index value from 0 to size - 1, where
     * size is the SearchSpace's size.
     * Note: Taken from sml's NTBEA implementation, extended to long values
     *
     * @param point A point in this search space
     * @return Index of the point
     */
    public long indexOfPoint(int[] point) {
        long factor = 1;
        long total = 0;
        for (int i = point.length - 1; i >= 0; i--) {
            total += point[i] * factor;
            factor *= specification[i];
        }
        return total;
    }

    /**
     * Computes and returns the size of this SearchSpace, which is the number of all possible points in it
     * @return The number of points in this search space
     */
    public long size() {
        long size = 1;
        for (int dimensionSize : specification)
            size *= dimensionSize;
        // Possible overflow: return maximum long value
        if (size < 0) return Long.MAX_VALUE;
        return size;
    }

    /**
     * Returns the number of dimensions of this SearchSpace
     * @return The number of dimensions in this search space
     */
    public int dimensions() {
        return specification.length;
    }

    /**
     * Returns the size of a dimension, given its index in the specification
     * @param dimensionIndex The index of the dimension in the specification
     * @return The size of the queried dimension
     */
    public int dimensionSize(int dimensionIndex) {
        return specification[dimensionIndex];
    }
}
