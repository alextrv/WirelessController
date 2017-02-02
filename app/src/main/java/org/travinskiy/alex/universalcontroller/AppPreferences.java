package org.travinskiy.alex.universalcontroller;

import android.content.Context;
import android.preference.PreferenceManager;

import java.util.Set;

public class AppPreferences {

    private static final String PREF_STOP_IN_AIRPLANE_MODE = "prefStopInAirplaneMode";
    private static final String PREF_WIFI_WHITELIST = "prefWifiWhitelist";
    private static final String PREF_ENABLE_WHITELIST = "prefEnableWhitelist";
    private static final String PREF_IS_ALARM_ON = "prefIsAlarmOn";

    public static boolean getPrefStopInAirplaneMode(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(PREF_STOP_IN_AIRPLANE_MODE, true);
    }

    public static void setPrefStopInAirplaneMode(Context context, boolean value) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(PREF_STOP_IN_AIRPLANE_MODE, value)
                .apply();
    }

    public static Set<String> getPrefWifiWhitelist(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getStringSet(PREF_WIFI_WHITELIST, null);
    }

    public static void setPrefWifiWhitelist(Context context, Set<String> values) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putStringSet(PREF_WIFI_WHITELIST, values)
                .apply();
    }

    public static boolean getPrefEnableWhitelist(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(PREF_ENABLE_WHITELIST, false);
    }

    public static void setPrefEnableWhitelist(Context context, boolean value) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(PREF_ENABLE_WHITELIST, value)
                .apply();
    }

    public static boolean getPrefIsAlarmOn(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(PREF_IS_ALARM_ON, false);
    }

    public static void setPrefIsAlarmOn(Context context, boolean isOn) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(PREF_IS_ALARM_ON, isOn)
                .apply();
    }

}
