package org.ros.android.android_robot_controller.listeners;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.widget.TextView;

import org.ros.android.android_robot_controller.nodes.NodeControl;

public class EventListenerAccelerometerMagnetometer implements SensorEventListener {

    @Override
    public void onSensorChanged(SensorEvent event) {


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
