package info634.alexnedelcu.com.info634.metrics;

import android.hardware.SensorEvent;

/**
 * Created by Alex on 4/26/2016.
 */
public class MetricAvgAccelerationPer1Second extends MetricSensorBase {
    float[] valuesX = new float[1000];
    float[] valuesY = new float[1000];
    float[] valuesZ = new float[1000];
    volatile int n=0;

    @Override
    public MetricDataObj getNewMetric() {

        lock.lock();

        // creating the metric by taking the average of the values
        double avg = 0;
        for (int i = 0; i < n; i++) {
            avg += Math.sqrt(Math.pow(valuesX[i], 2.0) + Math.pow(valuesY[i], 2.0) + Math.pow(valuesZ[i], 2.0));
        }
        avg = avg / n;


        MetricDataObj metric = new MetricDataObj(n, avg);

        n = 0;
        lock.unlock();

        return metric;
    }

    @Override
    public void clearData() {
        lock.lock();
        n=0;
        lock.unlock();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (state == State.ACTIVE) {
            lock.lock();
            valuesX[n] = event.values[0];
            valuesY[n] = event.values[1];
            valuesZ[n++] = event.values[2];
            lock.unlock();
        }
    }

}
