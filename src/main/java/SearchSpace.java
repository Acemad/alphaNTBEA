public class SearchSpace {

    private final int[] specification;

    public SearchSpace(int...specification) {

        for (int dimensionSize : specification)
            if (dimensionSize == 0) throw new RuntimeException("Search space specification contains a null dimension");

        this.specification = specification;
    }

    public int dimensions() {
        return specification.length;
    }

    public int dimensionSize(int dimension) {
        return specification[dimension];
    }

    public long size() {
        long size = 1;
        for (int dimensionSize : specification)
            size *= dimensionSize;
        return size;
    }

    public int[] randomPoint() {
        int[] randomPoint = new int[specification.length];
        for (int i = 0; i < randomPoint.length; i++)
            randomPoint[i] = PRNG.nextInt(specification[i]);

        return randomPoint;
    }

    // Taken from sml's ntbea implementation, extended to long values
    public int[] getPoint(long pointIndex) {

        long index = pointIndex;
        int[] point = new int[specification.length];

        for (int i = point.length - 1; i >= 0; i--) {
            point[i] = (int)(index % specification[i]);
            index /= specification[i];
        }

        return point;
    }

    // Taken from sml's ntbea implementation, extended to long values
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
