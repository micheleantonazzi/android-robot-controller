package org.ros.android.android_robot_controller.fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import org.ros.android.android_robot_controller.GlobalSettings;
import org.ros.android.android_robot_controller.R;

public class FragmentSettings extends Fragment {

    public final static String TAG = "fragment_settings";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        EditText textEditNamespace = view.findViewById(R.id.TextEditNamespace);
        
        // Setting preferences
        textEditNamespace.setText(GlobalSettings.getInstance().getApplicationNamespace());

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
}
