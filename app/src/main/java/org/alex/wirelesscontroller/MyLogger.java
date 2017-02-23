package org.alex.wirelesscontroller;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MyLogger {

    private static final String MY_LOGGER = "myLogger";

    public static final String LOGGER_FILENAME = "wirelessController.log";

    private static SimpleDateFormat sSimpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss",
            Locale.getDefault());

    private static MyLogger sMyLogger;

    private Context mContext;
    private String mLogFilePath;

    public static MyLogger getInstance(Context context) {
        if (sMyLogger == null) {
            sMyLogger = new MyLogger(context);
        }
        return sMyLogger;
    }

    private MyLogger(Context context) {
        mContext = context.getApplicationContext();
        mLogFilePath = generateFilePath();
    }

    public void writeToFile(String tag, String separator, Object... args) {

        if (!AppPreferences.getPrefEnableLogging(mContext)) {
            Log.i(MY_LOGGER, "Logging is disabled");
            return;
        }

        if (mLogFilePath == null) {
            Log.i(MY_LOGGER, "No filename to write log");
            return;
        }

        FileWriter out;
        PrintWriter printWriter = null;

        try {
            out = new FileWriter(mLogFilePath, true);
            printWriter = new PrintWriter(out);
            int count = args.length;
            if (count > 0) {
                printWriter.print(sSimpleDateFormat.format(new Date()));
                printWriter.print(" ");
                printWriter.print(tag);
                for (Object value : args) {
                    if (value != null) {
                        printWriter.print(separator);
                        printWriter.print(value.toString());
                    }
                }
            }
            printWriter.println();
            out.close();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        } finally {
            if (printWriter != null) {
                printWriter.close();
            }
        }

    }

    private String generateFilePath() {
        File cacheDir = mContext.getExternalCacheDir();
        if (cacheDir != null) {
            return new File(cacheDir, LOGGER_FILENAME).getAbsolutePath();
        }
        return null;
    }

    public boolean isLogFileExists() {
        if (mLogFilePath == null) {
            return false;
        }
        File file = new File(mLogFilePath);
        return file.exists();
    }

    public String getLogFilePath() {
        return mLogFilePath;
    }
}
