package org.alex.wirelesscontroller;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

public class SettingsFragment extends PreferenceFragment {

    private SharedPreferences.OnSharedPreferenceChangeListener mPreferenceChangeListener =
            new SharedPreferences.OnSharedPreferenceChangeListener() {
                @Override
                public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                    if (key.equals(AppPreferences.PREF_IS_AUTO_ENABLE_WIFI_ON)) {
                        boolean isOn = sharedPreferences.getBoolean(key, true);
                        Utils.setAutoEnableWifiService(getActivity(), isOn);

                    } else if (key.equals(AppPreferences.PREF_START_TIME)) {
                        Preference preference = findPreference(key);
                        preference.setSummary(sharedPreferences.getString(key, TimePickerFragment.START_TIME_DEFAULT));
                        boolean disableWireless = AppPreferences.getPrefDisableWireless(getActivity());
                        Utils.setForceDisableWirelessService(getActivity(), disableWireless);

                    } else if (key.equals(AppPreferences.PREF_END_TIME)) {
                        Preference preference = findPreference(key);
                        String startTime = AppPreferences.getPrefStartTime(getActivity());
                        String endTime = sharedPreferences.getString(key, TimePickerFragment.END_TIME_DEFAULT);
                        if (TimePickerFragment.isEndTimeNextDay(startTime, endTime)) {
                            String summary = getResources().getString(R.string.end_time_next_day, endTime);
                            preference.setSummary(summary);
                        } else {
                            preference.setSummary(endTime);
                        }
                        boolean disableWireless = AppPreferences.getPrefDisableWireless(getActivity());
                        Utils.setForceDisableWirelessService(getActivity(), disableWireless);

                    } else if (key.equals(AppPreferences.PREF_REPEAT_DISABLE_WIRELESS)) {
                        int value = Integer.parseInt(sharedPreferences.getString(key,
                                getString(R.string.pref_repeatDisableWireless_default)));
                        setListPreferenceSummary(key, value, R.array.pref_repeatDisableWireless_entries);
                        boolean disableWireless = AppPreferences.getPrefDisableWireless(getActivity());
                        Utils.setForceDisableWirelessService(getActivity(), disableWireless);

                    } else if (key.equals(AppPreferences.PREF_SERVICE_RUN_INTERVAL)) {
                        int value = Integer.parseInt(sharedPreferences.getString(key,
                                getString(R.string.pref_serviceRunInterval_default)));
                        setListPreferenceSummary(key, value, R.array.pref_serviceRunInterval_entries);
                        boolean isOn = AppPreferences.getPrefIsAutoEnableWifiOn(getActivity());
                        Utils.setAutoEnableWifiService(getActivity(), isOn);
                    } else if (key.equals(AppPreferences.PREF_DISABLE_WIRELESS)) {
                        boolean disableWireless = sharedPreferences.getBoolean(key, false);
                        Utils.setForceDisableWirelessService(getActivity(), disableWireless);
                    } else if (key.equals(AppPreferences.PREF_ENABLE_LOGGING)) {
                        getActivity().invalidateOptionsMenu();
                    }
                }
            };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);

        setListPreferenceSummary(AppPreferences.PREF_REPEAT_DISABLE_WIRELESS,
                AppPreferences.getPrefRepeatDisableWireless(getActivity()),
                R.array.pref_repeatDisableWireless_entries);

        setListPreferenceSummary(AppPreferences.PREF_SERVICE_RUN_INTERVAL,
                AppPreferences.getPrefServiceRunInterval(getActivity()),
                R.array.pref_serviceRunInterval_entries);
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(mPreferenceChangeListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(mPreferenceChangeListener);
    }

    public void setListPreferenceSummary(String key, int index, int entriesId) {
        Preference preference = findPreference(key);
        String[] prefEntries = getResources().getStringArray(entriesId);
        preference.setSummary(prefEntries[index]);
    }

}
