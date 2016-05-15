package info634.alexnedelcu.com.info634.metrics;

import android.hardware.SensorEvent;
import android.util.Log;

/**
 * Created by Alex on 4/26/2016.
 */
public class MetricAvgAccelerationChange extends MetricSensorBase {
    float[] valuesX = new float[1000];
    float[] valuesY = new float[1000];
    float[] valuesZ = new float[1000];
    volatile int n=0;

    float x,y,z;

    @Override
    public MetricDataObj getNewMetric() {
        lock.lock();

        // creating the metric by taking the average of the values
        double avg = 0.0;
        for (int i = 0; i < n; i++) {
            avg += Math.sqrt(Math.pow(Math.abs(valuesX[i]), 2.0) + Math.pow(Math.abs(valuesY[i]), 2.0) + Math.pow(Math.abs(valuesZ[i]), 2.0));
        }
        avg = avg / n;

        MetricDataObj metric = new MetricDataObj(n, avg);

        Log.i("metric", avg + " " + n);

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

            if (x == 0 && y == 0 && z == 0) {
                x = event.values[0];
                y = event.values[1];
                z = event.values[2];
            } else {
                lock.lock();

                valuesX[n] = event.values[0] - x;
                valuesY[n] = event.values[1] - y;
                valuesZ[n++] = event.values[2] - z;

                // update the last values of x,y,z
                x = event.values[0];
                y = event.values[1];
                z = event.values[2];

                lock.unlock();
            }
        }

    }

}
