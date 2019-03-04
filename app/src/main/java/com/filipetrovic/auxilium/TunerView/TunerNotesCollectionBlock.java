package com.filipetrovic.auxilium.TunerView;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.filipetrovic.auxilium.Interface.INoteClickToPlayEvent;
import com.filipetrovic.auxilium.R;
import com.filipetrovic.auxilium.TunerUtils.Note;
import com.filipetrovic.auxilium.TunerUtils.TunerOptions;
import com.filipetrovic.auxilium.Utils.CustomFontHelper;
import com.filipetrovic.auxilium.databinding.ViewNoteSingleBinding;

public class TunerNotesCollectionBlock extends LinearLayout {
    public ViewNoteSingleBinding[] noteViewBindings;
    private INoteClickToPlayEvent noteClickToPlayEvent;

    public TunerNotesCollectionBlock(Context context) {
        super(context);
        init(null, 0);
    }

    public TunerNotesCollectionBlock(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public TunerNotesCollectionBlock(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    public void init(AttributeSet attrs, int defStyle) {
        inflate(getContext(), R.layout.view_tuner_navigation_block, this);
    }

    public void setNoteClickToPlayEvent(INoteClickToPlayEvent noteClickToPlayEvent) {
        this.noteClickToPlayEvent = noteClickToPlayEvent;
    }

    public INoteClickToPlayEvent getNoteClickToPlayEvent() {
        return noteClickToPlayEvent;
    }

    @BindingAdapter({"notes"})
    public static <T> void setEntries(final TunerNotesCollectionBlock collectionBlockView,
                                      Note[] entries) {
        collectionBlockView.removeAllViews();

        if (entries != null) {
            LayoutInflater inflater = (LayoutInflater)
                    collectionBlockView.getContext()
                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            collectionBlockView.noteViewBindings = new ViewNoteSingleBinding[entries.length];
            for (int i = 0; i < entries.length; i++) {
                Note n = entries[i];
                final ViewNoteSingleBinding binding = DataBindingUtil
                        .inflate(inflater, R.layout.view_note_single, collectionBlockView, true);
                collectionBlockView.noteViewBindings[i] = binding;
                binding.getRoot().findViewById(R.id.viewRoot).setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        collectionBlockView.getNoteClickToPlayEvent().onEvent(binding.getNote().getRealNote() + binding.getNote().getOctave());
                    }
                });
                binding.setNote(n);
                binding.setIsInTune(n.isInTune);
            }
        }
    }

    @BindingAdapter({"currentNote"})
    public static void setCurrentNote(TunerNotesCollectionBlock collectionBlockView,
                                      String note) {
        if(collectionBlockView.noteViewBindings != null) {
            for(ViewNoteSingleBinding noteViewBinding : collectionBlockView.noteViewBindings) {
                noteViewBinding.setResult(note);
            }
        }
    }

    @BindingAdapter({"currentNotePlaying"})
    public static void setCurrentNotePlaying(TunerNotesCollectionBlock collectionBlockView,
                                      ObservableField<String> note) {
        if(collectionBlockView.noteViewBindings != null) {
            if(!note.get().equals("")) {
                Note parsedNote = Note.parse(note.get(), new TunerOptions(collectionBlockView.getContext()));
                String localizedNote = parsedNote.getTranslatedNote() + parsedNote.getOctave();
                for(ViewNoteSingleBinding noteViewBinding : collectionBlockView.noteViewBindings) {
                    noteViewBinding.setCurrentNotePlaying(new ObservableField<>(localizedNote));
                }
            } else {
                for(ViewNoteSingleBinding noteViewBinding : collectionBlockView.noteViewBindings) {
                    noteViewBinding.setCurrentNotePlaying(note);
                }
            }

        }
    }

    @BindingAdapter({"android:isNoteViewInTune"})
    public static void setisNoteViewInTune(TextView view, ObservableBoolean isInTune) {
        if(isInTune.get()) {
            view.setTextColor(ContextCompat.getColor(view.getContext(), R.color.colorGreen));
            CustomFontHelper.setCustomFont(view, "font/spacegrotesk_bold.otf", view.getContext());
        }
    }
}
