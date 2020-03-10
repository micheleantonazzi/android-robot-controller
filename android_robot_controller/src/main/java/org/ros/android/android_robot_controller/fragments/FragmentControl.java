package org.ros.android.android_robot_controller.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import org.ros.android.android_robot_controller.R;

public class FragmentControl extends Fragment {

    public final static String TAG = "fragment_control";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_control, container, false);

        return view;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }
}
