package info634.alexnedelcu.com.info634.metrics;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.ReentrantLock;

import info634.alexnedelcu.com.info634.MainActivity;

/**
 * Created by Alex on 4/26/2016.
 */
public abstract class Metric extends MainActivity implements SensorEventListener {
    protected String filename;
    protected Queue<String> metrics;
    protected ReentrantLock lock = new ReentrantLock();
    protected static enum State {ACTIVE, INACTIVE};
    protected static State state;

    public Metric () {
        metrics = new LinkedList<String>();
        state = State.INACTIVE;
    }

    public abstract MetricObj getNewMetric();

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {};

    public abstract void clearData();

    public void pauseRecording() { state=State.INACTIVE; clearData(); }

    public void resumeRecording() { clearData(); state=State.ACTIVE; }


}
