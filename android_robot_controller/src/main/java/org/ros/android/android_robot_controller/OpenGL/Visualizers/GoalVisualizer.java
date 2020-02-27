package org.ros.android.android_robot_controller.OpenGL.Visualizers;

import android.opengl.GLES30;

import org.ros.android.android_robot_controller.OpenGL.Renderes.RosRenderer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class GoalVisualizer {

    private int openGLProgram;

    private int vertexHandle;
    private int colorHandle;

    private FloatBuffer vertexBuffer;

    private float arrowCoordinates[] = {
            -0.5f, 0.3f, 0.0f,   // triangle - bottom left
            0.0f,  1.0f, 0.0f,   // triangle - top center
            0.0f, 0.3f, 0.0f,    // triangle - center
            0.5f,  0.3f, 0.0f,   // triangle - bottom right
            -0.2f, -1.0f, 0.0f,  // rectangle - bottom left
            -0.2f,  0.3f, 0.0f,  // rectangle - top left
            0.2f, -1.0f, 0.0f,   // rectangle - bottom right
            0.2f,  0.3f, 0.0f }; // rectangle - top right

    private final String vertexShaderCode =
            "uniform mat4 uMVPMatrix;" +
                    "attribute vec4 vPosition;" +
                    "void main() {" +
                    "  gl_Position = uMVPMatrix * vPosition;" +
                    "}";

    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main() {" +
                    "  gl_FragColor = vColor;" +
                    "}";


    public GoalVisualizer(){
        // Create the vertex buffer
        ByteBuffer bb = ByteBuffer.allocateDirect(
                this.arrowCoordinates.length * 4); // 4 is float's length
        bb.order(ByteOrder.nativeOrder());
        this.vertexBuffer = bb.asFloatBuffer();
        this.vertexBuffer.put(this.arrowCoordinates);
        this.vertexBuffer.position(0);

        // Compile the shaders
        int vertexShader = RosRenderer.loadShader(GLES30.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = RosRenderer.loadShader(GLES30.GL_FRAGMENT_SHADER, fragmentShaderCode);

        // Create empty OpenGL ES Program
        this.openGLProgram = GLES30.glCreateProgram();

        // add the vertex shader to program
        GLES30.glAttachShader(this.openGLProgram, vertexShader);

        // add the fragment shader to program
        GLES30.glAttachShader(this.openGLProgram, fragmentShader);

        // creates OpenGL ES program executables
        GLES30.glLinkProgram(this.openGLProgram);
    }

    public void draw(float[] resultMatrix) {

        // VERTEX
        this.vertexHandle = GLES30.glGetAttribLocation(this.openGLProgram, "vPosition");
        GLES30.glVertexAttribPointer(this.vertexHandle, 3,
                GLES30.GL_FLOAT, false,
                3 * 4, this.vertexBuffer);
        GLES30.glEnableVertexAttribArray(this.vertexHandle);

        // Add program to OpenGL ES environment
        GLES30.glUseProgram(this.openGLProgram);

        // get handle to shape's transformation matrix
        int vPMatrixHandle = GLES30.glGetUniformLocation(this.openGLProgram, "uMVPMatrix");

        // Pass the projection and view transformation to the shader
        GLES30.glUniformMatrix4fv(vPMatrixHandle, 1, false, resultMatrix, 0);

        // Get handle to fragment shader's vColor member
        colorHandle = GLES30.glGetUniformLocation(this.openGLProgram, "vColor");

        // Set color for drawing the triangle
        GLES30.glUniform4fv(colorHandle, 1, new float[]{1.0f, 0.2f, 0f, 1.0f}, 0);

        // Draw the triangles
        GLES30.glDrawArrays(
                GLES30.GL_TRIANGLE_STRIP, 0, 4);
        GLES30.glDrawArrays(
                GLES30.GL_TRIANGLE_STRIP, 4, 4);

        GLES30.glDisableVertexAttribArray(this.vertexHandle);
    }


}
