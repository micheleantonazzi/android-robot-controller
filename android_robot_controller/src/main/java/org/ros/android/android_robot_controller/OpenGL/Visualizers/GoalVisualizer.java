package org.ros.android.android_robot_controller.OpenGL.Visualizers;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

import org.ros.android.android_robot_controller.GlobalSettings;
import org.ros.android.android_robot_controller.OpenGL.Renderes.RosRenderer;
import org.ros.message.MessageListener;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Publisher;
import org.ros.node.topic.Subscriber;
import org.ros.rosjava_geometry.Quaternion;
import org.ros.rosjava_geometry.Vector3;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import geometry_msgs.PoseStamped;
import nav_msgs.MapMetaData;

public class GoalVisualizer extends AbstractNodeMain implements Visualizer{

    private Publisher<PoseStamped> publisherGoal;

    private float scale = 30.0f;

    private float translateX = 0.0f;
    private float translateY = 0.0f;
    private float rotationGlobal = 0.0f;
    private float rotation = 0.0f;

    // Map metadata
    private float mapWidth = Integer.MAX_VALUE;
    private float mapHeight = Integer.MAX_VALUE;
    private float mapResolution = 0.0f;
    private float mapOriginX = 0.0f;
    private float mapOriginY = 0.0f;

    private int openGLProgram;

    private int vertexHandle;
    private int colorHandle;

    private FloatBuffer vertexBuffer;

    private float arrowCoordinates[] = {
            -0.15f, 0.65f, 0.0f,   // triangle - bottom left
            0.0f,  1.0f, 0.0f,   // triangle - top center
            0.0f, 0.65f, 0.0f,    // triangle - center
            0.15f,  0.65f, 0.0f,   // triangle - bottom right
            -0.05f, 0.0f, 0.0f,  // rectangle - bottom left
            -0.05f,  0.65f, 0.0f,  // rectangle - top left
            0.05f, 0.0f, 0.0f,   // rectangle - bottom right
            0.05f,  0.65f, 0.0f }; // rectangle - top right

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
        int vertexShader = RosRenderer.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = RosRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

        // Create empty OpenGL ES Program
        this.openGLProgram = GLES20.glCreateProgram();

        // add the vertex shader to program
        GLES20.glAttachShader(this.openGLProgram, vertexShader);

        // add the fragment shader to program
        GLES20.glAttachShader(this.openGLProgram, fragmentShader);

        // creates OpenGL ES program executables
        GLES20.glLinkProgram(this.openGLProgram);
    }

    @Override
    public void draw(float[] resultMatrix) {

        // VERTEX
        this.vertexHandle = GLES20.glGetAttribLocation(this.openGLProgram, "vPosition");
        GLES20.glVertexAttribPointer(this.vertexHandle, 3,
                GLES20.GL_FLOAT, false,
                3 * 4, this.vertexBuffer);
        GLES20.glEnableVertexAttribArray(this.vertexHandle);

        // Add program to OpenGL ES environment
        GLES20.glUseProgram(this.openGLProgram);

        // get handle to shape's transformation matrix
        int vPMatrixHandle = GLES20.glGetUniformLocation(this.openGLProgram, "uMVPMatrix");

        synchronized (this) {

            Matrix.rotateM(resultMatrix, 0, this.rotationGlobal, 0, 0, 1);

            Matrix.translateM(resultMatrix, 0, this.translateX, this.translateY, 0.0f);

            Matrix.rotateM(resultMatrix, 0, this.rotation, 0, 0, 1);

            Matrix.scaleM(resultMatrix, 0, RosRenderer.GLOBAL_SCALE / this.mapWidth * this.scale, RosRenderer.GLOBAL_SCALE / this.mapWidth * this.scale, 1.0f);

            // Pass the projection and view transformation to the shader
            GLES20.glUniformMatrix4fv(vPMatrixHandle, 1, false, resultMatrix, 0);
        }

        // Get handle to fragment shader's vColor member
        colorHandle = GLES20.glGetUniformLocation(this.openGLProgram, "vColor");

        // Set color for drawing the triangle
        GLES20.glUniform4fv(colorHandle, 1, new float[]{0f, 0.6f, 0.544f, 1.0f}, 0);

        // Draw the triangles
        GLES20.glDrawArrays(
                GLES20.GL_TRIANGLE_STRIP, 0, 4);
        GLES20.glDrawArrays(
                GLES20.GL_TRIANGLE_STRIP, 4, 4);

        GLES20.glDisableVertexAttribArray(this.vertexHandle);
    }

    public synchronized void setAttributes(float translateX, float translateY, float rotationGlobal, float rotation){
        this.scale = 30.0f;
        this.translateY = translateY;
        this.translateX = translateX;
        this.rotationGlobal = rotationGlobal;
        this.rotation = rotation;
    }

    public synchronized void goalMarkerSet(){
        this.scale = 15f;

        // Rotation of X and Y based of rotation global
        float [] matrixGoal = new float[16];
        Matrix.setIdentityM(matrixGoal, 0);
        Matrix.translateM(matrixGoal, 0, this.translateX, translateY, 0.0f);
        Matrix.rotateM(matrixGoal, 0, -this.rotationGlobal, 0, 0, 1);
        // Matrix multiply
        float rotateX = matrixGoal[0] * matrixGoal[12] + matrixGoal[1] * matrixGoal[13];
        float rotateY = matrixGoal[4] * matrixGoal[12] + matrixGoal[5] * matrixGoal[13];
        //The new X and Y point after rotation
        float positionY = -((rotateX - 1.0f) / 2.0f * this.mapWidth) * this.mapResolution + this.mapOriginX;
        float positionX = ((rotateY + 1.0f) / 2.0f * this.mapHeight) * this.mapResolution + this.mapOriginY;

        // Add arrow rotation
        Quaternion goalRotation = Quaternion.fromAxisAngle(Vector3.zAxis(), (this.rotation + this.rotationGlobal) * (Math.PI / 180.0));

        PoseStamped goalMessage = this.publisherGoal.newMessage();
        goalMessage.getHeader().setFrameId("map");
        // Set orientation
        goalMessage.getPose().getOrientation().setZ(goalRotation.getZ());
        goalMessage.getPose().getOrientation().setW(goalRotation.getW());
        // Set position
        goalMessage.getPose().getPosition().setX(positionX);
        goalMessage.getPose().getPosition().setY(positionY);

        this.publisherGoal.publish(goalMessage);
    }

    private synchronized void setMapMetadata(float width, float height, float resolution, float originX, float originY){
        this.mapWidth = width;
        this.mapHeight = height;
        this.mapResolution = resolution;
        this.mapOriginX = originX;
        this.mapOriginY = originY;
    }

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of(GlobalSettings.getInstance().getApplicationNamespace()).join("node_goal");
    }

    @Override
    public void onStart(ConnectedNode connectedNode) {
        Subscriber<MapMetaData> subscriberMapMetaData = connectedNode.newSubscriber(GlobalSettings.getInstance().getMapMetadataTopic(), nav_msgs.MapMetaData._TYPE);
        subscriberMapMetaData.addMessageListener(new MessageListener<MapMetaData>() {
            @Override
            public void onNewMessage (nav_msgs.MapMetaData message){
                setMapMetadata(message.getWidth(), message.getHeight(), message.getResolution(),
                        (float) message.getOrigin().getPosition().getX(), (float) message.getOrigin().getPosition().getY());
            }
        });

        this.publisherGoal = connectedNode.newPublisher(GlobalSettings.getInstance().getGoalTopic(), PoseStamped._TYPE);
    }
}
