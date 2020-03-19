package org.ros.android.android_robot_controller.fragments;

import android.app.DialogFragment;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import org.ros.android.android_robot_controller.GlobalSettings;
import org.ros.android.android_robot_controller.R;
import org.ros.android.android_robot_controller.dialogs.DialogChangePreference;

public class FragmentSettings extends Fragment implements DialogChangePreference.SaveListener{

    public final static String TAG = "fragment_settings";

    private EditText editTextNamespace;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        this.editTextNamespace = view.findViewById(R.id.TextEditNamespace);
        
        // Setting preferences
        editTextNamespace.setText(GlobalSettings.getInstance().getApplicationNamespace());
        editTextNamespace.setOnClickListener(v -> {
            Bundle dialogArgs = new Bundle();
            dialogArgs.putString(DialogChangePreference.PARAM_TITLE, "Change namespace");
            dialogArgs.putString(DialogChangePreference.PARAM_PREFERENCE, GlobalSettings.PREFERENCE_NAMESPACE);

            DialogFragment dialog = new DialogChangePreference();
            dialog.setArguments(dialogArgs);
            dialog.show(getChildFragmentManager(), "Change namespace");
        });
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
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
    public void onSave(String preferenceName, String value) {
        GlobalSettings.getInstance().setPreferenceFromName(preferenceName, value);
        switch (preferenceName){
            case GlobalSettings.PREFERENCE_NAMESPACE:
                this.editTextNamespace.setText(value);
        }
    }
}
