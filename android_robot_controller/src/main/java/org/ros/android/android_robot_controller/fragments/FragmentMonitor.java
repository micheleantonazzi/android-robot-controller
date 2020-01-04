package org.ros.android.android_robot_controller.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.ros.android.android_robot_controller.OpenGL.Views.RosOpenGLView;
import org.ros.android.android_robot_controller.R;
import org.ros.node.AbstractNodeMain;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMainExecutor;

public class FragmentMonitor extends Fragment implements RosFragment {

    public final static String TAG = "fragment_monitor";

    private View view;
    private RosOpenGLView rosOpenGLView;

    private NodeConfiguration nodeConfiguration;
    private NodeMainExecutor nodeMainExecutor;

    boolean rosNodeExecuted = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.view = inflater.inflate(R.layout.fragment_monitor, container, false);
        this.rosOpenGLView = (RosOpenGLView) this.view.findViewById(R.id.RosOpenGLView);
        return view;
    }

    @Override
    public void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume(){
        super.onResume();
        Log.d("debugg", "resume");
    }

    @Override
    public void onPause(){
        super.onPause();
    }

    @Override
    public void setNodeConfigurationAndExecutor(NodeConfiguration nodeConfiguration, NodeMainExecutor nodeMainExecutor){
        this.nodeConfiguration = nodeConfiguration;
        this.nodeMainExecutor = nodeMainExecutor;

        this.executeNodes();
    }

    @Override
    public void executeNodes(){
        if(this.nodeMainExecutor != null && this.nodeMainExecutor != null && this.rosOpenGLView != null){
            Log.d("debugg", "execute");
            for (AbstractNodeMain node : this.rosOpenGLView.getVisualizers()){
                if(node != null){
                    this.nodeMainExecutor.execute(node, this.nodeConfiguration);
                }
            }
        }
    }
}
