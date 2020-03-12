package org.ros.android.android_robot_controller.nodes;

import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Publisher;
import org.ros.rosjava_geometry.Vector3;

import geometry_msgs.Twist;

public class NodeControl extends AbstractNodeMain {

    private Publisher<Twist> publisher;

    public void publish(float angle, float velocity){
        if(this.publisher != null){
            Twist message = this.publisher.newMessage();

            this.publisher.publish(message);
        }
    }

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("android_robot_controller/node_control");
    }

    @Override
    public void onStart(ConnectedNode connectedNode) {
        this.publisher = connectedNode.newPublisher("/cmd_vel", Twist._TYPE);
    }
}
