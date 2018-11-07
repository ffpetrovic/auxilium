package com.filipetrovic.auxilium.TunerView;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.filipetrovic.auxilium.BR;
import com.filipetrovic.auxilium.Interface.INoteClickToPlayEvent;
import com.filipetrovic.auxilium.R;
import com.filipetrovic.auxilium.TunerUtils.Note;
import com.filipetrovic.auxilium.TunerUtils.NotePlayer;
import com.filipetrovic.auxilium.TunerUtils.TunerMode;
import com.filipetrovic.auxilium.TunerUtils.TunerResult;
import com.filipetrovic.auxilium.TunerUtils.Tuner;
import com.filipetrovic.auxilium.databinding.FragmentTunerBinding;

public class TunerFragment extends Fragment {
    private Tuner tuner;
    private FragmentTunerBinding binding;
    private float pPerc;

    private NotePlayer notePlayer;

    INoteClickToPlayEvent noteClickToPlayEvent;

    public TunerFragment() {}
    public static TunerFragment newInstance(String param1, String param2) {
        TunerFragment fragment = new TunerFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        notePlayer = new NotePlayer(getContext());
//        initTuner();
    }

    public void resetTuner(View v) {
        stop();
        start();
    }

    public void initTuner() {
        TunerMode tunerMode = new TunerMode();
        tunerMode.setName("Standard");
        tunerMode.setGroup("Guitar");
        tunerMode.setNotes("E2 A2 D3 G3 B3 E4");
        tuner = new Tuner(getContext(), tunerMode);
        binding.setTuner(tuner);
        binding.setNotePlayer(notePlayer);
    }

    private void noteFound(final TunerResult note) {
        if(getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    binding.setResult(note);


                    String statusText = "";
                    if(note.percentageActual > 50f && note.type == Indicator.INDICATOR_TYPE.ACTIVE) {
                        statusText = "Too sharp!";
                    } else if(note.percentageActual < 50f && note.type == Indicator.INDICATOR_TYPE.ACTIVE) {
                        statusText = "Too flat!";
                    } else if(note.type == Indicator.INDICATOR_TYPE.CORRECT) {
                        statusText = "In tune!";
                    } else if(note.type == Indicator.INDICATOR_TYPE.INACTIVE) {
                        statusText = "...";
                    } else if(note.type == Indicator.INDICATOR_TYPE.INCORRECT) {
                        statusText = "Off by " + Math.abs(50.00 - note.getPercentageActual()) + "%";
                    }
                    ((TextView) getActivity().findViewById(R.id.tunerStatus)).setText(statusText);
//
//                    pPerc = (float) perc;
                }
            });
        }
    }

    public void start() {
        // Visual only
        initTuner();
        binding.tunerLine.setPercentage(50f);
        tuner.start();
        tuner.setOnNoteFoundListener(new Tuner.OnNoteFoundListener() {
            @Override
            public void onEvent(TunerResult note) {
                noteFound(note);
            }
        });
        tuner.sendNullResult();
    }

    public void stop() {
        notePlayer.playNote("");
        tuner.stop();
        tuner.destroy();
    }

    public boolean isRecording() {
        return tuner.isRecording;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentTunerBinding.inflate(inflater, container, false);
        binding.navigationBlockAutomatic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stop();
                TunerMode tunerMode = new TunerMode();
                tunerMode.setName("Chromatic");
                tunerMode.setGroup("");
                tunerMode.setNotes("A1 A#1 B1 C1 C#1 D1 D#1 E1 F1 F#1 G1 G#1 A2 A#2 B2 C2 C#2 D2 D#2 E2 F2 F#2 G2 G#2 A3 A#3 B3 C3 C#3 D3 D#3 E3 F3 F#3 G3 G#3 A4 A#4 B4 C4 C#4 D4 D#4 E4 F4 F#4 G4 G#4 A5 A#5 B5 C5 C#5 D5 D#5 E5 F5 F#5 G5 G#5 A6 A#6 B6 C6 C#6 D6 D#6 E6 F6 F#6 G6 G#6 A7 A#7 B7 C7 C#7 D7 D#7 E7 F7 F#7 G7 G#7 A8 A#8 B8 C8 C#8 D8 D#8 E8 F8 F#8 G8 G#8 ");
                tuner = new Tuner(getContext(), tunerMode);
                binding.setTuner(tuner);
                binding.tunerLine.setPercentage(50f);
                tuner.start();
                tuner.setOnNoteFoundListener(new Tuner.OnNoteFoundListener() {
                    @Override
                    public void onEvent(TunerResult note) {
                        noteFound(note);
                    }
                });
            }
        });
        binding.navigationBlockPick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stop();
                TunerMode tunerMode = new TunerMode();
                tunerMode.setName("Standard");
                tunerMode.setGroup("Guitar");
                tunerMode.setNotes("E2 A2 D3 G3 B3 E4");
                tuner = new Tuner(getContext(), tunerMode);
                binding.setTuner(tuner);
                binding.tunerLine.setPercentage(50f);
                tuner.start();
                tuner.setOnNoteFoundListener(new Tuner.OnNoteFoundListener() {
                    @Override
                    public void onEvent(TunerResult note) {
                        noteFound(note);
                    }
                });
            }
        });

        noteClickToPlayEvent = new INoteClickToPlayEvent() {
            @Override
            public void onEvent(String note) {
                notePlayer.playNote(note);
            }
        };
        binding.notesCollectionBlock.setNoteClickToPlayEvent(noteClickToPlayEvent);
        return binding.getRoot();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
//        mListener = null;
    }

    @BindingAdapter({"android:src"})
    public static void setImageViewResource(ImageView imageView, int resource) {
        imageView.setImageResource(resource);
    }
}
