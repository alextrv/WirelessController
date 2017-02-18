package org.alex.wirelesscontroller;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Build;

import java.util.Calendar;

public class DisableWireless extends IntentService {

    public static final int ONE_TIME = 0;
    public static final int EVERY_DAY = 1;

    public static final int START_TIME_CODE = 1;
    public static final int END_TIME_CODE = 2;

    public static final long TWENTY_FOUR_HOURS = 1000 * 60 * 60 * 24;

    public static final String REQUEST_CODE = "requestCode";

    private static final String TAG = "ForceDisableWireless";

    public DisableWireless() {
        super(null);
    }

    public static Intent newIntent(Context context, int requestCode) {
        Intent intent = new Intent(context, DisableWireless.class);
        intent.putExtra(REQUEST_CODE, requestCode);
        return intent;
    }

    public static void setServiceAlarm(Context context, String startTime, String endTime, boolean turnOn) {

        MyLogger.writeToFile(context, TAG, Utils.SEPARATOR, "setServiceAlarm", startTime, endTime, turnOn);

        // Intent for start time
        Intent startTimeIntent = newIntent(context, START_TIME_CODE);
        PendingIntent pIntentStartTime = PendingIntent.getService(context, START_TIME_CODE, startTimeIntent, 0);

        // Intent for end time
        Intent endTimeIntent = newIntent(context, END_TIME_CODE);
        PendingIntent pIntentEndTime = PendingIntent.getService(context, END_TIME_CODE, endTimeIntent, 0);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (turnOn) {

            // Alarm for start time
            int startHour = TimePickerFragment.getHourOfDay(startTime);
            int startMinute = TimePickerFragment.getMinute(startTime);
            Calendar calendarStart = Calendar.getInstance();
            calendarStart.setTimeInMillis(System.currentTimeMillis());
            calendarStart.set(Calendar.HOUR_OF_DAY, startHour);
            calendarStart.set(Calendar.MINUTE, startMinute);

            // Alarm for end time
            int endHour = TimePickerFragment.getHourOfDay(endTime);
            int endMinute = TimePickerFragment.getMinute(endTime);
            Calendar calendarEnd = Calendar.getInstance();
            if (TimePickerFragment.isEndTimeNextDay(startTime, endTime)) {
                calendarEnd.setTimeInMillis(System.currentTimeMillis() + TWENTY_FOUR_HOURS);
            } else {
                calendarEnd.setTimeInMillis(System.currentTimeMillis());
            }
            calendarEnd.set(Calendar.HOUR_OF_DAY, endHour);
            calendarEnd.set(Calendar.MINUTE, endMinute);

            // If was set past time then set time for next day
            if (System.currentTimeMillis() > calendarStart.getTimeInMillis() &&
                    System.currentTimeMillis() > calendarEnd.getTimeInMillis()) {
                calendarStart.setTimeInMillis(calendarStart.getTimeInMillis() + TWENTY_FOUR_HOURS);
                calendarEnd.setTimeInMillis(calendarEnd.getTimeInMillis() + TWENTY_FOUR_HOURS);
            }

            // If start time not coming yet
            if (calendarStart.getTimeInMillis() > System.currentTimeMillis()) {
                AppPreferences.setPrefForceDisabledWireless(context, false);
            }

            if (AppPreferences.getPrefRepeatDisableWireless(context) == EVERY_DAY) {
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendarStart.getTimeInMillis(),
                        AlarmManager.INTERVAL_DAY, pIntentStartTime);

                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendarEnd.getTimeInMillis(),
                        AlarmManager.INTERVAL_DAY, pIntentEndTime);
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

                    // Alarm for startTime
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendarStart.getTimeInMillis(),
                            pIntentStartTime);

                    // Alarm for EndTime
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendarEnd.getTimeInMillis(),
                            pIntentEndTime);
                } else {

                    // Alarm for StartTime
                    alarmManager.set(AlarmManager.RTC_WAKEUP, calendarStart.getTimeInMillis(),
                            pIntentStartTime);

                    // Alarm for EndTime
                    alarmManager.set(AlarmManager.RTC_WAKEUP, calendarEnd.getTimeInMillis(),
                            pIntentEndTime);
                }
            }

        } else {
            alarmManager.cancel(pIntentStartTime);
            pIntentStartTime.cancel();
            alarmManager.cancel(pIntentEndTime);
            pIntentEndTime.cancel();
        }
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        int requestCode = intent.getIntExtra(REQUEST_CODE, -1);

        MyLogger.writeToFile(getApplicationContext(), TAG, Utils.SEPARATOR, "REQUEST_CODE", requestCode);

        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);

        if (requestCode == START_TIME_CODE) {

            // Turn off Wi-Fi
            if (wifiManager.isWifiEnabled()) {
                wifiManager.setWifiEnabled(false);
            }

            // Turn off Bluetooth
            ConnectionService.disableBluetooth();

            AppPreferences.setPrefForceDisabledWireless(getApplicationContext(), true);
        } else if (requestCode == END_TIME_CODE) {
            AppPreferences.setPrefForceDisabledWireless(getApplicationContext(), false);
            int repeat = AppPreferences.getPrefRepeatDisableWireless(getApplicationContext());
            if (repeat == ONE_TIME) {
                AppPreferences.setPrefDisableWireless(getApplicationContext(), false);
                Utils.setForceDisableWirelessService(getApplicationContext(), false);
            }
        }

        MyLogger.writeToFile(getApplicationContext(), TAG, Utils.SEPARATOR,
                "Is Wifi enabled", wifiManager.isWifiEnabled());
    }
}
