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
import org.alex.wirelesscontroller.R;
import org.alex.wirelesscontroller.Utils;
import org.alex.wirelesscontroller.receivers.ScheduleWakefulReceiver;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.logging.Level;

public class ConnectionService extends IntentService {

    private static final String CLASS_NAME = ConnectionService.class.getName();

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
            Utils.getLogger(context, CLASS_NAME)
                    .log(Level.INFO, "Airplane Mode enabled: {0}", isAirplaneMode);
            ScheduleWakefulReceiver.completeWakefulIntent(intent);
            return;
        }

        // Turn off Bluetooth if preference set true
        if (AppPreferences.getPrefDisableBluetooth(context)) {
            disableBluetooth();
            Utils.getLogger(context, CLASS_NAME)
                    .log(Level.INFO, "Turn off Bluetooth");
        }

        // Try to connect to Wi-Fi network. If fail then disable Wi-Fi
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (!isWifiHotspotEnabled(wifiManager)) {
            if (wifiManager.isWifiEnabled()) {
                disableNotConnectedWifi(wifiManager);
            } else {
                wifiManager.setWifiEnabled(true);
                SystemClock.sleep(Utils.DELAY_TO_WIFI_CONNECT);
                disableNotConnectedWifi(wifiManager);
                boolean enableWhitelist = AppPreferences.getPrefEnableWhitelist(context);

                Utils.getLogger(context, CLASS_NAME)
                        .log(Level.INFO, "White list enabled: {0}", enableWhitelist);

                if (wifiManager.isWifiEnabled() && enableWhitelist) {
                    Set<String> wifiWhitelist = AppPreferences.getPrefWifiWhitelist(context);
                    if (wifiWhitelist != null && !wifiWhitelist.isEmpty()) {
                        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                        String BSSID = wifiInfo.getBSSID();
                        String SSID = wifiInfo.getSSID();
                        String SSID_BSSID = getResources().getString(R.string.ssid_bssid, SSID, BSSID);
                        Utils.getLogger(context, CLASS_NAME)
                                .log(Level.INFO, "SSID_BSSID: {0}", SSID_BSSID);
                        if (BSSID == null || !wifiWhitelist.contains(SSID_BSSID)) {
                            wifiManager.setWifiEnabled(false);
                        }
                    } else {
                        wifiManager.setWifiEnabled(false);
                    }
                }
            }
        }

        Utils.getLogger(context, CLASS_NAME)
                .log(Level.INFO, "Wifi enabled: {0}", wifiManager.isWifiEnabled());
        if (wifiManager.isWifiEnabled()) {
            Utils.getLogger(context, CLASS_NAME)
                    .log(Level.INFO, wifiManager.getConnectionInfo().getSSID());
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

    public static boolean isWifiHotspotEnabled(WifiManager wifiManager) {
        try {
            Method method = wifiManager.getClass().getDeclaredMethod("isWifiApEnabled");
            method.setAccessible(true);
            return (Boolean) method.invoke(wifiManager);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return false;
    }

}
