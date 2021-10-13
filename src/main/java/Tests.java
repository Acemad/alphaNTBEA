import java.util.Arrays;

public class Tests {

    public static void main(String[] args) {

        SearchSpace searchSpace = new SearchSpace(10,10,10,10);


        System.out.println("searchSpace.getDimensions() = " + searchSpace.dimensions());
        System.out.println("searchSpace.getDimensionSize(3) = " + searchSpace.dimensionSize(3));
        System.out.println("searchSpace.size() = " + searchSpace.size());
        System.out.println("searchSpace.randomPoint() = " + Arrays.toString(searchSpace.randomPoint()));

        System.out.println("Point " + 1 + ":\t" + Arrays.toString(searchSpace.getPoint(1)));
        System.out.println("searchSpace.indexOfPoint(new int[]{1, 1, 1, 1}) = " + searchSpace.indexOfPoint(new int[]{1, 1, 1, 1}));


        int[] randomPoint = searchSpace.randomPoint();
        System.out.println("randomPoint = " + Arrays.toString(randomPoint));
        NTuplePattern nTuplePatternA = new NTuplePattern(randomPoint, new NTuple(1,2));
        NTuplePattern nTuplePatternB = new NTuplePattern(randomPoint, new NTuple(1,2));

        NTuple tuple = new NTuple(0,1,2,3);
        tuple.addSample(randomPoint, 14);
        tuple.addSample(randomPoint, 2);
        tuple.addSample(randomPoint, 2);
        tuple.addSample(searchSpace.randomPoint(), 2);

        //tuple.reset();


        System.out.println("tuple.numEntries() = " + tuple.numEntries());
        System.out.println("tuple.numSamples() = " + tuple.numSamples());

        System.out.println("tuple.getPatterns() = " + tuple.getPatternStats());
    }

}
