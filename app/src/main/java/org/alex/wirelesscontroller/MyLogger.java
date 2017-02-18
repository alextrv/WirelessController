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

    private MyLogger(){
    }

    public static void writeToFile(String filename, String tag, String separator, Object... args) {

        if (filename == null) {
            Log.i(MY_LOGGER, "No filename to write log");
            return;
        }

        FileWriter out = null;
        PrintWriter printWriter = null;

        try {
            out = new FileWriter(filename, true);
            printWriter = new PrintWriter(out);
            int count = args.length;
            if (count > 0) {
                printWriter.print(sSimpleDateFormat.format(new Date()));
                printWriter.print(" ");
                printWriter.print(tag);
                for (Object value : args) {
                    printWriter.print(separator);
                    printWriter.print(value.toString());
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

    public static void writeToFile(Context context, String tag, String separator, Object... args) {
        writeToFile(getLoggerFilePath(context), tag, separator, args);
    }

    public static String getLoggerFilePath(Context context) {
        File cacheDir = context.getExternalCacheDir();
        if (cacheDir != null) {
            return new File(cacheDir, LOGGER_FILENAME).getAbsolutePath();
        }
        return null;
    }

    public static boolean isLogFileExists(Context context) {
        String filePath = getLoggerFilePath(context);
        if (filePath == null) {
            return false;
        }
        File file = new File(filePath);
        return file.exists();
    }

}
