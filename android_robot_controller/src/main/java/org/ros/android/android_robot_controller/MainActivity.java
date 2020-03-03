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
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;
import org.ros.android.RosActivity;
import org.ros.android.android_robot_controller.fragments.FragmentMonitor;
import org.ros.android.android_robot_controller.fragments.FragmentSettings;
import org.ros.android.android_robot_controller.fragments.RosFragment;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMainExecutor;

public class MainActivity extends RosActivity implements  NavigationView.OnNavigationItemSelectedListener {

    private FragmentMonitor fragmentMonitor;
    private FragmentSettings fragmentSettings;

    private TextView textViewTitle;
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

        this.textViewTitle = findViewById(R.id.textViewTitle);

        // Control if fragment is already created
        if(savedInstanceState == null) {
            navigationView.setCheckedItem(navigationView.getMenu().getItem(0));
            this.onNavigationItemSelected(navigationView.getCheckedItem());
        }


    }

    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    public void onPause(){
        super.onPause();
        //this.openGLViewMap.onPause();
    }

    NodeMainExecutor nodeMainExecutor;

    @Override
    protected void init(NodeMainExecutor nodeMainExecutor) {
        NodeConfiguration nodeConfiguration = NodeConfiguration.newPublic(getRosHostname());
        nodeConfiguration.setMasterUri(getMasterUri());
        NodesExecutor.getInstance().setNodeConfigurationAndExecutor(nodeConfiguration, nodeMainExecutor);
        this.nodeMainExecutor = nodeMainExecutor;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        switch (item.getItemId()){
            case R.id.menu_item_monitor:
                if(this.getFragmentManager().findFragmentByTag("fragment_monitor") == null)
                    this.fragmentMonitor = new FragmentMonitor();
                else
                    this.fragmentMonitor = (FragmentMonitor) this.getFragmentManager().findFragmentByTag("fragment_monitor");

                fragmentTransaction.replace(R.id.linear_layout, this.fragmentMonitor, "fragment_monitor");
                fragmentTransaction.commit();
                if(this.textViewTitle != null)
                    this.textViewTitle.setText(R.string.fragment_monitor_title);
                break;
            case R.id.menu_item_settings:
                if(this.getFragmentManager().findFragmentByTag("fragment_settings") == null)
                    this.fragmentSettings = new FragmentSettings();
                else
                    this.fragmentSettings = (FragmentSettings) this.getFragmentManager().findFragmentByTag("fragment_settings");

                fragmentTransaction.replace(R.id.linear_layout, this.fragmentSettings, "fragment_settings");
                fragmentTransaction.commit();
                if(this.textViewTitle != null)
                    this.textViewTitle.setText(R.string.fragment_settings_title);
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.main_layout);
        drawer.closeDrawers();

        return true;
    }
}
