package org.ros.android.android_robot_controller.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.ros.android.android_robot_controller.OpenGL.Views.RosOpenGLView;
import org.ros.android.android_robot_controller.R;
import org.ros.node.AbstractNodeMain;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMainExecutor;

public class FragmentMap extends Fragment {

    private NodeMainExecutor nodeMainExecutor;
    private NodeConfiguration nodeConfiguration;

    private View view;
    private RosOpenGLView rosOpenGLView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.view = inflater.inflate(R.layout.fragment_map, container, false);
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
    }

    @Override
    public void onPause(){
        super.onPause();
    }

    public void setNodeExecutor(NodeMainExecutor nodeMainExecutor, NodeConfiguration nodeConfiguration){
        this.nodeMainExecutor = nodeMainExecutor;
        this.nodeConfiguration = nodeConfiguration;
        for(AbstractNodeMain node : this.rosOpenGLView.getVisualizers()){
            this.nodeMainExecutor.execute(node, this.nodeConfiguration);
        }
    }
}
