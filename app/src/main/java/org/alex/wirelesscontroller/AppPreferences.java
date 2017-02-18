package org.alex.wirelesscontroller;

import android.content.Context;
import android.preference.PreferenceManager;

import java.util.HashSet;
import java.util.Set;

public class AppPreferences {

    public static final String PREF_STOP_IN_AIRPLANE_MODE = "prefStopInAirplaneMode";
    public static final String PREF_WIFI_WHITELIST = "prefWifiWhitelist";
    public static final String PREF_ENABLE_WHITELIST = "prefEnableWhitelist";
    public static final String PREF_IS_ALARM_ON = "prefIsAlarmOn";
    public static final String PREF_START_TIME = "prefStartTime";
    public static final String PREF_END_TIME = "prefEndTime";
    public static final String PREF_REPEAT_DISABLE_WIRELESS = "prefRepeatDisableWireless";
    public static final String PREF_SERVICE_RUN_INTERVAL = "prefServiceRunInterval";
    public static final String PREF_FORCE_DISABLED_WIRELESS = "prefForceDisabledWireless";
    public static final String PREF_DISABLE_WIRELESS = "prefDisableWireless";
    public static final String PREF_DISABLE_BLUETOOTH = "prefDisableBluetooth";

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
                .getStringSet(PREF_WIFI_WHITELIST, new HashSet<String>());
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

    public static String getPrefStartTime(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(PREF_START_TIME, TimePickerFragment.START_TIME_DEFAULT);
    }

    public static void setPrefStartTime(Context context, String value) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(PREF_START_TIME, value)
                .apply();
    }

    public static String getPrefEndTime(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(PREF_END_TIME, TimePickerFragment.START_TIME_DEFAULT);
    }

    public static void setPrefEndTime(Context context, String value) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(PREF_END_TIME, value)
                .apply();
    }

    public static int getPrefRepeatDisableWireless(Context context) {
        String value = PreferenceManager.getDefaultSharedPreferences(context)
                .getString(PREF_REPEAT_DISABLE_WIRELESS, context.getString(R.string.pref_repeatDisableWireless_default));
        return Integer.parseInt(value);
    }

    public static void setPrefRepeatDisableWireless(Context context, String value) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(PREF_REPEAT_DISABLE_WIRELESS, value)
                .apply();
    }

    public static int getPrefServiceRunInterval(Context context) {
        String value = PreferenceManager.getDefaultSharedPreferences(context)
                .getString(PREF_SERVICE_RUN_INTERVAL, context.getString(R.string.pref_serviceRunInterval_default));
        return Integer.parseInt(value);
    }

    public static boolean getPrefForceDisabledWireless(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(PREF_FORCE_DISABLED_WIRELESS, false);
    }

    public static void setPrefForceDisabledWireless(Context context, boolean value) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(PREF_FORCE_DISABLED_WIRELESS, value)
                .apply();
    }

    public static boolean getPrefDisableWireless(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(PREF_DISABLE_WIRELESS, false);
    }

    public static void setPrefDisableWireless(Context context, boolean value) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(PREF_DISABLE_WIRELESS, value)
                .apply();
    }

    public static boolean getPrefDisableBluetooth(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(PREF_DISABLE_BLUETOOTH, true);
    }

    public static void setPrefDisableBluetooth(Context context, boolean value) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(PREF_DISABLE_BLUETOOTH, value)
                .apply();
    }

}
