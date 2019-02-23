package com.filipetrovic.auxilium.TunerView;

import android.animation.Animator;
import android.content.Context;
import android.databinding.BindingAdapter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.filipetrovic.auxilium.Interface.INoteClickToPlayEvent;
import com.filipetrovic.auxilium.Interface.INotePlayerFinished;
import com.filipetrovic.auxilium.MainActivity;
import com.filipetrovic.auxilium.R;
import com.filipetrovic.auxilium.TunerUtils.SoundPlayer;
import com.filipetrovic.auxilium.TunerUtils.TunerMode;
import com.filipetrovic.auxilium.TunerUtils.TunerResult;
import com.filipetrovic.auxilium.TunerUtils.Tuner;
import com.filipetrovic.auxilium.Utils.SharedPreferencesHelper;
import com.filipetrovic.auxilium.databinding.FragmentTunerBinding;

public class TunerFragment extends Fragment {
    @Override
    public void onResume() {
        super.onResume();

        Log.d("AUX_LOG", "onResume()");
    }

    private Tuner tuner;
    private FragmentTunerBinding binding;
    private float pPerc;
    private MainActivity activity;

    private SoundPlayer soundPlayer;

    INoteClickToPlayEvent noteClickToPlayEvent;
    DialogUnavailableMicrophone dialogUnavailableMicrophone;

    public TunerFragment() {}
    public static TunerFragment newInstance(String param1, String param2) {
        TunerFragment fragment = new TunerFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        soundPlayer = new SoundPlayer(getContext());

        Log.d("AUX_LOG", "TunerFragment onCreate");

//        initTuner();
    }

    public void resetTuner(View v) {
        stop();
        start();
    }

    public void initTuner() {
        String tunerModeString =
                SharedPreferencesHelper.getSharedPreferenceString(getContext(), "selectedTunerMode", SharedPreferencesHelper.defaultTunerMode);
        TunerMode tunerMode = TunerMode.valueOf(tunerModeString);
        tuner = new Tuner(getContext(), tunerMode);
        tuner.setSoundPlayer(soundPlayer);
        binding.setTuner(tuner);
        binding.setCurrentNotePlaying(soundPlayer.currentNote);
    }

    private void noteFound(final TunerResult note) {
        if(getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(!soundPlayer.isPlaying()) {
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
                            statusText = "Off by " + Math.abs(50 - Math.round(note.getPercentageActual())) + "%";
                        }
                        (binding.tunerStatus).setText(statusText);
    //
                        //    //                    pPerc = (float) perc;
                    }
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
        if(tuner.isRecording) {
            soundPlayer.playNote("");
            tuner.stop();
            tuner.destroy();
        }
    }

    public boolean isRecording() {
        return tuner.isRecording;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentTunerBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    private void setupUI() {
        binding.navigationBlockAutomatic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stop();
                TunerMode tunerMode = TunerMode.getChromaticMode();
                SharedPreferencesHelper.setSharedPreferenceString(getContext(), "selectedTunerMode", tunerMode.toString());
                start();
            }
        });
        binding.navigationBlockPick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                stop();
//                TunerMode tunerMode = new TunerMode();
//                tunerMode.setName("Standard");
//                tunerMode.setGroup("Guitar");
//                tunerMode.setNotes("E2 A2 D3 G3 B3 E4");
//                tuner = new Tuner(getContext(), tunerMode);
//                binding.setTuner(tuner);
//                binding.tunerLine.setPercentage(50f);
//                tuner.start();
//                tuner.setOnNoteFoundListener(new Tuner.OnNoteFoundListener() {
//                    @Override
//                    public void onEvent(TunerResult note) {
//                        noteFound(note);
//                    }
//                });

                if(isRecording()) {
                    stop();
                }
                // Bottom Sheet was performing really badly. Perhaps due to inflating tens of
                // dozens of views each time it's open. Also it was behaving strangely onPause
                // and on other lifecycle events. See TunerModesBottomSheet.java for usage.
                // Resorting to a static full-screen modal type of view.
//                binding.tunerModesDialog.showDialog();
                activity.showTunerModesFragment();
//                bottomSheet.show(getFragmentManager(), "tunerModesBottomSheet");
//                bottomSheet.setBottomSheetListener(new TunerModesBottomSheet.BottomSheetListener() {
//                    @Override
//                    public void onTuningSelected(TunerMode mode) {
//                        if(mode != null) {
//                            SharedPreferencesHelper.setSharedPreferenceString(getContext(), "selectedTunerMode", mode.toString());
//                        }
//                        start();
//                    }
//                });
            }
        });

        noteClickToPlayEvent = new INoteClickToPlayEvent() {
            @Override
            public void onEvent(String note) {
                tuner.sendNullResult();
                soundPlayer.playNote(note);
                tuner.setMuted(soundPlayer.isPlayingNote());
            }
        };
//        binding.viewCurrentNotePlayingIndicator.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                soundPlayer.playNote("");
//                tuner.setMuted(soundPlayer.isPlaying());
//            }
//        });
        binding.notesCollectionBlock.setNoteClickToPlayEvent(noteClickToPlayEvent);
        soundPlayer.setOnNotePlayerFinished(new INotePlayerFinished() {
            @Override
            public void onEvent() {
                binding.tunerPlayingBlock.stopAnimation();
                if(soundPlayer.isPlaying()) {
                    binding.tunerPlayingBlock.startAnimation();
                }
            }
        });
        binding.tunerPlayingBlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                soundPlayer.playNote("");
                tuner.setMuted(false);
            }
        });
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof AppCompatActivity){
            this.activity = (MainActivity) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.activity = null;
    }

    @Override
    public void onStart() {
        super.onStart();
        startAttempt();
    }

    @Override
    public void onPause() {
        super.onPause();
        stop();
    }

    public void startAttempt() {
        if(Tuner.checkIfMicrophoneIsAvailable(getContext())) {
            // Checking if the input is already occupied by another app.
            setupUI();
            start();

            // Uncomment to play SFX on app start
            // soundPlayer.playSfxOnStart();

            if(dialogUnavailableMicrophone != null && dialogUnavailableMicrophone.isVisible()) {
                // Closing the dialog if it's visible and the input is available.
                dialogUnavailableMicrophone.dismiss();
            }
        } else if((tuner == null || tuner.isFake) || (!tuner.isRecording)) {
            // Checking if we've already created a placeholder tuner
            // OR if a tuner already exists but isn't recording
            tuner = new Tuner(getContext(), TunerMode.getChromaticMode());
            tuner.isFake = true;
            binding.setTuner(tuner);
            TunerResult fakeTunerResult =
                    new TunerResult(0.00, tuner.tunerMode.getNotesObjects());
            binding.setResult(fakeTunerResult);
            binding.setCurrentNotePlaying(soundPlayer.currentNote);

            if(dialogUnavailableMicrophone == null) {
                Log.d("AUX_LOG", "DIALOG IS NULL");
                dialogUnavailableMicrophone = DialogUnavailableMicrophone.newInstance();
            }
            dialogUnavailableMicrophone.setOnDismissListener(new DialogUnavailableMicrophone.onDismissListener() {
                @Override
                public void onDismiss() {
                    startAttempt();
                }
            });
            if(!dialogUnavailableMicrophone.isAdded()) {
                dialogUnavailableMicrophone.show(getFragmentManager(), "fragment_busy_microphone");
            } else {
                dialogUnavailableMicrophone.getDialog().show();
            }
        }
    }


    @BindingAdapter({"app:isTunerResultVisible"})
    public static void setIsTunerResultVisible(final View v, boolean isActive) {
        if(v.getId() == R.id.tunerListeningBlock) {
            TunerModeListeningBlock view = (TunerModeListeningBlock) v;
            if(isActive && !view.isActive) {
                view.setVisibility(View.VISIBLE);
                view.isActive = true;
                view.clearAnimation();
                view.animate().alpha(1f).setDuration(400);
                view.startAnimation();
            } else if(!isActive &&  view.isActive) {
                view.stopAnimation();
                view.isActive = false;
                view.clearAnimation();
                view.animate().alpha(0f).setDuration(0);
                view.setVisibility(View.INVISIBLE);
            }
        }
        if(v.getId() == R.id.tunerPlayingBlock) {
            TunerModePlayingBlock view = (TunerModePlayingBlock) v;
            if(isActive && !view.isActive) {
                view.setVisibility(View.VISIBLE);
                view.isActive = true;
                view.clearAnimation();
                view.animate().alpha(1f).setDuration(400);
//                view.startAnimation();
            } else if(!isActive && view.isActive) {
//                view.stopAnimation();
                view.isActive = false;
                view.clearAnimation();
                view.animate().alpha(0f).setDuration(0);
                view.setVisibility(View.GONE);
            }
        }
        if(v.getId() == R.id.tvNote || v.getId() == R.id.tvOctave || v.getId() == R.id.tvNoteSharp) {
            final View view = v;
            if(isActive) {
                view.setVisibility(View.VISIBLE);
                view.setAlpha(1f);
                view.animate().setListener(null);
            } else {
                view.animate().alpha(0).setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        view.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {

                    }
                });
            }
        }
    }

    @BindingAdapter({"android:src"})
    public static void setImageViewResource(ImageView imageView, int resource) {
        imageView.setImageResource(resource);
    }
}
