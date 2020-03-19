package org.ros.android.android_robot_controller.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import org.ros.android.android_robot_controller.GlobalSettings;
import org.ros.android.android_robot_controller.R;

public class DialogChangePreference extends DialogFragment {

    public static final String PARAM_TITLE = "PARAM_TITLE";
    public static final String PARAM_PREFERENCE = "PARAM_PREFERENCE";

    private SaveListener saveListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Set save listener
        try {
            this.saveListener = (SaveListener) getParentFragment();
        }
        catch (ClassCastException e){
            throw new ClassCastException(e.toString() + " must implement SaveListener");
        }

        // Get args
        Bundle args = getArguments();

        // Create view
        View view = getActivity().getLayoutInflater().inflate(R.layout.layout_dialog_change_preference, null);
        EditText editText = view.findViewById(R.id.EditTextChangePreference);
        editText.setText(GlobalSettings.getInstance().getPreferenceFromName(args.getString(PARAM_PREFERENCE)));
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(args.getString(PARAM_TITLE)).setView(view)
                .setPositiveButton(R.string.dialog_change_preferences_save_button, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        saveListener.onSave(args.getString(PARAM_PREFERENCE), editText.getText().toString());
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });

        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        Button positive = ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_POSITIVE);
        positive.setTextColor(getResources().getColor(R.color.primaryColor));
    }

    public interface SaveListener{
        void onSave(String preferenceName, String value);
    }
}
