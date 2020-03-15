package org.ros.android.android_robot_controller.fragments;

import android.app.Fragment;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
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

        EventListenerAccelerometerMagnetometer listenerAccelerometerMagnetometer = new EventListenerAccelerometerMagnetometer(textViewRotationVector, this.nodeControl);

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

        // Setup listeners about buttonEnableRotationVector

        SensorManager sensorManager = (SensorManager) view.getContext().getSystemService(SENSOR_SERVICE);
        EventListenerAccelerometerMagnetometer sensorEventListener = new EventListenerAccelerometerMagnetometer(textViewRotationVector, this.nodeControl);

        buttonEnableRotationVector.setOnTouchListener(new TouchListenerButtonEnableRotationVector(sensorManager, sensorEventListener));

        return view;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        NodesExecutor.getInstance().shutDownNodes(Arrays.asList(this.nodeControl, this.nodeReadImage));
    }
}
