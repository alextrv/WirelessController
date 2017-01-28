package org.travinskiy.alex.universalcontroller;

import android.content.Context;
import android.preference.PreferenceManager;

public class AppPreferences {

    private static final String PREF_STOP_IN_AIRPLANE_MODE = "prefStopInAirplaneMode";

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

}
