package org.travinskiy.alex.universalcontroller;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;

import java.util.Set;

public class ConnectionService extends IntentService {

    private static final long RUN_INTERVAL = 60 * 1000;
    private static final long DELAY_TO_WIFI_CONNECT = 15 * 1000;

    public static Intent newIntent(Context context) {
        return new Intent(context, ConnectionService.class);
    }

    public ConnectionService() {
        super(null);
    }

    public static boolean isServiceOn(Context context) {
        Intent intent = newIntent(context);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_NO_CREATE);
        return pendingIntent != null;
    }

    public static void setServiceAlarm(Context context, boolean turnOn) {
        Intent intent = newIntent(context);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, 0);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (turnOn) {
            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(),
                    RUN_INTERVAL, pendingIntent);
        } else {
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();
        }
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        boolean stopInAirplaneMode = AppPreferences.getPrefStopInAirplaneMode(getApplicationContext());
        if (stopInAirplaneMode && isAirplaneModeOn(getApplicationContext())) {
//            Log.i("AIRPLANE_MODE_ON", "TRUE");
            return;
        }
        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        if (wifiManager.isWifiEnabled()) {
            disableNotConnectedWifi(wifiManager);
        } else {
            wifiManager.setWifiEnabled(true);
            SystemClock.sleep(DELAY_TO_WIFI_CONNECT);
            disableNotConnectedWifi(wifiManager);
            boolean enableWhitelist = AppPreferences.getPrefEnableWhitelist(getApplicationContext());
            if (wifiManager.isWifiEnabled() && enableWhitelist) {
                Set<String> wifiWhitelist = AppPreferences.getPrefWifiWhitelist(getApplicationContext());
                if (wifiWhitelist != null && !wifiWhitelist.isEmpty()) {
                    WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                    String BSSID = wifiInfo.getBSSID();
                    String SSID = wifiInfo.getSSID();
                    String SSID_BSSID = getResources().getString(R.string.ssid_bssid, SSID, BSSID);
//                    Log.i("SSID_BSSID", SSID_BSSID);
                    if (BSSID == null || !wifiWhitelist.contains(SSID_BSSID)) {
                        wifiManager.setWifiEnabled(false);
                    }
                } else {
                    wifiManager.setWifiEnabled(false);
                }
            }
        }
    }

    private void disableNotConnectedWifi(WifiManager wifiManager) {
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if (wifiInfo.getBSSID() == null) {
            wifiManager.setWifiEnabled(false);
        } else {
            Log.i("WIFI_SSID", wifiInfo.getSSID());
        }
    }

    private static boolean isAirplaneModeOn(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return Settings.System.getInt(context.getContentResolver(),
                    Settings.System.AIRPLANE_MODE_ON, 0) != 0;
        } else {
            return Settings.Global.getInt(context.getContentResolver(),
                    Settings.Global.AIRPLANE_MODE_ON, 0) != 0;
        }
    }

}
