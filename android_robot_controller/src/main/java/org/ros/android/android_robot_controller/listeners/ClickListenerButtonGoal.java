package org.ros.android.android_robot_controller.listeners;

import android.content.res.ColorStateList;
import android.view.View;
import android.widget.Button;
import androidx.core.view.ViewCompat;

import org.ros.android.android_robot_controller.R;

public class ClickListenerButtonGoal implements View.OnClickListener {

    private Button button;
    private boolean setGoal = false;

    public ClickListenerButtonGoal(Button button){
        this.button = button;
    }

    @Override
    public void onClick(View v) {
        if(!setGoal){
            this.setGoal = true;
            ViewCompat.setBackgroundTintList(
                    button,
                    ColorStateList.valueOf(v.getResources().getColor(R.color.lightGray)));
        }
        else{
            this.setGoal = false;
            ViewCompat.setBackgroundTintList(
                    button,
                    ColorStateList.valueOf(v.getResources().getColor(R.color.mediumGray)));
        }
    }

    public boolean isSettingGoal(){
        return this.setGoal;
    }

    public void disable(){
        this.button.performClick();
    }
}
