package org.travinskiy.alex.universalcontroller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class StartupReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        boolean isOn = AppPreferences.getPrefIsAlarmOn(context);
        ConnectionService.setServiceAlarm(context, isOn);
    }
}
