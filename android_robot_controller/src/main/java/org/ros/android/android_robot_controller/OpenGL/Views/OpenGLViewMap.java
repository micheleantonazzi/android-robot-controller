package org.ros.android.android_robot_controller.OpenGL.Views;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import org.ros.android.android_robot_controller.OpenGL.Renderes.MapRenderer;

public class OpenGLViewMap extends GLSurfaceView {

    private void init() {
        this.setEGLContextClientVersion(3);
        this.setRenderer(new MapRenderer());
    }

    public OpenGLViewMap(Context context) {
        super(context);
        init();
    }

    public OpenGLViewMap(Context context, AttributeSet attrs){
        super(context, attrs);
        init();
    }


}
