package org.alex.wirelesscontroller;

import android.app.AlarmManager;
import android.content.Context;

import org.alex.wirelesscontroller.receivers.ScheduleWakefulReceiver;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

public class Utils {

    public static final String SEPARATOR = ": ";

    public static final int ONE_TIME = 0;
    public static final int EVERY_DAY = 1;

    // Request codes for broadcast
    public static final int START_TIME_CODE = 1;
    public static final int END_TIME_CODE = 2;
    public static final int CONNECTION_SERVICE_CODE = 3;

    public static final long TWENTY_FOUR_HOURS = 1000 * 60 * 60 * 24;

    public static final long[] RUN_INTERVAL = {
            AlarmManager.INTERVAL_FIFTEEN_MINUTES,
            AlarmManager.INTERVAL_HALF_HOUR,
            AlarmManager.INTERVAL_HOUR
    };

    public static final long DELAY_TO_WIFI_CONNECT = 15 * 1000;

    public static final String ZERO_MAC = "00:00:00:00:00:00";

    public static final String REQUEST_CODE = "requestCode";

    public static void setAutoEnableWifiService(Context context, boolean isOn) {
        int intervalIndex = AppPreferences.getPrefServiceRunInterval(context);
        ScheduleWakefulReceiver.setAutoEnableWifiBroadcast(context, isOn, intervalIndex);
    }

    public static void setForceDisableWirelessService(Context context, boolean isOn) {
        String startTime = AppPreferences.getPrefStartTime(context);
        String endTime = AppPreferences.getPrefEndTime(context);
        ScheduleWakefulReceiver.setAutoDisableRulesBroadcast(context, startTime, endTime, isOn);
    }

    public static Set<String> deepSetCopy(Set<String> original) {
        Set<String> copy = new HashSet<>(original.size());
        for (String element : original) {
            copy.add(element);
        }
        return copy;
    }

    public static Calendar getTimeCalendar(String time, long appendTimeMillis) {
        Calendar calendar = Calendar.getInstance();
        int hour = TimePickerFragment.getHourOfDay(time);
        int minute = TimePickerFragment.getMinute(time);
        calendar.setTimeInMillis(System.currentTimeMillis() + appendTimeMillis);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        return calendar;
    }

}
