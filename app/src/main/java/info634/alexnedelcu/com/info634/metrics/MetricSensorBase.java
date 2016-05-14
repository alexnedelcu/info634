package info634.alexnedelcu.com.info634.metrics;

import android.hardware.Sensor;
import android.hardware.SensorEventListener;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.ReentrantLock;

import info634.alexnedelcu.com.info634.MainActivity;

/**
 * Created by Alex on 4/26/2016.
 */
public abstract class MetricSensorBase extends MetricBase implements SensorEventListener {

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {};

}
