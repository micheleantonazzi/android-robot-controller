package org.ros.android.android_robot_controller.listeners;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import org.ros.android.android_robot_controller.R;

public class TouchListenerButtonEnableRotationVector implements View.OnTouchListener {

    private SensorManager sensorManager;
    private SensorEventListener listenerAccelerometerMagnetometer;

    public TouchListenerButtonEnableRotationVector(SensorManager sensorManager, SensorEventListener listenerAccelerometerMagnetometer){
        this.listenerAccelerometerMagnetometer = listenerAccelerometerMagnetometer;
        this.sensorManager = sensorManager;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        ImageButton button = v.findViewById(R.id.ButtonEnableRotationVector);

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:

                // Set up the rotation vector sensor
                Sensor sensorRotation = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
                if (sensorRotation != null) {
                    this.sensorManager.registerListener(this.listenerAccelerometerMagnetometer, sensorRotation,
                            SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI);
                }
                button.setBackgroundResource(R.drawable.button_enable_rotation_vector_on);
                break;
            case MotionEvent.ACTION_UP:

                this.sensorManager.unregisterListener(this.listenerAccelerometerMagnetometer);
                button.setBackgroundResource(R.drawable.button_enable_rotation_vector_off);
                break;
        }

        return true;
    }
}
