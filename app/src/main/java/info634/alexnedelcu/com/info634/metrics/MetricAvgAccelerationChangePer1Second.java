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
    int n=0;

    public MetricAvgAccelerationChangePer1Second(Context context) {
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
                            avg += Math.abs(valuesX[i]) + Math.abs(valuesY[i]) + Math.abs(valuesZ[i]);
                        }
                        avg = avg / n;

                        addToLog("Avg acc change/sec: " + avg);
                        Log.i("Avg acc change/sec: ", "" + avg + "  ("+n+" values averaged)");


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

    float x,y,z;
    public void onSensorChanged(SensorEvent event) {

        if (x == 0 && y == 0 && z == 0) {
            x=event.values[0];
            y=event.values[1];
            z=event.values[2];
        } else {
            lock.lock();

            valuesX[n] = event.values[0]-x;
            valuesY[n] = event.values[1]-y;
            valuesZ[n++] = event.values[2]-z;

            // update the last values of x,y,z
            x=event.values[0];
            y=event.values[1];
            z=event.values[2];

            lock.unlock();
        }
    }

}
