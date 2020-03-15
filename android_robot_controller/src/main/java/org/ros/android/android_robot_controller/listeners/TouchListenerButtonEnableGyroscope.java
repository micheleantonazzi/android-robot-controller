package org.ros.android.android_robot_controller.listeners;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;

import org.ros.android.android_robot_controller.R;

public class TouchListenerButtonEnableGyroscope implements View.OnTouchListener {

    private volatile boolean gyroscopeActivated = false;

    public boolean isGyroscopeActivated(){
        return this.gyroscopeActivated;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        ImageButton button = v.findViewById(R.id.ButtonEnableGyroscope);

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                this.gyroscopeActivated = true;
                button.setBackgroundResource(R.drawable.button_enable_gyroscope_on);
                break;
            case MotionEvent.ACTION_UP:
                this.gyroscopeActivated = false;
                button.setBackgroundResource(R.drawable.button_enable_gyroscope_off);
                break;
        }

        return true;
    }
}
