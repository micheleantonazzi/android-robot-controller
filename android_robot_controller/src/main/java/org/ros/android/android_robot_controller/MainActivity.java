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

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;

import org.ros.android.RosActivity;
import org.ros.android.android_robot_controller.fragments.FragmentMap;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMainExecutor;

public class MainActivity extends RosActivity {

    private FragmentMap fragmentMap;

    private NodeMainExecutor nodeMainExecutor;
    private NodeConfiguration nodeConfiguration;

    public MainActivity() {
        // The RosActivity constructor configures the notification title and ticker
        // messages.
        super("Robot controller", "Robot controller");
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("debugg", "activity on create");

        setContentView(R.layout.main);

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        this.fragmentMap = new FragmentMap();
        fragmentTransaction.add(R.id.layout, fragmentMap);
        fragmentTransaction.commit();

    }

    @Override
    public void onResume(){
        super.onResume();
        Log.d("debugg", "activity resume");
        //this.openGLViewMap.onResume();
    }

    @Override
    public void onPause(){
        super.onPause();
        Log.d("debugg", "activity pause");

        //this.openGLViewMap.onPause();
    }

    @Override
    protected void init(NodeMainExecutor nodeMainExecutor) {
        Log.d("debugg", "init");

        this.nodeConfiguration = NodeConfiguration.newPublic(getRosHostname());
        this.nodeConfiguration.setMasterUri(getMasterUri());

        this.nodeMainExecutor = nodeMainExecutor;
        fragmentMap.setNodeExecutor(this.nodeMainExecutor, this.nodeConfiguration);
    }
}
