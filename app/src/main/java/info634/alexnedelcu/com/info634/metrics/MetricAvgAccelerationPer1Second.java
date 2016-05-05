package info634.alexnedelcu.com.info634.metrics;

import android.content.Context;
import android.hardware.SensorEvent;
import android.util.Log;

import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Alex on 4/26/2016.
 */
public class MetricAvgAccelerationPer1Second extends Metric {
    float[] valuesX = new float[1000];
    float[] valuesY = new float[1000];
    float[] valuesZ = new float[1000];
    int n=0;

    public MetricAvgAccelerationPer1Second(Context context) {
        super(context);

        new Thread(new Runnable () {
            public void run () {
                try {
                    while (true) {

                        Thread.currentThread().sleep(1000);

                        lock.lock();

                        // creating the metric by taking the average of the values
                        float avg = 0;
                        for (int i = 0; i < n; i++) {
                            avg += Math.sqrt(Math.pow(valuesX[i], 2.0) + Math.pow(valuesY[i], 2.0) + Math.pow(valuesZ[i], 2.0));
                        }
                        avg = avg / n;

                        n = 0;
                        lock.unlock();

                        metrics.add(String.valueOf(avg));

                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public MetricObj getNewMetric() {
        return null;
    }

    @Override
    public void clearData() {
        lock.lock();
        n=0;
        lock.unlock();
    }

    public void onSensorChanged(SensorEvent event) {
        lock.lock();
        valuesX[n] = event.values[0];
        valuesY[n] = event.values[1];
        valuesZ[n++] = event.values[2];
        lock.unlock();
    }

}
