package examples;

import evo.NTBEA;
import space.SearchSpace;
import utils.PRNG;

import java.util.Arrays;

public class MaxMTest {

    public static boolean trap = false;
    public static long optimalValue;

    public static void main(String[] args) {

        // Define the search space
        SearchSpace searchSpace = new SearchSpace(5,5,5,5,5);
        // Compute the optimal value
        optimalValue = maxMOptimalValue(searchSpace);

        // Initiate an NTBEA instance, and configure the parameters. This will use all possible tuples
        NTBEA ntbea = NTBEA.init(searchSpace, 1,2,3,4,5)
                           .kExplore(2).neighbours(200).mutateAtLeastOneIndex()
                           .distinctNeighbors();

        // Perform an NTBEA run for the MaxM problem, with the given number of generations
        ntbea.run(MaxMTest::maxM, 200,1, true);

        // Save evolution and nTuple reports
        ntbea.saveEvolutionStatsCSVReport("./EvoStats.csv");
        ntbea.saveReport("./NTupleReport.txt");
    }

    /**
     * The maxM evaluation function. Takes a single point and returns its value, which is the sum of the point's
     * elements. To simulate noisy fitness an amount of Gaussian noise is added to the evaluation, plus a trap is
     * added to disqualify the optimal solution.
     *
     * @param point The point to evaluate
     * @return Value of the point (sum of elements)
     */
    public static double maxM(int[] point) {

        if (trap && Arrays.stream(point).sum() == optimalValue)
            return PRNG.nextGaussian(1);

        return Arrays.stream(point).sum() + PRNG.nextGaussian(1);
    }

    /**
     * Computes the optimal value of an instance of a MaxM problem
     * @return The optimal value of this MaxM problem
     */
    private static int maxMOptimalValue(SearchSpace searchSpace) {
        int optimalValue = 0;
        for (int i = 0; i < searchSpace.dimensions(); i++)
            optimalValue += searchSpace.dimensionSize(i) - 1;
        return optimalValue;
    }





}
