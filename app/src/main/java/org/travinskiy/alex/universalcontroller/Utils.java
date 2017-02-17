package org.travinskiy.alex.universalcontroller;

import android.content.Context;

public class Utils {

    public static void setMainService(Context context, boolean isOn) {
        int intervalIndex = AppPreferences.getPrefServiceRunInterval(context);
        ConnectionService.setServiceAlarm(context, isOn, intervalIndex);
    }

    public static void setForceDisableWirelessService(Context context, boolean isOn) {
        String startTime = AppPreferences.getPrefStartTime(context);
        String endTime = AppPreferences.getPrefEndTime(context);
        DisableWireless.setServiceAlarm(context, startTime, endTime, isOn);
    }

}
