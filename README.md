# alphaNTBEA

alphaNTBEA is a refactored version of Simon Lucas' [NTBEA implementation](https://github.com/SimonLucas/ntbea) that 
includes some additional features and enhancements. Similarly, it is written in Java.

## What is NTBEA?

NTBEA stands for N-Tuple Bandits Evolutionary Algorithm, which is an algorithm for finding solutions to noisy 
combinatorial optimization problems. NTBEA was designed initially for optimizing the parameters of game-playing agents,
a problem with inherently noisy fitness functions. NTBEA works by incrementally building a fitness landscape model using
an underlying N-Tuple system that keeps track of the value estimates of a number of combinations of parameters (N-Tuples).
With each evaluation the model's accuracy improves and steers search closer to an optimal solution. NTBEA's most appealing
feature is its ability to find good solutions while staying sample-efficient. For more details please consult the 
[original paper](https://arxiv.org/abs/1802.05991).

## alphaNTBEA's Features

- The ability to use arbitrary sized N-Tuples (Not limited to only 1,2,3, or N-Tuples).
- The ability to save the evolution statistics and N-Tuple statistics externally after each run.
- Multi-threaded fitness evaluation (in case of more than a single sample).
- Centralized RNG.
- Simple fluent interface API.

## Downloads

Precompiled JARs with all dependencies included are available in the
[releases](https://github.com/Acemad/alphaNTBEA/releases) page.

## Usage Example

In the example bellow, NTBEA is used to find a solution for the noisy max m problem. First, a 
[SearchSpace](https://github.com/Acemad/alphaNTBEA/blob/main/src/main/java/space/SearchSpace.java) instance is
created using a [SearchSpace](https://github.com/Acemad/alphaNTBEA/blob/main/src/main/java/space/SearchSpace.java) 
specification (An array of integers, with length equalling the number of dimensions, and
the elements representing the size of each dimension). Next, an 
[NTBEA](https://github.com/Acemad/alphaNTBEA/blob/main/src/main/java/evo/NTBEA.java) instance is initialized and 
configured using the `init` static factory method and parameter initializers. Afterwards, we launch a single NTBEA run 
using the provided evaluation function, and save the necessary reports.

```java
import evo.NTBEA;
import examples.MaxMTest;
import space.SearchSpace;

public class Example {

    public static void main(String[] args) {

        // Define the search space: A max m problem with 5 dimensions and m = 5
        SearchSpace searchSpace = new SearchSpace(5, 5, 5, 5, 5);
        
        /* 
        Initialize and configure an NTBEA instance: 
            - After passing the searchSpace to init, the next parameters (a vararg) of init denote the lengths of 
              N-Tuples to consider, in this case: 1,2,3,4, and 5-Tuples
            - The next method calls specify number of neighbours, exploration coefficient, index mutation prob, and 
              enables unique neighbours. More configuration calls exist.
        */
        NTBEA ntbea = NTBEA.init(searchSpace, 1, 2, 3, 4, 5)
                           .neighbours(100).kExplore(2)    
                           .indexMutationProb(0.5).distinctNeighbors(); 
        
        // Launch a single NTBEA run using the maxM evaluation function for 200 generations using a single thread.
        ntbea.run(MaxMTest::maxM, 200, 1, true);
        
        // Export the evolution reports
        ntbea.saveEvolutionStatsCSVReport("./EvoStats.csv");
        ntbea.saveReport("./NTupleReport.txt");
    }
}
```
An example of the evolution reports obtained after the above run can be found through these links: 
- [EvoStats.csv](https://github.com/Acemad/alphaNTBEA/blob/main/MaxMReports/EvoStats.csv) 
- [NTupleReport.txt](https://github.com/Acemad/alphaNTBEA/blob/main/MaxMReports/NTupleReport.txt)

To use NTBEA for a different problem domain, simply implement the related evaluation function as a static method taking
a point from the search space (`int[]`) and returning the fitness value as a `double`. In the `run` method of NTBEA pass
the method reference of the new evaluation function as the 1st argument. Please refer to 
[MaxMTest](https://github.com/Acemad/alphaNTBEA/blob/main/src/main/java/examples/MaxMTest.java) class for the 
complete example, with the evaluation function.

## To-Do

- General code optimization.
- JUnit tests.
- More problem domains and examples.

## Contributing

All contributions are welcome!