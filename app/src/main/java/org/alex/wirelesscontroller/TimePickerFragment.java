package org.alex.wirelesscontroller;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.DialogPreference;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TimePicker;

public class TimePickerFragment extends DialogPreference {

    public static final String START_TIME_DEFAULT = "23:00";
    public static final String END_TIME_DEFAULT = "07:00";

    private int mHourOfDay;
    private int mMinute;
    private TimePicker mTimePicker;

    private boolean mIsDialogOpened;

    private final String DEFAULT_VALUE =
            getKey().equals(AppPreferences.PREF_START_TIME) ? START_TIME_DEFAULT : END_TIME_DEFAULT;

    public TimePickerFragment(Context context, AttributeSet attrs) {
        super(context, attrs);

        setNegativeButtonText(android.R.string.cancel);
        setPositiveButtonText(android.R.string.ok);
    }

    public static int getHourOfDay(String time) {
        return Integer.parseInt(time.split(":")[0]);
    }

    public static int getMinute(String time) {
        return Integer.parseInt(time.split(":")[1]);
    }

    public static String getStringTime(int hour, int minute) {
        return String.format("%02d:%02d", hour, minute);
    }

    public void setTimePickerTime() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mTimePicker.setHour(mHourOfDay);
            mTimePicker.setMinute(mMinute);
        } else {
            mTimePicker.setCurrentHour(mHourOfDay);
            mTimePicker.setCurrentMinute(mMinute);
        }
    }

    public void setHourAndMinute() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mHourOfDay = mTimePicker.getHour();
            mMinute = mTimePicker.getMinute();
        } else {
            mHourOfDay = mTimePicker.getCurrentHour();
            mMinute = mTimePicker.getCurrentMinute();
        }
    }

    @Override
    protected View onCreateDialogView() {
        mTimePicker = new TimePicker(getContext());
        mTimePicker.setIs24HourView(true);
        mIsDialogOpened = true;
        return mTimePicker;
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);

        setTimePickerTime();
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {

            setHourAndMinute();

            String time = getStringTime(mHourOfDay, mMinute);
            persistString(time);
        }
        onSetInitialValue(true, null);
        mIsDialogOpened = false;
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getString(index);
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        String time;

        if (restorePersistedValue) {
            if (defaultValue == null) {
                time = getPersistedString(DEFAULT_VALUE);
            } else {
                time = getPersistedString(defaultValue.toString());
            }
        } else {
            time = defaultValue.toString();
        }

        mHourOfDay = getHourOfDay(time);
        mMinute = getMinute(time);

    }

    @Override
    public CharSequence getSummary() {
        if (getKey().equals(AppPreferences.PREF_END_TIME)) {
            String startTime = AppPreferences.getPrefStartTime(getContext());
            String endTime = getPersistedString(DEFAULT_VALUE);
            if (isEndTimeNextDay(startTime, endTime)) {
                return getContext().getResources().getString(R.string.end_time_next_day, endTime);
            }
        }
        return getPersistedString(DEFAULT_VALUE);
    }

    public static boolean isEndTimeNextDay(String startTime, String endTime) {
        int startHour = getHourOfDay(startTime);
        int startMinute = getMinute(startTime);
        int endHour = getHourOfDay(endTime);
        int endMinute = getMinute(endTime);
        return (startHour > endHour) || (startHour == endHour && startMinute >= endMinute);
    }

    private static class SavedState extends Preference.BaseSavedState {

        String mValue;

        public SavedState(Parcelable superState) {
            super(superState);
        }

        public SavedState(Parcel source) {
            super(source);

            mValue = source.readString();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);

            dest.writeString(mValue);
        }

        public static final Parcelable.Creator<SavedState> CREATOR =
                new Creator<SavedState>() {
                    @Override
                    public SavedState createFromParcel(Parcel source) {
                        return new SavedState(source);
                    }

                    @Override
                    public SavedState[] newArray(int size) {
                        return new SavedState[size];
                    }
                };

    }

    @Override
    protected Parcelable onSaveInstanceState() {
        final Parcelable superState = super.onSaveInstanceState();
        if (!mIsDialogOpened) {
            return superState;
        }

        SavedState myState = new SavedState(superState);
        setHourAndMinute();
        myState.mValue = getStringTime(mHourOfDay, mMinute);
        return myState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state == null || !state.getClass().equals(SavedState.class)) {
            super.onRestoreInstanceState(state);
            return;
        }

        SavedState myState = (SavedState) state;
        super.onRestoreInstanceState(myState.getSuperState());

        mHourOfDay = getHourOfDay(myState.mValue);
        mMinute = getMinute(myState.mValue);

        setTimePickerTime();

    }

}
