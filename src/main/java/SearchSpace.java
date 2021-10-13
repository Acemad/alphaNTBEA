
/**
 * A class that models a discrete search space using a search space specification, which is an int array a number of
 * elements as large as the number of dimensions of the search space, and each element representing the size (max number
 * of values) of the dimension it represents.
 */
public class SearchSpace {

    // The search space specification: length = number of dimensions, elements = size of the dimension
    private final int[] specification;

    /**
     * Create a SearchSpace using the given specification.
     *
     * @param specification
     */
    public SearchSpace(int... specification) {

        for (int dimensionSize : specification)
            if (dimensionSize == 0) throw new RuntimeException("Search space specification contains a null dimension");

        this.specification = specification;
    }

    /**
     * Returns the number of dimensions of this SearchSpace
     *
     * @return
     */
    public int dimensions() {
        return specification.length;
    }

    /**
     * Returns the size of a dimension, given its index in the specification
     *
     * @param dimension
     * @return
     */
    public int dimensionSize(int dimension) {
        return specification[dimension];
    }

    /**
     * Computes and returns the size of this SearchSpace, which is the number of all possible points in the SearchSpace
     *
     * @return
     */
    public long size() {
        long size = 1;
        for (int dimensionSize : specification)
            size *= dimensionSize;
        return size;
    }

    /**
     * Generates a random point from this SearchSpace.
     *
     * @return
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
     * @param pointIndex
     * @return
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
     * @param point
     * @return
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
}
