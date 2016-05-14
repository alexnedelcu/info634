package info634.alexnedelcu.com.info634;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.TextView;

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
    private Thread loop;
    private int loopingInterval = 1000;
    private boolean looping = false;
    private String csv=""; // holds the temp CSV. Can be saved if the user presses the button to save.
    private static String log="";
    static SensorRecorder.Command logUpdatingProcedure;
    private static String classificationLabel;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private double lat1 = 0.0;
    private double lon1 = 0.0;
    private double lat2 = 0.0;
    private double lon2 = 0.0;
    private double distance;
    private int gpsRefreshRate = 1000; //in milliseconds
    private double speed;

    ArrayList<Metric> runningMetrics = new ArrayList<Metric>();

    public SensorRecorder (final Context context, String label) {
        this.classificationLabel = label;
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);

        if (mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null){
            mAcc = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            mGyro = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        }

        createLoop();

        // add the running metrics to the running metrics array
        runningMetrics.add(new MetricAvgAccelerationChangePer1Second());   // index 0
        runningMetrics.add(new MetricAvgAccelerationPer1Second());   // index 1
        runningMetrics.add(new MetricAvgRotationRatePer1Second());   //index 2

        /**
         * Add more metrics here
         */

        locationManager = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

               // Log.i("gps location" , location.getLatitude() + " " + location.getLongitude());

                lat2 = location.getLatitude();
                lon2 = location.getLongitude();

                distance = distance(lat1, lon1, lat2, lon2, "M");
                speed = distance / (gpsRefreshRate /1000);

                //if (looping) {
                    //addToLog("gps location" + location.getLatitude() + " " + location.getLongitude() + "\n");
                    //addToLog("meters: " + lat1 + ","  + lon1 + "," + lat2 + "," + lon2+ "\n");
                    addToLog("meters: " + distance + " speed m/s: " + speed +"\n");
                //}
                lat1 = lat2;
                lon1 = lon2;
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                 context.startActivity(intent);

            }
        };
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            /*if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION
                },10);
            }*/
            return;
        }else{
            startRecording();
        }

    }

    public void recordMetrics() {
        mSensorManager.registerListener(runningMetrics.get(0), mAcc, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(runningMetrics.get(1), mAcc, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(runningMetrics.get(2), mGyro, SensorManager.SENSOR_DELAY_NORMAL);

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
        return csv;
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
                        log = ""+time+"  "+classificationLabel.substring(0,1).toUpperCase();

                        csvInstance = time+","+classificationLabel.toUpperCase();


                        for (int i = 0; i < runningMetrics.size(); i++) { // iterate through all the classes that output metrics
                            MetricObj m = runningMetrics.get(i).getNewMetric();
                            if (m.numValues > 0) {
                                // if there are  multiple metrics logged by one class, iterate through them
                                for (int j=0; j<m.metric.size(); j++) {
                                    csvInstance += "," + m.metric.get(j);
                                    log += ("  " + m.metric.get(j)).substring(0, 8);
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

    public static void setLabel(String lbl) {
        classificationLabel = lbl;
    }

    public interface Command {
        public void execute(final String s);
    }
    protected void callGps() {

    }
    //@Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 10:
                if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    startRecording();
                    return;
                }
        }
    }

    private void startRecording(){
        locationManager.requestLocationUpdates("gps", gpsRefreshRate, 0, locationListener);
    }


    private static double distance(double lat1, double lon1, double lat2, double lon2, String unit) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        if (unit == "K") {
            dist = dist * 1.609344;
        } else if (unit == "N") {
            dist = dist * 0.8684;
        }
        return (dist);
    }
    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }
    private static double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }

}
