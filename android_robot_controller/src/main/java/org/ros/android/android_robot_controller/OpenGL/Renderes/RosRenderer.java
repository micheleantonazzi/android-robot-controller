package org.ros.android.android_robot_controller.OpenGL.Renderes;

import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
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
    private float moveX;
    private float moveY;

    private float[] viewMatrix = new float[16];

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
        Matrix.setIdentityM(this.viewMatrix, 0);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES30.glClearColor(0, 0, 0, 1);
        this.mapVisualizer = new MapVisualizer();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES30.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT);
        synchronized (this) {
            mapVisualizer.draw(this.viewMatrix);
        }
    }

    public synchronized void updateViewMatrix(){
        Matrix.setIdentityM(this.viewMatrix, 0);
        Matrix.scaleM(this.viewMatrix, 0, this.scaleFactor, this.scaleFactor, 0);
        Matrix.translateM(this.viewMatrix, 0, this.moveX, this.moveY, 0);
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

    public List<AbstractNodeMain> getVisualizer(){
        return Arrays.asList((AbstractNodeMain) this.mapVisualizer);
    }
}
