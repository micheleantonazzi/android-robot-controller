package org.ros.android.android_robot_controller.listeners;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.widget.TextView;

import org.ros.android.android_robot_controller.nodes.NodeControl;

public class EventListenerAccelerometerMagnetometer implements SensorEventListener {

    private TextView textViewOrientation;
    private NodeControl nodeControl;

    public EventListenerAccelerometerMagnetometer(TextView textView, NodeControl nodeControl){
        this.nodeControl = nodeControl;
        this.textViewOrientation = textView;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        float[] rotationMatrix = new float[16], remappedRotationMatrix = new float[16];
        SensorManager.getRotationMatrixFromVector(
                rotationMatrix, event.values);

        SensorManager.remapCoordinateSystem(rotationMatrix,
                SensorManager.AXIS_X,
                SensorManager.AXIS_Z,
                remappedRotationMatrix);

        // Convert to orientations
        float[] orientations = new float[3];
        SensorManager.getOrientation(remappedRotationMatrix, orientations);
        Log.d("debugg", "fitoo");
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
