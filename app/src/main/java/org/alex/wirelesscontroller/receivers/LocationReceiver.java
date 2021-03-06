package org.alex.wirelesscontroller.receivers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;

import org.alex.wirelesscontroller.AppPreferences;
import org.alex.wirelesscontroller.R;

public class LocationReceiver extends BroadcastReceiver {

    public static final int NOTIFICATION_ID = 2;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!AppPreferences.getPrefLocationNotification(context)) {
            return;
        }
        String action = intent.getAction();
        if (LocationManager.PROVIDERS_CHANGED_ACTION.equals(action)) {
            manageLocation(context);
        }
    }

    public static void manageLocation(Context context) {
        Context appContext = context.getApplicationContext();
        LocationManager locationManager =
                (LocationManager) appContext.getSystemService(Context.LOCATION_SERVICE);
        NotificationManager nManager =
                (NotificationManager) appContext.getSystemService(Context.NOTIFICATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Intent locationIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            PendingIntent pendingIntent = PendingIntent.getActivity(appContext, 0, locationIntent, 0);
            Notification notification = new NotificationCompat.Builder(appContext)
                    .setSmallIcon(android.R.drawable.ic_dialog_info)
                    .setContentTitle(appContext.getString(R.string.location_notification_title))
                    .setContentText(appContext.getString(R.string.location_notification_content))
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(appContext.getString(R.string.location_notification_content)))
                    .setContentIntent(pendingIntent)
                    .setOngoing(true)
                    .setAutoCancel(false)
                    .build();
            nManager.notify(NOTIFICATION_ID, notification);
        } else {
            nManager.cancel(NOTIFICATION_ID);
        }
    }
}
