package org.alex.wirelesscontroller;

import android.content.Context;
import android.preference.PreferenceManager;

import java.util.HashSet;
import java.util.Set;

public class AppPreferences {

    public static final String PREF_STOP_IN_AIRPLANE_MODE = "prefStopInAirplaneMode";
    public static final String PREF_WIFI_WHITELIST = "prefWifiWhitelist";
    public static final String PREF_ENABLE_WHITELIST = "prefEnableWhitelist";
    public static final String PREF_IS_AUTO_ENABLE_WIFI_ON = "prefIsAutoEnableWifiOn";
    public static final String PREF_START_TIME = "prefStartTime";
    public static final String PREF_END_TIME = "prefEndTime";
    public static final String PREF_REPEAT_DISABLE_WIRELESS = "prefRepeatDisableWireless";
    public static final String PREF_SERVICE_RUN_INTERVAL = "prefServiceRunInterval";
    public static final String PREF_SUSPEND_AUTO_ENABLE_WIFI = "prefSuspendAutoEnableWifi";
    public static final String PREF_DISABLE_WIRELESS = "prefDisableWireless";
    public static final String PREF_DISABLE_BLUETOOTH = "prefDisableBluetooth";
    public static final String PREF_ENABLE_LOGGING = "prefEnableLogging";
    public static final String PREF_LOCATION_NOTIFICATION = "prefLocationNotification";

    /* Getter and Setter for PREF_STOP_IN_AIRPLANE_MODE */
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

    /* Getter and Setter for PREF_WIFI_WHITELIST */
    public static Set<String> getPrefWifiWhitelist(Context context) {
        Set<String> whitelist = PreferenceManager.getDefaultSharedPreferences(context)
                .getStringSet(PREF_WIFI_WHITELIST, new HashSet<>());

        if (whitelist.size() > 0) {
            return new HashSet<>(whitelist);
        }
        return new HashSet<>();
    }

    public static void setPrefWifiWhitelist(Context context, Set<String> values) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putStringSet(PREF_WIFI_WHITELIST, values)
                .apply();
    }

    /* Getter and Setter for PREF_ENABLE_WHITELIST */
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

    /* Getter and Setter for PREF_IS_AUTO_ENABLE_WIFI_ON */
    public static boolean getPrefIsAutoEnableWifiOn(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(PREF_IS_AUTO_ENABLE_WIFI_ON, false);
    }

    public static void setPrefIsAutoEnableWifiOn(Context context, boolean isOn) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(PREF_IS_AUTO_ENABLE_WIFI_ON, isOn)
                .apply();
    }

    /* Getter and Setter for PREF_START_TIME */
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

    /* Getter and Setter for PREF_END_TIME */
    public static String getPrefEndTime(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(PREF_END_TIME, TimePickerFragment.END_TIME_DEFAULT);
    }

    public static void setPrefEndTime(Context context, String value) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(PREF_END_TIME, value)
                .apply();
    }

    /* Getter and Setter for PREF_REPEAT_DISABLE_WIRELESS */
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

    /* Getter for PREF_SERVICE_RUN_INTERVAL */
    public static int getPrefServiceRunInterval(Context context) {
        String value = PreferenceManager.getDefaultSharedPreferences(context)
                .getString(PREF_SERVICE_RUN_INTERVAL, context.getString(R.string.pref_serviceRunInterval_default));
        return Integer.parseInt(value);
    }

    /* Getter and Setter for PREF_SUSPEND_AUTO_ENABLE_WIFI */
    public static boolean getPrefSuspendAutoEnableWifi(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(PREF_SUSPEND_AUTO_ENABLE_WIFI, false);
    }

    public static void setPrefSuspendAutoEnableWifi(Context context, boolean value) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(PREF_SUSPEND_AUTO_ENABLE_WIFI, value)
                .apply();
    }

    /* Getter and Setter for PREF_DISABLE_WIRELESS */
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

    /* Getter and Setter for PREF_DISABLE_BLUETOOTH */
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

    /* Getter and Setter for PREF_ENABLE_LOGGING */
    public static boolean getPrefEnableLogging(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(PREF_ENABLE_LOGGING, false);
    }

    public static void setPrefEnableLogging(Context context, boolean value) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(PREF_ENABLE_LOGGING, value)
                .apply();
    }

    /* Getter and Setter for PREF_LOCATION_NOTIFICATION */
    public static boolean getPrefLocationNotification(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(PREF_LOCATION_NOTIFICATION, false);
    }

    public static void setPrefLocationNotification(Context context, boolean value) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(PREF_LOCATION_NOTIFICATION, value)
                .apply();
    }

}
