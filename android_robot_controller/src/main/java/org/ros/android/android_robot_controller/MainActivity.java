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
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toolbar;

import org.ros.android.RosActivity;
import org.ros.android.android_robot_controller.fragments.FragmentMap;
import org.ros.node.AbstractNodeMain;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMainExecutor;

public class MainActivity extends RosActivity {

    private FragmentMap fragmentMap;

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
        /*
        Toolbar toolbar = new Toolbar(this);
        LinearLayout.LayoutParams toolBarParams = new LinearLayout.LayoutParams(
                Toolbar.LayoutParams.MATCH_PARENT,
                150
        );
        toolbar.setLayoutParams(toolBarParams);
        toolbar.setBackgroundColor(Color.BLACK);
        toolbar.setPopupTheme(R.style.MyAppTheme);
        toolbar.setVisibility(View.VISIBLE);


        LinearLayout ll = findViewById(R.id.layout);
        ll.addView(toolbar, 0);
        toolbar.


         */
        // Control if fragment is already created
        if(savedInstanceState == null){
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            this.fragmentMap = new FragmentMap();
            fragmentTransaction.add(R.id.LinearLayout, fragmentMap, "map_fragment");
            fragmentTransaction.commit();
        }
        else
            this.fragmentMap = (FragmentMap) this.getFragmentManager().findFragmentByTag("map_fragment");

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

        for (AbstractNodeMain node : this.fragmentMap.getRosOpenGLView().getVisualizers()){
            if(node != null){
                nodeMainExecutor.execute(node, nodeConfiguration);
            }
        }
    }
}
