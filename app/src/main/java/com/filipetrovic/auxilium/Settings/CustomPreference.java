package com.filipetrovic.auxilium.Settings;

import android.content.Context;
import android.support.v7.preference.Preference;
import android.util.AttributeSet;

import com.filipetrovic.auxilium.R;

public abstract class CustomPreference extends Preference {
    public CustomPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public abstract void updatePreferenceView();
}
