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
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import org.ros.android.android_robot_controller.NodesExecutor;
import org.ros.android.android_robot_controller.R;
import org.ros.android.android_robot_controller.listeners.EventListenerAccelerometerMagnetometer;
import org.ros.android.android_robot_controller.listeners.TouchListenerButtonEnableGyroscope;
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
        NodesExecutor.getInstance().executeNode(this.nodeControl);

        this.nodeReadImage = new NodeReadImage(view.findViewById(R.id.ImageViewCamera));
        NodesExecutor.getInstance().executeNode(this.nodeReadImage);

        TextView textViewSwitch = view.findViewById(R.id.TextViewSwitchGyroscope);

        // Enable gyroscope components
        LinearLayout linearLayoutEnableGyroscope = view.findViewById(R.id.LinearLayoutEnableGyroscope);
        ImageButton buttonEnableGyroscope = view.findViewById(R.id.ButtonEnableGyroscope);
        TextView textViewEnableGyroscope = view.findViewById(R.id.TextViewEnableGyroscope);
        buttonEnableGyroscope.setEnabled(false);
        linearLayoutEnableGyroscope.setVisibility(View.INVISIBLE);

        // Set behaviour of switch that enables gyroscope
        Switch switchGyroscope = view.findViewById(R.id.SwitchGyroscope);
        switchGyroscope.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    textViewSwitch.setText(R.string.fragment_control_switch_gyroscope_on);
                    joystickHorizontal.setEnabled(false);
                    joystickHorizontal.setVisibility(View.INVISIBLE);
                    buttonEnableGyroscope.setEnabled(true);
                    linearLayoutEnableGyroscope.setVisibility(View.VISIBLE);
                }
                else{
                    textViewSwitch.setText(R.string.fragment_control_switch_gyroscope_off);
                    buttonEnableGyroscope.setEnabled(false);
                    joystickHorizontal.setVisibility(View.VISIBLE);
                    joystickHorizontal.setEnabled(true);
                    linearLayoutEnableGyroscope.setVisibility(View.INVISIBLE);

                }
            }
        });

        TouchListenerButtonEnableGyroscope listenerButtonEnableGyroscope = new TouchListenerButtonEnableGyroscope();
        buttonEnableGyroscope.setOnTouchListener(listenerButtonEnableGyroscope);

        // Set up the orientation sensor
        SensorManager sensorManager = (SensorManager) view.getContext().getSystemService(SENSOR_SERVICE);

        SensorEventListener sensorEventListener = new EventListenerAccelerometerMagnetometer();

        Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (accelerometer != null) {
            sensorManager.registerListener(sensorEventListener, accelerometer,
                    SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI);
        }

        Sensor magneticField = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        if (magneticField != null) {
            sensorManager.registerListener(sensorEventListener, magneticField,
                    SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI);
        }

        return view;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        NodesExecutor.getInstance().shutDownNodes(Arrays.asList(this.nodeControl, this.nodeReadImage));
    }
}
