package org.ros.android.android_robot_controller;
import android.content.SharedPreferences;

public class GlobalSettings {

    private static GlobalSettings instance = new GlobalSettings();
    private SharedPreferences preferencesSettings;

    // Preferences strings
    public static final String PREFERENCES_SETTINGS = "preferences_settings";

    public static final String PREFERENCE_NAMESPACE = "preference_settings_namespace";
    private static final String PREFERENCE_NAMESPACE_DEFAULT = "android_robot_controller";

    public static final String PREFERENCE_MAP_TOPIC = "preference_setting_map_topic";
    private static final String PREFERENCE_MAP_TOPIC_DEFAULT = "map";

    public static final String PREFERENCE_MAP_METADATA_TOPIC = "preference_map_metadata_topic";
    private static final String PREFERENCE_MAP_METADATA_TOPIC_DEFAULT = "/map_metadata";

    public static final String PREFERENCE_TF_TOPIC = "preference_tf_topic";
    private static final String PREFERENCE_TF_TOPIC_DEFAULT = "tf";

    public static final String PREFERENCE_GOAL_TOPIC = "preference_goal_topic";
    private static final String PREFERENCE_GOAL_TOPIC_DEFAULT = "move_base_simple/goal";

    public static final String PREFERENCE_CAMERA_COMPRESS_TOPIC = "preference_camera_compress_topic";
    private static final String PREFERENCE_CAMERA_COMPRESS_TOPIC_DEFAULT = "usb_cam/image_raw/compressed";

    public static final String PREFERENCE_CONTROL_TOPIC = "preference_control_topic";
    private static final String PREFERENCE_CONTROL_TOPIC_DEFAULT = "cmd_vel";

    private GlobalSettings(){}

    public static GlobalSettings getInstance(){
        return instance;
    }

    public void setPreferencesSettings(SharedPreferences preferencesSettings){
        this.preferencesSettings = preferencesSettings;
    }

    public String getPreferenceFromName(String preferenceName){
        switch (preferenceName){
            case PREFERENCE_NAMESPACE:
                return this.getApplicationNamespace();
            case PREFERENCE_MAP_TOPIC:
                return this.getMapTopic();
            case PREFERENCE_MAP_METADATA_TOPIC:
                return this.getMapMetadataTopic();
            case PREFERENCE_TF_TOPIC:
                return this.getTFTopic();
            case PREFERENCE_GOAL_TOPIC:
                return this.getGoalTopic();
            case PREFERENCE_CAMERA_COMPRESS_TOPIC:
                return this.getCameraCompressedTopic();
            case PREFERENCE_CONTROL_TOPIC:
                return this.getControlTopic();
        }
        return "";
    }

    public void setPreferenceFromName(String preferenceName, String value){
        switch (preferenceName){
            case PREFERENCE_NAMESPACE:
                this.setApplicationNamespace(value);
                break;
            case PREFERENCE_MAP_TOPIC:
                this.setMapTopic(value);
                break;
            case PREFERENCE_MAP_METADATA_TOPIC:
                this.setMapMetadataTopic(value);
                break;
            case PREFERENCE_TF_TOPIC:
                this.setTFTopic(value);
                break;
            case PREFERENCE_GOAL_TOPIC:
                this.setGoalTopic(value);
                break;
            case PREFERENCE_CAMERA_COMPRESS_TOPIC:
                this.setCameraCompressedTopic(value);
                break;
            case PREFERENCE_CONTROL_TOPIC:
                this.setControlTopic(value);
                break;
        }
    }

    public String getApplicationNamespace(){
        return this.preferencesSettings != null ?
                this.preferencesSettings.getString(PREFERENCE_NAMESPACE, PREFERENCE_NAMESPACE_DEFAULT)
                : "";
    }

    public void setApplicationNamespace(String applicationNamespace){
        this.preferencesSettings.edit().putString(PREFERENCE_NAMESPACE, applicationNamespace).apply();
    }

    public String getMapTopic(){
        return this.preferencesSettings != null ?
                this.preferencesSettings.getString(PREFERENCE_MAP_TOPIC, PREFERENCE_MAP_TOPIC_DEFAULT)
                : "";
    }

    public void setMapTopic(String mapTopic){
        this.preferencesSettings.edit().putString(PREFERENCE_MAP_TOPIC, mapTopic).apply();
    }

    public String getMapMetadataTopic(){
        return this.preferencesSettings != null ?
                this.preferencesSettings.getString(PREFERENCE_MAP_METADATA_TOPIC, PREFERENCE_MAP_METADATA_TOPIC_DEFAULT)
                : "";
    }

    public void setMapMetadataTopic(String mapMetadataTopic){
        this.preferencesSettings.edit().putString(PREFERENCE_MAP_METADATA_TOPIC, mapMetadataTopic).apply();
    }

    public String getTFTopic(){
        return this.preferencesSettings != null ?
                this.preferencesSettings.getString(PREFERENCE_TF_TOPIC, PREFERENCE_TF_TOPIC_DEFAULT)
                : "";
    }

    public void setTFTopic(String tfTopic){
        this.preferencesSettings.edit().putString(PREFERENCE_TF_TOPIC, tfTopic).apply();
    }

    public String getGoalTopic(){
        return this.preferencesSettings != null ?
                this.preferencesSettings.getString(PREFERENCE_GOAL_TOPIC, PREFERENCE_GOAL_TOPIC_DEFAULT)
                : "";
    }

    public void setGoalTopic(String goalTopic){
        this.preferencesSettings.edit().putString(PREFERENCE_GOAL_TOPIC, goalTopic).apply();
    }

    public String getCameraCompressedTopic(){
        return this.preferencesSettings != null ?
                this.preferencesSettings.getString(PREFERENCE_CAMERA_COMPRESS_TOPIC, PREFERENCE_CAMERA_COMPRESS_TOPIC_DEFAULT)
                : "";
    }

    public void setCameraCompressedTopic(String cameraCompressedTopic){
        this.preferencesSettings.edit().putString(PREFERENCE_CAMERA_COMPRESS_TOPIC, cameraCompressedTopic).apply();
    }

    public String getControlTopic(){
        return this.preferencesSettings != null ?
                this.preferencesSettings.getString(PREFERENCE_CONTROL_TOPIC, PREFERENCE_CONTROL_TOPIC_DEFAULT)
                : "";
    }

    public void setControlTopic(String controlTopic){
        this.preferencesSettings.edit().putString(PREFERENCE_CONTROL_TOPIC, controlTopic).apply();
    }
}
