package org.ros.android.android_robot_controller.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import org.ros.android.android_robot_controller.NodesExecutor;
import org.ros.android.android_robot_controller.R;
import org.ros.android.android_robot_controller.nodes.NodeReadImage;


public class FragmentCamera extends Fragment {

    public final static String TAG = "fragment_camera";

    private NodeReadImage nodeReadImage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_camera, container, false);
        ImageView imageView = view.findViewById(R.id.ImageViewCamera);
        this.nodeReadImage = new NodeReadImage(imageView);
        NodesExecutor.getInstance().executeNode(this.nodeReadImage);

        // Inflate the layout for this fragment
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
    @Override
    public void onDestroy(){
        super.onDestroy();
        NodesExecutor.getInstance().shutDownNode(this.nodeReadImage);
    }
}
