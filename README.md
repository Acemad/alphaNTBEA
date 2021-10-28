# alphaNTBEA

alphaNTBEA is a refactored version of Simon Lucas' [NTBEA implementation](https://github.com/SimonLucas/ntbea). 
It is similarly written in Java, and includes some additional features and enhancements to the original implementation.

## What is NTBEA?

NTBEA stands for N-Tuple Bandits Evolutionary Algorithm, which is an algorithm for finding solutions to noisy 
combinatorial optimization problems. NTBEA was designed initially for optimizing the parameters of game-playing agents,
a problem with inherently noisy fitness functions. NTBEA works by incrementally building a fitness landscape model using
an underlying N-Tuple system that keeps track of the value estimate of a number of combinations of parameters (N-Tuples).
With each evaluation the model's accuracy improves and steers search closer to an optimal solution. NTBEA's most appealing
feature is its ability to find solutions while staying sample-efficient. For more details please consult the 
[original paper](https://arxiv.org/abs/1802.05991)


## alphaNTBEA's Features

- The ability to use arbitrary sized N-Tuples (Not limited to only 1,2,3, or N-Tuples).
- The ability to save the evolution and N-Tuple statistics externally after each run.
- Multi-threaded fitness evaluation (in case of more than a single sample).
- Centralized RNG.
- Simple fluent-interface API.

## Usage Example

In the example bellow, NTBEA is used to find a solution for the noisy max m problem. First, a SearchSpace instance is
created using a SearchSpace specification (An array of integers, with length equalling the number of dimensions, and
the elements denoting the size of each dimension). Next, an NTBEA instance is initialized and configured using the
static factory method and parameter initializers. Afterwards, we launch a single NTBEA run using the provided evaluation
function, and save the necessary report.

```java
import evo.NTBEA;
import examples.MaxMTest;
import space.SearchSpace;

public class Example {

    public static void main(String[] args) {

        // Define the search space
        SearchSpace searchSpace = new SearchSpace(5, 5, 5, 5, 5);
        
        // Initialize and configure an NTBEA instance. After passing the searchSpace to init, the next parameters 
        // denote the lengths of N-Tuples to consider, in this case: 1,2,3,4, and 5-Tuples
        NTBEA ntbea = NTBEA.init(searchSpace, 1, 2, 3, 4, 5)
                           .neighbours(100).kExplore(2)    // Specify number of neighbours, and exploration coefficient 
                           .indexMutationProb(0.5).distinctNeighbors(); // Specify index mutation prob, and avoid duplicate neighbours
        
        // Launch a single NTBEA run using maxM evaluation function for 200 generations using a single thread.
        ntbea.run(MaxMTest::maxM, 200, 1, true);
        
        // Export the evolution reports
        ntbea.saveEvolutionStatsCSVReport("./evoStats.csv");
        ntbea.saveReport("./tupleReport.txt");
    }
}
```

## Downloads

## To-Do

- General code optimization
- JUnit tests
- More examples

## Contributing


