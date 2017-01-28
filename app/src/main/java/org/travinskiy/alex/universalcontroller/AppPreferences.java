package org.travinskiy.alex.universalcontroller;

import android.content.Context;
import android.preference.PreferenceManager;

import java.util.Set;

public class AppPreferences {

    private static final String PREF_STOP_IN_AIRPLANE_MODE = "prefStopInAirplaneMode";
    private static final String PREF_WIFI_WHITELIST = "prefWifiWhitelist";

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

}
