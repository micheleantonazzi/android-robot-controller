package org.ros.android.android_robot_controller.OpenGL.Renderes;

import android.opengl.GLES10;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import org.ros.android.android_robot_controller.MapVisualizer;
import org.ros.node.AbstractNodeMain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class RosRenderer implements GLSurfaceView.Renderer {

    private MapVisualizer mapVisualizer;

    // Variables to move the map
    private float scaleFactor = 1;
    private float rotationAngle = 0;
    private float moveX;
    private float moveY;

    private float[] resultMatrix = new float[16];
    private float[] projectionMatrix= new float[16];

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
        Matrix.setIdentityM(this.resultMatrix, 0);
        //Matrix.scaleM(this.resultMatrix, 0, 1f, 0.5f,1f);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES30.glClearColor(0, 0, 0, 1);
        this.mapVisualizer = new MapVisualizer();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        float ratio = (float) width / height;

        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method
        Matrix.frustumM(projectionMatrix, 0, -1, 1, -1f, 1f, 1f, 7);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT);

        synchronized (this) {
            mapVisualizer.draw(this.resultMatrix);
        }

    }

    public synchronized void updateViewMatrix(){
        /*
        Matrix.setIdentityM(this.viewMatrix, 0);
        //Matrix.scaleM(this.viewMatrix, 0, this.scaleFactor, this.scaleFactor, 0);
        //Matrix.translateM(this.viewMatrix, 0, this.moveX, this.moveY, 0);
        Matrix.setRotateEulerM(this.viewMatrix, 0, 0, 0, -30);
         */

        // Set the camera position (View matrix)
        float[] viewMatrix = new float[16];
        float[] scaleMatrix = new float[16];
        float[] rotationMatrix = new float[16];
        float[] intermediate = new float[16];
        float[] intermediate2 = new float[16];

        Matrix.setLookAtM(viewMatrix, 0, 0, 0, 1.0001f, 0f, 0f, 0f, 0f, 1.0f, 0f);
        Matrix.setIdentityM(scaleMatrix, 0);

        Matrix.scaleM(scaleMatrix, 0, 1, 0.5f, this.scaleFactor);
        Matrix.translateM(scaleMatrix, 0, this.moveX, this.moveY, 0f);
        Matrix.setRotateM(rotationMatrix, 0, 45, 0f, 0f, -1f);
        float angleAbsolute = Math.abs(this.rotationAngle) % 91;

        float proportion = angleAbsolute * 0.5f / 90;
        Log.d("debugg", proportion + "");
        // Rescale in order to maintain the proportion
        //Matrix.scaleM(scaleMatrix, 0, 0.75f, 1f, 0);
        Matrix.multiplyMM(intermediate2, 0, scaleMatrix, 0, rotationMatrix, 0);
        // Calculate the projection and view transformation
        Matrix.multiplyMM(intermediate, 0, projectionMatrix, 0, viewMatrix, 0);
        Matrix.multiplyMM(this.resultMatrix, 0, intermediate, 0, intermediate2, 0);
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

    public List<AbstractNodeMain> getVisualizer(){
        return Arrays.asList((AbstractNodeMain) this.mapVisualizer);
    }
}
