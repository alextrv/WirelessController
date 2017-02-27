package org.alex.wirelesscontroller.receivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.SystemClock;
import android.support.v4.content.WakefulBroadcastReceiver;

import org.alex.wirelesscontroller.AppPreferences;
import org.alex.wirelesscontroller.MyLogger;
import org.alex.wirelesscontroller.TimePickerFragment;
import org.alex.wirelesscontroller.Utils;
import org.alex.wirelesscontroller.services.AutoDisableRulesService;
import org.alex.wirelesscontroller.services.ConnectionService;

import java.util.Calendar;

public class ScheduleWakefulReceiver extends WakefulBroadcastReceiver {

    private static final String TAG = "ScheduleWakefulReceiver";

    public static Intent newIntent(Context context, int requestCode) {
        Intent intent = new Intent(context, ScheduleWakefulReceiver.class);
        intent.putExtra(Utils.REQUEST_CODE, requestCode);
        return intent;
    }

    public static boolean isBroadcastOn(Context context, int requestCode) {
        Intent intent = newIntent(context, requestCode);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode,
                intent, PendingIntent.FLAG_NO_CREATE);
        return pendingIntent != null;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        int requestCode = intent.getIntExtra(Utils.REQUEST_CODE, 0);

        Intent serviceIntent = null;
        if (requestCode == Utils.CONNECTION_SERVICE_CODE) {
            serviceIntent = ConnectionService.newIntent(context, requestCode);
        } else if (requestCode == Utils.START_TIME_CODE || requestCode == Utils.END_TIME_CODE) {
            serviceIntent = AutoDisableRulesService.newIntent(context, requestCode);
        }

        if (serviceIntent != null) {
            startWakefulService(context, serviceIntent);
        }
    }

    public static void setAutoDisableRulesBroadcast(Context context, String startTime, String endTime,
                                                    boolean turnOn) {

        MyLogger.getInstance(context).writeToFile(TAG, Utils.SEPARATOR, "setAutoDisableRulesBroadcast",
                startTime, endTime, turnOn);

        // Intent for start time
        Intent startTimeIntent = newIntent(context, Utils.START_TIME_CODE);
        PendingIntent pIntentStartTime = PendingIntent.getBroadcast(context, Utils.START_TIME_CODE,
                startTimeIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        // Intent for end time
        Intent endTimeIntent = newIntent(context, Utils.END_TIME_CODE);
        PendingIntent pIntentEndTime = PendingIntent.getBroadcast(context, Utils.END_TIME_CODE,
                endTimeIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (turnOn) {

            // Calendar for start time
            Calendar calendarStart = Utils.getTimeCalendar(startTime, 0);

            // Calendar for end time
            Calendar calendarEnd;
            if (TimePickerFragment.isEndTimeNextDay(startTime, endTime)) {
                calendarEnd = Utils.getTimeCalendar(endTime, Utils.TWENTY_FOUR_HOURS);
            } else {
                calendarEnd = Utils.getTimeCalendar(endTime, 0);
            }

            // If was set past time then set time for next day
            if (System.currentTimeMillis() > calendarStart.getTimeInMillis() &&
                    System.currentTimeMillis() > calendarEnd.getTimeInMillis()) {
                calendarStart.setTimeInMillis(calendarStart.getTimeInMillis() + Utils.TWENTY_FOUR_HOURS);
                calendarEnd.setTimeInMillis(calendarEnd.getTimeInMillis() + Utils.TWENTY_FOUR_HOURS);
            }

            // If start time not coming yet
            if (calendarStart.getTimeInMillis() > System.currentTimeMillis()) {
                AppPreferences.setPrefSuspendAutoEnableWifi(context, false);
            }

            // Setting alarm time
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                // Alarm for startTime
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendarStart.getTimeInMillis(),
                        pIntentStartTime);

                // Alarm for endTime
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendarEnd.getTimeInMillis(),
                        pIntentEndTime);

            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

                // Alarm for startTime
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendarStart.getTimeInMillis(),
                        pIntentStartTime);

                // Alarm for endTime
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendarEnd.getTimeInMillis(),
                        pIntentEndTime);

            } else {

                // Alarm for startTime
                alarmManager.set(AlarmManager.RTC_WAKEUP, calendarStart.getTimeInMillis(),
                        pIntentStartTime);

                // Alarm for endTime
                alarmManager.set(AlarmManager.RTC_WAKEUP, calendarEnd.getTimeInMillis(),
                        pIntentEndTime);

            }

        } else {
            alarmManager.cancel(pIntentStartTime);
            pIntentStartTime.cancel();
            alarmManager.cancel(pIntentEndTime);
            pIntentEndTime.cancel();
            AppPreferences.setPrefSuspendAutoEnableWifi(context, false);
        }
    }

    public static void setAutoEnableWifiBroadcast(Context context, boolean turnOn, int runIntervalIndex) {

        MyLogger.getInstance(context).writeToFile(TAG, Utils.SEPARATOR, "setAutoEnableWifiBroadcast", turnOn, runIntervalIndex);

        Intent intent = ScheduleWakefulReceiver.newIntent(context, Utils.CONNECTION_SERVICE_CODE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, Utils.CONNECTION_SERVICE_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (turnOn) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                alarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                        SystemClock.elapsedRealtime() + Utils.RUN_INTERVAL[runIntervalIndex], pendingIntent);
            } else {
                alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                        SystemClock.elapsedRealtime() + Utils.RUN_INTERVAL[runIntervalIndex], pendingIntent);
            }
        } else {
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();
        }
    }

}
