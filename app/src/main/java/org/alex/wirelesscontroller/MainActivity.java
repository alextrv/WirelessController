package org.alex.wirelesscontroller;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import org.alex.wirelesscontroller.receivers.ScheduleWakefulReceiver;

import java.io.File;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String PLAIN_TEXT_TYPE = "text/plain";
    private static final String EMAIL_SUBJECT = "Wireless Controller log file";
    private static final String DEVELOPER_EMAIL = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        boolean isPrefOn = AppPreferences.getPrefIsAutoEnableWifiOn(this);
        if (isPrefOn != ScheduleWakefulReceiver.isBroadcastOn(this, Utils.CONNECTION_SERVICE_CODE)) {
            if (isPrefOn) {
                // Send broadcast immediately to start service
                sendBroadcast(ScheduleWakefulReceiver.newIntent(this, Utils.CONNECTION_SERVICE_CODE));
            } else {
                Utils.setAutoEnableWifiService(this, false);
            }
        }

        isPrefOn = AppPreferences.getPrefDisableWireless(this);
        if (isPrefOn && !ScheduleWakefulReceiver.isBroadcastOn(this, Utils.START_TIME_CODE) &&
                !ScheduleWakefulReceiver.isBroadcastOn(this, Utils.END_TIME_CODE)) {
            Utils.setForceDisableWirelessService(this, true);
        } else if (!isPrefOn && (ScheduleWakefulReceiver.isBroadcastOn(this, Utils.START_TIME_CODE) ||
                ScheduleWakefulReceiver.isBroadcastOn(this, Utils.END_TIME_CODE))) {
            Utils.setForceDisableWirelessService(this, false);
        }

        FragmentManager fragmentManager = getFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(android.R.id.content);

        if (fragment == null) {
            fragmentManager.beginTransaction()
                    .replace(android.R.id.content, new SettingsFragment())
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean isLoggingEnabled = AppPreferences.getPrefEnableLogging(this);
        if (isLoggingEnabled) {
            MenuInflater menuInflater = getMenuInflater();
            menuInflater.inflate(R.menu.main_menu, menu);
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_send_log:
                if (Utils.logFileExists(getApplicationContext())) {
                    String logFile = Utils.getLogFilePath(getApplicationContext());
                    Intent emailIntent = new Intent(Intent.ACTION_SEND);
                    emailIntent.setType(PLAIN_TEXT_TYPE);
                    emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{ DEVELOPER_EMAIL });
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, EMAIL_SUBJECT);
                    emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(logFile)));

                    PackageManager packageManager = getPackageManager();
                    List activities = packageManager.queryIntentActivities(emailIntent,
                            PackageManager.MATCH_DEFAULT_ONLY);
                    boolean isIntentSafe = activities.size() > 0;

                    if (isIntentSafe) {
                        startActivity(emailIntent);
                    } else {
                        Toast.makeText(this, R.string.cannot_find_email_app, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, R.string.cannot_find_log_file, Toast.LENGTH_SHORT).show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
