package org.travinskiy.alex.universalcontroller;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

public class ConnectionService extends IntentService {

    private static final int RUN_INTERVAL = 60 * 1000;

    public static Intent newIntent(Context context) {
        return new Intent(context, ConnectionService.class);
    }

    public ConnectionService() {
        super(null);
    }

    public static boolean isServiceOn(Context context) {
        Intent intent = newIntent(context);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_NO_CREATE);
        return pendingIntent != null;
    }

    public static void setServiceAlarm(Context context, boolean turnOn) {
        Intent intent = newIntent(context);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, 0);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (turnOn) {
            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(),
                    RUN_INTERVAL, pendingIntent);
        } else {
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();
        }
    }

    @Override
    protected void onHandleIntent(Intent intent) {
    }
}
