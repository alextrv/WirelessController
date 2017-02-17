package org.travinskiy.alex.universalcontroller;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        boolean isOn = AppPreferences.getPrefIsAlarmOn(this);
        Utils.setMainService(this, isOn);

        isOn = AppPreferences.getPrefDisableWireless(this);
        Utils.setForceDisableWirelessService(this, isOn);

        FragmentManager fragmentManager = getFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(android.R.id.content);

        if (fragment == null) {
            fragmentManager.beginTransaction()
                    .replace(android.R.id.content, new SettingsFragment())
                    .commit();
        }
    }

}
