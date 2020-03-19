package org.ros.android.android_robot_controller;
import android.content.SharedPreferences;

public class GlobalSettings {

    private static GlobalSettings instance = new GlobalSettings();
    private SharedPreferences preferencesSettings;

    // Preferences strings
    public static final String PREFERENCES_SETTINGS = "preferences_settings";
    private static final String PREFERENCES_NAMESPACE = "preferences_settings_namespace";
    private static final String PREFERENCES_DEFAULT_NAMESPACE = "android_robot_controller/";

    private GlobalSettings(){}

    public static GlobalSettings getInstance(){
        return instance;
    }

    public void setPreferencesSettings(SharedPreferences preferencesSettings){
        this.preferencesSettings = preferencesSettings;
    }

    public String getApplicationNamespace(){
        return this.preferencesSettings != null ?
                this.preferencesSettings.getString(PREFERENCES_NAMESPACE, PREFERENCES_DEFAULT_NAMESPACE)
                : "";
    }

    public void setApplicationNamespace(String applicationNamespace){
        this.preferencesSettings.edit().putString(PREFERENCES_NAMESPACE, applicationNamespace);
    }
}
