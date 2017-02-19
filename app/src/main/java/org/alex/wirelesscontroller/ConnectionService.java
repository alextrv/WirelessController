package org.alex.wirelesscontroller;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.SystemClock;
import android.provider.Settings;

import java.util.Set;

public class ConnectionService extends IntentService {

    private static final String TAG = "ConnectionService";

    private static final long[] RUN_INTERVAL = {
            AlarmManager.INTERVAL_FIFTEEN_MINUTES,
            AlarmManager.INTERVAL_HALF_HOUR,
            AlarmManager.INTERVAL_HOUR
    };
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

    public static void setServiceAlarm(Context context, boolean turnOn, int runIntervalIndex) {

        MyLogger.writeToFile(context, TAG, Utils.SEPARATOR, "setServiceAlarm", turnOn, runIntervalIndex);

        Intent intent = newIntent(context);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, 0);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (turnOn) {
            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(),
                    RUN_INTERVAL[runIntervalIndex], pendingIntent);
        } else {
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();
        }
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        boolean stopInAirplaneMode = AppPreferences.getPrefStopInAirplaneMode(getApplicationContext());
        boolean isAirplaneMode = isAirplaneModeOn(getApplicationContext());
        if ((stopInAirplaneMode && isAirplaneMode) ||
                AppPreferences.getPrefForceDisabledWireless(getApplicationContext())) {
            MyLogger.writeToFile(getApplicationContext(), TAG, Utils.SEPARATOR, "AIRPLANE_MODE",
                    isAirplaneMode);
            return;
        }

        // Turn off Bluetooth if preference set true
        if (AppPreferences.getPrefDisableBluetooth(getApplicationContext())) {
            disableBluetooth();
            MyLogger.writeToFile(getApplicationContext(), TAG, Utils.SEPARATOR, "Turning off Bluetooth");
        }

        // Try to connect to Wi-Fi network. If fail then disable Wi-Fi
        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        if (wifiManager.isWifiEnabled()) {
            disableNotConnectedWifi(wifiManager);
        } else {
            wifiManager.setWifiEnabled(true);
            SystemClock.sleep(DELAY_TO_WIFI_CONNECT);
            disableNotConnectedWifi(wifiManager);
            boolean enableWhitelist = AppPreferences.getPrefEnableWhitelist(getApplicationContext());
            MyLogger.writeToFile(getApplicationContext(), TAG, Utils.SEPARATOR, "White list enabled",
                    enableWhitelist);
            if (wifiManager.isWifiEnabled() && enableWhitelist) {
                Set<String> wifiWhitelist = AppPreferences.getPrefWifiWhitelist(getApplicationContext());
                if (wifiWhitelist != null && !wifiWhitelist.isEmpty()) {
                    WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                    String BSSID = wifiInfo.getBSSID();
                    String SSID = wifiInfo.getSSID();
                    String SSID_BSSID = getResources().getString(R.string.ssid_bssid, SSID, BSSID);
                    MyLogger.writeToFile(getApplicationContext(), TAG, Utils.SEPARATOR, "SSID_BSSID",
                            SSID_BSSID);
                    if (BSSID == null || !wifiWhitelist.contains(SSID_BSSID)) {
                        wifiManager.setWifiEnabled(false);
                    }
                } else {
                    wifiManager.setWifiEnabled(false);
                }
            }
        }
        MyLogger.writeToFile(getApplicationContext(), TAG, Utils.SEPARATOR,
                "Is Wifi enabled", wifiManager.isWifiEnabled());
    }

    private void disableNotConnectedWifi(WifiManager wifiManager) {
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if (wifiInfo.getBSSID() == null || wifiInfo.getNetworkId() == -1) {
            wifiManager.setWifiEnabled(false);
        } else {
            MyLogger.writeToFile(getApplicationContext(), TAG, Utils.SEPARATOR, wifiInfo.getSSID());
        }
    }

    public static void disableBluetooth() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null && bluetoothAdapter.isEnabled()) {
            bluetoothAdapter.disable();
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
