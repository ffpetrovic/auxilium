package com.filipetrovic.auxilium.Settings;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceViewHolder;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.filipetrovic.auxilium.R;

import java.util.ArrayList;

public class PreferenceChoice extends CustomPreference {

    /*  Couldn't get `android:title` and `android:summary` to
        work and actually set the text of the android:id/title
        and android:id/summary so had to use custom app:* attributes.
     */


    protected String mKey;
    protected String mTitle;
    protected String mSummary;
    protected String[] mEntries = new String[0];
    protected String[] mEntryValues = new String[0];

    private String defaultValue;

    private PreferenceChoiceAdapter preferenceChoiceAdapter;

    public PreferenceChoice(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        init(context, attrs);
    }

    public PreferenceChoice(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setLayoutResource(R.layout.pref_choice);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.PreferenceChoice);

        int entriesResId = ta.getResourceId(R.styleable.PreferenceChoice_entries, 0);
        if (entriesResId != 0) {
            mEntries = context.getResources().getStringArray(entriesResId);
        }
        int valuesResId = ta.getResourceId(R.styleable.PreferenceChoice_entryValues, 0);
        if (valuesResId != 0) {
            mEntryValues = context.getResources().getStringArray(valuesResId);
        }

        int titleResId = ta.getResourceId(R.styleable.PreferenceChoice_title, 0);
        if (titleResId != 0) {
            mTitle = context.getResources().getString(titleResId);
        }

        int summaryResId = ta.getResourceId(R.styleable.PreferenceChoice_summary, 0);
        if (summaryResId != 0) {
            mSummary = context.getResources().getString(summaryResId);
        }

        ta.recycle();
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        defaultValue = a.getString(index);
        return a.getString(index);
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);

        ((TextView) holder.findViewById(R.id.title)).setText(mTitle);
        ((TextView) holder.findViewById(R.id.summary)).setText(mSummary);

        ArrayList<PreferenceChoiceItem> itemList = new ArrayList<>();
        for(int i = 0; i < mEntries.length; i++) {
            itemList.add(new PreferenceChoiceItem(mEntries[i], mEntryValues[i], i));
        }

        preferenceChoiceAdapter = new PreferenceChoiceAdapter(R.layout.pref_choice_item, itemList);
        final RecyclerView recyclerView = (RecyclerView) holder.findViewById(R.id.prefRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(preferenceChoiceAdapter);

        preferenceChoiceAdapter.prefValue = getPrefValue();
        preferenceChoiceAdapter.mListener = new OnPrefChoiceSelectedListener() {
            @Override
            public void onEvent(PreferenceChoiceItem preferenceChoiceItem) {
                persistString(preferenceChoiceItem.getValue());
                preferenceChoiceAdapter.prefValue = getPrefValue();
                preferenceChoiceAdapter.notifyDataSetChanged();
            }
        };
    }

    private String getPrefValue() {
        return getPersistedString(defaultValue);
    }

    @Override
    public boolean isSelectable() {
        return false;
    }

    @Override
    public void updatePreferenceView() {
        preferenceChoiceAdapter.prefValue = getPrefValue();
        preferenceChoiceAdapter.notifyDataSetChanged();
    }

    public interface OnPrefChoiceSelectedListener {
        void onEvent(PreferenceChoiceItem preferenceChoiceItem);
    }
}
