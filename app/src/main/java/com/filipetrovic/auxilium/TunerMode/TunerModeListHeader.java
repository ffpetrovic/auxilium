package com.filipetrovic.auxilium.TunerMode;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.filipetrovic.auxilium.R;
import com.filipetrovic.auxilium.TunerUtils.TunerMode;

public class TunerModeListHeader implements ITunerModeListItem {
    private final String groupName;

    public TunerModeListHeader(String groupName) {
        this.groupName = groupName;
    }

    @Override
    public int getViewType() {
        return TunerModeArrayAdapter.RowType.LIST_ITEM.ordinal();
    }

    @Override
    public View getView(final LayoutInflater inflater, View convertView) {
        View view;
        if (convertView == null) {
            view = (View) inflater.inflate(R.layout.view_tunings_bottom_sheet_title, null);
            // Do some initialization
        } else {
            view = convertView;
        }

        ((TextView) view).setText(groupName);
        return view;
    }
}

