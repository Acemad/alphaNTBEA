package utils;

/**
 * Adapted from sml's util.Picker<T>
 * @param <T> The type of the item to select
 * @author Acemad
 */
public class BestItemSelector<T> {

    // The best item
    private T bestItem = null;
    // The score of the best item
    private double bestItemScore;
    // The number of items compared thus far
    private int numItems = 0;

    // The best item is the one with the highest score
    public static final int HIGHER_IS_BETTER = 1;
    // The best item is the one with the lowest score
    public static final int LOWER_IS_BETTER = -1;

    // The order of comparison
    private final int order;

    /**
     * Creates a best item selector using the given order type (HIGHER_IS_BETTER or LOWER_IS_BETTER)
     * @param order The ordering type
     */
    public BestItemSelector(int order) {
       this.order = order;
    }

    /**
     * Adds an item and its score to the selector. Compare with the current best item, and set the new best item if
     * applicable.
     *
     * @param item The new item to add
     * @param score The score of the new item
     */
    public void addItem(T item, double score) {

        // Compare with the actual best item
        if (bestItem == null || (score * order) > (bestItemScore * order)) {
            bestItem = item;
            bestItemScore = score;
        }

        // Increment the number of items added
        numItems++;
    }

    /**
     * Resets the selector
     */
    public void reset() {
        bestItem = null;
        numItems = 0;
    }

    /**
     * Retrieves the best item
     * @return The best item
     */
    public T getBestItem() {
        return bestItem;
    }

    /**
     * Retrieves the score of the best item, if the best item was found, or NaN otherwise
     * @return The score of the best item
     */
    public double getBestItemScore() {
        if (bestItem != null) return bestItemScore;
        else return Double.NaN;
    }

    /**
     * Returns the number of items compared
     * @return the number of items compared
     */
    public int numItems() {
        return numItems;
    }
}
