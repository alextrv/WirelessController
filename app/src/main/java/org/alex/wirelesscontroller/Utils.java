package org.alex.wirelesscontroller;

import android.app.AlarmManager;
import android.content.Context;

import org.alex.wirelesscontroller.receivers.ScheduleWakefulReceiver;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Utils {

    public static final String LOGGER_FILENAME = "wirelessController.log";

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

    private static FileHandler sFileHandler = null;

    public static void setAutoEnableWifiService(Context context, boolean isOn) {
        int intervalIndex = AppPreferences.getPrefServiceRunInterval(context);
        ScheduleWakefulReceiver.setAutoEnableWifiBroadcast(context, isOn, intervalIndex);
    }

    public static void setForceDisableWirelessService(Context context, boolean isOn) {
        String startTime = AppPreferences.getPrefStartTime(context);
        String endTime = AppPreferences.getPrefEndTime(context);
        ScheduleWakefulReceiver.setAutoDisableRulesBroadcast(context, startTime, endTime, isOn);
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

    public static String getLogFilePath(Context context) {
        File cacheDir = context.getExternalCacheDir();
        return new File(cacheDir, LOGGER_FILENAME).getAbsolutePath();
    }

    public static Logger getLogger(Context context, String name) {
        Logger logger = Logger.getLogger(name);
        try {
            String filePath = getLogFilePath(context);
            if (sFileHandler == null) {
                sFileHandler = new FileHandler(filePath, true);
                sFileHandler.setFormatter(new SimpleFormatter());
            }
            if (AppPreferences.getPrefEnableLogging(context)) {
                sFileHandler.setLevel(Level.INFO);
            } else {
                sFileHandler.setLevel(Level.OFF);
            }
            if (logger.getHandlers().length == 0) {
                logger.addHandler(sFileHandler);
            }
        } catch (IOException e) {
            // do nothing
        }
        return logger;
    }

    public static boolean logFileExists(Context context) {
        File file = new File(getLogFilePath(context));
        return file.exists();
    }

}
