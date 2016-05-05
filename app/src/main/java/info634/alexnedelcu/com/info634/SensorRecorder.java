package info634.alexnedelcu.com.info634;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.util.Log;

import java.lang.Thread;
import java.util.ArrayList;
import java.util.Date;

import info634.alexnedelcu.com.info634.metrics.Metric;
import info634.alexnedelcu.com.info634.metrics.MetricAvgAccelerationChangePer1Second;
import info634.alexnedelcu.com.info634.metrics.MetricAvgAccelerationPer1Second;
import info634.alexnedelcu.com.info634.metrics.MetricAvgRotationRatePer1Second;
import info634.alexnedelcu.com.info634.metrics.MetricObj;


/**
 * Created by Alex on 4/26/2016.
 */
public class SensorRecorder {

    private SensorManager mSensorManager;
    private Sensor mAcc;
    private Sensor mGyro;
    private Context context;
    private Thread loop;
    private int loopingInterval = 1000;
    private enum UserActivity {RUNNING, WALKING, BIKING};
    UserActivity userActivity;
    private boolean looping = false;
    private String csv; // holds the temp CSV. Can be saved if the user presses the button to save.
    private static String log="";
    static SensorRecorder.Command logUpdatingProcedure;


    ArrayList<Metric> runningMetrics = new ArrayList<Metric>();

    public SensorRecorder (Context context) {
        this.context = context;
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);

        if (mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null){
            mAcc = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            mGyro = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        }
        else{
            // Sorry, there are no accelerometers on your device.
            // You can't play this game.
        }

        createLoop();

        // add the running metrics to the running metrics array
        runningMetrics.add(new MetricAvgAccelerationChangePer1Second(context));   // index 0
        runningMetrics.add(new MetricAvgAccelerationPer1Second(context));   // index 1
        runningMetrics.add(new MetricAvgRotationRatePer1Second(context));   //index 2

        /**
         * Add more metrics here
         */

    }

    public void recordWalkingMetrics() {
        mSensorManager.registerListener(runningMetrics.get(0), mAcc, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(runningMetrics.get(1), mAcc, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(runningMetrics.get(2), mGyro, SensorManager.SENSOR_DELAY_NORMAL);

        /**
         * Add more metrics here
         */
    }

    public void pauseRunning () {
        stopLoop();

        for (int i=0; i<runningMetrics.size(); i++) {
            runningMetrics.get(i).pauseRecording();
        }
    }

    public void startRunning(int interval) {
        loopingInterval = interval;
        userActivity = UserActivity.RUNNING;
        for (int i=0; i<runningMetrics.size(); i++) {
            runningMetrics.get(i).resumeRecording();
        }
        csv = "";
        startLoop();
    }

    public void saveRunningData() {
        IO.save(csv, "running.csv");
    }

    public void removeRunningData() {
        IO.remove("running.csv");
    }

    public void setLoggingProcedure(Command c) {
        logUpdatingProcedure = c;
    }

    public void startLoop() {
        looping = true;
        createLoop();
        loop.start();
    }

    public void stopLoop() {
        looping = false;
    }

    private void createLoop() {
        Log.i("SensorRecorder", "initializing loop");

        loop = new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i("SensorRecorder", "loop started");
                while (looping) {

                    try {
                        Thread.currentThread().sleep(loopingInterval);

                        String csvInstance = "";

                        long time = new Date().getTime()/1000%1000;
                        String log = "";
                        log = ""+time;

                        csvInstance += time;


                        for (int i = 0; i < runningMetrics.size(); i++) { // iterate through all the classes that output metrics
                            MetricObj m = runningMetrics.get(i).getNewMetric();
                            if (m.numValues > 0) {
                                // if there are  multiple metrics logged by one class, iterate through them
                                for (int j=0; j<m.metric.size(); j++) {
                                    csvInstance += "," + m.metric.get(j);
                                    log += ("   " + m.metric.get(j)).substring(0, 8);
                                }
                            }
                        }

                        if (looping) {
                            addToLog(log + "\n");
                            csv += csvInstance + "\n";
                        }

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public static void addToLog (String s) {
        log = s + log;

        logUpdatingProcedure.execute(log);
    }

    public static void clearLog () {
        log = "";
        logUpdatingProcedure.execute(log);
    }

    public interface Command {
        public void execute(final String s);
    }
}
