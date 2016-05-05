package info634.alexnedelcu.com.info634.metrics;

import android.content.Context;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.util.Log;

import java.lang.Math.*;
import java.util.ArrayList;

/**
 * Created by Ross on 4/30/16.
 */
public class MetricAvgRotationRatePer1Second extends Metric {
    private static final float NS2S = 1.0f / 1000000000.0f;
    private final static double EPSILON = 0.00001;
    private final float[] deltaRotationVector = new float[4];
    private float timestamp;
    float axisX = 1000;
    float axisY = 1000;
    float axisZ = 1000;

    @Override
    public MetricObj getNewMetric() {
        ArrayList<Double> metrics = new ArrayList<Double>();

        metrics.add((double) axisX);
        metrics.add((double) axisY);
        metrics.add((double) axisZ);

        MetricObj m = new MetricObj(1, metrics);

        return m;
    }

    @Override
    public void clearData() {
        axisX = 1000;
        axisY = 1000;
        axisZ = 1000;
    }

    public void onSensorChanged(SensorEvent event) {
        if (state == State.INACTIVE) {
            return;
        }

        // This timestep's delta rotation to be multiplied by the current rotation
        // after computing it from the gyro sample data.
        if (timestamp != 0) {
            final float dT = (event.timestamp - timestamp) * NS2S;
            // Axis of the rotation sample, not normalized yet.
            axisX = event.values[0];
            axisY = event.values[1];
            axisZ = event.values[2];

            // Calculate the angular speed of the sample
            double d = axisX*axisX + axisY*axisY + axisZ*axisZ;

            float omegaMagnitude = (float)Math.sqrt(d);

            // Normalize the rotation vector if it's big enough to get the axis
            if (omegaMagnitude > EPSILON) {
                axisX /= omegaMagnitude;
                axisY /= omegaMagnitude;
                axisZ /= omegaMagnitude;
            }

            // Integrate around this axis with the angular speed by the timestep
            // in order to get a delta rotation from this sample over the timestep
            // We will convert this axis-angle representation of the delta rotation
            // into a quaternion before turning it into the rotation matrix.
            float thetaOverTwo = omegaMagnitude * dT / 2.0f;
            float sinThetaOverTwo = (float)Math.sin(thetaOverTwo);
            float cosThetaOverTwo = (float)Math.cos(thetaOverTwo);
            deltaRotationVector[0] = sinThetaOverTwo * axisX;
            deltaRotationVector[1] = sinThetaOverTwo * axisY;
            deltaRotationVector[2] = sinThetaOverTwo * axisZ;
            deltaRotationVector[3] = cosThetaOverTwo;
        }
        timestamp = event.timestamp;
        float[] deltaRotationMatrix = new float[9];
        SensorManager.getRotationMatrixFromVector(deltaRotationMatrix, deltaRotationVector);
        // User code should concatenate the delta rotation we computed with the current rotation
        // in order to get the updated rotation.
        // rotationCurrent = rotationCurrent * deltaRotationMatrix;

    }



}
