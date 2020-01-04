package org.ros.android.android_robot_controller.fragments;

import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMainExecutor;

public interface RosFragment {

    default void setNodeConfigurationAndExecutor(NodeConfiguration nodeConfiguration, NodeMainExecutor nodeMainExecutor){}

    default void executeNodes(){}
}
