package org.ros.android.android_robot_controller.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.ros.android.android_robot_controller.NodesExecutor;
import org.ros.android.android_robot_controller.R;
import org.ros.android.android_robot_controller.nodes.NodeControl;
import org.ros.android.android_robot_controller.nodes.NodeReadImage;
import org.ros.android.view.VirtualJoystickView;

import java.util.Arrays;

import io.github.controlwear.virtual.joystick.android.JoystickView;

public class FragmentControl extends Fragment {

    public final static String TAG = "fragment_control";

    private NodeControl nodeControl;
    private NodeReadImage nodeReadImage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_control, container, false);

        this.nodeControl = new NodeControl();
        JoystickView joystick = (JoystickView) view.findViewById(R.id.JoystickHorizontal);
        joystick.setOnMoveListener(new JoystickView.OnMoveListener() {
            @Override
            public void onMove(int angle, int strength) {
                nodeControl.publish(angle, strength);
                angle = (angle+270) % 360 %180;
                Log.d("debugg", angle + " " + strength);
            }
        }, 200);
        NodesExecutor.getInstance().executeNode(this.nodeControl);

        this.nodeReadImage = new NodeReadImage(view.findViewById(R.id.ImageViewCamera));
        NodesExecutor.getInstance().executeNode(this.nodeReadImage);

        return view;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        NodesExecutor.getInstance().shutDownNodes(Arrays.asList(this.nodeControl, this.nodeReadImage));
    }
}
