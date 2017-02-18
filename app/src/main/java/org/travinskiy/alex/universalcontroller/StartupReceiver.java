package org.travinskiy.alex.universalcontroller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class StartupReceiver extends BroadcastReceiver {

    private static final String TAG = "StartupReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {

        MyLogger.writeToFile(context, TAG, Utils.SEPARATOR, "BOOT_COMPLETED");

        boolean isOn = AppPreferences.getPrefIsAlarmOn(context);
        int intervalIndex = AppPreferences.getPrefServiceRunInterval(context);
        ConnectionService.setServiceAlarm(context, isOn, intervalIndex);

        boolean disableWireless = AppPreferences.getPrefDisableWireless(context);
        Utils.setForceDisableWirelessService(context, disableWireless);
    }
}
