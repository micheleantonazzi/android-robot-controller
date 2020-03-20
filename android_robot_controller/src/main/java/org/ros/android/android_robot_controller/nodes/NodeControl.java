package org.ros.android.android_robot_controller.nodes;

import org.ros.android.android_robot_controller.GlobalSettings;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Publisher;
import geometry_msgs.Twist;

public class NodeControl extends AbstractNodeMain {

    public static final int DIRECTION_UP = 1;
    public static final int DIRECTION_DOWN = -1;

    public static final int DIRECTION_LEFT = 1;
    public static final int DIRECTION_RIGHT = -1;

    private Publisher<Twist> publisher;

    private int directionVertical = 0;
    private float velocityVertical = 0;

    private int directionHorizontal = 0;
    private float velocityHorizontal = 0;

    private void publish(){
        if(this.publisher != null){
            Twist message = this.publisher.newMessage();
            message.getAngular().setZ(this.velocityHorizontal * this.directionHorizontal);
            message.getLinear().setX(this.velocityVertical * this.directionVertical);
            this.publisher.publish(message);
        }
    }

    public void publishVertical(int directionVertical, float velocity){
        if((directionVertical == DIRECTION_UP || directionVertical == DIRECTION_DOWN) &&
                velocity >= 0.0f &&velocity <= 1.0f){
            this.directionVertical = directionVertical;
            this.velocityVertical = velocity;
            this.publish();
        }
        this.publish();
    }

    public synchronized void publishHorizontal(int directionHorizontal, float velocity){
        if((directionHorizontal == DIRECTION_RIGHT || directionHorizontal == DIRECTION_LEFT) &&
                velocity >= 0.0f &&velocity <= 1.0f){
            this.directionHorizontal = directionHorizontal;
            this.velocityHorizontal = velocity;
            this.publish();
        }
        this.publish();
    }

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of(GlobalSettings.getInstance().getApplicationNamespace()).join("node_control");
    }

    @Override
    public void onStart(ConnectedNode connectedNode) {
        this.publisher = connectedNode.newPublisher(GlobalSettings.getInstance().getControlTopic(), Twist._TYPE);
    }
}
