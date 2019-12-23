package org.ros.android.android_robot_controller;

import android.opengl.GLES30;
import android.util.Log;

import org.jboss.netty.buffer.ChannelBuffer;
import org.ros.android.android_robot_controller.OpenGL.Renderes.MapRenderer;
import org.ros.message.MessageListener;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Subscriber;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import nav_msgs.OccupancyGrid;

public class MapSquare extends AbstractNodeMain {

    static private MapSquare instance;

    // This variable indicates if the map texture is updated
    private boolean mapUpdate = false;

    private int openGLProgram;

    private int positionHandle;
    private int textureVertexHandle;
    private int textureHandle;
    private int colorHandle;

    private FloatBuffer vertexBuffer;
    private FloatBuffer vertexTextureBuffer;

    private ByteBuffer textureBuffer = ByteBuffer.allocateDirect(0);
    private int textureDim = 0;

    static float rectangleCoordinates[] = {
            -1.0f, -0.5f, 0.0f,   // bottom left
            -1.0f,  0.5f, 0.0f,   // top left
            1.0f, -0.5f, 0.0f,    // bottom right
            1.0f,  0.5f, 0.0f };  // top right

    static float textureCoordinates[] = {
            0.0f, 1.0f, // top left
            1.0f, 1.0f, // top right
            0.0f, 0.0f, // bottom left
            1.0f, 0.0f};  // bottom right


    private short drawOrder[] = { 0, 1, 2, 0, 2, 3 }; // The order in which to draw vertices

    private final String vertexShaderCode =
            "uniform mat4 uMVPMatrix;" +
                    "attribute vec4 vPosition;" +
                    "attribute vec2 tPosition;" +
                    "varying vec2 TexCoordinate;" +
                    "void main() {" +
                    "  gl_Position = uMVPMatrix * vPosition;" +
                    "  TexCoordinate = tPosition;" +
                    "}";

    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "uniform sampler2D Texture;" +
                    "varying vec2 TexCoordinate;" +
                    "void main() {" +
                    "  gl_FragColor = texture2D(Texture, TexCoordinate);" +
                    "}";

    private MapSquare() {
        this.textureBuffer.position(0);
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

        // VERTEX
        this.positionHandle = GLES30.glGetAttribLocation(this.openGLProgram, "vPosition");
        GLES30.glVertexAttribPointer(this.positionHandle, 3,
                GLES30.GL_FLOAT, false,
                3 * 4, this.vertexBuffer);
        GLES30.glEnableVertexAttribArray(this.positionHandle);

        // TEXTURE VERTEX
        this.textureVertexHandle = GLES30.glGetAttribLocation(this.openGLProgram, "tPosition");
        GLES30.glVertexAttribPointer(this.textureVertexHandle, 2,
                GLES30.GL_FLOAT, false,
                2 * 4, this.vertexTextureBuffer);
        GLES30.glEnableVertexAttribArray(this.textureVertexHandle);


        // TEXTURE
        int[] textures = new int[1];
        GLES30.glGenTextures(1, textures, 0);
        this.textureHandle = textures[0];
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, this.textureHandle);

        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_REPEAT);	// set texture wrapping to GL_REPEAT (default wrapping method)
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_REPEAT);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_NEAREST);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_NEAREST);

    }

    private synchronized void setBufferData(ByteBuffer textureBuffer, int textureDim){
        this.textureBuffer = textureBuffer;
        this.textureDim = textureDim;
        this.mapUpdate = true;

    }

    public static MapSquare getInstance(){
        if(instance == null)
            instance = new MapSquare();
        return instance;
    }

    public synchronized void draw(float[] mvpMatrix) {
        // Add program to OpenGL ES environment
        GLES30.glUseProgram(this.openGLProgram);

        // get handle to shape's transformation matrix
        int vPMatrixHandle = GLES30.glGetUniformLocation(this.openGLProgram, "uMVPMatrix");

        // Pass the projection and view transformation to the shader
        GLES30.glUniformMatrix4fv(vPMatrixHandle, 1, false, mvpMatrix, 0);

        // MAP TEXTURE
        if(this.mapUpdate == true && this.textureBuffer.capacity() > 0) {
            GLES30.glTexImage2D(GLES30.GL_TEXTURE_2D, 0, GLES30.GL_RGB,
                    this.textureDim, this.textureDim, 0, GLES30.GL_RGB, GLES30.GL_UNSIGNED_BYTE, this.textureBuffer);
            GLES30.glGenerateMipmap(GLES30.GL_TEXTURE_2D);
            this.mapUpdate = false;
        }

        int mTextureUniformHandle = GLES30.glGetUniformLocation(this.openGLProgram, "Texture");
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, this.textureHandle);
        GLES30.glUniform1i(mTextureUniformHandle, 0);

        // get handle to fragment shader's vColor member
        colorHandle = GLES30.glGetUniformLocation(this.openGLProgram, "vColor");

        // Set color for drawing the triangle
        GLES30.glUniform4fv(colorHandle, 1, new float[]{0.63671875f, 0.76953125f, 0.22265625f, 1.0f}, 0);

        // Draw the triangles
        GLES30.glDrawArrays(
                GLES30.GL_TRIANGLE_STRIP, 0, 4);


    }

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("android_robot_controller/node_map_reader");
    }

    @Override
    public void onStart(ConnectedNode connectedNode) {
        Subscriber<OccupancyGrid> subscriber = connectedNode.newSubscriber("map", nav_msgs.OccupancyGrid._TYPE);
        subscriber.addMessageListener(new MessageListener<OccupancyGrid>() {
            @Override
            public void onNewMessage (nav_msgs.OccupancyGrid message){
                Log.d("debugg", "new Map");

                int mapWidth = message.getInfo().getWidth();

                ChannelBuffer messageTexture = message.getData();
                ByteBuffer textureBuffer = ByteBuffer.allocateDirect(mapWidth * mapWidth * 3);
                textureBuffer.position(0);
                for(int i = 0; i < message.getData().capacity(); i++){
                    byte b = messageTexture.readByte();
                    textureBuffer.put(b < 0 ? (byte) 205: (byte) (255 * (100-b) / 100));
                    textureBuffer.put(b < 0 ? (byte) 205: (byte) (255 * (100-b) / 100));
                    textureBuffer.put(b < 0 ? (byte) 205: (byte) (255 * (100-b) / 100));
                }
                textureBuffer.position(0);

                setBufferData(textureBuffer, mapWidth);
            }
        });
    }

}
