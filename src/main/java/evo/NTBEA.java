package evo;

import ntuple.NTupleSystem;
import org.apache.commons.math3.util.Pair;
import space.SearchSpace;
import utils.BestItemSelector;

import java.io.FileWriter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * The main N-Tuple Bandit Evolutionary Algorithm class, contains the full implementation of the search algorithm, and
 * should be used to initialize a search using NTBEA. This class is used by means of the init method, followed by any
 * number of parameter initialization methods, if a parameter isn't initialized explicitly it will keep its default
 * value
 *
 * @author Acemad
 */
public class NTBEA {

    // The underlying NTupleSystem (n-tuple fitness landscape model)
    private final NTupleSystem nTupleSystem;

    // The number of neighbours to generate
    private int neighbours = 100;
    // While generating neighbours, do not allow duplicates
    private boolean distinctNeighbours = false;
    // The number of evaluate samples we take for a single solution
    private int evaluationSamples = 1;
    // The exploration coefficient for UCB calculations
    private double kExplore = 2;
    // The exploration term parameter
    private double epsilon = 0.5;

    // Mutation parameters *********************
    // Swap mutation probability
    private double swapMutationProb = 0;
    // Total Random Chaos mutation probability
    private double totalRCMutationProb = 0;
    // Probability of mutating each solution index through value (default) mutation
    private double indexMutationProb = 0.4;
    // At the very least, mutate at least one index (in value (default) mutation)
    private boolean mutateAtLeastOneIndex = false;

    // Optional initial seed point, to use as the first current point
    private int[] initialPoint;

    // The final solution
    private Pair<int[], Double> solution;

    // For keeping evolution statistics
    EvolutionStatistics evolutionStatistics;

    /**
     * Constructs an NTBEA instance for the given search space, and tuple sizes. This constructor is private and is not
     * meant to be called directly by the user. It is used by the following static factory method.
     * @param searchSpace The search space to use for this NTBEA instance
     * @param tupleLengths The tuple sizes to consider
     */
    private NTBEA(SearchSpace searchSpace, int... tupleLengths) {
        this.nTupleSystem = new NTupleSystem(searchSpace, tupleLengths);
        evolutionStatistics = new EvolutionStatistics(tupleLengths);
    }

    /**
     * Static factory method. Creates and returns a new NTBEA instances using the given parameters.
     * @param searchSpace The search space to use for this NTBEA instance
     * @param tupleLengths The tuple sizes to consider
     * @return A new NTBEA instance
     */
    public static NTBEA init(SearchSpace searchSpace, int... tupleLengths) {
        return new NTBEA(searchSpace, tupleLengths);
    }

    /**
     * Launches an NTBEA run using the given evaluation function. The run will go for the number of generations given
     * and will use the given number of threads for evaluation samples.
     *
     * @param evaluationFunction The evaluation function to use for evaluating solutions.
     * @param numGenerations The number of generations to take for this run
     * @param evaluationThreads The number of threads to use for solution evaluation
     * @param printStatus If true, prints the status of the evolution on each generation
     */
    public void run(EvaluationFunction evaluationFunction, int numGenerations, int evaluationThreads,
                     boolean printStatus) {

        // 1. Set the current point: if no initial point specified, set current point to a random point
        int[] currentPoint;
        if (initialPoint == null)
            currentPoint = nTupleSystem.getRandomPoint();
        else
            currentPoint = initialPoint;

        // Set the number of neighbour to generate
        neighbours = Math.min(neighbours, (int) (getSearchSpace().size() / 4));

        // 2. Start the evolution.
        for (int gen = 1; gen <= numGenerations; gen++) {

            // 1. Evaluate the fitness of the current point
            double currentPointFitness = evaluationFunction.evaluate(currentPoint, evaluationSamples,
                    evaluationThreads).getMean();
            // 2. Add the current point, and its fitness value to the model
            nTupleSystem.addPoint(currentPoint, currentPointFitness);

            // 3. Generate neighbours: Use a selector to select the best neighbour
            BestItemSelector<int[]> selector = new BestItemSelector<>(BestItemSelector.HIGHER_IS_BETTER);
            // distinctNeighbours: Use a set of neighbour indices to keep track of all neighbours
            Set<Long> neighbourIndices = new HashSet<>();
            // Start the generation of neighbours
            while (selector.numItems() < neighbours) {
                // Spawn a neighbour through mutation
                int[] neighbour = Mutations.mutatePoint(currentPoint, getSearchSpace(),
                        swapMutationProb, totalRCMutationProb, indexMutationProb,
                        mutateAtLeastOneIndex);

                if (distinctNeighbours) {
                    // Check if the neighbour was already spawned or not
                    long neighbourIndex = getSearchSpace().indexOfPoint(neighbour);
                    if (!neighbourIndices.contains(neighbourIndex)) // Not spawned
                        neighbourIndices.add(neighbourIndex);
                    else continue; // Previously spawned. Skip
                }

                // Compute the UCB value of the new neighbour
                double ucbValue = nTupleSystem.getUCBValue(neighbour, epsilon, kExplore);
                // Add neighbour to the item selector
                selector.addItem(neighbour, ucbValue);
            }

            // Print status if the option is enabled
            if (printStatus)
                printStatus(gen, numGenerations, currentPoint, currentPointFitness, nTupleSystem.getBestOfSampled());

            // Update evolution statistics
            evolutionStatistics.update(this, currentPointFitness, selector.getBestItemScore());

            // 4. Update current point with the best neighbour found.
            currentPoint = selector.getBestItem();
        }

        // 3. Retrieve the best of the sampled points
        solution = nTupleSystem.getBestOfSampled();

        // Print final solution
        if (printStatus)
            System.out.println("Solution: " + Arrays.toString(solution.getFirst()) + " -> f = " +
                    nTupleSystem.getMeanValueEstimate(solution.getFirst()));
    }

    /**
     * Prints a one-line evolution progress report.
     *
     * @param generation The current generation
     * @param maxGenerations The number of generations for this run
     * @param currentPoint The current point
     * @param currentFitness The fitness of the current point
     * @param bestOfSampled The best of sampled points
     */
    public void printStatus(int generation, int maxGenerations, int[] currentPoint, double currentFitness,
                            Pair<int[], Double> bestOfSampled) {
        String report = "Gen: " + String.format("%-4d", generation) + " | Current: " +
                Arrays.toString(currentPoint) + " f -> " + String.format("%.2f", currentFitness) + " | BoS: " +
                Arrays.toString(bestOfSampled.getFirst()) + " UCB -> " + String.format("%.2f", bestOfSampled.getSecond());

        System.out.print(report + (generation == maxGenerations ? "\n" : "\r"));
    }

    /**
     * Saves a detailed evolution report as a text file. The report contains NTuples statistics and the series of best
     * of sampled points and their mean estimate value in each generation
     * @param filePath The path of the report file
     */
    public void saveReport(String filePath) {
        try {
            FileWriter fileWriter = new FileWriter(filePath);
            fileWriter.write(nTupleSystem.generateReport());
            fileWriter.write(evolutionStatistics.getBestOfSampledList());
            fileWriter.close();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Saves the evolution statistics as a CSV
     * @param filePath Path of the file
     */
    public void saveEvolutionStatsCSVReport(String filePath) {
        evolutionStatistics.saveAsCSV(filePath);
    }

    /* Parameter initialization through a fluent interface ********************/

    /**
     * Sets the initial seed point.
     * @param point The initial point
     * @return The current NTBEA instance for chaining
     */
    public NTBEA initialPoint(int... point) {
        this.initialPoint = point;
        return this;
    }

    /**
     * Sets the exploration coefficient.
     * @param kExplore The exploration coefficient
     * @return The current NTBEA instance for chaining
     */
    public NTBEA kExplore(double kExplore) {
        this.kExplore = kExplore;
        return this;
    }

    /**
     * Sets the epsilon exploration term parameter
     * @param epsilon The epsilon value
     * @return The current NTBEA instance for chaining
     */
    public NTBEA epsilon(double epsilon) {
        this.epsilon = epsilon;
        return this;
    }

    /**
     * Sets the number of evaluation samples to take
     * @param evaluationSamples The number of evaluation samples
     * @return The current NTBEA instance for chaining
     */
    public NTBEA evaluationSamples(int evaluationSamples) {
        this.evaluationSamples = evaluationSamples;
        return this;
    }

    /**
     * Sets the number of neighbours to spawn
     * @param numNeighbours The number of neighbours
     * @return The current NTBEA instance for chaining
     */
    public NTBEA neighbours(int numNeighbours) {
        this.neighbours = numNeighbours;
        return this;
    }

    /**
     * Sets the swap mutation probability
     * @param swapMutationProbability The swap mutation probability
     * @return The current NTBEA instance for chaining
     */
    public NTBEA swapMutationProb(double swapMutationProbability) {
        this.swapMutationProb = swapMutationProbability;
        return this;
    }

    /**
     * Sets the total random chaos mutation probability
     * @param totalRCMutationProb The total random chaos probability
     * @return The current NTBEA instance for chaining
     */
    public NTBEA totalRCMutationProb(double totalRCMutationProb) {
        this.totalRCMutationProb = totalRCMutationProb;
        return this;
    }

    /**
     * Sets the index mutation probability
     * @param indexMutationProb The index mutation probability
     * @return The current NTBEA instance for chaining
     */
    public NTBEA indexMutationProb(double indexMutationProb) {
        this.indexMutationProb = indexMutationProb;
        return this;
    }

    /**
     * Enable the option to mutate at least one index for value mutation
     * @return The current NTBEA instance for chaining
     */
    public NTBEA mutateAtLeastOneIndex() {
        this.mutateAtLeastOneIndex = true;
        return this;
    }

    /**
     * Enables distinct neighbours for neighbour generation
     * @return The current NTBEA instance for chaining
     */
    public NTBEA distinctNeighbors() {
        this.distinctNeighbours = true;
        return this;
    }

    /**
     * Returns the best of sampled points if a run was performed before the call to this method
     * @return The best of sampled points
     */
    public int[] getSolution() {
        return (solution != null) ? solution.getFirst() : null;
    }

    /**
     * Returns the value estimate of best of sampled points if a run was performed before the call to this method
     * @return The value estimate of the best of sampled points
     */
    public Double getSolutionValueEstimate() {
        return (solution != null) ? solution.getSecond() : null;
    }

    public SearchSpace getSearchSpace() {
        return nTupleSystem.getSearchSpace();
    }

    public NTupleSystem getNTupleSystem() {
        return nTupleSystem;
    }
}
