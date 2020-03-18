package org.ros.android.android_robot_controller.fragments;

import android.app.Fragment;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import org.ros.android.android_robot_controller.NodesExecutor;
import org.ros.android.android_robot_controller.R;
import org.ros.android.android_robot_controller.listeners.EventListenerAccelerometerMagnetometer;
import org.ros.android.android_robot_controller.listeners.TouchListenerButtonEnableRotationVector;
import org.ros.android.android_robot_controller.nodes.NodeControl;
import org.ros.android.android_robot_controller.nodes.NodeReadImage;
import java.util.Arrays;

import io.github.controlwear.virtual.joystick.android.JoystickView;

import static android.content.Context.SENSOR_SERVICE;

public class FragmentControl extends Fragment {

    public final static String TAG = "fragment_control";

    private NodeControl nodeControl;
    private NodeReadImage nodeReadImage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_control, container, false);

        this.nodeControl = new NodeControl();

        // Set the two joysticks
        JoystickView joystickVertical = (JoystickView) view.findViewById(R.id.JoystickVertical);
        joystickVertical.setOnMoveListener(new JoystickView.OnMoveListener() {
            @Override
            public void onMove(int angle, int strength) {
                if(angle == 90)
                    angle = NodeControl.DIRECTION_UP;
                else
                    angle = NodeControl.DIRECTION_DOWN;
                nodeControl.publishVertical(angle, strength / 100f * 0.5f);
            }
        }, 200);

        JoystickView joystickHorizontal = (JoystickView) view.findViewById(R.id.JoystickHorizontal);
        joystickHorizontal.setOnMoveListener(new JoystickView.OnMoveListener() {
            @Override
            public void onMove(int angle, int strength) {
                if(angle == 180)
                    angle = NodeControl.DIRECTION_LEFT;
                else
                    angle = NodeControl.DIRECTION_RIGHT;

                nodeControl.publishHorizontal(angle, strength / 100.0f);
            }
        }, 200);

        // Execute nodeControl
        NodesExecutor.getInstance().executeNode(this.nodeControl);

        // Setup the node to show the camera's view
        this.nodeReadImage = new NodeReadImage(view.findViewById(R.id.ImageViewCamera));
        NodesExecutor.getInstance().executeNode(this.nodeReadImage);

        // Get the graphic elements
        LinearLayout linearLayoutEnableRotationVector = view.findViewById(R.id.LinearLayoutEnableRotationVector);
        TextView textViewSwitch = view.findViewById(R.id.TextViewSwitchRotationVector);
        ImageButton buttonEnableRotationVector = view.findViewById(R.id.ButtonEnableRotationVector);
        TextView textViewRotationVector = view.findViewById(R.id.TextViewRotationVector);

        // Disable rotationVector components
        buttonEnableRotationVector.setEnabled(false);
        linearLayoutEnableRotationVector.setVisibility(View.INVISIBLE);

        // Set behaviour of switch that enables gyroscope
        Switch switchGyroscope = view.findViewById(R.id.SwitchRotationVector);
        switchGyroscope.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    textViewSwitch.setText(R.string.fragment_control_switch_rotation_vector_on);
                    joystickHorizontal.setEnabled(false);
                    joystickHorizontal.setVisibility(View.INVISIBLE);
                    buttonEnableRotationVector.setEnabled(true);
                    linearLayoutEnableRotationVector.setVisibility(View.VISIBLE);
                }
                else{
                    textViewSwitch.setText(R.string.fragment_control_switch_rotation_vector_off);
                    buttonEnableRotationVector.setEnabled(false);
                    joystickHorizontal.setVisibility(View.VISIBLE);
                    joystickHorizontal.setEnabled(true);
                    linearLayoutEnableRotationVector.setVisibility(View.INVISIBLE);

                }
            }
        });

        // Sensor listener accelerometer and magnetometer
        SensorManager sensorManager = (SensorManager) view.getContext().getSystemService(SENSOR_SERVICE);
        SensorEventListener listenerRotation = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                float[] rotationMatrix = new float[9];
                SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values);

                final int worldAxisForDeviceAxisX;
                final int worldAxisForDeviceAxisY;

                // Remap the axes as if the device screen was the instrument panel,
                // and adjust the rotation matrix for the device orientation.
                switch (getActivity().getWindowManager().getDefaultDisplay().getRotation()) {
                    case Surface.ROTATION_0:
                    default:
                        worldAxisForDeviceAxisX = SensorManager.AXIS_X;
                        worldAxisForDeviceAxisY = SensorManager.AXIS_Z;
                        break;
                    case Surface.ROTATION_90:
                        worldAxisForDeviceAxisX = SensorManager.AXIS_Z;
                        worldAxisForDeviceAxisY = SensorManager.AXIS_MINUS_X;
                        break;
                    case Surface.ROTATION_180:
                        worldAxisForDeviceAxisX = SensorManager.AXIS_MINUS_X;
                        worldAxisForDeviceAxisY = SensorManager.AXIS_MINUS_Z;
                        break;
                    case Surface.ROTATION_270:
                        worldAxisForDeviceAxisX = SensorManager.AXIS_MINUS_Z;
                        worldAxisForDeviceAxisY = SensorManager.AXIS_X;
                        break;
                }

                float[] adjustedRotationMatrix = new float[9];
                SensorManager.remapCoordinateSystem(rotationMatrix, worldAxisForDeviceAxisX,
                        worldAxisForDeviceAxisY, adjustedRotationMatrix);

                // Transform rotation matrix into azimuth/pitch/roll
                float[] orientation = new float[3];
                SensorManager.getOrientation(adjustedRotationMatrix, orientation);

                // Convert radians to degrees
                float roll = (float) Math.toDegrees(orientation[2]);
                if(roll < -90.0f)
                    roll = -90.0f;
                else if (roll >90.0f)
                    roll = 90.0f;

                textViewRotationVector.setText(Integer.toString((int) roll) + "°");

            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };

        Sensor sensorGameRotation = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

        // Button enable rotation listener
        View.OnTouchListener touchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ImageButton button = v.findViewById(R.id.ButtonEnableRotationVector);

                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:

                        // Set up the rotation vector sensor
                        sensorManager.registerListener(listenerRotation, sensorGameRotation, SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI);
                        button.setBackgroundResource(R.drawable.button_enable_rotation_vector_on);
                        break;
                    case MotionEvent.ACTION_UP:

                        sensorManager.unregisterListener(listenerRotation);
                        button.setBackgroundResource(R.drawable.button_enable_rotation_vector_off);
                        break;
                }

                return true;
            }
        };
        buttonEnableRotationVector.setOnTouchListener(touchListener);

        return view;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        NodesExecutor.getInstance().shutDownNodes(Arrays.asList(this.nodeControl, this.nodeReadImage));
    }
}
