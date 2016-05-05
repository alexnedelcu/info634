package info634.alexnedelcu.com.info634.metrics;

import android.content.Context;
import android.hardware.SensorEvent;
import android.util.Log;

import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Alex on 4/26/2016.
 */
public class MetricAvgAccelerationChangePer1Second extends Metric {
    float[] valuesX = new float[1000];
    float[] valuesY = new float[1000];
    float[] valuesZ = new float[1000];
    volatile int n=0;

    float x,y,z;

    @Override
    public MetricObj getNewMetric() {
        lock.lock();

        // creating the metric by taking the average of the values
        double avg = 0.0;
        for (int i = 0; i < n; i++) {
            avg += Math.abs(valuesX[i]) + Math.abs(valuesY[i]) + Math.abs(valuesZ[i]);
        }
        avg = avg / n;

        MetricObj metric = new MetricObj(n, avg);

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
