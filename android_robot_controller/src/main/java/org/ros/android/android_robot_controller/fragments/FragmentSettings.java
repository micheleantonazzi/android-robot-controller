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
    private EditText editTextMapTopic;
    private EditText editTextMapMetadataTopic;
    private EditText editTextTFTopic;
    private EditText editTextGoalTopic;
    private EditText editTextCameraCompressedTopic;
    private EditText editTextControlTopic;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        this.editTextNamespace = view.findViewById(R.id.TextEditNamespace);
        this.editTextMapTopic = view.findViewById(R.id.TextEditMapTopic);
        this.editTextMapMetadataTopic = view.findViewById(R.id.TextEditMapMetadata);
        this.editTextTFTopic = view.findViewById(R.id.TextEditTFTopic);
        this.editTextGoalTopic = view.findViewById(R.id.TextEditGoalTopic);
        this.editTextCameraCompressedTopic = view.findViewById(R.id.TextEditCameraTopic);
        this.editTextControlTopic = view.findViewById(R.id.TextEditControlTopic);
        
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

        editTextMapTopic.setText(GlobalSettings.getInstance().getMapTopic());
        editTextMapTopic.setOnClickListener(v -> {
            Bundle dialogArgs = new Bundle();
            dialogArgs.putString(DialogChangePreference.PARAM_TITLE, "Change topic map");
            dialogArgs.putString(DialogChangePreference.PARAM_PREFERENCE, GlobalSettings.PREFERENCE_MAP_TOPIC);

            DialogFragment dialog = new DialogChangePreference();
            dialog.setArguments(dialogArgs);
            dialog.show(getChildFragmentManager(), "Change topic map");
        });

        editTextMapMetadataTopic.setText(GlobalSettings.getInstance().getMapMetadataTopic());
        editTextMapMetadataTopic.setOnClickListener(v -> {
            Bundle dialogArgs = new Bundle();
            dialogArgs.putString(DialogChangePreference.PARAM_TITLE, "Change topic map metadata");
            dialogArgs.putString(DialogChangePreference.PARAM_PREFERENCE, GlobalSettings.PREFERENCE_MAP_METADATA_TOPIC);

            DialogFragment dialog = new DialogChangePreference();
            dialog.setArguments(dialogArgs);
            dialog.show(getChildFragmentManager(), "Change topic map metadata");
        });

        editTextTFTopic.setText(GlobalSettings.getInstance().getTFTopic());
        editTextTFTopic.setOnClickListener(v -> {
            Bundle dialogArgs = new Bundle();
            dialogArgs.putString(DialogChangePreference.PARAM_TITLE, "Change topic TF");
            dialogArgs.putString(DialogChangePreference.PARAM_PREFERENCE, GlobalSettings.PREFERENCE_TF_TOPIC);

            DialogFragment dialog = new DialogChangePreference();
            dialog.setArguments(dialogArgs);
            dialog.show(getChildFragmentManager(), "Change topic TF");
        });

        editTextGoalTopic.setText(GlobalSettings.getInstance().getGoalTopic());
        editTextGoalTopic.setOnClickListener(v -> {
            Bundle dialogArgs = new Bundle();
            dialogArgs.putString(DialogChangePreference.PARAM_TITLE, "Change topic goal");
            dialogArgs.putString(DialogChangePreference.PARAM_PREFERENCE, GlobalSettings.PREFERENCE_GOAL_TOPIC);

            DialogFragment dialog = new DialogChangePreference();
            dialog.setArguments(dialogArgs);
            dialog.show(getChildFragmentManager(), "Change topic goal");
        });

        editTextCameraCompressedTopic.setText(GlobalSettings.getInstance().getCameraCompressedTopic());
        editTextCameraCompressedTopic.setOnClickListener(v -> {
            Bundle dialogArgs = new Bundle();
            dialogArgs.putString(DialogChangePreference.PARAM_TITLE, "Change topic camera compressed");
            dialogArgs.putString(DialogChangePreference.PARAM_PREFERENCE, GlobalSettings.PREFERENCE_CAMERA_COMPRESS_TOPIC);

            DialogFragment dialog = new DialogChangePreference();
            dialog.setArguments(dialogArgs);
            dialog.show(getChildFragmentManager(), "Change topic camera compressed");
        });

        editTextControlTopic.setText(GlobalSettings.getInstance().getControlTopic());
        editTextControlTopic.setOnClickListener(v -> {
            Bundle dialogArgs = new Bundle();
            dialogArgs.putString(DialogChangePreference.PARAM_TITLE, "Change topic control");
            dialogArgs.putString(DialogChangePreference.PARAM_PREFERENCE, GlobalSettings.PREFERENCE_CONTROL_TOPIC);

            DialogFragment dialog = new DialogChangePreference();
            dialog.setArguments(dialogArgs);
            dialog.show(getChildFragmentManager(), "Change topic control");
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
                break;
            case GlobalSettings.PREFERENCE_MAP_TOPIC:
                this.editTextMapTopic.setText(value);
                break;
            case GlobalSettings.PREFERENCE_MAP_METADATA_TOPIC:
                this.editTextMapMetadataTopic.setText(value);
                break;
            case GlobalSettings.PREFERENCE_TF_TOPIC:
                this.editTextTFTopic.setText(value);
                break;
            case GlobalSettings.PREFERENCE_GOAL_TOPIC:
                this.editTextGoalTopic.setText(value);
                break;
            case GlobalSettings.PREFERENCE_CAMERA_COMPRESS_TOPIC:
                this.editTextCameraCompressedTopic.setText(value);
                break;
            case GlobalSettings.PREFERENCE_CONTROL_TOPIC:
                this.editTextControlTopic.setText(value);
                break;
        }
    }
}
