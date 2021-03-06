package com.adrianczuczka.songle;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Preference class which allows the user to set the timer by hours, minutes and seconds. Contains three input fields, a cancel button, and a confirm
 * button.
 */
public class SetTimerPreference extends DialogPreference {
    private int hours, minutes, seconds, total;
    private EditText editTextHours = null, editTextMinutes = null, editTextSeconds = null;
    private TextView hoursWarning = null, minutesWarning = null, secondsWarning = null;

    public SetTimerPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setDialogLayoutResource(R.layout.pref_set_time);
        setPositiveButtonText("Set");
        setNegativeButtonText("Cancel");
        setDefaultValue(1800000);
        setDialogIcon(null);
    }

    private void setTotal(int total) {
        this.total = total;
        persistInt(total);
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getInt(index, 0);
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        setTotal(restorePersistedValue ?
                getPersistedInt(total) : (int) defaultValue);
        hours = total / 3600000;
        minutes = (total % 3600000) / 60000;
        seconds = (total % 60000) / 1000;
        setSummary("Currently " + format(hours) + ":" + format(minutes) + ":" + format(seconds));
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);
        hoursWarning = view.findViewById(R.id.hours_warning);
        minutesWarning = view.findViewById(R.id.minutes_warning);
        secondsWarning = view.findViewById(R.id.seconds_warning);
        editTextHours = view.findViewById(R.id.set_time_hours);
        editTextMinutes = view.findViewById(R.id.set_time_minutes);
        editTextSeconds = view.findViewById(R.id.set_time_seconds);
        editTextSeconds.setFilters(new InputFilter[]{new MinMaxFilter(0, 59, "editTextSeconds")});
        editTextMinutes.setFilters(new InputFilter[]{new MinMaxFilter(0, 59, "editTextMinutes")});
        editTextHours.setFilters(new InputFilter[]{new MinMaxFilter(0, 99, "editTextHours")});
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);
        if(positiveResult){
            if(editTextHours.getText().toString().equals("")){
                hours = 0;
            } else{
                hours = Integer.parseInt(editTextHours.getText().toString());
            }
            if(editTextMinutes.getText().toString().equals("")){
                minutes = 0;
            } else{
                minutes = Integer.parseInt(editTextMinutes.getText().toString());
            }
            if(editTextSeconds.getText().toString().equals("")){
                seconds = 0;
            } else{
                seconds = Integer.parseInt(editTextSeconds.getText().toString());
            }
            total = (hours * 3600000) + (minutes * 60000) + (seconds * 1000);
            setSummary("Currently " + format(hours) + ":" + format(minutes) + ":" + format(seconds));
            if(callChangeListener(total)){
                setTotal(total);
                setDefaultValue(total);
            }
        }
    }

    private String format(int amount) {
        if(amount < 10){
            return "0" + String.valueOf(amount);
        } else{
            return String.valueOf(amount);
        }
    }

    public class MinMaxFilter implements InputFilter {

        private final int mIntMin;
        private final int mIntMax;
        private final String view;

        public MinMaxFilter(int minValue, int maxValue, String view) {
            this.mIntMin = minValue;
            this.mIntMax = maxValue;
            this.view = view;
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            try{
                int input = Integer.parseInt(dest.toString() + source.toString());
                if(isInRange(mIntMin, mIntMax, input)){
                    hoursWarning.setVisibility(View.GONE);
                    minutesWarning.setVisibility(View.GONE);
                    secondsWarning.setVisibility(View.GONE);
                    return null;
                }
            } catch(NumberFormatException ignored){
            }
            switch(view){
                case "editTextHours":
                    hoursWarning.setVisibility(View.VISIBLE);
                    break;
                case "editTextMinutes":
                    minutesWarning.setVisibility(View.VISIBLE);
                    break;
                case "editTextSeconds":
                    secondsWarning.setVisibility(View.VISIBLE);
                    break;
            }
            return "";
        }

        private boolean isInRange(int a, int b, int c) {
            return b > a ? c >= a && c <= b : c >= b && c <= a;
        }
    }
}
