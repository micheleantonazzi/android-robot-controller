package org.ros.android.android_robot_controller.OpenGL.Views;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import org.ros.android.android_robot_controller.OpenGL.Renderes.MapRenderer;

public class OpenGLViewMap extends GLSurfaceView {

    // This variable is true as long as all fingers leave the screen after a multitouch action
    private boolean multiTouchInProgress = false;

    private MapRenderer renderer;
    private ScaleGestureDetector scaleGestureDetector;

    private float oldX, oldY;

    private void init(Context context) {
        this.setEGLContextClientVersion(3);
        this.renderer = new MapRenderer();
        this.setRenderer(this.renderer);

        this.scaleGestureDetector = new ScaleGestureDetector(context,
                new ScaleGestureDetector.SimpleOnScaleGestureListener() {
                    @Override
                    public boolean onScale(ScaleGestureDetector detector) {
                        renderer.modifyScaleFactor(detector.getScaleFactor());
                        return true;
                    }
                });
    }

    public OpenGLViewMap(Context context) {
        super(context);
        init(context);
    }

    public OpenGLViewMap(Context context, AttributeSet attrs){
        super(context, attrs);
        init(context);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        this.scaleGestureDetector.onTouchEvent(event);

        // Start a multitouch gesture
        if(event.getPointerCount() > 1) {
            this.multiTouchInProgress = true;
        }

        // Multitouch gesture terminates
        if (this.multiTouchInProgress && event.getAction() == MotionEvent.ACTION_UP)
            this.multiTouchInProgress = false;

        // Move the map
        if(!this.multiTouchInProgress && event.getPointerCount() == 1){
            switch (event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    this.oldX = event.getX();
                    this.oldY = event.getY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    float deltaX = event.getX() - this.oldX;
                    float deltaY = this.oldY - event.getY();
                    this.renderer.modifyMoveValues(deltaX / this.getWidth(), deltaY / this.getHeight());
                    this.oldX = event.getX();
                    this.oldY = event.getY();
                    break;
            }
        }
        return true;
    }
}
