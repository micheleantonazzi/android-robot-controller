package org.ros.android.android_robot_controller;

import android.util.Log;

import org.ros.node.AbstractNodeMain;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMain;
import org.ros.node.NodeMainExecutor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NodesExecutor {

    private NodeConfiguration nodeConfiguration;
    private NodeMainExecutor nodeMainExecutor;

    private Map<AbstractNodeMain, Boolean> nodes = new HashMap<>();

    private static NodesExecutor instance;

    private NodesExecutor(){}

    private synchronized void executeNodes(){
        Log.d("debugg", nodes.keySet().size() + "");
        if(this.nodeConfiguration != null && this.nodeMainExecutor != null){
            for (AbstractNodeMain node : this.nodes.keySet()){
                if(!this.nodes.get(node)){
                    this.nodeMainExecutor.execute(node, this.nodeConfiguration);
                    this.nodes.put(node, true);
                }
            }
        }
    }

    public static synchronized NodesExecutor getInstance(){
        if(instance == null)
            instance = new NodesExecutor();

        return instance;
    }

    public synchronized void setNodeConfigurationAndExecutor(NodeConfiguration nodeConfiguration, NodeMainExecutor nodeMainExecutor){
        Log.d("debugg", "setexecutor");
        if(this.nodeMainExecutor != null){
            for(AbstractNodeMain node : this.nodes.keySet()){
                this.nodeMainExecutor.shutdownNodeMain(node);
                this.nodes.put(node, false);
            }

        }

        this.nodeConfiguration = nodeConfiguration;
        this.nodeMainExecutor = nodeMainExecutor;

        this.executeNodes();
    }

    public synchronized void setNodes(List<AbstractNodeMain> nodes){
        Log.d("debugg", "setnodes");
        for(AbstractNodeMain node : nodes){
            if(node != null){
                this.nodes.put(node, false);
            }
        }
        this.executeNodes();
    }

    public synchronized void shutDownNode(List<AbstractNodeMain> nodesToShutdown){
        if(this.nodeMainExecutor != null) {
            Log.d("debugg", "shutdownnodes");
            for (AbstractNodeMain node : nodesToShutdown) {
                if (node != null) {
                    Log.d("debugg", "noderemoved");
                    this.nodes.remove(node);
                    this.nodeMainExecutor.shutdownNodeMain(node);
                }
            }
        }
    }
}
