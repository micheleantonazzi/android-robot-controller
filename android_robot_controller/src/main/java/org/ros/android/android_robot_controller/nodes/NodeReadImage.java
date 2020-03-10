package org.ros.android.android_robot_controller.nodes;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;
import org.ros.message.MessageListener;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Subscriber;
import sensor_msgs.CompressedImage;

public class NodeReadImage extends AbstractNodeMain {

    private ImageView imageView;

    public NodeReadImage(ImageView imageView){
        this.imageView = imageView;
    }

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("android_robot_controller/node_read_image");
    }

    @Override
    public void onStart(ConnectedNode connectedNode) {
        Subscriber<CompressedImage> subscriberReadImage = connectedNode.newSubscriber("/usb_cam/image_raw/compressed", CompressedImage._TYPE);
        subscriberReadImage.addMessageListener(new MessageListener<CompressedImage>() {
            @Override
            public void onNewMessage (CompressedImage message){
                Bitmap imageBitmap = BitmapFactory.decodeByteArray(message.getData().array(), message.getData().arrayOffset(), message.getData().readableBytes());
                imageView.post(new Runnable() {
                    @Override
                    public void run() {
                        imageView.setImageBitmap(imageBitmap);
                    }
                });
            }
        });
    }
}
