package org.alex.wirelesscontroller.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.alex.wirelesscontroller.AppPreferences;
import org.alex.wirelesscontroller.MyLogger;
import org.alex.wirelesscontroller.Utils;

public class StartupReceiver extends BroadcastReceiver {

    private static final String TAG = "StartupReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {

        MyLogger.getInstance(context).writeToFile(TAG, Utils.SEPARATOR, "BOOT_COMPLETED");

        boolean isOn = AppPreferences.getPrefIsAutoEnableWifiOn(context);
        Utils.setAutoEnableWifiService(context, isOn);

        boolean disableWireless = AppPreferences.getPrefDisableWireless(context);
        Utils.setForceDisableWirelessService(context, disableWireless);
    }
}
