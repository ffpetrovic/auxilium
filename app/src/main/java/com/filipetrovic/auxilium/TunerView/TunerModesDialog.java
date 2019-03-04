package com.filipetrovic.auxilium.TunerView;

import android.animation.Animator;
import android.content.Context;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ViewDataBinding;
import android.graphics.Typeface;
import android.graphics.drawable.Animatable2;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Layout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.filipetrovic.auxilium.BR;
import com.filipetrovic.auxilium.Interface.INoteClickToPlayEvent;
import com.filipetrovic.auxilium.MainActivity;
import com.filipetrovic.auxilium.ModesActivity;
import com.filipetrovic.auxilium.R;
import com.filipetrovic.auxilium.TunerUtils.Note;
import com.filipetrovic.auxilium.TunerUtils.Tuner;
import com.filipetrovic.auxilium.TunerUtils.TunerMode;
import com.filipetrovic.auxilium.TunerUtils.TunerOptions;
import com.filipetrovic.auxilium.Utils.CustomFontHelper;
import com.filipetrovic.auxilium.Utils.SharedPreferencesHelper;
import com.filipetrovic.auxilium.databinding.ViewNoteSingleBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TunerModesDialog extends Fragment {
    String tuningsJson;

    private TuningSelectedListener mListener;
    private TunerMode tunerModeSelected = null;
    private List<String> tunings = new ArrayList<>();
    private List<View> tuningActiveIndicators = new ArrayList<>();
    TunerOptions tunerOptions;

    private MainActivity activity;

    private View rootView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.view_tunings_bottom_sheet, container, false);
        this.rootView = root;
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();

        tunerOptions = new TunerOptions(getContext());
        tuningsJson = getString(R.string.tunings_json);



        inflateScreen(rootView, getLayoutInflater());
        for(int i = 0; i < tunings.size(); i++) {
            if(tunings.get(i).equals(SharedPreferencesHelper.getSharedPreferenceString(rootView.getContext(), "selectedTunerMode", getString(R.string.tuner_mode_default)))) {
                tuningActiveIndicators.get(i).setVisibility(View.VISIBLE);
            } else {
                tuningActiveIndicators.get(i).setVisibility(View.INVISIBLE);
            }
        }
    }

    public void inflateScreen(View root, LayoutInflater inflater) {

        JSONObject mainObject = null;
        try {
            mainObject = new JSONObject(tuningsJson);
            JSONArray tuningsArray = mainObject.getJSONArray("tunings");
            for(int i = 0; i < tuningsArray.length(); i++) {
                JSONObject tuningsGroup = tuningsArray.getJSONObject(i);
                String tuningsGroupTitle = tuningsGroup.getString("groupName");
                JSONArray tunings = tuningsGroup.getJSONArray("groupTunings");
                addTitleView(tuningsGroupTitle, root.findViewById(R.id.tuningsInnerWrapper),inflater);
                for(int j = 0; j < tunings.length(); j++) {
                    JSONObject tuning = tunings.getJSONObject(j);
                    TunerMode mode;
                    if(tuning.getString("tuningName").equals("Automatic")) {
                        mode = TunerMode.getChromaticMode(getContext());
                    } else {
                        mode = new TunerMode(tunerOptions);
                        mode.setName(tuning.getString("tuningName"));
                        mode.setNotes(tuning.getString("tuningNotes"));
                        mode.setGroup(tuningsGroupTitle);
                    }
                    addTuningsItemView(mode, root.findViewById(R.id.tuningsInnerWrapper),inflater);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        root.findViewById(R.id.buttonClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });
    }

    void addTitleView(String title, View r, LayoutInflater inflater) {
        ViewGroup root = (ViewGroup) r;
        View view = inflater
                .inflate(R.layout.view_tunings_bottom_sheet_title, root, false);

        ((TextView) view).setText(title);
        root.addView(view);
    }


    void addTuningsItemView(final TunerMode mode, final View r, LayoutInflater inflater) {
        ViewGroup root = (ViewGroup) r;
        String title = mode.getNameLabel();
        String notes = mode.getNotesLabel();
        View view = inflater
                .inflate(R.layout.view_tunings_bottom_sheet_item, root, false);

        tunings.add(mode.toString());
        tuningActiveIndicators.add(view.findViewById(R.id.activeModeIndicator));

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferencesHelper.setSharedPreferenceString(r.getContext(), "selectedTunerMode", mode.toString());
                getActivity().finish();
            }
        });

        ((TextView) view.findViewById(R.id.tuningName)).setText(title);
        ((TextView) view.findViewById(R.id.tuningNotes)).setText(notes);

        root.addView(view);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    //    public void showDialog() {
//        setVisibility(VISIBLE);
//        animate().setDuration(300).translationY(0f).alpha(1).setListener(null);
//
//        for(int i = 0; i < tunings.size(); i++) {
//            if(tunings.get(i).equals(SharedPreferencesHelper.getSharedPreferenceString(getContext(), "selectedTunerMode", SharedPreferencesHelper.defaultTunerMode))) {
//                tuningActiveIndicators.get(i).setVisibility(VISIBLE);
//            } else {
//                tuningActiveIndicators.get(i).setVisibility(INVISIBLE);
//            }
//        }
//    }
//
//    public void hideDialog() {
//        if(tunerModeSelected != null) {
//            mListener.onEvent(tunerModeSelected);
//            tunerModeSelected = null;
//        }
//        animate().setDuration(300).translationY(100f).alpha(0).setListener(new Animator.AnimatorListener() {
//            @Override
//            public void onAnimationStart(Animator animator) {
//
//            }
//
//            @Override
//            public void onAnimationEnd(Animator animator) {
//                setVisibility(GONE);
//            }
//
//            @Override
//            public void onAnimationCancel(Animator animator) {
//
//            }
//
//            @Override
//            public void onAnimationRepeat(Animator animator) {
//
//            }
//        });
//    }


    @Override
    public void onPause() {
        super.onPause();
    }

    public interface TuningSelectedListener {
        void onEvent(TunerMode mode);
    }
    public void setTuningSelectedListener(TuningSelectedListener listener) {
        mListener = listener;
    }
}