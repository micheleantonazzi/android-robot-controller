/*
 * Copyright (C) 2011 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.ros.android.android_robot_controller;

import android.os.Bundle;
import android.util.Log;

import org.ros.android.RosActivity;
import org.ros.android.android_robot_controller.OpenGL.Views.OpenGLViewMap;
import org.ros.message.MessageListener;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMainExecutor;
import org.ros.node.topic.Subscriber;

import nav_msgs.OccupancyGrid;

public class MainActivity extends RosActivity {

    private AbstractNodeMain nodeMapReader;

    private OpenGLViewMap openGLViewMap;

    public MainActivity() {
        // The RosActivity constructor configures the notification title and ticker
        // messages.
        super("Robot controller", "Robot controller");

    }

    @SuppressWarnings("unchecked")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        nodeMapReader = new AbstractNodeMain() {
            @Override
            public GraphName getDefaultNodeName() {
                return GraphName.of("android_robot_controller/node_map_reader");
            }

            @Override
            public void onStart(ConnectedNode connectedNode) {
                Subscriber<nav_msgs.OccupancyGrid> subscriber = connectedNode.newSubscriber("map", nav_msgs.OccupancyGrid._TYPE);
                subscriber.addMessageListener(new MessageListener<OccupancyGrid>() {
                    @Override
                    public void onNewMessage (nav_msgs.OccupancyGrid message){
                        Log.d("debugg", "new Map");
                    }
                });
            }
        };

        openGLViewMap = (OpenGLViewMap) findViewById(R.id.OpenGLViewMa);

    }

    @Override
    public void onResume(){
        super.onResume();
        //this.openGLViewMap.onResume();
    }

    @Override
    public void onPause(){
        super.onPause();
        //this.openGLViewMap.onPause();
    }

    @Override
    protected void init(NodeMainExecutor nodeMainExecutor) {
        NodeConfiguration nodeConfiguration = NodeConfiguration.newPublic(getRosHostname());
        nodeConfiguration.setMasterUri(getMasterUri());
        nodeMainExecutor.execute(nodeMapReader, nodeConfiguration);

    }
}
