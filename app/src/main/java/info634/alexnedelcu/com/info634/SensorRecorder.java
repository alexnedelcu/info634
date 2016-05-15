package info634.alexnedelcu.com.info634;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.util.Log;

import java.lang.Thread;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;

import info634.alexnedelcu.com.info634.metrics.MetricAvgAccelerationChange;
import info634.alexnedelcu.com.info634.metrics.MetricAvgAcceleration;
import info634.alexnedelcu.com.info634.metrics.MetricAvgLinearAcceleration;
import info634.alexnedelcu.com.info634.metrics.MetricAvgLinearAccelerationChange;
import info634.alexnedelcu.com.info634.metrics.MetricAvgRotationRate;
import info634.alexnedelcu.com.info634.metrics.MetricBase;
import info634.alexnedelcu.com.info634.metrics.MetricDataObj;
import info634.alexnedelcu.com.info634.metrics.MetricSensorBase;
import info634.alexnedelcu.com.info634.metrics.MetricGPSDistanceAndSpeed;


/**
 * Created by Alex on 4/26/2016.
 */
public class SensorRecorder {

    private SensorManager mSensorManager;
    private Sensor mAcc, mGyro, mLinAcc;
    private Thread loop;
    private int loopingInterval = 1000;
    private boolean looping = false;
    private String csv=""; // holds the temp CSV. Can be saved if the user presses the button to save.
    private static String log="";
    static SensorRecorder.Command logUpdatingProcedure;
    private static String classificationLabel;
    private String headers;

    ArrayList<MetricBase> runningMetrics = new ArrayList<MetricBase>();

    public SensorRecorder (final Context context, String label) {
        this.classificationLabel = label;
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);

        if (mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null){
            mAcc = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            mGyro = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
            mLinAcc = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

        }

        createLoop();

        // add the running metrics to the running metrics array
        runningMetrics.add(new MetricAvgAccelerationChange());   // index 0
        runningMetrics.add(new MetricAvgAcceleration());   // index 1
        runningMetrics.add(new MetricAvgRotationRate());   //index 2
        runningMetrics.add(new MetricGPSDistanceAndSpeed(context));   //index 3
        runningMetrics.add(new MetricAvgLinearAccelerationChange());   // index 4
        runningMetrics.add(new MetricAvgLinearAcceleration());   // index 5


        headers="Interval,Time,Label,AvgAccelerationChange,AvgAcceleration,AvgRotationX,AvgRotationY,AvgRotationZ,GPSDistance,Speed,AvgLinearAccelerationChange,AvgLinearAcceleration";
    }

    /**
     * Only need to bind sensors such as accelerometer, gyroscope, etc, except the GPS
     */
    public void bindSensorsToMetrics() {
        mSensorManager.registerListener( (MetricSensorBase) runningMetrics.get(0), mAcc, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener( (MetricSensorBase) runningMetrics.get(1), mAcc, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener( (MetricSensorBase) runningMetrics.get(2), mGyro, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener( (MetricSensorBase) runningMetrics.get(4), mLinAcc, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener( (MetricSensorBase) runningMetrics.get(5), mLinAcc, SensorManager.SENSOR_DELAY_NORMAL);

        /**
         * Add more metrics here
         */
    }

    public void pause() {
        stopLoop();

        for (int i=0; i<runningMetrics.size(); i++) {
            runningMetrics.get(i).pauseRecording();
        }
    }

    public void start(int interval) {
        loopingInterval = interval;
        for (int i=0; i<runningMetrics.size(); i++) {
            runningMetrics.get(i).resumeRecording();
        }
        startLoop();
    }

    public String getCSV() {
        return headers+"\n"+csv;
    }

    public void clear() {
        csv = "";
        log = "";
        addToLog("");
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
                        log = loopingInterval+"  "+time+"  "+classificationLabel.substring(0,1).toUpperCase();

                        csvInstance = loopingInterval+","+time+","+classificationLabel.toUpperCase();


                        for (int i = 0; i < runningMetrics.size(); i++) { // iterate through all the classes that output metrics
                            MetricDataObj m = runningMetrics.get(i).getNewMetric();
                            //if (m.numValues > 0) {
                                // if there are  multiple metrics logged by one class, iterate through them
                                for (int j=0; j<m.metric.size(); j++) {
                                    csvInstance += "," + m.metric.get(j);

                                    DecimalFormat df = new DecimalFormat("#.000");
                                    log += "  " + df.format(m.metric.get(j));
                                }
                            //}
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

    public static void setLabel(String lbl) {
        classificationLabel = lbl;
    }

    public interface Command {
        public void execute(final String s);
    }



}
