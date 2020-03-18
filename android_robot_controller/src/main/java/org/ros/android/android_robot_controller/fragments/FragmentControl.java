package org.ros.android.android_robot_controller.fragments;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import org.ros.android.android_robot_controller.NodesExecutor;
import org.ros.android.android_robot_controller.OpenGL.Views.RosOpenGLView;
import org.ros.android.android_robot_controller.R;
import org.ros.android.android_robot_controller.listeners.ListenerRotationVector;
import org.ros.android.android_robot_controller.listeners.RotationListener;
import org.ros.android.android_robot_controller.nodes.NodeControl;
import org.ros.android.android_robot_controller.nodes.NodeReadImage;
import java.util.Arrays;

import io.github.controlwear.virtual.joystick.android.JoystickView;

import static android.content.Context.SENSOR_SERVICE;

public class FragmentControl extends Fragment {

    public final static String TAG = "fragment_control";

    private RosOpenGLView rosOpenGLView;
    private NodeControl nodeControl;
    private NodeReadImage nodeReadImage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_control, container, false);

        // Get the graphic elements
        JoystickView joystickVertical = view.findViewById(R.id.JoystickVertical);
        JoystickView joystickHorizontal = view.findViewById(R.id.JoystickHorizontal);
        LinearLayout linearLayoutEnableRotationVector = view.findViewById(R.id.LinearLayoutEnableRotationVector);
        TextView textViewSwitch = view.findViewById(R.id.TextViewSwitchRotationVector);
        ImageButton buttonEnableRotationVector = view.findViewById(R.id.ButtonEnableRotationVector);
        TextView textViewRotationVector = view.findViewById(R.id.TextViewRotationVector);
        Switch switchGyroscope = view.findViewById(R.id.SwitchRotationVector);
        ImageView imageViewCamera = view.findViewById(R.id.ImageViewCamera);

        // Set the two joysticks listeners
        joystickVertical.setOnMoveListener((angle, strength) -> {
            if(angle == 90)
                angle = NodeControl.DIRECTION_UP;
            else
                angle = NodeControl.DIRECTION_DOWN;
            nodeControl.publishVertical(angle, strength / 100f * 0.5f);
        }, 200);
        joystickHorizontal.setOnMoveListener((angle, strength) -> {
            if(angle == 180)
                angle = NodeControl.DIRECTION_LEFT;
            else
                angle = NodeControl.DIRECTION_RIGHT;
            nodeControl.publishHorizontal(angle, strength / 100.0f);
        }, 200);

        // Execute nodeControl
        this.nodeControl = new NodeControl();
        NodesExecutor.getInstance().executeNode(this.nodeControl);

        // Setup the node to show the camera's view
        this.nodeReadImage = new NodeReadImage(imageViewCamera);
        NodesExecutor.getInstance().executeNode(this.nodeReadImage);

        // Disable rotation vector components
        buttonEnableRotationVector.setEnabled(false);
        linearLayoutEnableRotationVector.setVisibility(View.INVISIBLE);

        // Set behaviour of switch that enables rotation vector
        switchGyroscope.setOnCheckedChangeListener((buttonView, isChecked) -> {
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
        });

        // Set up the rotation vector sensor
        SensorManager sensorManager = (SensorManager) view.getContext().getSystemService(SENSOR_SERVICE);
        Sensor sensorRotationVector = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

        // Sensor listener rotation vector
        RotationListener rotationListener = roll -> {
            textViewRotationVector.setText(Integer.toString((int) roll) + "°");
            int direction = NodeControl.DIRECTION_LEFT;
            if(roll >=0)
                direction = NodeControl.DIRECTION_RIGHT;

            this.nodeControl.publishHorizontal(direction, Math.abs(roll * 2) / 90.0f);
        };
        SensorEventListener listenerRotation = new ListenerRotationVector(
                getActivity().getWindowManager().getDefaultDisplay().getRotation(), rotationListener);

        // Listener of button that enable rotation vector
        @SuppressLint("ClickableViewAccessibility")
        View.OnTouchListener touchListener = (v, event) -> {
            ImageButton button = v.findViewById(R.id.ButtonEnableRotationVector);

            switch (event.getAction()){
                case MotionEvent.ACTION_DOWN:

                    // Set up the rotation vector sensor
                    sensorManager.registerListener(listenerRotation, sensorRotationVector, SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI);
                    textViewRotationVector.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 28);
                    textViewRotationVector.setText("0°");
                    button.setBackgroundResource(R.drawable.button_enable_rotation_vector_on);
                    break;
                case MotionEvent.ACTION_UP:

                    sensorManager.unregisterListener(listenerRotation);
                    textViewRotationVector.setText(R.string.fragment_control_switch_rotation_vector_off);
                    textViewRotationVector.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
                    nodeControl.publishHorizontal(NodeControl.DIRECTION_LEFT, 0);
                    button.setBackgroundResource(R.drawable.button_enable_rotation_vector_off);
                    break;
            }

            return true;
        };
        buttonEnableRotationVector.setOnTouchListener(touchListener);


        // Set up  switch between Camera and Map
        // Collecting variables
        this.rosOpenGLView = view.findViewById(R.id.RosOpenGLViewControl);
        TextView textViewMapCamera = view.findViewById(R.id.TextViewMapCamera);
        Switch switchMapCamera = view.findViewById(R.id.SwitchMapCamera);

        switchMapCamera.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                textViewMapCamera.setText(R.string.fragment_control_switch_map);
                this.rosOpenGLView.setEnabled(true);
                this.rosOpenGLView.setVisibility(View.VISIBLE);
                imageViewCamera.setVisibility(View.INVISIBLE);
                NodesExecutor.getInstance().shutDownNode(this.nodeReadImage);
            } else {
                textViewMapCamera.setText(R.string.fragment_control_switch_camera);

                this.rosOpenGLView.setVisibility(View.INVISIBLE);
                imageViewCamera.setVisibility(View.VISIBLE);
                NodesExecutor.getInstance().executeNode(this.nodeReadImage);
                this.rosOpenGLView.setEnabled(false);

            }
        });

        return view;
    }

    public void onResume(){
        super.onResume();
        rosOpenGLView.setEnabled(false);
        rosOpenGLView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        NodesExecutor.getInstance().shutDownNodes(Arrays.asList(this.nodeControl, this.nodeReadImage));
        this.rosOpenGLView.onDestroy();
    }
}
