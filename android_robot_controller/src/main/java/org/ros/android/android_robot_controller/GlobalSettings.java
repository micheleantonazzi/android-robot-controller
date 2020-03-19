package org.ros.android.android_robot_controller;
import android.content.SharedPreferences;

public class GlobalSettings {

    private static GlobalSettings instance = new GlobalSettings();
    private SharedPreferences preferencesSettings;

    // Preferences strings
    public static final String PREFERENCES_SETTINGS = "preferences_settings";

    public static final String PREFERENCES_NAMESPACE = "preference_settings_namespace";
    private static final String PREFERENCES_NAMESPACE_DEFAULT = "android_robot_controller/";

    public static final String PREFERENCE_MAP_TOPIC = "preference_setting_map_topic";
    private static final String PREFERENCE_MAP_TOPIC_DEFAULT = "/map";

    public static final String PREFERENCE_MAP_METADATA_TOPIC = "preference_map_metadata_topic";
    private static final String PREFERENCE_MAP_METADATA_TOPIC_DEFAULT = "/map_metadata";

    public static final String PREFERENCE_TF_TOPIC = "preference_tf_topic";
    private static final String PREFERENCE_TF_TOPIC_DEFAULT = "/tf";

    public static final String PREFERENCE_GOAL_TOPIC = "preference_goal_topic";
    private static final String PREFERENCE_GOAL_TOPIC_DEFAULT = "/move_base_simple/goal";

    public static final String PREFERENCE_CAMERA_COMPRESS_TOPIC = "preference_camera_compress_topic";
    private static final String PREFERENCE_CAMERA_COMPRESS_TOPIC_DEFAULT = "/usb_cam/image_raw/compressed";

    public static final String PREFERENCE_CONTROL_TOPIC = "preference_control_topic";
    private static final String PREFERENCE_CONTROL_TOPIC_DEFAULT = "/cmd_vel";

    private GlobalSettings(){}

    public static GlobalSettings getInstance(){
        return instance;
    }

    public void setPreferencesSettings(SharedPreferences preferencesSettings){
        this.preferencesSettings = preferencesSettings;
    }

    public String getPreferenceFromName(String preferenceName){
        switch (preferenceName){
            case PREFERENCES_NAMESPACE:
                return this.getApplicationNamespace();
        }
        return "";
    }

    public void setPreferenceFromName(String preferenceName, String value){
        switch (preferenceName){
            case PREFERENCES_NAMESPACE:
                this.setApplicationNamespace(value);
                break;
        }
    }

    public String getApplicationNamespace(){
        return this.preferencesSettings != null ?
                this.preferencesSettings.getString(PREFERENCES_NAMESPACE, PREFERENCES_NAMESPACE_DEFAULT)
                : "";
    }

    public void setApplicationNamespace(String applicationNamespace){
        this.preferencesSettings.edit().putString(PREFERENCES_NAMESPACE, applicationNamespace).commit();
    }
}
