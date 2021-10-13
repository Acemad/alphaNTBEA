import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.apache.commons.math3.util.FastMath;

/**
 * An extension to Apache commons SummaryStatistics that also calculates the Standard Error.
 */
public class SummaryStatisticsPlus extends SummaryStatistics {

    public double getStandardError() {
        return getStandardDeviation() / FastMath.sqrt(getN());
    }

    @Override
    public String toString() {
        return super.toString() + "standard error: " + getStandardError() + "\n";
    }
}
