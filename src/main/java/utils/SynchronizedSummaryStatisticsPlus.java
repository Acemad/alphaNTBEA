package utils;

import org.apache.commons.math3.stat.descriptive.SynchronizedSummaryStatistics;
import org.apache.commons.math3.util.FastMath;

public class SynchronizedSummaryStatisticsPlus extends SynchronizedSummaryStatistics {

    /**
     * Computes the Standard Error value
     * @return The Standard Error
     */
    public synchronized double getStandardError() {
        return getStandardDeviation() / FastMath.sqrt(getN());
    }

    @Override
    public synchronized String toString() {
        return super.toString() + "standard error: " + getStandardError() + "\n";
    }
}
