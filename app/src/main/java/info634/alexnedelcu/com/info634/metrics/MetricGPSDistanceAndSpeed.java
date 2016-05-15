package info634.alexnedelcu.com.info634.metrics;

import android.content.Context;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;

import java.util.ArrayList;
import java.util.Date;


public class MetricGPSDistanceAndSpeed extends MetricBase implements LocationListener {

    private LocationManager locationManager;

    private double timeStart = new Date().getTime(), timeEnd;
    private double lat1 = 0.0;
    private double lon1 = 0.0;
    private double lat2 = 0.0;
    private double lon2 = 0.0;
    private int countMeasurements = 0;
    private double distance = 0;


    final Criteria criteria = new Criteria();
    Context context;
    MetricGPSDistanceAndSpeed instance;

    public MetricGPSDistanceAndSpeed(Context context) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }


        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setSpeedRequired(true);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_HIGH);


        locationManager = (LocationManager) context.getSystemService(context.LOCATION_SERVICE); // replaced 'context' to 'this'


        this.context=context;
        this.instance=this;


        requestSingleLocation();// this will trigger a continuous series of GPS location updates, at a higher rate than Android would normally provide
    }

    @Override
    public void onLocationChanged(Location location) {

        if (state == State.ACTIVE) {

            lat2 = location.getLatitude();
            lon2 = location.getLongitude();

            if (lon1 != 0.0 && lat1 != 0.0) {
                distance += distance(lat1, lon1, lat2, lon2, "M");
                System.out.println("Distance Updated : "+distance);
                timeEnd = location.getTime();
                countMeasurements++;
            } else {
                System.out.println("Initial coordinates updated : "+lat2 + " "+lon2);
                timeStart = location.getTime();
                countMeasurements++;
            }

            lat1 = lat2;
            lon1 = lon2;

        }

        requestSingleLocation();
    }

    private void requestSingleLocation() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.removeUpdates(instance);
        locationManager.requestSingleUpdate(criteria, instance, null);
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
    private static double deg2rad(double deg) { return (deg * Math.PI / 180.0); }
    private static double rad2deg(double rad) { return (rad * 180 / Math.PI); }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) { }

    @Override
    public void onProviderEnabled(String s) { }

    @Override
    public void onProviderDisabled(String s) {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        this.startActivity(intent); // replaced 'context' to 'this'
        // TODO: Are these lines needed?

    }

    @Override
    public MetricDataObj getNewMetric() {
        ArrayList<Double> r = new ArrayList<Double>();
        if (distance == 0.0) {
            r.add(0.0);
            r.add(0.0);
        } else {
            r.add(distance); // distance
            r.add(distance / (timeEnd - timeStart)); // speed
        }

        MetricDataObj metric = new MetricDataObj(countMeasurements, r);

        // remove  the data before the next iterval measurement
        clearData();

        return metric;
    }

    @Override
    public void clearData() {
        timeStart = 0;
        timeEnd = 0;
        distance = 0.0;
        countMeasurements=0;
        lat1=0.0;
        lon1=0.0;
    }
}
