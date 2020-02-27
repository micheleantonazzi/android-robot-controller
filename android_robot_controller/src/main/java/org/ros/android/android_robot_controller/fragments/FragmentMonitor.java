package org.ros.android.android_robot_controller.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.ros.android.android_robot_controller.OpenGL.Views.RosOpenGLView;
import org.ros.android.android_robot_controller.R;
import org.ros.android.android_robot_controller.listeners.ClickListenerButtonGoal;

public class FragmentMonitor extends Fragment {

    public final static String TAG = "fragment_monitor";

    private View view;
    private RosOpenGLView rosOpenGLView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.view = inflater.inflate(R.layout.fragment_monitor, container, false);
        this.rosOpenGLView = (RosOpenGLView) view.findViewById(R.id.RosOpenGLView);

        Button buttonGoal = view.findViewById(R.id.ButtonGoal);
        ClickListenerButtonGoal clickListenerButtonGoal = new ClickListenerButtonGoal(buttonGoal);
        buttonGoal.setOnClickListener(clickListenerButtonGoal);

        this.rosOpenGLView.setClickListenerButtonGoal(clickListenerButtonGoal);
        return view;
    }

    @Override
    public void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStop(){
        super.onStop();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        this.rosOpenGLView.onDestroy();
    }
}
