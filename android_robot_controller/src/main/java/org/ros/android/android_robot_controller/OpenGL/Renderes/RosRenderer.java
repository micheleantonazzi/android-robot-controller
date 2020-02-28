package org.ros.android.android_robot_controller.OpenGL.Renderes;

import android.content.res.Configuration;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import org.ros.android.android_robot_controller.NodesExecutor;
import org.ros.android.android_robot_controller.OpenGL.Visualizers.GoalVisualizer;
import org.ros.android.android_robot_controller.OpenGL.Visualizers.MapVisualizer;
import org.ros.android.android_robot_controller.OpenGL.Visualizers.PoseVisualizer;
import org.ros.android.android_robot_controller.OpenGL.Visualizers.Visualizer;
import org.ros.node.AbstractNodeMain;
import java.util.ArrayList;
import java.util.List;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class RosRenderer implements GLSurfaceView.Renderer {

    public static final float GLOBAL_SCALE = 0.017f * 384f;

    private GoalVisualizer goalVisualizer = null;
    // If this variable is true a new goalVisualizer is added in onDrawFrame
    private volatile boolean addGoalVisualizer = false;

    private List<Visualizer> visualizers = new ArrayList<>(0);
    private List<AbstractNodeMain> nodes = new ArrayList<>(0);

    // Variables to move the map
    private float scaleFactor = 1;
    private float rotationAngle = 0;
    private float moveX = 0;
    private float moveY = 0;
    private int screenOrientation = Configuration.ORIENTATION_LANDSCAPE;

    private float[] resultMatrix = new float[16];
    private float[] projectionMatrix= new float[16];
    private float ratio = 1;

    // This method compiles the OpenGL Shading Language
    // Create a vertex shader type (GLES30.GL_VERTEX_SHADER)
    // or a fragment shader type (GLES30.GL_FRAGMENT_SHADER)
    public static int loadShader(int type, String shaderCode) {

        // Create a vertex shader type (GLES30.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES30.GL_FRAGMENT_SHADER)
        int shader = GLES30.glCreateShader(type);

        // add the source code to the shader and compile it
        GLES30.glShaderSource(shader, shaderCode);
        GLES30.glCompileShader(shader);
        return shader;
    }

    public RosRenderer() {
        Matrix.frustumM(projectionMatrix, 0, -1, 1, -1f, 1f, 1f, 7);
        this.updateViewMatrix();

    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES30.glClearColor(0, 0, 0, 1);

        MapVisualizer mapVisualizer = new MapVisualizer();
        this.nodes.add(mapVisualizer);
        this.visualizers.add(mapVisualizer);

        PoseVisualizer poseVisualizer = new PoseVisualizer();
        this.nodes.add(poseVisualizer);
        this.visualizers.add(poseVisualizer);

        NodesExecutor.getInstance().setNodes(this.nodes);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {

        if(height >= width) {
            this.ratio = (float) height / width;

            // this projection matrix is applied to object coordinates
            // in the onDrawFrame() method
            Matrix.frustumM(projectionMatrix, 0, -1, 1, -ratio, ratio, 1f, 7);
        }
        else{
            this.ratio = (float) width / height;

            // this projection matrix is applied to object coordinates
            // in the onDrawFrame() method
            Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1, 1, 1f, 7);
        }
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT);

        synchronized (this) {

            if (this.addGoalVisualizer) {
                // Goal visualiser must be unique
                if(this.goalVisualizer != null)
                    this.visualizers.remove(this.goalVisualizer);

                this.goalVisualizer = new GoalVisualizer();
                this.visualizers.add(this.goalVisualizer);
                this.addGoalVisualizer = false;
            }

            for (Visualizer visualizer: this.visualizers) {
                visualizer.draw(this.resultMatrix.clone());
            }
        }
    }

    // Scale * Rotation * Translation
    private synchronized void updateViewMatrix(){

        // Set the camera position (View matrix)
        float[] viewMatrix = new float[16];
        float[] scaleMatrix = new float[16];
        float[] rotationMatrix = new float[16];
        float[] rotationScaleMatrix = new float[16];
        float[] projectionViewMatrix = new float[16];

        // Set the view camera
        Matrix.setLookAtM(viewMatrix, 0, 0, 0, 1.0001f, 0f, 0f, 0f, 0f, 1.0f, 0f);

        // Set rotation to rotation matrix
        Matrix.setRotateM(rotationMatrix, 0, this.rotationAngle, 0f, 0f, 1.0f);

        // Set scale matrix
        Matrix.setIdentityM(scaleMatrix, 0);
        Matrix.scaleM(scaleMatrix, 0, this.scaleFactor, this.scaleFactor, 1.0f);

        // Translate scale matrix based on screen orientation
        if(this.screenOrientation == Configuration.ORIENTATION_LANDSCAPE)
            Matrix.translateM(scaleMatrix, 0, this.moveX * this.ratio, this.moveY, 0.0f);
        else if(this.screenOrientation == Configuration.ORIENTATION_PORTRAIT)
            Matrix.translateM(scaleMatrix, 0, this.moveX, this.moveY * this.ratio, 0.0f);

        // Calculate scale rotation matrix
        Matrix.multiplyMM(rotationScaleMatrix, 0, scaleMatrix, 0, rotationMatrix, 0);

        // Calculate the projection and view transformation
        Matrix.multiplyMM(projectionViewMatrix, 0, projectionMatrix, 0, viewMatrix, 0);

        // Result matrix
        Matrix.multiplyMM(this.resultMatrix, 0, projectionViewMatrix, 0, rotationScaleMatrix, 0);
    }

    public synchronized void modifyScaleFactor(float scaleFactor) {
        this.scaleFactor *= scaleFactor;
        this.updateViewMatrix();
    }

    public synchronized void modifyMoveValues(float moveX, float moveY){
        this.moveX += moveX * 2 / this.scaleFactor;
        this.moveY += moveY * 2 / this.scaleFactor;
        this.updateViewMatrix();
    }

    public synchronized void modifyRotationAngle(float delta){
        this.rotationAngle += delta;
        this.updateViewMatrix();
    }

    public synchronized void setScreenOrientation(int screenOrientation){
        this.screenOrientation = screenOrientation;
    }

    public synchronized void setGoalVisualizerDimensions(float width, float height, float oldX, float oldY, float newX, float newY){
        if(this.goalVisualizer != null){
            float[] matrix = new float[16];
            // Set scale matrix
            Matrix.setIdentityM(matrix, 0);

            float translateX = -this.moveX + (newX - (width / 2)) / (width / 2) / this.scaleFactor;
            float translateY = (-this.moveY -(newY - (height / 2)) / (height / 2) + 0.5f) / this.scaleFactor;

            if(this.screenOrientation == Configuration.ORIENTATION_LANDSCAPE)
                Matrix.translateM(matrix, 0, translateX * this.ratio, translateY, 0.0f);
            else if(this.screenOrientation == Configuration.ORIENTATION_PORTRAIT)
                Matrix.translateM(matrix, 0, translateX, translateY * this.ratio, 0.0f);

            this.goalVisualizer.setDimensions(matrix.clone());
        }

    }

    public void setViewDimensions(int width, int height){
        this.onSurfaceChanged(null, width, height);
        this.updateViewMatrix();
    }

    public void addGoalVisualizer(){
        this.addGoalVisualizer = true;
    }

    public void onDestroy(){
        NodesExecutor.getInstance().shutDownNodes(this.nodes);
    }
}
