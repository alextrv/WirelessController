package org.travinskiy.alex.universalcontroller;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.CheckBox;
import android.widget.CompoundButton;

public class MainActivity extends AppCompatActivity {

    private CheckBox mStartServiceCheckBox;
    private CheckBox mStopInAirplaneModeCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mStartServiceCheckBox = (CheckBox) findViewById(R.id.start_service_checkbox);
        mStopInAirplaneModeCheckBox = (CheckBox) findViewById(R.id.stop_in_airplane_mode);

        mStartServiceCheckBox.setChecked(ConnectionService.isServiceOn(this));

        mStartServiceCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ConnectionService.setServiceAlarm(MainActivity.this, isChecked);
            }
        });

        boolean stopInAirplaneMode = AppPreferences.getPrefStopInAirplaneMode(getApplicationContext());
        mStopInAirplaneModeCheckBox.setChecked(stopInAirplaneMode);

        mStopInAirplaneModeCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                AppPreferences.setPrefStopInAirplaneMode(getApplicationContext(), isChecked);
            }
        });

    }
}
