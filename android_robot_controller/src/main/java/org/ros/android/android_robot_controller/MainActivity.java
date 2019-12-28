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
import org.ros.android.android_robot_controller.OpenGL.Views.RosOpenGLView;
import org.ros.node.AbstractNodeMain;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMainExecutor;

public class MainActivity extends RosActivity {

    private RosOpenGLView rosOpenGLView;

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

        rosOpenGLView = (RosOpenGLView) findViewById(R.id.RosOpenGLView);

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

        for(AbstractNodeMain node : this.rosOpenGLView.getVisualizer()){
            if(node != null)
                nodeMainExecutor.execute(node, nodeConfiguration);
        }
    }
}
