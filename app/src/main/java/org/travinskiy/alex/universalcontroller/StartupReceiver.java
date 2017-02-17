package org.travinskiy.alex.universalcontroller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class StartupReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        boolean isOn = AppPreferences.getPrefIsAlarmOn(context);
        int intervalIndex = AppPreferences.getPrefServiceRunInterval(context);
        ConnectionService.setServiceAlarm(context, isOn, intervalIndex);

        boolean disableWireless = AppPreferences.getPrefDisableWireless(context);
        Utils.setForceDisableWirelessService(context, disableWireless);
    }
}
