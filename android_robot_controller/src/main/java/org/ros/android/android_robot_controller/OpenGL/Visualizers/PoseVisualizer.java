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
import org.ros.rosjava_geometry.FrameTransform;
import org.ros.rosjava_geometry.FrameTransformTree;
import org.ros.rosjava_geometry.Quaternion;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import geometry_msgs.TransformStamped;

public class PoseVisualizer extends AbstractNodeMain implements Visualizer{

    private float mapDimension = 1;
    private float mapResolution = 1;
    private float mapOriginX = 1;
    private float mapOriginY = 1;
    private float positionX = 0;
    private float positionY = 0;
    private float rotationAngle = 0;
    private float scale = 0.05f;

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

    @Override
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

        synchronized (this){
            // Set robot position and scale
            Matrix.translateM(resultMatrix, 0, this.positionX, this.positionY, 0);
            Matrix.rotateM(resultMatrix, 0, this.rotationAngle, 0, 0, 1);
            Matrix.scaleM(resultMatrix, 0, this.scale , this.scale, 1.0f);

            // Pass the projection and view transformation to the shader
            GLES30.glUniformMatrix4fv(vPMatrixHandle, 1, false, resultMatrix, 0);
        }


        // Get handle to fragment shader's vColor member
        colorHandle = GLES30.glGetUniformLocation(this.openGLProgram, "vColor");

        // Set color for drawing the triangle
        GLES30.glUniform4fv(colorHandle, 1, new float[]{0.1f, 0.1f, 0.8f, 1.0f}, 0);

        // Draw the triangles
        GLES30.glDrawArrays(
                GLES30.GL_TRIANGLE_STRIP, 0, 4);

        GLES30.glDisableVertexAttribArray(this.vertexHandle);

    }

    private synchronized void setMapMetaData(float mapDimension, float mapResolution, float mapOriginX, float mapOriginY){
        this.mapDimension = mapDimension;
        this.mapResolution = mapResolution;
        this.mapOriginX = mapOriginX;
        this.mapOriginY = mapOriginY;
    }

    private synchronized void setPosition(float positionX, float positionY, float rotationAngle){
        this.positionX = (this.mapDimension / 2f - ((positionY - this.mapOriginY) / this.mapResolution)) / (this.mapDimension / 2f);
        this.positionY = (-this.mapDimension / 2f + ((positionX - this.mapOriginX) / this.mapResolution)) / (this.mapDimension / 2f);
        this.rotationAngle = rotationAngle;
        this.scale = RosRenderer.GLOBAL_SCALE / this.mapDimension * 2.0f;
    }

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("android_robot_controller/node_pose_reader");
    }

    FrameTransformTree transformTree = new FrameTransformTree();

    @Override
    public void onStart(ConnectedNode connectedNode) {

        Subscriber<tf2_msgs.TFMessage> subscriberTF = connectedNode.newSubscriber("tf", tf2_msgs.TFMessage._TYPE);
        subscriberTF.addMessageListener(new MessageListener<tf2_msgs.TFMessage>() {
            @Override
            public void onNewMessage (tf2_msgs.TFMessage message){
                // Set rotation
                for(TransformStamped transform : message.getTransforms()){

                    transformTree.update(transform);

                }

                FrameTransform frameTransform = transformTree.transform(GraphName.of("base_footprint"), GraphName.of("map"));
                if(frameTransform != null){
                    Quaternion q =  frameTransform.getTransform().getRotationAndScale();
                    double siny_cosp = 2 * (q.getW() * q.getZ() + q.getX() * q.getY());
                    double cosy_cosp = 1 - 2 * (q.getY() * q.getY() + q.getZ() * q.getZ());
                    float theta = (float) Math.atan2(siny_cosp, cosy_cosp);
                    float rotation = theta * 180f / (float) Math.PI ;

                    setPosition((float)frameTransform.getTransform().getTranslation().getX(),
                            (float) frameTransform.getTransform().getTranslation().getY(), rotation);
                }
            }
        });

        Subscriber<nav_msgs.MapMetaData> subscriberMapMetaData = connectedNode.newSubscriber("map_metadata", nav_msgs.MapMetaData._TYPE);
        subscriberMapMetaData.addMessageListener(new MessageListener<nav_msgs.MapMetaData>() {
            @Override
            public void onNewMessage (nav_msgs.MapMetaData message){
                setMapMetaData(message.getHeight(), message.getResolution(),
                        (float) message.getOrigin().getPosition().getX(),
                        (float) message.getOrigin().getPosition().getY());
            }
        });
    }
}
