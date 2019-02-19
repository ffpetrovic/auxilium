package com.filipetrovic.auxilium.TunerMode;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.filipetrovic.auxilium.R;

import java.util.List;

// https://stackoverflow.com/a/13634801/2551834

public class TunerModeArrayAdapter extends ArrayAdapter<ITunerModeListItem> {
    private LayoutInflater mInflater;
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_SEPARATOR = 1;

    public enum RowType {
        LIST_ITEM, HEADER_ITEM
    }

    public TunerModeArrayAdapter(Context context, List<ITunerModeListItem> items) {
        super(context, 0, items);
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getViewTypeCount() {
        return RowType.values().length;
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).getViewType();
    }

    public View getView(int position, View convertView, ViewGroup parent)  {
        ViewHolder holder = null;
        int rowType = getItemViewType(position);
        View View;
        if (convertView == null) {
            holder = new ViewHolder();
            switch (rowType) {
                case TYPE_ITEM:
                    convertView = mInflater.inflate(R.layout.view_tunings_bottom_sheet_item, null);
                    holder.view=getItem(position).getView(mInflater, convertView);
                    break;
                case TYPE_SEPARATOR:
                    convertView = mInflater.inflate(R.layout.view_tunings_bottom_sheet_title, null);
                    holder.view=getItem(position).getView(mInflater, convertView);
                    break;
            }
            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }
        return convertView;
    }

    public static class ViewHolder {
        public  View view;
    }
}
