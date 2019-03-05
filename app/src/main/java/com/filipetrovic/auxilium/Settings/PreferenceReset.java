package com.filipetrovic.auxilium.Settings;

import android.content.Context;
import android.support.v7.preference.PreferenceViewHolder;
import android.util.AttributeSet;
import android.view.View;

import com.filipetrovic.auxilium.TunerView.Button;
import com.filipetrovic.auxilium.R;

public class PreferenceReset extends CustomPreference {

    public OnPreferencesResetListener onPreferencesResetListener;

    public PreferenceReset(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PreferenceReset(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setLayoutResource(R.layout.pref_reset);
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        Button button = ((Button) holder.findViewById(R.id.pref_reset_button));

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(onPreferencesResetListener != null) {
                    onPreferencesResetListener.onEvent();
                }
            }
        });
    }

    @Override
    public boolean isSelectable() {
        return false;
    }

    @Override
    public void updatePreferenceView() { }

    public void setOnPreferencesResetListener(OnPreferencesResetListener onPreferencesResetListener) {
        this.onPreferencesResetListener = onPreferencesResetListener;
    }

    public interface OnPreferencesResetListener {
        void onEvent();
    }
}