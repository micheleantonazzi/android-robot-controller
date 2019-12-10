# Android Robot Controller
This is an Android application to control a robot using ROS

## Setting the system to compile the application
In order to have the environment correctly set to develop in android, follow the this [guide](http://wiki.ros.org/android/Tutorials/kinetic/Installation%20-%20ROS%20Development%20Environment).
Issues:
* Problem during ros-java compiling: ignore it's complaints about rosjava_messages
* Problem during android_core compiling (cv_bridbe): read this [page](https://github.com/rosjava/android_core/issues/303). Modify the file ```android_core/src/android_extras/cv_bridge/src/main/AndroidManifest.xml``` and replace the line ```package="cv_bridge"``` with ```package="com.github.rosjava.android_extras.cv_bridge"```.
* Problem during compile tutorials in Android Studio (task wrapper already exist): see this [answer](https://stackoverflow.com/questions/53709282/cannot-add-task-wrapper-as-a-task-with-that-name-already-exists) on StackOverflow. Modify the file that contain the error and replace ```task wrapper(type: Wrapper)``` with ```wrapper```
* Problem during compile tutorials in Android Studio (Apk data are null): see this [answer](https://stackoverflow.com/questions/54503325/cause-buildoutput-apkdata-must-not-be-null) on StackOverflow. Clean the project and remake it.
