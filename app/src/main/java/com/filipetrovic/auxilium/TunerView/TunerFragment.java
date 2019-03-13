package com.filipetrovic.auxilium.TunerView;

import android.animation.Animator;
import android.content.Context;
import android.databinding.BindingAdapter;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.filipetrovic.auxilium.Dialog.AlertDialog;
import com.filipetrovic.auxilium.Interface.INoteClickToPlayEvent;
import com.filipetrovic.auxilium.Interface.INotePlayerFinished;
import com.filipetrovic.auxilium.MainActivity;
import com.filipetrovic.auxilium.R;
import com.filipetrovic.auxilium.TunerUtils.SoundPlayer;
import com.filipetrovic.auxilium.TunerUtils.TunerMode;
import com.filipetrovic.auxilium.TunerUtils.TunerOptions;
import com.filipetrovic.auxilium.TunerUtils.TunerResult;
import com.filipetrovic.auxilium.TunerUtils.Tuner;
import com.filipetrovic.auxilium.Utils.SharedPreferencesHelper;
import com.filipetrovic.auxilium.databinding.FragmentTunerBinding;

public class TunerFragment extends Fragment {

    private MainActivity activity;
    private Tuner tuner;
    private FragmentTunerBinding binding;


    private SoundPlayer soundPlayer;
    private boolean startSfxPlayed = false;
    INoteClickToPlayEvent noteClickToPlayEvent;

    public TunerFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void initTuner() {
        String tunerModeString =
                SharedPreferencesHelper.getSharedPreferenceString(getContext(), "selectedTunerMode", getString(R.string.tuner_mode_default));
        TunerMode tunerMode = TunerMode.valueOf(tunerModeString, getContext());
        tuner = new Tuner(getContext(), tunerMode);
        tuner.setSoundPlayer(soundPlayer);
        binding.setTuner(tuner);
        binding.setCurrentNotePlaying(soundPlayer.currentNote);
    }

    private void initFakeTuner() {
        tuner = new Tuner(getContext(), TunerMode.getChromaticMode(getContext()));
        tuner.isFake = true;
        binding.setTuner(tuner);
        TunerResult fakeTunerResult =
                new TunerResult(0.00, tuner.tunerMode.getNotesObjects(), new TunerOptions(getContext()));
        binding.setResult(fakeTunerResult);
        binding.setCurrentNotePlaying(soundPlayer.currentNote);
    }

    private void noteFound(final TunerResult note) {
        if(getActivity() != null && !tuner.isMuted()) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    binding.setResult(note);
                }
            });
        }
    }

    public void startAttempt() {
        if(Tuner.checkIfMicrophoneIsAvailable(getContext())) {
            // Checking if the input is already occupied by another app.
            setupUI();
            start();

            // On App Start SFX
            if(!startSfxPlayed) {
                soundPlayer.playSfxOnStart();
                startSfxPlayed = true;
            }
        } else if((tuner == null || tuner.isFake) || (!tuner.isRecording)) {
            // Checking if we've already created a placeholder tuner
            // OR if a tuner already exists but isn't recording
            initFakeTuner();
            showBusyMicrophoneDialog();
        }
    }

    public void start() {
        // Visual only
        if(tuner != null && !tuner.isFake && tuner.isRecording) {
            return;
        }
        Log.d("AUX_LOG", "TUNER START");
        initTuner();
        binding.tunerLine.setPercentage(50.0);
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

    private void setupUI() {
        binding.navigationBlockPick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isRecording()) {
                    stop();
                }
                activity.openModes();
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

    private void showBusyMicrophoneDialog() {
        final com.filipetrovic.auxilium.Dialog.AlertDialog dialog = new AlertDialog();

        dialog.show(getFragmentManager(), "dialog_busy_microphone");

        dialog.setDialogCreatedListener(new AlertDialog.OnDialogCreatedListener() {
            @Override
            public void onCreate() {
                dialog.getDialogTitle().setText(getString(R.string.busy_microphone_dialog_title));
                dialog.getDialogContent().setText(getString(R.string.busy_microphone_dialog_text));

                dialog.getPositiveButton().setText(getString(R.string.busy_microphone_dialog_button_text));
                dialog.getPositiveButton().setButtonVariant(getString(R.string.button_variant_white));
                dialog.getPositiveButton().updateStyle();

                dialog.getNegativeButton().setVisibility(View.GONE);
            }
        });

        dialog.setDialogResultListener(new AlertDialog.OnDialogResultListener() {
            @Override
            public void onPositiveResult() {
                startAttempt();
            }

            @Override
            public void onNegativeResult() {
                startAttempt();
            }
        });
    }

    public boolean isRecording() {
        return tuner.isRecording;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentTunerBinding.inflate(inflater, container, false);



        binding.menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View popupView = layoutInflater.inflate(R.layout.popup_menu, null);


                View settingsItem = popupView.findViewById(R.id.menu_item_settings);
                // Do your customised stuff

                final PopupWindow popupWindow = new PopupWindow(
                        popupView,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                popupWindow.setOutsideTouchable(true);
//                popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
//                    @Override
//                    public void onDismiss() {
//                    }
//                });
                settingsItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ((MainActivity) getActivity()).openSettings();
                        popupWindow.dismiss();
                    }
                });
                popupWindow.showAsDropDown(binding.menuButton, 1000, -1000);
            }
        });

        return binding.getRoot();
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
        soundPlayer = new SoundPlayer(getContext());
        startAttempt();
    }

    @Override
    public void onPause() {
        super.onPause();
        stop();
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
