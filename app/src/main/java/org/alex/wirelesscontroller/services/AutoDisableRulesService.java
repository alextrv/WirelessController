package org.alex.wirelesscontroller.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.SystemClock;

import org.alex.wirelesscontroller.AppPreferences;
import org.alex.wirelesscontroller.Utils;
import org.alex.wirelesscontroller.receivers.ScheduleWakefulReceiver;

import java.util.logging.Level;

public class AutoDisableRulesService extends IntentService {

    private static final String CLASS_NAME = AutoDisableRulesService.class.getName();

    public AutoDisableRulesService() {
        super(null);
    }

    public static Intent newIntent(Context context, int requestCode) {
        Intent intent = new Intent(context, AutoDisableRulesService.class);
        intent.putExtra(Utils.REQUEST_CODE, requestCode);
        return intent;
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Context context = getApplicationContext();

        int requestCode = intent.getIntExtra(Utils.REQUEST_CODE, -1);

        Utils.getLogger(context, CLASS_NAME)
                .log(Level.INFO, "REQUEST_CODE: {0}", requestCode);

        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

        if (requestCode == Utils.START_TIME_CODE) {

            // If end time broadcast set then do work
            if (ScheduleWakefulReceiver.isBroadcastOn(context, Utils.END_TIME_CODE)) {

                // Turn off Wi-Fi
                if (wifiManager.isWifiEnabled()) {
                    wifiManager.setWifiEnabled(false);
                }

                // Turn off Bluetooth
                ConnectionService.disableBluetooth();

                AppPreferences.setPrefSuspendAutoEnableWifi(context, true);
            }
        } else if (requestCode == Utils.END_TIME_CODE) {
            // Sleep 2 seconds to ensure that if startTime and endTime running at the same time
            // startTime will end its work first.
            // Also if repeat == EVERY_DAY wait 2 seconds to set broadcasts correctly
            SystemClock.sleep(2000);

            AppPreferences.setPrefSuspendAutoEnableWifi(context, false);

            int repeat = AppPreferences.getPrefRepeatDisableWireless(context);
            if (repeat == Utils.ONE_TIME) {
                AppPreferences.setPrefDisableWireless(context, false);
                Utils.setForceDisableWirelessService(context, false);
            } else if (repeat == Utils.EVERY_DAY) {
                Utils.setForceDisableWirelessService(context, true);
            }

            // Send broadcast to start ConnectionService
            if (ScheduleWakefulReceiver.isBroadcastOn(context, Utils.CONNECTION_SERVICE_CODE)) {
                sendBroadcast(ScheduleWakefulReceiver.newIntent(context, Utils.CONNECTION_SERVICE_CODE));
            }
        }

        Utils.getLogger(context, CLASS_NAME)
                .log(Level.INFO, "Wifi enabled: {0}", wifiManager.isWifiEnabled());

        ScheduleWakefulReceiver.completeWakefulIntent(intent);
    }
}
