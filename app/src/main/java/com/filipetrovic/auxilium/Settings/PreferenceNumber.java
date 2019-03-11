package com.filipetrovic.auxilium.Settings;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceViewHolder;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.TextView;

import com.filipetrovic.auxilium.R;

import java.util.ArrayList;

public class PreferenceNumber extends CustomPreference {

    /*  Couldn't get `android:title` and `android:summary` to
        work and actually set the text of the android:id/title
        and android:id/summary so had to use custom app:* attributes.
     */


    protected String mKey;
    protected String mTitle;
    protected String mSummary;
    protected int mNumberStep;
    protected int mNumberMin;
    protected int mNumberMax;
    protected String mLabelFormat;
    private int defaultValue;

    private SeekBar seekBar;



    public PreferenceNumber(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        init(context, attrs);
    }

    public PreferenceNumber(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setLayoutResource(R.layout.pref_number);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.PreferenceNumber);

        int titleResId = ta.getResourceId(R.styleable.PreferenceNumber_title, 0);
        if (titleResId != 0) {
            mTitle = context.getResources().getString(titleResId);
        }

        int summaryResId = ta.getResourceId(R.styleable.PreferenceNumber_summary, 0);
        if (summaryResId != 0) {
            mSummary = context.getResources().getString(summaryResId);
        }

        int stepResId = ta.getResourceId(R.styleable.PreferenceNumber_numberStep, 0);
        if (stepResId != 0) {
            mNumberStep = context.getResources().getInteger(stepResId);
        }
    
        int minResId = ta.getResourceId(R.styleable.PreferenceNumber_numberMin, 0);
        if (minResId != 0) {
            mNumberMin = context.getResources().getInteger(minResId);
        }

        int maxResId = ta.getResourceId(R.styleable.PreferenceNumber_numberMax, 0);
        if (maxResId != 0) {
            mNumberMax = context.getResources().getInteger(maxResId);
        }

        int labelFormatResId = ta.getResourceId(R.styleable.PreferenceNumber_labelFormat, 0);
        if (labelFormatResId != 0) {
            mLabelFormat = context.getResources().getString(labelFormatResId);
        }

        ta.recycle();
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        defaultValue = a.getInt(index, 0);
        return a.getInt(index, 0);
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);

        TextView titleView = ((TextView) holder.findViewById(R.id.title));
        TextView summaryView = ((TextView) holder.findViewById(R.id.summary));
        titleView.setText(mTitle);
        summaryView.setText(mSummary);

        TextView minView = ((TextView) holder.findViewById(R.id.pref_label_min));
        TextView maxView = ((TextView) holder.findViewById(R.id.pref_label_max));
        final TextView valView = ((TextView) holder.findViewById(R.id.pref_label_val));
        seekBar = ((SeekBar) holder.findViewById(R.id.pref_seekbar));

        minView.setText(String.format(mLabelFormat, mNumberMin));
        maxView.setText(String.format(mLabelFormat, mNumberMax));

        seekBar.incrementProgressBy(mNumberStep);
        updatePreferenceView();
        valView.setText(String.format(mLabelFormat, getPersistedInt(defaultValue)));
        seekBar.setMax(mNumberMax - mNumberMin);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                InterpolatedSeekBarResult result =
                        new InterpolatedSeekBarResult(i, mNumberMin, mNumberMax);

                valView.setText(String.format(mLabelFormat, result.val));

                persistInt(result.val);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    public boolean isSelectable() {
        return false;
    }

    @Override
    public void updatePreferenceView() {
        seekBar.setProgress(getPersistedInt(defaultValue) - mNumberMin);
    }


    class InterpolatedSeekBarResult {
        public int min;
        public int max;
        public int val;

        public InterpolatedSeekBarResult(int val, int min, int max) {
            this.min = min;
            this.max = max;

            this.val = val + min;
        }
    }
}
