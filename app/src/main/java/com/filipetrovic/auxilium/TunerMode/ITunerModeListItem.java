package com.filipetrovic.auxilium.TunerMode;

import android.view.LayoutInflater;
import android.view.View;

public interface ITunerModeListItem {
    public int getViewType();
    public View getView(LayoutInflater inflater, View convertView);
}
