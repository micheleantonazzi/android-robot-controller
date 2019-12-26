package org.ros.android.android_robot_controller.OpenGL.Visualizers;

import android.opengl.GLES30;
import android.opengl.Matrix;
import android.util.Log;

import org.ros.android.android_robot_controller.OpenGL.Renderes.RosRenderer;
import org.ros.message.MessageListener;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Subscriber;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class PoseVisualizer extends AbstractNodeMain {

    float[] scaleMatrix;
    private int openGLProgram;

    private int vertexHandle;
    private int colorHandle;

    private FloatBuffer vertexBuffer;

    private float arrowCoordinates[] = {
            -1.0f, -1.0f, 0.0f,   // bottom left
            0.0f,  1.0f, 0.0f,   // top center
            0.0f, -0.45f, 0.0f,    // center
            1.0f,  -1.0f, 0.0f };  // bottom right

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

    public PoseVisualizer(){

        this.scaleMatrix = new float[16];
        Matrix.setIdentityM(this.scaleMatrix, 0);
        Matrix.scaleM(this.scaleMatrix, 0, 0.2f, 0.2f, 1.0f);

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

    public synchronized void draw(float[] mvpMatrix) {

        //Matrix.translateM(this.scaleMatrix, 0, 1/ 0.2f, 1/0.2f, 1);

        Matrix.multiplyMM(mvpMatrix, 0, this.scaleMatrix, 0, mvpMatrix, 0);
        //Matrix.translateM(this.scaleMatrix, 0, 0.2f, 0.2f, 1);

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
        GLES30.glUniformMatrix4fv(vPMatrixHandle, 1, false, mvpMatrix, 0);

        // Get handle to fragment shader's vColor member
        colorHandle = GLES30.glGetUniformLocation(this.openGLProgram, "vColor");

        // Set color for drawing the triangle
        GLES30.glUniform4fv(colorHandle, 1, new float[]{0.1f, 0.1f, 0.8f, 1.0f}, 0);

        // Draw the triangles
        GLES30.glDrawArrays(
                GLES30.GL_TRIANGLE_STRIP, 0, 4);

        GLES30.glDisableVertexAttribArray(this.vertexHandle);

    }

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("android_robot_controller/node_pose_reader");
    }

    @Override
    public void onStart(ConnectedNode connectedNode) {
        Subscriber<geometry_msgs.PoseWithCovarianceStamped> subscriber = connectedNode.newSubscriber("amcl_pose", geometry_msgs.PoseWithCovarianceStamped._TYPE);
        subscriber.addMessageListener(new MessageListener<geometry_msgs.PoseWithCovarianceStamped>() {
            @Override
            public void onNewMessage (geometry_msgs.PoseWithCovarianceStamped message){
                Log.d("debugg", "new pose");

            }
        });
    }
}
