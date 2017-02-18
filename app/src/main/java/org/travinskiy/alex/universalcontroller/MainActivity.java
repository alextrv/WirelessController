package org.travinskiy.alex.universalcontroller;

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

import java.io.File;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String PLAIN_TEXT_TYPE = "text/plain";
    private static final String EMAIL_SUBJECT = "Wireless Controller log file";
    private static final String DEVELOPER_EMAIL = "alex.trv92@gmail.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        boolean isOn = AppPreferences.getPrefIsAlarmOn(this);
        Utils.setMainService(this, isOn);

        isOn = AppPreferences.getPrefDisableWireless(this);
        Utils.setForceDisableWirelessService(this, isOn);

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
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_send_log:
                if (MyLogger.isLogFileExists(this)) {
                    String logFile = MyLogger.getLoggerFilePath(this);
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
