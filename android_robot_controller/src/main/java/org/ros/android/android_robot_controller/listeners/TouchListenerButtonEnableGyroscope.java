package org.ros.android.android_robot_controller.listeners;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;

import org.ros.android.android_robot_controller.R;

public class TouchListenerButtonEnableGyroscope implements View.OnTouchListener {

    private volatile boolean gyroscopeActivate = false;

    public boolean gyroscopeIsActivate(){
        return this.gyroscopeActivate;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        ImageButton button = v.findViewById(R.id.ButtonEnableGyroscope);

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                this.gyroscopeActivate = true;
                button.setBackgroundResource(R.drawable.button_enable_gyroscope_on);
                break;
            case MotionEvent.ACTION_UP:
                this.gyroscopeActivate = false;
                button.setBackgroundResource(R.drawable.button_enable_gyroscope_off);
                break;
        }

        return true;
    }
}
