package evo;

import ntuple.NTuple;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.util.Pair;
import utils.SummaryStatisticsPlus;

import java.io.FileWriter;
import java.util.*;

/**
 * Defines a number of useful statistics to keep track of during the NTBEA evolution.
 * @author Acemad
 */
public class EvolutionStatistics {

    // Track the evolution of search space coverage rates, of each nTuple
    Map<Integer, DescriptiveStatistics> tupleCoverageRateEvolution = new HashMap<>();
    // Track the fitness of the current point
    DescriptiveStatistics currentPointFitnessEvolution = new DescriptiveStatistics();
    // Track the UCB value of the best neighbour
    DescriptiveStatistics bestNeighbourUCBEvolution = new DescriptiveStatistics();
    // Track the value of the best of sampled points
    DescriptiveStatistics bestOfSampledValueEvolution = new DescriptiveStatistics();
    // Track the best of sampled points
    List<int[]> bestOfSampledEvolution = new ArrayList<>();

    /**
     * Creates an EvolutionStatistics instance using the given tuple lengths. Tuple lengths are used to initialize
     * the tupleCoverageRateEvolution map
     *
     * @param tupleLengths An int array specifying the tuple lengths considered
     */
    public EvolutionStatistics(int[] tupleLengths) {
        for (int tupleLength : tupleLengths)
            tupleCoverageRateEvolution.put(tupleLength, new DescriptiveStatistics());
    }

    /**
     * Updates the different statistics using the given NTBEA instance, the fitness of the current point, and the
     * UCB value of the best neighbour. First updates tuple coverage rates, then adds the values of the fitness of the
     * current point, and the UCB of the best neighbour, to the relevant stats instances.
     *
     * @param ntbea The NTBEA instance to gather statistics from
     * @param currentPointFitness The fitness of the current point
     * @param bestNeighbourUCB The UCB value of the best neighbour around the current point
     */
    public void update(NTBEA ntbea, double currentPointFitness, double bestNeighbourUCB) {

        // Update tuple coverage rates
        updateTupleCoverageRate(ntbea);
        // Add new values to the stats instances
        currentPointFitnessEvolution.addValue(currentPointFitness);
        bestNeighbourUCBEvolution.addValue(bestNeighbourUCB);

        // Retrieve best of sampled and add to the relevant stats instances
        Pair<int[], Double> bestOfSampled = ntbea.getNTupleSystem().getBestOfSampled();
        bestOfSampledEvolution.add(bestOfSampled.getFirst());
        bestOfSampledValueEvolution.addValue(bestOfSampled.getSecond());
    }

    /**
     * Updates the tuple coverage rates. Coverage rate is the percent of combinations sampled out of the total
     * combinations possible in a tuple. We compute the coverage rate for each tuple length, by taking the mean of the
     * coverage rates of each nTuple of similar length.
     *
     * @param ntbea The NTBEA instance to get stats from
     */
    private void updateTupleCoverageRate(NTBEA ntbea) {

        // Record the tuple coverage rates for this generation
        Map<Integer, SummaryStatisticsPlus> currentTupleCoverageRates = new HashMap<>();
        for (Integer tupleLength : tupleCoverageRateEvolution.keySet())
            currentTupleCoverageRates.put(tupleLength, new SummaryStatisticsPlus());

        // Update this generation's coverage rates for each nTuple length by adding the percent observed to the relevant
        // nTuple length stats
        for (NTuple nTuple : ntbea.getNTupleSystem().getNTuples())
            currentTupleCoverageRates.get(nTuple.length()).addValue(nTuple.getPercentObserved());

        // Record the mean coverage rate of each nTuple length for this generation in the tupleCoverageRateEvolution map
        for (Integer tupleLength : tupleCoverageRateEvolution.keySet())
            tupleCoverageRateEvolution.get(tupleLength).addValue(currentTupleCoverageRates.get(tupleLength).getMean());
    }

    /**
     * Creates and saves a CSV report of the available stats at the given file path.
     * @param filePath Path of the CSV report
     */
    public void saveAsCSV(String filePath) {

        try (CSVPrinter csvPrinter = new CSVPrinter(new FileWriter(filePath), CSVFormat.EXCEL)) {

            // Create header
            StringBuilder header = new StringBuilder("gen,currentPointFitness,bestNeighbourUCB,bestOfSampled,");
            TreeSet<Integer> tupleLengths = new TreeSet<>(tupleCoverageRateEvolution.keySet());
            for (Integer tupleLength : tupleLengths) {
                header.append(tupleLength).append("-tupleCoverage");
                if (!Objects.equals(tupleLengths.last(), tupleLength)) header.append(",");
            }

            // Print the header
            csvPrinter.printRecord((Object[]) header.toString().split(","));

            // Assemble the data
            for (int i = 0; i < currentPointFitnessEvolution.getN(); i++) {
                StringBuilder dataRecord = new StringBuilder((i+1) +
                        "," + currentPointFitnessEvolution.getValues()[i] +
                        "," + bestNeighbourUCBEvolution.getValues()[i] +
                        "," + bestOfSampledValueEvolution.getValues()[i] + ",");

                // Coverage rates
                for (Integer tupleLength : tupleLengths) {
                    dataRecord.append(tupleCoverageRateEvolution.get(tupleLength).getValues()[i]);
                    if (!Objects.equals(tupleLengths.last(), tupleLength)) dataRecord.append(",");
                }

                // Print the data
                csvPrinter.printRecord((Object[]) dataRecord.toString().split(","));
            }

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Creates a String containing the list of best of sampled points for each generation
     * @return A String representation of the list of best of sampled points.
     */
    public String getBestOfSampledList() {
        StringBuilder builder = new StringBuilder("Best of Sampled | Mean value estimate \n");
        for (int i = 0; i < bestOfSampledEvolution.size(); i++)
            builder.append(i + 1).append(":\t").append(Arrays.toString(bestOfSampledEvolution.get(i)))
                    .append("\t->\t").append(bestOfSampledValueEvolution.getValues()[i]).append("\n");
        return builder.toString();
    }

}
