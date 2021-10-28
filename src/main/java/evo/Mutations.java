package evo;

import space.SearchSpace;
import utils.PRNG;

import java.util.Arrays;

/**
 * A collection of mutation operators used to generate neighbours for a given point.
 * @author Acemad
 */
public class Mutations {

    /**
     * Generate a new point by performing mutations on a given point. Different mutations are applied, each depending
     * on a given probability. If swap mutation, or total random mutations didn't execute, value mutation will
     * take place.
     *
     * @param point The point to mutate
     * @param searchSpace The related search space
     * @param swapMutationProbability Probability of doing a swap mutation
     * @param totalRCMutationProbability Probability of doing a total random chaos mutation (i.e. generate a random
     *                                   point)
     * @param indexMutationProbability For value mutation: The probability of mutating each position (index),
     *                                      1: mutate all indices
     *                                      0: do not mutate any
     * @param mutateAtLeastOneIndex For value mutation: If true, mutate at least one position in the given point
     * @return A new point resulting from the mutation of the given point.
     */
    public static int[] mutatePoint(int[] point, SearchSpace searchSpace,
                                    double swapMutationProbability,
                                    double totalRCMutationProbability,
                                    double indexMutationProbability,
                                    boolean mutateAtLeastOneIndex) {

        // Execute swap mutation
        if (PRNG.nextDouble() < swapMutationProbability)
            return swapMutation(point, searchSpace);

        // Execute total random chaos mutation
        if (PRNG.nextDouble() < totalRCMutationProbability)
            return totalRandomChaosMutation(searchSpace);

        // Execute default position by position value mutation
        return valueMutation(point, searchSpace, indexMutationProbability, mutateAtLeastOneIndex);
    }

    /**
     * Swap mutation: swaps two randomly selected points in the given point, and return the resulting point
     * @param point The point to mutate
     * @param searchSpace The associated search space (to avoid overflowing)
     * @return The point resulting from swap mutation
     */
    public static int[] swapMutation(int[] point, SearchSpace searchSpace) {

        // Only mutate points having length >= 2
        if (point.length < 2)
            return point;

        // copy the point
        int[] newPoint = Arrays.copyOf(point, point.length);

        // Select two random and different indices
        int[] indices = PRNG.sampleKFromN(2, newPoint.length);

        // Swap indices, making sure the values do not overflow the respective dimensions' size
        if (point[indices[1]] < searchSpace.dimensionSize(indices[0]))
            newPoint[indices[0]] = point[indices[1]];
        if (point[indices[0]] < searchSpace.dimensionSize(indices[1]))
            newPoint[indices[1]] = point[indices[0]];

        return newPoint;
    }

    /**
     * Total random chaos mutation: Returns a random point from the search space.
     * @param searchSpace The related search space
     * @return A random point belonging to the given search space
     */
    public static int[] totalRandomChaosMutation(SearchSpace searchSpace) {
        return searchSpace.randomPoint();
    }

    /**
     * Value mutations: mutates a point by iterating through each of its components and deciding whether to mutate that
     * value based on a given indexMutationProbability. This method will also mutate at least one index if the relevant
     * parameter is enabled.
     *
     * @param point The point to mutate
     * @param searchSpace The associated search space
     * @param indexMutationProbability The index mutation probability (1: mutate all, 0:mutate none)
     * @param mutateAtLeastOneIndex A single index is mutated at the very least, if true.
     * @return The resulting point from this mutations.
     */
    public static int[] valueMutation(int[] point, SearchSpace searchSpace, 
                                      double indexMutationProbability, boolean mutateAtLeastOneIndex) {

        // Only mutate points with at least one element
        if (point.length < 1)
            return point;

        // Copy the point
        int[] newPoint = Arrays.copyOf(point, point.length);

        // Determine the index to forcibly mutate if mutateAtLeastOneIndex is enabled
        int required = -1;
        if (mutateAtLeastOneIndex) required = PRNG.nextInt(newPoint.length);

        // Iterate through the indices and mutate the one marked, and the ones where probability permits
        for (int i = 0; i < newPoint.length; i++)
            if (PRNG.nextDouble() < indexMutationProbability || i == required)
                newPoint[i] = mutateIndex(newPoint[i], searchSpace.dimensionSize(i));

        return newPoint;
    }

    /**
     * Chooses a random index that is different from the current one, within the given dimension size
     *
     * @param currentIndex The current index
     * @param dimensionSize The size of the dimension in which this index belongs
     * @return A new random index different from the current index (if possible) and belonging to the same dimension
     */
    private static int mutateIndex(int currentIndex, int dimensionSize) {

        // Not enough indices, just keep the current one
        if (dimensionSize <= 1)
            return currentIndex;

        // Select a random index (minus the highest index)
        int newIndex = PRNG.nextInt(dimensionSize - 1);
        // Shift the new index one position right if it's superior or equal to the current index
        // (to offset the effect of leaving out the highest index, and not select the current index again)
        return newIndex >= currentIndex ? newIndex + 1 : newIndex;
    }

}
