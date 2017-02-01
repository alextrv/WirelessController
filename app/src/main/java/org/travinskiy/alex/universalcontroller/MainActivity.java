package org.travinskiy.alex.universalcontroller;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private CheckBox mStartServiceCheckBox;
    private CheckBox mStopInAirplaneModeCheckBox;
    private CheckBox mEnableWhitelistCheckbox;
    private Button mAddToWhitelistButton;
    private Button mShowWhitelistButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mStartServiceCheckBox = (CheckBox) findViewById(R.id.start_service_checkbox);
        mStopInAirplaneModeCheckBox = (CheckBox) findViewById(R.id.stop_in_airplane_mode);
        mEnableWhitelistCheckbox = (CheckBox) findViewById(R.id.enable_whitelist);

        mAddToWhitelistButton = (Button) findViewById(R.id.add_wifi_to_whitelist);
        mShowWhitelistButton = (Button) findViewById(R.id.show_whitelist);

        mStartServiceCheckBox.setChecked(ConnectionService.isServiceOn(this));

        mStartServiceCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ConnectionService.setServiceAlarm(MainActivity.this, isChecked);
            }
        });

        boolean stopInAirplaneMode = AppPreferences.getPrefStopInAirplaneMode(getApplicationContext());
        mStopInAirplaneModeCheckBox.setChecked(stopInAirplaneMode);

        mStopInAirplaneModeCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                AppPreferences.setPrefStopInAirplaneMode(getApplicationContext(), isChecked);
            }
        });

        boolean enableWhitelist = AppPreferences.getPrefEnableWhitelist(getApplicationContext());
        mEnableWhitelistCheckbox.setChecked(enableWhitelist);

        mEnableWhitelistCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                AppPreferences.setPrefEnableWhitelist(getApplicationContext(), isChecked);
            }
        });

        mAddToWhitelistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
                if (wifiManager.isWifiEnabled()) {
                    WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                    if (wifiInfo.getBSSID() != null) {
                        Set<String> wifiWhitelist = AppPreferences.getPrefWifiWhitelist(getApplicationContext());
                        if (wifiWhitelist == null) {
                            wifiWhitelist = new HashSet<>();
                        }
                        String SSID = wifiInfo.getSSID();
                        String BSSID = wifiInfo.getBSSID();
                        String SSID_BSSID = getResources().getString(R.string.ssid_bssid, SSID, BSSID);
                        boolean isAdded = wifiWhitelist.add(SSID_BSSID);
                        if (isAdded) {
                            AppPreferences.setPrefWifiWhitelist(getApplicationContext(), wifiWhitelist);
                            Toast.makeText(getApplicationContext(), R.string.added_to_whitelist, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), R.string.already_in_whitelist, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), R.string.connect_to_wifi, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), R.string.enable_wifi, Toast.LENGTH_SHORT).show();
                }
            }
        });

        mShowWhitelistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = WhitelistActivity.newIntent(getApplicationContext());
                startActivity(intent);
            }
        });

    }
}
