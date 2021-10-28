package evo;

import utils.SynchronizedSummaryStatisticsPlus;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * A functional interface describing an evaluation function for evaluating points of a search space
 * @author Acemad
 */
public interface EvaluationFunction {

    /**
     * Evaluates a single point and returns its fitness value
     * @param point The point to evaluate
     * @return Fitness value
     */
    double evaluate(int[] point);

    /**
     * Evaluates a single point multiple times (taking multiple samples) across one thread or more
     *
     * @param point The point to evaluate
     * @param numSamples The number of evaluation samples
     * @param threads The number of threads to spread evaluation across
     * @return A SynchronizedSummaryStatisticsPlus object incorporating running stats of the evaluation of samples
     */
    default SynchronizedSummaryStatisticsPlus evaluate(int[] point, int numSamples, int threads) {

        // To hold evaluation results as a running mean, min, max,...etc.
        SynchronizedSummaryStatisticsPlus summaryStatistics = new SynchronizedSummaryStatisticsPlus();
        // Set the number of threads to use for evaluation.
        int threadsToUse;
        // In case threads is 0 use all the number of available cores
        if (threads == 0) threadsToUse = Runtime.getRuntime().availableProcessors();
        else threadsToUse = threads;
        // Create a Runnable task for each evaluation and add to the list of tasks
        List<Runnable> tasks = new ArrayList<>();
        for (int i = 0; i < numSamples; i++)
            tasks.add(() -> summaryStatistics.addValue(evaluate(point))); // Directly set the fitness of the Genome

        // Create a fixed thread pool and execute the tasks.
        ExecutorService executorService = Executors.newFixedThreadPool(threadsToUse);
        tasks.forEach(executorService::execute);
        // Initiate a proper shutdown, executing all previously submitted tasks and preventing submission of new tasks
        executorService.shutdown();

        // Wait for the termination of all tasks
        try {
            if (!executorService.awaitTermination(10, TimeUnit.HOURS)) {
                executorService.shutdownNow();
                return null;
            } else
                return summaryStatistics; // All tasks terminated

        } catch (Exception exception) {
            executorService.shutdownNow();
            return null;
        }
    }
}
