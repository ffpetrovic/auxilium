package com.filipetrovic.auxilium.TunerView;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.filipetrovic.auxilium.R;
import com.filipetrovic.auxilium.TunerUtils.TunerMode;
import com.filipetrovic.auxilium.Utils.SharedPreferencesHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* Tunings JSON Tool: https://codepen.io/ffpetrovic/pen/pQGVxo */

public class TunerModesBottomSheet extends BottomSheetDialogFragment {
    String tuningsJson = "{\"tunings\":[{\"groupName\":\"Guitar\",\"groupTunings\":[{\"tuningName\":\"Standard\",\"tuningNotes\":\"E2 A2 D3 G3 B3 E4\"},{\"tuningName\":\"Half-Step Down\",\"tuningNotes\":\"D#2 G#2 C#3 F#3 A#3 D#4\"},{\"tuningName\":\"Drop D\",\"tuningNotes\":\"D2 A2 D3 G3 B3 E4\"},{\"tuningName\":\"Open D\",\"tuningNotes\":\"D2 A2 D3 F#3 A3 D4\"},{\"tuningName\":\"Open G\",\"tuningNotes\":\"D2 G2 D3 G3 B3 D4\"},{\"tuningName\":\"Open A\",\"tuningNotes\":\"E2 A2 E3 A3 C#4 E4\"},{\"tuningName\":\"Lute\",\"tuningNotes\":\"E2 A2 D3 F#3 B3 E4\"},{\"tuningName\":\"Irish\",\"tuningNotes\":\"D2 A2 D3 G3 A3 D4\"}]},{\"groupName\":\"Bass\",\"groupTunings\":[{\"tuningName\":\"Standard\",\"tuningNotes\":\"E1 A1 D2 G2\"},{\"tuningName\":\"Open D\",\"tuningNotes\":\"D1 A1 D2 G2\"},{\"tuningName\":\"Drop D\",\"tuningNotes\":\"D1 G1 C2 F2\"}]},{\"groupName\":\"Fiddle\",\"groupTunings\":[{\"tuningName\":\"Violin\",\"tuningNotes\":\"G3 D4 A4 E5\"},{\"tuningName\":\"Viola\",\"tuningNotes\":\"C3 G3 D4 A4\"},{\"tuningName\":\"Cello\",\"tuningNotes\":\"C2 G2 D3 A3\"}]}]}";

    private BottomSheetListener mListener;

    private TunerMode tunerModeSelected = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.view_tunings_bottom_sheet, container, false);

        JSONObject mainObject = null;
        try {
            mainObject = new JSONObject(tuningsJson);
            JSONArray tuningsArray = mainObject.getJSONArray("tunings");
            for(int i = 0; i < tuningsArray.length(); i++) {
                JSONObject tuningsGroup = tuningsArray.getJSONObject(i);
                String tuningsGroupTitle = tuningsGroup.getString("groupName");
                JSONArray tunings = tuningsGroup.getJSONArray("groupTunings");

                addTitleView(tuningsGroupTitle, root.findViewById(R.id.tuningsInnerWrapper), inflater);
                for(int j = 0; j < tunings.length(); j++) {
                    JSONObject tuning = tunings.getJSONObject(j);
                    TunerMode mode = new TunerMode();
                    mode.setName(tuning.getString("tuningName"));
                    mode.setNotes(tuning.getString("tuningNotes"));
                    mode.setGroup(tuningsGroupTitle);
                    addTuningsItemView(mode, root.findViewById(R.id.tuningsInnerWrapper), inflater);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        root.findViewById(R.id.buttonClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });



        return root;
    }

    @Override
    public int getTheme() {
        return R.style.AppTheme_BottomSheet;
    }

    void addTitleView(String title, View r, LayoutInflater inflater) {
        ViewGroup root = (ViewGroup) r;
        View view = inflater
                .inflate(R.layout.view_tunings_bottom_sheet_title, root, false);

        ((TextView) view).setText(title);
        root.addView(view);
    }

    void addTuningsItemView(final TunerMode mode, View r, LayoutInflater inflater) {
        ViewGroup root = (ViewGroup) r;
        String title = mode.getName();
        String notes = mode.getNotes();
        View view = inflater
                .inflate(R.layout.view_tunings_bottom_sheet_item, root, false);

        if(mode.toString().equals(SharedPreferencesHelper.getSharedPreferenceString(r.getContext(), "selectedTunerMode", SharedPreferencesHelper.defaultTunerMode))) {
            view.findViewById(R.id.activeModeIndicator).setVisibility(View.VISIBLE);
        }

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tunerModeSelected = mode;
                dismiss();
            }
        });

        ((TextView) view.findViewById(R.id.tuningName)).setText(title);
        ((TextView) view.findViewById(R.id.tuningNotes)).setText(notes);
        root.addView(view);
    }

    public interface BottomSheetListener {
        void onTuningSelected(TunerMode mode);
    }

    public void setBottomSheetListener(BottomSheetListener listener) {
        mListener = listener;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);

        mListener.onTuningSelected(tunerModeSelected);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog d = super.onCreateDialog(savedInstanceState);
        d.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                BottomSheetDialog d = (BottomSheetDialog) dialog;
                FrameLayout bottomSheet = (FrameLayout) d.findViewById(android.support.design.R.id.design_bottom_sheet);
                BottomSheetBehavior behaviour = BottomSheetBehavior.from(bottomSheet);
                behaviour.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                    @Override
                    public void onStateChanged(@NonNull View bottomSheet, int newState) {
                        if(newState == BottomSheetBehavior.STATE_HIDDEN) {
                            dismiss();
                        }
                    }

                    @Override
                    public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                        if (slideOffset == 1.0) {
                            bottomSheet.findViewById(R.id.navigationBlockActiveIndicator)
                                    .setVisibility(View.INVISIBLE);
                        } else {
                            bottomSheet.findViewById(R.id.navigationBlockActiveIndicator)
                                    .setVisibility(View.VISIBLE);
                        }
                    }
                });
            }
        });
        return d;
    }
}
