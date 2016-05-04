package info634.alexnedelcu.com.info634;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.ArrayList;

import info634.alexnedelcu.com.info634.metrics.Metric;
import info634.alexnedelcu.com.info634.metrics.MetricAvgAccelerationChangePer1Second;
import info634.alexnedelcu.com.info634.metrics.MetricAvgAccelerationPer1Second;
import info634.alexnedelcu.com.info634.metrics.MetricAvgRotationRatePer1Second;

/**
 * Created by Alex on 4/26/2016.
 */
public class SensorRecorder {

    private SensorManager mSensorManager;
    private Sensor mAcc;
    private Sensor mGyro;
    private Context context;

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



        // add the running metrics to the running metrics array
        runningMetrics.add(new MetricAvgAccelerationPer1Second(context));   // index 0
        runningMetrics.add(new MetricAvgAccelerationChangePer1Second(context));   // index 1
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
        for (int i=0; i<runningMetrics.size(); i++) {
            runningMetrics.get(i).pause();
        }
    }

    public void startRunning() {
        for (int i=0; i<runningMetrics.size(); i++) {
            runningMetrics.get(i).resume();
        }
    }

    public void saveRunningData() {
        for (int i=0; i<runningMetrics.size(); i++) {
            runningMetrics.get(i).saveData();
        }
    }

    public void clearRunningData() {
        for (int i=0; i<runningMetrics.size(); i++) {
            runningMetrics.get(i).clearData();
        }
    }

    public void setLoggingProcedure(Command c) {
        Metric.setLoggingProcedure(c);
    }

    public interface Command {
        public void execute(final String s);
    }
}
