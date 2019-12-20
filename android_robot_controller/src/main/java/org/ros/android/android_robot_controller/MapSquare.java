package org.ros.android.android_robot_controller;

import android.opengl.GLES30;

import org.ros.android.android_robot_controller.OpenGL.Renderes.MapRenderer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class MapSquare {

    private final int COORDS_PER_VERTEX = 3;
    private final int vertexCount = rectangleCoordinates.length / COORDS_PER_VERTEX;
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

    private int openGLProgram;

    private int positionHandle;
    private int colorHandle;

    private FloatBuffer vertexBuffer;
    private FloatBuffer vertexTextureBuffer;
    private ShortBuffer drawListBuffer;

    static float rectangleCoordinates[] = {
            -1.0f,  0.5f, 0.0f,   // top left
            -1.0f, -0.5f, 0.0f,   // bottom left
            1.0f, -0.5f, 0.0f,    // bottom right
            1.0f,  0.5f, 0.0f };  // top right

    static float textureCoordinates[] = {
            0.0f, 1.0f,  // top left
            0.0f, 0.0f,  // bottom left
            1.0f, 0.0f,  // bottom right
            1.0f,  1.0f };  // top right


    private short drawOrder[] = { 0, 1, 2, 0, 2, 3 }; // The order in which to draw vertices

    private final String vertexShaderCode =
            "attribute vec4 vPosition;" +
                    "void main() {" +
                    "  gl_Position = vPosition;" +
                    "}";

    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main() {" +
                    "  gl_FragColor = vColor;" +
                    "}";

    public MapSquare() {
        // Initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(
                rectangleCoordinates.length * 4); // 4 is float's length
        bb.order(ByteOrder.nativeOrder());
        this.vertexBuffer = bb.asFloatBuffer();
        this.vertexBuffer.put(rectangleCoordinates);
        this.vertexBuffer.position(0);

        // Initialize texture vertex byte buffer for shape coordinates
        ByteBuffer vb = ByteBuffer.allocateDirect(
                textureCoordinates.length * 4); // 4 is float's length
        vb.order(ByteOrder.nativeOrder());
        this.vertexTextureBuffer = vb.asFloatBuffer();
        this.vertexTextureBuffer.put(textureCoordinates);
        this.vertexTextureBuffer.position(0);

        // initialize byte buffer for the draw list
        ByteBuffer dlb = ByteBuffer.allocateDirect(
                this.drawOrder.length * 2); // 2 is the shot's length
        dlb.order(ByteOrder.nativeOrder());
        this.drawListBuffer = dlb.asShortBuffer();
        this.drawListBuffer.put(this.drawOrder);
        this.drawListBuffer.position(0);

        // Compile the shaders
        int vertexShader = MapRenderer.loadShader(GLES30.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = MapRenderer.loadShader(GLES30.GL_FRAGMENT_SHADER, fragmentShaderCode);

        // Create empty OpenGL ES Program
        this.openGLProgram = GLES30.glCreateProgram();

        // add the vertex shader to program
        GLES30.glAttachShader(this.openGLProgram, vertexShader);

        // add the fragment shader to program
        GLES30.glAttachShader(this.openGLProgram, fragmentShader);

        // creates OpenGL ES program executables
        GLES30.glLinkProgram(this.openGLProgram);
    }

    public void draw() {
        // Add program to OpenGL ES environment
        GLES30.glUseProgram(this.openGLProgram);

        // get handle to vertex shader's vPosition member
        this.positionHandle = GLES30.glGetAttribLocation(this.openGLProgram, "vPosition");

        // Enable a handle to the triangle vertices
        GLES30.glEnableVertexAttribArray(this.positionHandle);

        // Prepare the triangle coordinate data
        GLES30.glVertexAttribPointer(this.positionHandle, this.COORDS_PER_VERTEX,
                GLES30.GL_FLOAT, false,
                this.vertexStride, this.vertexBuffer);

        // get handle to fragment shader's vColor member
        colorHandle = GLES30.glGetUniformLocation(this.openGLProgram, "vColor");

        // Set color for drawing the triangle
        GLES30.glUniform4fv(colorHandle, 1, new float[]{0.63671875f, 0.76953125f, 0.22265625f, 1.0f}, 0);

        // Draw the triangle
        GLES30.glDrawElements(
                GLES30.GL_TRIANGLES, this.drawOrder.length,
                GLES30.GL_UNSIGNED_SHORT, this.drawListBuffer);

        // Disable vertex array
        GLES30.glDisableVertexAttribArray(this.positionHandle);
    }

}
