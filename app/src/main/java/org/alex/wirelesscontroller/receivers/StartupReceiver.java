package org.alex.wirelesscontroller.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.alex.wirelesscontroller.AppPreferences;
import org.alex.wirelesscontroller.Utils;

import java.util.logging.Level;

public class StartupReceiver extends BroadcastReceiver {

    private static final String CLASS_NAME = StartupReceiver.class.getName();

    @Override
    public void onReceive(Context context, Intent intent) {

        Utils.getLogger(context, CLASS_NAME)
                .log(Level.INFO, "BOOT_COMPLETED");

        boolean isOn = AppPreferences.getPrefIsAutoEnableWifiOn(context);
        Utils.setAutoEnableWifiService(context, isOn);

        boolean disableWireless = AppPreferences.getPrefDisableWireless(context);
        Utils.setForceDisableWirelessService(context, disableWireless);
    }
}
