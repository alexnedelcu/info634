package info634.alexnedelcu.com.info634.metrics;

import java.util.ArrayList;

/**
 * Created by Alex on 5/5/2016.
 */
public class MetricDataObj {
    public int numValues;
    public ArrayList<Double> metric;


    public MetricDataObj(int numValues, ArrayList<Double> metrics) {
        this.numValues=numValues;
        this.metric=metrics;
    }

    public MetricDataObj(int numValues, Double metric) {
        this.numValues=numValues;
        this.metric=new ArrayList<Double>();
        this.metric.add(metric);
    }
}
