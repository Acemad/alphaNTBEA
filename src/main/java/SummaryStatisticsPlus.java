import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.apache.commons.math3.util.FastMath;

public class SummaryStatisticsPlus extends SummaryStatistics {

    public double getStandardError() {
        return getStandardDeviation() / FastMath.sqrt(getN());
    }

    @Override
    public String toString() {
        return super.toString() + "standard error: " + getStandardError() + "\n";
    }
}
