package org.ros.android.android_robot_controller.OpenGL.Views;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import com.almeros.android.multitouch.RotateGestureDetector;

import org.ros.android.android_robot_controller.OpenGL.Renderes.RosRenderer;
import org.ros.node.AbstractNodeMain;

import java.util.List;

public class RosOpenGLView extends GLSurfaceView {

    // This variable is true as long as all fingers leave the screen after a multitouch action
    private boolean multiTouchInProgress = false;

    private RosRenderer renderer;

    private ScaleGestureDetector scaleGestureDetector;
    private RotateGestureDetector rotateGestureDetector;

    private float oldX, oldY;

    private void init(Context context) {
        this.setEGLContextClientVersion(3);
        this.renderer = new RosRenderer();
        this.setRenderer(this.renderer);

        this.scaleGestureDetector = new ScaleGestureDetector(context,
                new ScaleGestureDetector.SimpleOnScaleGestureListener() {
                    @Override
                    public boolean onScale(ScaleGestureDetector detector) {
                        renderer.modifyScaleFactor(detector.getScaleFactor());
                        return true;
                    }
                });

        this.rotateGestureDetector = new RotateGestureDetector(context, new RotateGestureDetector.OnRotateGestureListener() {
            @Override
            public boolean onRotate(RotateGestureDetector detector) {
                renderer.modifyRotationAngle(detector.getRotationDegreesDelta());
                return true;
            }

            @Override
            public boolean onRotateBegin(RotateGestureDetector detector) {
                return true;
            }

            @Override
            public void onRotateEnd(RotateGestureDetector detector) {

            }
        });

    }

    public RosOpenGLView(Context context) {
        super(context);
        init(context);
    }

    public RosOpenGLView(Context context, AttributeSet attrs){
        super(context, attrs);
        init(context);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        this.scaleGestureDetector.onTouchEvent(event);
        this.rotateGestureDetector.onTouchEvent(event);
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

    public List<AbstractNodeMain> getVisualizers(){
        return this.renderer.getVisualizers();
    }
}