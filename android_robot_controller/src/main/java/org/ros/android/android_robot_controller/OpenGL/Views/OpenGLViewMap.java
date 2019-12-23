package org.ros.android.android_robot_controller.OpenGL.Views;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import org.ros.android.android_robot_controller.OpenGL.Renderes.MapRenderer;

public class OpenGLViewMap extends GLSurfaceView {

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

        // Move the map
        if(event.getPointerCount() == 1){
            switch (event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    oldX = event.getX();
                    oldY = event.getY();
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
            Log.d("debugg", "move "+ " " + this.getWidth() + " " +event.getX() + " " + event.getY() );
        return true;
    }

}
