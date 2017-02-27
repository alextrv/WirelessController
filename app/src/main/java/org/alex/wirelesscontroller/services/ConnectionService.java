package org.alex.wirelesscontroller.services;

import android.app.IntentService;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.SystemClock;
import android.provider.Settings;

import org.alex.wirelesscontroller.AppPreferences;
import org.alex.wirelesscontroller.MyLogger;
import org.alex.wirelesscontroller.R;
import org.alex.wirelesscontroller.receivers.ScheduleWakefulReceiver;
import org.alex.wirelesscontroller.Utils;

import java.util.Set;

public class ConnectionService extends IntentService {

    private static final String TAG = "ConnectionService";

    public static Intent newIntent(Context context, int requestCode) {
        Intent intent = new Intent(context, ConnectionService.class);
        intent.putExtra(Utils.REQUEST_CODE, requestCode);
        return intent;
    }

    public ConnectionService() {
        super(null);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Context context = getApplicationContext();

        // Set broadcast for next service start
        boolean isOn = AppPreferences.getPrefIsAutoEnableWifiOn(context);
        Utils.setAutoEnableWifiService(context, isOn);

        boolean stopInAirplaneMode = AppPreferences.getPrefStopInAirplaneMode(context);
        boolean isAirplaneMode = isAirplaneModeOn(context);
        if ((stopInAirplaneMode && isAirplaneMode) ||
                AppPreferences.getPrefSuspendAutoEnableWifi(context)) {
            MyLogger.getInstance(context).writeToFile(TAG, Utils.SEPARATOR, "AIRPLANE_MODE",
                    isAirplaneMode);
            ScheduleWakefulReceiver.completeWakefulIntent(intent);
            return;
        }

        // Turn off Bluetooth if preference set true
        if (AppPreferences.getPrefDisableBluetooth(context)) {
            disableBluetooth();
            MyLogger.getInstance(context).writeToFile(TAG, Utils.SEPARATOR, "Turn off Bluetooth");
        }

        // Try to connect to Wi-Fi network. If fail then disable Wi-Fi
        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        if (wifiManager.isWifiEnabled()) {
            disableNotConnectedWifi(wifiManager);
        } else {
            wifiManager.setWifiEnabled(true);
            SystemClock.sleep(Utils.DELAY_TO_WIFI_CONNECT);
            disableNotConnectedWifi(wifiManager);
            boolean enableWhitelist = AppPreferences.getPrefEnableWhitelist(context);

            MyLogger.getInstance(context).writeToFile(TAG, Utils.SEPARATOR, "White list enabled",
                    enableWhitelist);

            if (wifiManager.isWifiEnabled() && enableWhitelist) {
                Set<String> wifiWhitelist = AppPreferences.getPrefWifiWhitelist(context);
                if (wifiWhitelist != null && !wifiWhitelist.isEmpty()) {
                    WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                    String BSSID = wifiInfo.getBSSID();
                    String SSID = wifiInfo.getSSID();
                    String SSID_BSSID = getResources().getString(R.string.ssid_bssid, SSID, BSSID);
                    MyLogger.getInstance(context).writeToFile(TAG, Utils.SEPARATOR, "SSID_BSSID",
                            SSID_BSSID);
                    if (BSSID == null || !wifiWhitelist.contains(SSID_BSSID)) {
                        wifiManager.setWifiEnabled(false);
                    }
                } else {
                    wifiManager.setWifiEnabled(false);
                }
            }
        }

        MyLogger.getInstance(context).writeToFile(TAG, Utils.SEPARATOR,
                "Is Wifi enabled", wifiManager.isWifiEnabled());
        if (wifiManager.isWifiEnabled()) {
            MyLogger.getInstance(context).writeToFile(TAG, Utils.SEPARATOR,
                    wifiManager.getConnectionInfo().getSSID());
        }

        ScheduleWakefulReceiver.completeWakefulIntent(intent);
    }

    public static void disableNotConnectedWifi(WifiManager wifiManager) {
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if (!isWifiConnected(wifiInfo)) {
            wifiManager.setWifiEnabled(false);
        }
    }

    public static void disableBluetooth() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null && bluetoothAdapter.isEnabled()) {
            bluetoothAdapter.disable();
        }
    }

    public static boolean isAirplaneModeOn(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return Settings.System.getInt(context.getContentResolver(),
                    Settings.System.AIRPLANE_MODE_ON, 0) != 0;
        } else {
            return Settings.Global.getInt(context.getContentResolver(),
                    Settings.Global.AIRPLANE_MODE_ON, 0) != 0;
        }
    }

    public static boolean isWifiConnected(WifiInfo wifiInfo) {
        return wifiInfo.getBSSID() != null && wifiInfo.getNetworkId() != -1 &&
                !wifiInfo.getBSSID().equals(Utils.ZERO_MAC);
    }

}
