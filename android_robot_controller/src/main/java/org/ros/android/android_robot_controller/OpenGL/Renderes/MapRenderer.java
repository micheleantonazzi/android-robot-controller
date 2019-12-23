package org.ros.android.android_robot_controller.OpenGL.Renderes;

import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import org.ros.android.android_robot_controller.MapSquare;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MapRenderer implements GLSurfaceView.Renderer {

    private float scaleFactor = 1;

    private float[] viewMatrix = new float[16];
    // This method compiles the OpenGL Shading Language
    // Create a vertex shader type (GLES30.GL_VERTEX_SHADER)
    // or a fragment shader type (GLES30.GL_FRAGMENT_SHADER)
    public static int loadShader(int type, String shaderCode){

        // Create a vertex shader type (GLES30.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES30.GL_FRAGMENT_SHADER)
        int shader = GLES30.glCreateShader(type);

        // add the source code to the shader and compile it
        GLES30.glShaderSource(shader, shaderCode);
        GLES30.glCompileShader(shader);
        return shader;
    }

    public MapRenderer(){
        Matrix.setIdentityM(this.viewMatrix, 0);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES30.glClearColor(1, 0, 0, 1);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        synchronized (this) {
            MapSquare.getInstance().draw(this.viewMatrix);
        }
    }

    public void modifyScaleFactor(float scaleFactor){
        this.scaleFactor *= scaleFactor;
        float[] scaleMatrix = new float[16];
        Matrix.setIdentityM(scaleMatrix, 0);
        Matrix.scaleM(scaleMatrix, 0, this.scaleFactor, this.scaleFactor, this.scaleFactor);
        synchronized (this){
            this.viewMatrix = scaleMatrix;
        }
    }

    public void scale(){

        float[] scaleMatrix = new float[16];
        Matrix.setIdentityM(scaleMatrix, 0);
        Matrix.scaleM(scaleMatrix, 0, this.scaleFactor, this.scaleFactor, this.scaleFactor);
        Log.d("debugg", "scale = " + this.scaleFactor);
        synchronized (this){
            //Matrix.multiplyMM(this.viewMatrix, 0, scaleMatrix, 0, this.viewMatrix, 0);
            for(int i = 0; i < 4; i++){
                String s = "";
                for(int y = 0; y < 4; y++)
                    s += this.viewMatrix[4*i + y] + " ";
                Log.d("debugg", s);

            }
            this.viewMatrix = scaleMatrix;
        }
    }
}
