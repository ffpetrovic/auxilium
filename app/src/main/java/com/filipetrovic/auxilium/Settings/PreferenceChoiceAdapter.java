package com.filipetrovic.auxilium.Settings;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.filipetrovic.auxilium.R;

import java.util.ArrayList;

public class PreferenceChoiceAdapter extends RecyclerView.Adapter<PreferenceChoiceAdapter.ViewHolder> {

    //All methods in this adapter are required for a bare minimum recyclerview adapter
    private int listItemLayout;
    private ArrayList<PreferenceChoiceItem> itemList;
    public PreferenceChoice.OnPrefChoiceSelectedListener mListener;
    public String prefValue;

    // Constructor of the class
    public PreferenceChoiceAdapter(int layoutId, ArrayList<PreferenceChoiceItem> itemList) {
        listItemLayout = layoutId;
        this.itemList = itemList;
    }

    // get the size of the list
    @Override
    public int getItemCount() {
        return itemList == null ? 0 : itemList.size();
    }


    // specify the row layout file and click for each row
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(listItemLayout, parent, false);
        ViewHolder myViewHolder = new ViewHolder(view);
        return myViewHolder;
    }

    // load data in each row element
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int listPosition) {
        final PreferenceChoiceItem currentItem = itemList.get(holder.getAdapterPosition());

        if(prefValue.equals(currentItem.getValue())) {
            holder.root.setBackground(holder.root.getContext().getDrawable(R.drawable.pref_item_bg_active));
            holder.item.setTextColor(holder.root.getContext().getColor(R.color.colorGreen));
        } else {
            holder.root.setBackground(holder.root.getContext().getDrawable(R.drawable.pref_item_bg));
            holder.item.setTextColor(holder.root.getContext().getColor(R.color.colorTextSecondary));
        }

        holder.item.setText(currentItem.getName());
        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mListener != null) {
                    mListener.onEvent(itemList.get(holder.getAdapterPosition()));
                }
            }
        });
    }

    // Static inner class to initialize the views of rows
    static class ViewHolder extends RecyclerView.ViewHolder {
        public View root;
        public TextView item;
        public ViewHolder(View itemView) {
            super(itemView);
            root = itemView.findViewById(R.id.pref_choice_root);
            item = (TextView) itemView.findViewById(R.id.pref_choice_tv);
        }
    }
}
