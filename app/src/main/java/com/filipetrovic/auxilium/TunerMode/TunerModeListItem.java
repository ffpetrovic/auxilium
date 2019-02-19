package com.filipetrovic.auxilium.TunerMode;

import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.filipetrovic.auxilium.R;
import com.filipetrovic.auxilium.TunerUtils.TunerMode;

public class TunerModeListItem implements ITunerModeListItem {
    private final TunerMode mode;

    public TunerModeListItem(TunerMode mode) {
        this.mode = mode;
    }

    @Override
    public int getViewType() {
        return TunerModeArrayAdapter.RowType.LIST_ITEM.ordinal();
    }

    @Override
    public View getView(final LayoutInflater inflater, View convertView) {
        View view;
        if (convertView == null) {
            view = (View) inflater.inflate(R.layout.view_tunings_bottom_sheet_item, null);
            // Do some initialization
        } else {
            view = convertView;
        }

        view.setTag(mode);
        ((TextView) view.findViewById(R.id.tuningName)).setText(mode.name);
        ((TextView) view.findViewById(R.id.tuningName)).setTag(mode);
        ((TextView) view.findViewById(R.id.tuningNotes)).setText(mode.notes);
        return view;
    }
}
