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
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import org.ros.android.RosActivity;
import org.ros.android.android_robot_controller.fragments.FragmentMonitor;
import org.ros.node.AbstractNodeMain;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMainExecutor;

public class MainActivity extends RosActivity implements  NavigationView.OnNavigationItemSelectedListener {

    private FragmentMonitor fragmentMonitor;

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

        // Control if fragment is already created
        if(savedInstanceState == null){
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            this.fragmentMonitor = new FragmentMonitor();
            fragmentTransaction.add(R.id.linear_layout, fragmentMonitor, "map_fragment");
            fragmentTransaction.commit();
        }
        else
            this.fragmentMonitor = (FragmentMonitor) this.getFragmentManager().findFragmentByTag("map_fragment");

        // Create drawer toggle
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.main_layout);

        Toolbar toolbar =  findViewById(R.id.toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.app_name, R.string.app_name);
        toggle.syncState();
        drawer.addDrawerListener(toggle);

        // Set navigation view (menu) onItemPressedListener
        NavigationView navigationView = (NavigationView)findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);
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

        for (AbstractNodeMain node : this.fragmentMonitor.getRosOpenGLView().getVisualizers()){
            if(node != null){
                nodeMainExecutor.execute(node, nodeConfiguration);
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.menu_item_monitor:
                break;
            case R.id.menu_item_settings:
                break;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.main_layout);
        drawer.closeDrawers();

        return true;
    }
}
