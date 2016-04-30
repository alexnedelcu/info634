package info634.alexnedelcu.com.info634.metrics;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

import java.io.File;
import java.io.FileOutputStream;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.ReentrantLock;

import info634.alexnedelcu.com.info634.MainActivity;
import info634.alexnedelcu.com.info634.SensorRecorder;

/**
 * Created by Alex on 4/26/2016.
 */
public abstract class Metric extends MainActivity implements SensorEventListener {
    protected String filename;
    protected Queue<String> metrics;
    protected ReentrantLock lock = new ReentrantLock();
    static String log = "";
    static SensorRecorder.Command logUpdatingProcedure;

    Context context;
    static String label;

    public Metric (Context context) {
        this.context = context;
        metrics = new LinkedList<String>();
        lock.lock(); // this will assure that the thread won't start recording values when hooked to a sensor
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {};

    @Override
    public abstract void onSensorChanged(SensorEvent event);

    public void saveData () {

        FileOutputStream outputStream;

        try {
            outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
            while (!metrics.isEmpty()) {
                outputStream.write((metrics.poll() + "\n").getBytes());
            }
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void clearData() {
        File f = new File(filename);
        f.delete();
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void pause() {
        lock.lock();
    }

    public void resume() {
        lock.unlock();
    }

    public static void addToLog (String s) {
        log = s + "\n" + log;
        logUpdatingProcedure.execute(log);
    }

    public static void clearLog () {
        log = "";
        logUpdatingProcedure.execute(log);
    }


    public static void setLoggingProcedure(SensorRecorder.Command c) {
        logUpdatingProcedure = c;
    }

}
